package cn.sunline.icore.ap.trxnctrl;
import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.namedsql.ApRuleBaseDao;
import cn.sunline.icore.ap.tables.TabApRule.App_scene_authDao;
import cn.sunline.icore.ap.tables.TabApRule.App_trxn_controlDao;
import cn.sunline.icore.ap.tables.TabApRule.app_scene_auth;
import cn.sunline.icore.ap.tables.TabApRule.app_trxn_control;
import cn.sunline.icore.ap.type.ComApBasic.ApSceneAuthInfo;
import cn.sunline.icore.ap.type.ComApBasic.ApTrxnControl;
import cn.sunline.icore.ap.type.ComApBasic.ApTrxnCtrlInput;
import cn.sunline.icore.ap.type.ComApBasic.ApTrxnCtrlMntInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_TRXNCTRLTYPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
public class ApTrxnCtrlMnt {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApTrxnCtrlMnt.class);
	

	/**
	 * @Author Dengyu
	 *         <p>
	 *         <li>2017年11月1日-上午10:28:35</li>
	 *         <li>功能说明：维护交易控制信息</li>
	 *         </p>
	 * @param infoInput 交易控制符合类型
	 */
	public static void mntTrxnCtrl( ApTrxnCtrlMntInfo infoInput ) {
		
		bizlog.method(" ApTrxnCtrlMnt.mntTrxnCtrl begin >>>>>>>>>>>>>>>>");
		
		int i = 0;
			
		i +=mntTrxnCtrlInfo(infoInput);
		
		if (i == 0) {
			// throw
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_trxn_control.class).getLongname());
		}
		bizlog.method(" ApTrxnCtrlMnt.mntTrxnCtrl end >>>>>>>>>>>>>>>>");
	}
	
	private static void addTrxnCtrlInfo( ApTrxnCtrlMntInfo infoInput ) {
		
		app_trxn_control trxnCtrlInfo = App_trxn_controlDao.selectOne_odb1(infoInput.getTrxn_event_id(), infoInput.getTrxn_ctrl_run_cond(), false);

		if ( trxnCtrlInfo !=null )
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_trxn_control.class).getLongname(), infoInput.getTrxn_event_id());
		
		validTrxnCtrlInfo(infoInput);
		
		//如果交易控制类型不为C,错误信息所在数据集和错误信息所在数据集字段名空值的属性保持一致
		if( infoInput.getTrxn_ctrl_type() !=E_TRXNCTRLTYPE.SCENE_AUTHOR ){
			boolean dataMart = CommUtil.isNull(infoInput.getHint_data_mart());
			boolean dataName = CommUtil.isNull(infoInput.getHint_field_name());
			if( dataMart != dataName )
				throw ApPubErr.APPUB.E0034(SysDict.A.hint_data_mart.getLongName(), SysDict.A.hint_field_name.getLongName());
		}
		
		trxnCtrlInfo = BizUtil.getInstance(app_trxn_control.class);
		
		trxnCtrlInfo.setTrxn_event_id(infoInput.getTrxn_event_id());
		trxnCtrlInfo.setTrxn_ctrl_desc(infoInput.getTrxn_ctrl_desc());
		trxnCtrlInfo.setTrxn_ctrl_type(infoInput.getTrxn_ctrl_type());
		trxnCtrlInfo.setTrxn_ctrl_run_cond(infoInput.getTrxn_ctrl_run_cond());
		trxnCtrlInfo.setHint_text(infoInput.getHint_text());
		trxnCtrlInfo.setHint_data_mart(infoInput.getHint_data_mart());
		trxnCtrlInfo.setHint_field_name(infoInput.getHint_field_name());
		trxnCtrlInfo.setScene_auth_id(infoInput.getScene_auth_id());
		trxnCtrlInfo.setReturn_code(infoInput.getReturn_code());
		
		App_trxn_controlDao.insert(trxnCtrlInfo);
		
		ApDataAuditApi.regLogOnInsertParameter(trxnCtrlInfo);
		
	}
	
	private static int modifyTrxnCtrlSingle( ApTrxnCtrlMntInfo infoInput ) {
//		维护版本号不为空
		BizUtil.fieldNotNull(infoInput.getData_version(), SysDict.A.data_version.getLongName(), SysDict.A.data_version.getDescription());	
		
//		输入值不为空
		validTrxnCtrlInfo(infoInput);
		
//		修改的记录存在
		app_trxn_control oldInfo = App_trxn_controlDao.selectOneWithLock_odb1(infoInput.getTrxn_event_id(), infoInput.getTrxn_ctrl_run_cond(), false);
		if( oldInfo ==null ){
			throw ApPubErr.APPUB.E0024(OdbFactory.getTable(app_trxn_control.class).getLongname(), SysDict.A.trxn_ctrl_id.getLongName(),
					infoInput.getTrxn_event_id(), SysDict.A.trxn_ctrl_run_cond.getLongName(), infoInput.getTrxn_ctrl_run_cond());
		}
		
//		交易控制类型为场景授权
		if( CommUtil.compare(infoInput.getTrxn_ctrl_type(), E_TRXNCTRLTYPE.SCENE_AUTHOR ) ==0 ){
			
			List<String> sceneAuth = ApRuleBaseDao.selDistinctAuthId(false);
			if( CommUtil.isNull(sceneAuth) ){
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_scene_auth.class).getLongname(), SysDict.A.scene_auth_id.getLongName(), infoInput.getScene_auth_id().toString());
			}
			if( !sceneAuth.contains(infoInput.getScene_auth_id()) ){
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_scene_auth.class).getLongname(), SysDict.A.scene_auth_id.getLongName(), infoInput.getScene_auth_id().toString());
			}
		}
		
//		赋值
		app_trxn_control newTrxnCtrlInfo = BizUtil.clone(app_trxn_control.class, oldInfo);		
		newTrxnCtrlInfo.setTrxn_ctrl_desc(infoInput.getTrxn_ctrl_desc());  //transaction control describe
		newTrxnCtrlInfo.setTrxn_ctrl_type(infoInput.getTrxn_ctrl_type());  //transaction control type
		newTrxnCtrlInfo.setHint_text(infoInput.getHint_text());  //hint text
		newTrxnCtrlInfo.setHint_data_mart(infoInput.getHint_data_mart());  //hint data mart
		newTrxnCtrlInfo.setHint_field_name(infoInput.getHint_field_name());  //hint field name
		newTrxnCtrlInfo.setScene_auth_id(infoInput.getScene_auth_id());  //scene authorization id
		newTrxnCtrlInfo.setReturn_code(infoInput.getReturn_code());//return code
		
//		对比版本号
		if( CommUtil.compare(infoInput.getData_version(), oldInfo.getData_version()) != 0 ) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_trxn_control.class).getLongname());
		}
		
//		登记审计
		int i = ApDataAuditApi.regLogOnUpdateParameter(oldInfo, newTrxnCtrlInfo);	
		if( i ==0 )
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_trxn_control.class).getLongname());
		
//		更新数据
		App_trxn_controlDao.updateOne_odb1(newTrxnCtrlInfo);
		
		return i;
	}
	
	
	private static int mntTrxnCtrlInfo( ApTrxnCtrlMntInfo infoInput ) {
		bizlog.method(" ApAttributeMnt.modifyAttr begin >>>>>>>>>>>>>>>>");
		
		int i = 0;

		E_DATAOPERATE operater = infoInput.getOperater_ind();
		if( operater == E_DATAOPERATE.ADD ){
			addTrxnCtrlInfo(infoInput);
			i++;
		}
		else if( operater == E_DATAOPERATE.MODIFY ){
			modifyTrxnCtrlSingle(infoInput);
			i++;
		}
		else if( operater == E_DATAOPERATE.DELETE ){
			App_trxn_controlDao.deleteOne_odb1(infoInput.getTrxn_event_id(), infoInput.getTrxn_ctrl_run_cond());
			i++;
		}
		
		return i;
		
	}
	
	private static void validTrxnCtrlInfo( ApTrxnCtrlMntInfo infoInput ) {
		BizUtil.fieldNotNull(infoInput.getOperater_ind(), SysDict.A.operater_ind.getLongName(), SysDict.A.operater_ind.getDescription());
		BizUtil.fieldNotNull(infoInput.getTrxn_event_id(), SysDict.A.trxn_ctrl_id.getLongName(), SysDict.A.trxn_ctrl_id.getDescription());
		BizUtil.fieldNotNull(infoInput.getTrxn_ctrl_desc(), SysDict.A.trxn_ctrl_desc.getLongName(), SysDict.A.trxn_ctrl_desc.getDescription());
		BizUtil.fieldNotNull(infoInput.getTrxn_ctrl_type(), SysDict.A.trxn_ctrl_type.getLongName(), SysDict.A.trxn_ctrl_type.getDescription());
		BizUtil.fieldNotNull(infoInput.getTrxn_ctrl_run_cond(), SysDict.A.trxn_ctrl_run_cond.getLongName(), SysDict.A.trxn_ctrl_run_cond.getDescription());
		BizUtil.fieldNotNull(infoInput.getHint_text(), SysDict.A.hint_text.getLongName(), SysDict.A.hint_text.getDescription());
		
	}
	
	
	/**
	 * @Author Dengyu
	 *         <p>
	 *         <li>2017年11月2日-下午2:12:35</li>
	 *         <li>功能说明：维护场景授权</li>
	 *         </p>
	 * @param input  场景授权符合类型
	 */
	
	public static void mntmntSceneAuthInfo( ApSceneAuthInfo input ) {
		int i =0;
		
		if( input.getOperater_ind() == E_DATAOPERATE.ADD ){
			addSceneAuth(input);
		}
		if( input.getOperater_ind() == E_DATAOPERATE.DELETE ){
			checkSceneAuthValid(input);
			App_scene_authDao.deleteOne_odb1(input.getScene_auth_id(), input.getRole_id());
		}
		
	}
	
	
	private static void addSceneAuth(  ApSceneAuthInfo input ) {
		checkSceneAuthValid(input);

		app_scene_auth info = App_scene_authDao.selectOne_odb1(input.getScene_auth_id(), input.getRole_id(), false);
		if( info !=null )
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_scene_auth.class).getLongname(), input.getScene_auth_id()+" "+input.getRole_id()	);
		
		app_scene_auth table = BizUtil.getInstance(app_scene_auth.class);
		table.setRole_id( input.getRole_id() );
		table.setScene_auth_id( input.getScene_auth_id() );
		
		App_scene_authDao.insert(table);
		
		ApDataAuditApi.regLogOnInsertParameter(table);
	}

	
	private static void checkSceneAuthValid( ApSceneAuthInfo input ) {
		BizUtil.fieldNotNull(input.getScene_auth_id(),SysDict.A.scene_auth_id.getLongName(), SysDict.A.scene_auth_id.getDescription());
		BizUtil.fieldNotNull(input.getRole_id(),SysDict.A.role_id.getLongName(), SysDict.A.role_id.getDescription() );
	}

	/**
	 * @Author Dengyu
	 *         <p>
	 *         <li>2017年10月31日-下午2:14:04</li>
	 *         <li>功能说明：交易控制信息查询</li>
	 *         </p>
	 * @param input
	 *            交易控制信息输入复合类型
	 * @return
	 */
	public static Options<ApTrxnControl> queryTrxnControlInfo(ApTrxnCtrlInput input) {

		bizlog.method(" TaAccount.getAccountBill bigain <<<<<<<<<<<<<<<<");
		Options<ApTrxnControl> trxnCtrlOut = new DefaultOptions<ApTrxnControl>();

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Page<ApTrxnControl> page = ApRuleBaseDao.selTrxnCtrlInfo(input.getTrxn_event_id(), input.getTrxn_ctrl_type(), runEnvs.getPage_start(), runEnvs.getPage_size(),
				runEnvs.getTotal_count(), false);

		runEnvs.setTotal_count(page.getRecordCount());

		trxnCtrlOut.setValues(page.getRecords());

		return trxnCtrlOut;

	}

	/**
	 * @Author Dengyu
	 *         <p>
	 *         <li>2017年11月2日-下午1:51:42</li>
	 *         <li>功能说明：场景授权定义查询</li>
	 *         </p>
	 * @param input
	 *            场景授权复合类型
	 * @return
	 */
	public static Options<ApSceneAuthInfo> querySceneAuthInfo(ApSceneAuthInfo input) {

		Options<ApSceneAuthInfo> sceneAuthInfo = new DefaultOptions<ApSceneAuthInfo>();
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Page<ApSceneAuthInfo> page = ApRuleBaseDao.selSceneAuth(input.getScene_auth_id(), input.getRole_id(), MsOrg.getReferenceOrgId(app_scene_auth.class), runEnvs.getPage_start(),
				runEnvs.getPage_size(), runEnvs.getTotal_count(), false);

		runEnvs.setTotal_count(page.getRecordCount());

		sceneAuthInfo.setValues(page.getRecords());

		return sceneAuthInfo;

	}
}
