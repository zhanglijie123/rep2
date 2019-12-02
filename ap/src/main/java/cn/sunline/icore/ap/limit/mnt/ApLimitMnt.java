package cn.sunline.icore.ap.limit.mnt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.api.ApBufferApi;
import cn.sunline.icore.ap.api.ApCurrencyApi;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.api.ApDropListApi;
import cn.sunline.icore.ap.api.ApSeqApi;
import cn.sunline.icore.ap.namedsql.ApAttributeDao;
import cn.sunline.icore.ap.namedsql.ApBasicDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_limitDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_limit_driveDao;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit_drive;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitBasicInfo;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitBasicInfoLstQryIn;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitDetailInfo;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitDriveInfo;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitDriveMntInput;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitMntIn;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitStatisResult;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_ADDSUBTRACT;
import cn.sunline.icore.sys.type.EnumType.E_LIMITCTRLCLASS;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

public class ApLimitMnt {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApLimitMnt.class);
	private static final String LIMIT_SEQ_CODE = "LIMIT_NO";
	private static final String LIMIT_SEQ_PREFIX = "LIMIT";
	
	public static String genLimitNO() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("prefix_seq", LIMIT_SEQ_PREFIX);
		ApBufferApi.appendData(ApConst.PARM_DATA_MART, param);
		
		return ApSeqApi.genSeq(LIMIT_SEQ_CODE);
	}

	/**
	 * @Author wangzhw
	 *         <p>
	 *         <li>2017年6月2日-上午10:51:22</li>
	 *         <li>功能说明：限额登记簿查询</li>
	 *         </p>
	 * @param limitOwnerId
	 * @param limitStatisNo
	 * @return
	 */
	public static Options<ApLimitStatisResult> queryLimitStatis(String limitOwnerId, String limitStatisNo) {

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		String orgID = MsOrg.getReferenceOrgId(app_limit.class);

		//对limit_statis_no进行必输项检查
		BizUtil.fieldNotNull(limitStatisNo, SysDict.A.limit_statis_no.getId(), SysDict.A.limit_statis_no.getLongName());

		Page<ApLimitStatisResult> page = ApAttributeDao.selApbLimitStatis(orgID, limitOwnerId, limitStatisNo, runEnvs.getPage_start(), runEnvs.getPage_size(),
				runEnvs.getTotal_count(), false);

		runEnvs.setTotal_count(page.getRecordCount());

		Options<ApLimitStatisResult> apLimitStatisResult = new DefaultOptions<ApLimitStatisResult>();

		apLimitStatisResult.setValues(page.getRecords());

		return apLimitStatisResult;
	}

	/**
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>function：query the list of limit basic information </li>
	 *         </p>
	 * @param qryInput
	 * @return
	 */
	public static Options<ApLimitBasicInfo> lstLimitBasicInfo(ApLimitBasicInfoLstQryIn qryInput) {
		qryInput.setOrg_id(MsOrg.getReferenceOrgId(app_limit.class));

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Page<ApLimitBasicInfo> page = ApAttributeDao.selAppLimitParam(qryInput, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);

		runEnvs.setTotal_count(page.getRecordCount());

		Options<ApLimitBasicInfo> list = new DefaultOptions<>();
		list.setValues(page.getRecords());

		return list;
	}

	/**
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>function：query the detail information of limit, contains the limit drive list</li>
	 *         </p>
	 * @param limitNo
	 * @param effectDate
	 * @return
	 */
	public static ApLimitDetailInfo getLimitDetailInfo(String limitNo, String effectDate) {
		BizUtil.fieldNotNull(limitNo, SysDict.A.limit_no.getId(), SysDict.A.limit_no.getLongName());
		BizUtil.fieldNotNull(effectDate, SysDict.A.effect_date.getId(), SysDict.A.effect_date.getLongName());

		app_limit limitInfo = ApBasicDao.selectApplimitInfo(limitNo, effectDate, false);
		if (CommUtil.isNull(limitInfo)) {
			throw APPUB.E0005(OdbFactory.getTable(app_limit.class).getLongname(), "(" + SysDict.A.limit_no.getId() + "," + SysDict.A.expiry_date.getId() + ")", "(" + limitNo + ","
					+ effectDate + ")");
		}

		ApLimitDetailInfo result = BizUtil.getInstance(ApLimitDetailInfo.class);

		fillResultWithEntity(result, limitInfo);

		fetchDrivesIntoResult(result, limitNo);

		return result;
	}

	private static void fetchDrivesIntoResult(ApLimitDetailInfo result, String limitNo) {
		List<app_limit_drive> drives = App_limit_driveDao.selectAll_odb2(limitNo, false);

		for (app_limit_drive dbDrive : drives) {
			ApLimitDriveInfo driveInfo = BizUtil.getInstance(ApLimitDriveInfo.class);
			driveInfo.setLimit_no(limitNo);
			driveInfo.setTrxn_event_id(dbDrive.getTrxn_event_id());
			driveInfo.setLimit_drive_cond(dbDrive.getLimit_drive_cond());
			driveInfo.setLimit_sum_way(dbDrive.getLimit_sum_way());

			result.getList01().add(driveInfo);
		}
	}

	private static void fillResultWithEntity(ApLimitDetailInfo result, app_limit limitInfo) {
		result.setLimit_no(limitInfo.getLimit_no()); //limit no
		result.setLimit_desc(limitInfo.getLimit_desc()); //limit describe
		result.setEffect_date(limitInfo.getEffect_date()); //effect date
		result.setExpiry_date(limitInfo.getExpiry_date()); //expiry date
		result.setLimit_level(limitInfo.getLimit_level()); //limit level
		result.setLimit_ctrl_class(limitInfo.getLimit_ctrl_class()); //limit control class
		result.setLimit_reset_cycle(limitInfo.getLimit_reset_cycle()); //limit reset cycle
		result.setLimit_statis_no(limitInfo.getLimit_statis_no()); //limit statis no
		result.setOwner_mart(limitInfo.getOwner_mart()); //owner mart
		result.setOwner_field(limitInfo.getOwner_field()); //owner field
		result.setLimit_ccy(limitInfo.getLimit_ccy()); //limit currency
		result.setLimit_value(limitInfo.getLimit_value()); //limit value
		result.setLimit_custom_allow(limitInfo.getLimit_custom_allow()); //limit_custom_allow
		result.setMin_limit_value(limitInfo.getMin_limit_value()); //min limit value
		result.setMax_limit_value(limitInfo.getMax_limit_value()); //max limit value
		result.setLimit_sms_template_no(limitInfo.getLimit_sms_template_no()); //limit SMS template
		result.setData_version(limitInfo.getData_version()); //data version
	}

	/**
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>function：maintain the limit information, contains the limit drive list.</li>
	 *         </p>
	 * @param limitInfo
	 */
	public static void maintainLimitInfo(ApLimitMntIn limitInfo) {
		bizlog.method(" ApLimitMnt.maintainLimitInfo begin.");
		bizlog.debug(" ApLimitMnt.maintainLimitInfo input[1]-limitInfo=%s", limitInfo);

		validParamIsNullable(limitInfo);

		validParamWithBusiRule(limitInfo);

		doMaintain(limitInfo);

		bizlog.method(" ApLimitMnt.maintainLimitInfo end.");
	}

	private static void validParamIsNullable(ApLimitMntIn limitInfo) {
		validBasicPropsNullable(limitInfo);
		validLstDrivePropsNullable(limitInfo.getList01(), limitInfo);
	}

	private static void validBasicPropsNullable(ApLimitMntIn limitInfo) {
		BizUtil.fieldNotNull(limitInfo.getOperater_ind(), SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());
		BizUtil.fieldNotNull(limitInfo.getLimit_no(), SysDict.A.limit_no.getId(), SysDict.A.limit_no.getLongName());
		BizUtil.fieldNotNull(limitInfo.getEffect_date(), SysDict.A.effect_date.getId(), SysDict.A.effect_date.getLongName());
		
		if (limitInfo.getOperater_ind() != E_DATAOPERATE.DELETE) {
			BizUtil.fieldNotNull(limitInfo.getLimit_desc(), SysDict.A.limit_desc.getId(), SysDict.A.limit_desc.getLongName());
			BizUtil.fieldNotNull(limitInfo.getExpiry_date(), SysDict.A.expiry_date.getId(), SysDict.A.expiry_date.getLongName());
			BizUtil.fieldNotNull(limitInfo.getLimit_ctrl_class(), SysDict.A.limit_ctrl_class.getId(), SysDict.A.limit_ctrl_class.getLongName());
			BizUtil.fieldNotNull(limitInfo.getLimit_statis_no(), SysDict.A.limit_statis_no.getId(), SysDict.A.limit_statis_no.getLongName());
			
			if (limitInfo.getLimit_ctrl_class() == E_LIMITCTRLCLASS.CUMULATIVE_AMOUNT || limitInfo.getLimit_ctrl_class() == E_LIMITCTRLCLASS.CUMULATIVE_NUMBER) {
				BizUtil.fieldNotNull(limitInfo.getLimit_reset_cycle(), SysDict.A.limit_reset_cycle.getId(), SysDict.A.limit_reset_cycle.getLongName());
			}
			
			if (limitInfo.getLimit_ctrl_class() != E_LIMITCTRLCLASS.CUMULATIVE_NUMBER && limitInfo.getLimit_ctrl_class() != E_LIMITCTRLCLASS.TIMES_COUNTER) {
				BizUtil.fieldNotNull(limitInfo.getLimit_ccy(), SysDict.A.limit_ccy.getId(), SysDict.A.limit_ccy.getLongName());
			}
			
			if (limitInfo.getLimit_ctrl_class() != E_LIMITCTRLCLASS.AMOUNT_COUNTER && limitInfo.getLimit_ctrl_class() != E_LIMITCTRLCLASS.TIMES_COUNTER) {
				BizUtil.fieldNotNull(limitInfo.getLimit_value(), SysDict.A.limit_value.getId(), SysDict.A.limit_value.getLongName());
				BizUtil.fieldNotNull(limitInfo.getLimit_custom_allow(), SysDict.A.limit_custom_allow.getId(), SysDict.A.limit_custom_allow.getLongName());
			}
		}
	}

	private static void validLstDrivePropsNullable(Options<ApLimitDriveMntInput> list01, ApLimitMntIn limitInfo) {
		E_DATAOPERATE holeOperator = limitInfo.getOperater_ind();
		if (holeOperator == E_DATAOPERATE.DELETE) {
			return;
		}
		
		for (int i = 1; i <= list01.size(); i++) { 
			ApLimitDriveMntInput driveInfo = list01.get(i - 1);
			
			checkListLineFieldNotNull(ApLimitDriveMntInput.class, i, SysDict.A.trxn_event_id.getLongName(), driveInfo.getTrxn_event_id());
			// checkListLineFieldNotNull(ApLimitDriveMntInput.class, i, SysDict.A.limit_drive_cond.getLongName(), driveInfo.getLimit_drive_cond());
			if (limitInfo.getLimit_ctrl_class() != E_LIMITCTRLCLASS.SINGLE_AMOUNT) {
				checkListLineFieldNotNull(ApLimitDriveMntInput.class, i, SysDict.A.limit_sum_way.getLongName(), driveInfo.getLimit_sum_way());
				
				if (limitInfo.getLimit_ctrl_class() == E_LIMITCTRLCLASS.CUMULATIVE_NUMBER && driveInfo.getLimit_sum_way() == null) {
					driveInfo.setLimit_sum_way(E_ADDSUBTRACT.ADD);
				}
			}
				
		}
	}
	
	private static void checkListLineFieldNotNull(Class<?> lstEleType, int rowNO, String fieldName, Object data) {
		if (CommUtil.isNull(data)) {
			throw ApPubErr.APPUB.E0035(fieldName, (long)rowNO, OdbFactory.getComplexType(lstEleType).getLongname());
		}
	}

	private static void validParamWithBusiRule(ApLimitMntIn limitInfo) {
		app_limit dbLimitEntity = App_limitDao.selectOneWithLock_odb3(limitInfo.getLimit_no(), limitInfo.getEffect_date(), false);
		
		E_DATAOPERATE holeOperator = limitInfo.getOperater_ind();
		if (holeOperator == E_DATAOPERATE.DELETE) {
			checkDirtyData(limitInfo, dbLimitEntity);
		}
		else {
			if (holeOperator == E_DATAOPERATE.ADD) {
				if (CommUtil.isNotNull(dbLimitEntity)) {
					throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_limit.class).getLongname(), limitInfo.getLimit_no() + "," + limitInfo.getEffect_date());
				}
			}
			else {
				checkDirtyData(limitInfo, dbLimitEntity);
			}
			
			if ((CommUtil.isNotNull(limitInfo.getOwner_mart()) && CommUtil.isNull(limitInfo.getOwner_field()))
					|| (CommUtil.isNull(limitInfo.getOwner_mart()) && CommUtil.isNotNull(limitInfo.getOwner_field()))) {
				throw ApPubErr.APPUB.E0034(SysDict.A.owner_mart.getLongName(), SysDict.A.owner_field.getLongName());
			}
			
			if (CommUtil.isNotNull(limitInfo.getOwner_mart())) { // check whether data_mart exists or not
				ApDropListApi.exists(ApConst.DATA_MART, limitInfo.getOwner_mart());
			}
			
			validDataFormat(limitInfo);
			limitExpiryDateCheck(limitInfo);
			
			driveDuplicateCheck(limitInfo.getList01());
		}
	}
	
	private static void validDataFormat(ApLimitMntIn limitInfo) {
		if (BizUtil.isDateString(limitInfo.getEffect_date()) == false) {
			throw ApPubErr.APPUB.E0002(limitInfo.getEffect_date(), SysDict.A.effect_date.getId(), SysDict.A.effect_date.getLongName());
		}
		
		if (BizUtil.isDateString(limitInfo.getExpiry_date()) == false) {
			throw ApPubErr.APPUB.E0002(limitInfo.getExpiry_date(), SysDict.A.expiry_date.getId(), SysDict.A.expiry_date.getLongName());
		}
		
		if (CommUtil.compare(limitInfo.getEffect_date(), limitInfo.getExpiry_date()) > 0) {
			throw ApPubErr.APPUB.E0015(limitInfo.getEffect_date(), limitInfo.getExpiry_date());
		}
		
		if (CommUtil.isNotNull(limitInfo.getLimit_ccy()) && CommUtil.isNotNull(limitInfo.getLimit_value())) {
			ApCurrencyApi.chkAmountByCcy(limitInfo.getLimit_ccy(), limitInfo.getLimit_value());
		}
		
		if (CommUtil.isNotNull(limitInfo.getLimit_ccy()) && CommUtil.isNotNull(limitInfo.getMax_limit_value())) {
			ApCurrencyApi.chkAmountByCcy(limitInfo.getLimit_ccy(), limitInfo.getMax_limit_value());
		}
		
		if (CommUtil.isNotNull(limitInfo.getLimit_ccy()) && CommUtil.isNotNull(limitInfo.getMin_limit_value())) {
			ApCurrencyApi.chkAmountByCcy(limitInfo.getLimit_ccy(), limitInfo.getMin_limit_value());
		}

		if (CommUtil.isNotNull(limitInfo.getMax_limit_value()) && CommUtil.isNotNull(limitInfo.getMin_limit_value())) {
			if (CommUtil.compare(limitInfo.getMax_limit_value(), limitInfo.getMin_limit_value()) < 0) {
				throw ApPubErr.APPUB.E0036(SysDict.A.max_limit_value.getLongName(), SysDict.A.min_limit_value.getLongName());
			}
		}
		
	}

	private static void limitExpiryDateCheck(ApLimitMntIn limitInfo) {
		List<app_limit> limitEntities = App_limitDao.selectAll_odb1(limitInfo.getLimit_no(), false);
		for (app_limit limitEntity : limitEntities) {
			if (CommUtil.compare(limitEntity.getEffect_date(), limitInfo.getEffect_date()) == 0) {
				continue;
			}
			
			if (!(CommUtil.compare(limitInfo.getEffect_date(), limitEntity.getExpiry_date()) > 0 || CommUtil.compare(limitInfo.getExpiry_date(), limitEntity.getEffect_date()) < 0)) {
				throw ApErr.AP.E0066(limitInfo.getLimit_no(), limitInfo.getEffect_date(), limitInfo.getExpiry_date());
			}
		}
	}

	private static void checkDirtyData(ApLimitMntIn limitInfo, app_limit dbLimitEntity) {
		if (CommUtil.isNull(dbLimitEntity)) {
			throw ApPubErr.APPUB.E0024(OdbFactory.getTable(app_limit.class).getLongname(), SysDict.A.limit_no.getLongName(),  limitInfo.getLimit_no(), SysDict.A.effect_date.getLongName(), limitInfo.getEffect_date());
		}
		
		BizUtil.fieldNotNull(limitInfo.getData_version(), SysDict.A.data_version.getId(), SysDict.A.data_version.getLongName());
		
		if (CommUtil.compare(limitInfo.getData_version(), dbLimitEntity.getData_version()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_limit.class).getLongname());
		}
	}
	
	private static void driveDuplicateCheck(Options<ApLimitDriveMntInput> list) {
		for (int i = 0; i < list.size(); i++) {
			String eventIDMirror = list.get(i).getTrxn_event_id();
			for (int j = i + 1; j < list.size(); j++) {
				String eventID = list.get(j).getTrxn_event_id();
				if (eventIDMirror.equals(eventID)) {
					throw ApPubErr.APPUB.E0033(SysDict.A.trxn_event_id.getLongName(), eventIDMirror, OdbFactory.getComplexType(ApLimitDriveMntInput.class).getLongname());
				}
			}
		}	
	}

	private static void doMaintain(ApLimitMntIn limitInfo) {
		if (limitInfo.getOperater_ind() == E_DATAOPERATE.ADD) {
			doAdd(limitInfo);
		}
		else if (limitInfo.getOperater_ind() == E_DATAOPERATE.DELETE) {
			doDelete(limitInfo);
		}
		else if (limitInfo.getOperater_ind() == E_DATAOPERATE.MODIFY) {
			doModify(limitInfo);
		}
	}

	private static void doAdd(ApLimitMntIn limitInfo) {
		app_limit limitEntity = BizUtil.getInstance(app_limit.class);
		setLimitEntityProps(limitEntity, limitInfo);
		App_limitDao.insert(limitEntity);
		ApDataAuditApi.regLogOnInsertParameter(limitEntity);
		
		for (ApLimitDriveMntInput driveInfo : limitInfo.getList01()) {
			saveLimitDriveEntity(limitInfo.getLimit_no(), driveInfo);
		}
	}
	
	private static void saveLimitDriveEntity(String limitNO, ApLimitDriveMntInput driveInfo) {
		app_limit_drive driveEntity = BizUtil.getInstance(app_limit_drive.class);
		driveEntity.setLimit_no(limitNO);
		setLimitDriveProps(driveEntity, driveInfo);
		
		App_limit_driveDao.insert(driveEntity);
		ApDataAuditApi.regLogOnInsertParameter(driveEntity);
	}
	
	private static void doDelete(ApLimitMntIn limitInfo) {
		app_limit limitEntity = App_limitDao.selectOne_odb3(limitInfo.getLimit_no(), limitInfo.getEffect_date(), false);
		App_limitDao.deleteOne_odb3(limitInfo.getLimit_no(), limitInfo.getEffect_date());
		ApDataAuditApi.regLogOnDeleteParameter(limitEntity);
		
		List<app_limit_drive> driveEntities = App_limit_driveDao.selectAll_odb2(limitInfo.getLimit_no(), false);
		for (app_limit_drive driveEntity : driveEntities) {
			App_limit_driveDao.deleteOne_odb3(driveEntity.getLimit_no(), driveEntity.getTrxn_event_id());
			ApDataAuditApi.regLogOnDeleteParameter(driveEntity);
		}
	}

	private static void doModify(ApLimitMntIn limitInfo) {
		app_limit hisLimitEntity = App_limitDao.selectOne_odb3(limitInfo.getLimit_no(), limitInfo.getEffect_date(), false);
		
		app_limit backupLimit = BizUtil.clone(app_limit.class, hisLimitEntity);
		setLimitEntityProps(hisLimitEntity, limitInfo);
		int changes = ApDataAuditApi.regLogOnUpdateParameter(backupLimit, hisLimitEntity);
		
		changes += doModifyOnLstDrive(limitInfo.getLimit_no(), limitInfo.getList01());
		
		if (changes == 0) {
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_limit.class).getLongname());
		}
		
		App_limitDao.updateOne_odb3(hisLimitEntity);
	}
	
	private static int doModifyOnLstDrive(String limitNO, Options<ApLimitDriveMntInput> list01) {
		int changes = 0;
		
		changes += deleteHisDrives(limitNO, list01);
		
		for (ApLimitDriveMntInput driveInfo : list01) {
			app_limit_drive hisDriveEntity = App_limit_driveDao.selectOne_odb3(limitNO, driveInfo.getTrxn_event_id(), false);
			if (CommUtil.isNull(hisDriveEntity)) {
				saveLimitDriveEntity(limitNO, driveInfo);
				changes++;
			}
			else {
				app_limit_drive backupDrive = BizUtil.clone(app_limit_drive.class, hisDriveEntity);
				setLimitDriveProps(hisDriveEntity, driveInfo);
				int singleChanges = ApDataAuditApi.regLogOnUpdateParameter(backupDrive, hisDriveEntity);
				
				if (singleChanges > 0) {
					App_limit_driveDao.updateOne_odb3(hisDriveEntity);
				}
				
				changes += singleChanges;
			}
		}
		
		return changes;
	}

	private static int deleteHisDrives(String limitNO, Options<ApLimitDriveMntInput> list01) {
		int changes = 0;
		List<app_limit_drive> hisDrives = App_limit_driveDao.selectAll_odb2(limitNO, false);
		for (app_limit_drive driveEntity : hisDrives) {
			boolean needDelete = true;
			for (ApLimitDriveMntInput driveInfo : list01) {
				if (driveEntity.getTrxn_event_id().equals(driveInfo.getTrxn_event_id())) {
					needDelete = false;
					break;
				}
			}
			
			if (needDelete) {
				App_limit_driveDao.deleteOne_odb3(driveEntity.getLimit_no(), driveEntity.getTrxn_event_id());
				
				ApDataAuditApi.regLogOnDeleteParameter(driveEntity);
				changes++;
			}
		}
		
		return changes;
	}

	private static void setLimitDriveProps(app_limit_drive driveEntity, ApLimitDriveMntInput driveInfo) {
		driveEntity.setTrxn_event_id(driveInfo.getTrxn_event_id());  //transaction event id
		driveEntity.setLimit_drive_cond(driveInfo.getLimit_drive_cond());  //limit drive condition
		driveEntity.setLimit_sum_way(driveInfo.getLimit_sum_way());  //limit sum way 
		driveEntity.setData_version(driveInfo.getData_version());
	}
	
	private static void setLimitEntityProps(app_limit limitEntity, ApLimitMntIn limitInfo) {
		limitEntity.setLimit_no(limitInfo.getLimit_no());  //limit no
		limitEntity.setLimit_desc(limitInfo.getLimit_desc());  //limit describe
		limitEntity.setEffect_date(limitInfo.getEffect_date());  //effect date
		limitEntity.setExpiry_date(limitInfo.getExpiry_date());  //expiry date
		limitEntity.setLimit_level(limitInfo.getLimit_level());  //limit level
		limitEntity.setLimit_ctrl_class(limitInfo.getLimit_ctrl_class());  //limit control class
		limitEntity.setLimit_reset_cycle(limitInfo.getLimit_reset_cycle());  //limit reset cycle
		limitEntity.setLimit_statis_no(limitInfo.getLimit_statis_no());  //limit statis no
		limitEntity.setOwner_mart(limitInfo.getOwner_mart());  //owner mart
		limitEntity.setOwner_field(limitInfo.getOwner_field());  //owner field
		limitEntity.setLimit_ccy(limitInfo.getLimit_ccy());  //limit currency
		limitEntity.setLimit_value(limitInfo.getLimit_value());  //limit value
		limitEntity.setLimit_custom_allow(limitInfo.getLimit_custom_allow());  //limit_custom_allow
		limitEntity.setMin_limit_value(limitInfo.getMin_limit_value());  //min limit value
		limitEntity.setMax_limit_value(limitInfo.getMax_limit_value());  //max limit value
		limitEntity.setLimit_sms_template_no(limitInfo.getLimit_sms_template_no());  //limit SMS template
		limitEntity.setData_version(limitInfo.getData_version());  //limit SMS template
	}
}
