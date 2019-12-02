package cn.sunline.icore.ap.parm.mnt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.api.ApBranchApi;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.api.ApHolidayApi;
import cn.sunline.icore.ap.namedsql.ApBasicDao;
import cn.sunline.icore.ap.tables.TabApBasic.App_holidayDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_holiday;
import cn.sunline.icore.ap.type.ComApBasic.ApHolidayInfo;
import cn.sunline.icore.ap.type.ComApBasic.ApHolidayQueryOut;
import cn.sunline.icore.ap.type.ComApBasic.ApHolidayWithInd;
import cn.sunline.icore.ap.type.ComApBasic.ApWorkdayInfo;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_CUSTOMERTYPE;
import cn.sunline.icore.sys.type.EnumType.E_HOLIDAYCLASS;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

public class ApHolidayMnt {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApHolidayMnt.class);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月4日-下午2:39:54</li>
	 *         <li>功能说明：节假日信息查询</li>
	 *         </p>
	 * @param holidayCode
	 * @param holidayYear
	 * @param holidayClass
	 * @return
	 */
	public static Options<ApHolidayInfo> queryHolidayList(String holidayCode, String holidayYear, E_HOLIDAYCLASS holidayClass) {
		bizlog.method(" ApHolidayMnt.queryHolidayList begin >>>>>>>>>>>>>>>>");

		BizUtil.fieldNotNull(holidayCode, SysDict.A.holiday_code.getId(), SysDict.A.holiday_code.getLongName());
		BizUtil.fieldNotNull(holidayYear, SysDict.A.holiday_year.getId(), SysDict.A.holiday_year.getLongName());

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Page<ApHolidayInfo> page = ApBasicDao.selHolidayList(holidayCode, holidayYear, holidayClass, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(),
				false);
		runEnvs.setTotal_count(page.getRecordCount());

		Options<ApHolidayInfo> holidayList = new DefaultOptions<ApHolidayInfo>();
		holidayList.setValues(page.getRecords());

		bizlog.method(" ApHolidayMnt.queryHolidayList end <<<<<<<<<<<<<<<<");
		return holidayList;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月4日-下午2:40:05</li>
	 *         <li>功能说明：节假日信息维护</li>
	 *         </p>
	 * @param holidayList
	 */
	public static void modifyHoliday(ApHolidayWithInd holiday) {
		bizlog.method(" ApHolidayMnt.modifyHoliday begin >>>>>>>>>>>>>>>>");
		int i = 0;
		//for (ApHolidayWithInd holiday : holidayList) {
		E_DATAOPERATE operate = holiday.getOperater_ind();
		BizUtil.fieldNotNull(operate, SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());

		if (operate == E_DATAOPERATE.ADD) {
			i = addHolidaySingle(holiday);
		}
		else if (operate == E_DATAOPERATE.DELETE) {
			i = deleteHolidaySingle(holiday);
		}
		else if (operate == E_DATAOPERATE.MODIFY) {
			i = modifyHolidaySingle(holiday);
		}
		//}
		//如果没做维护，报错
		if (i == 0) {
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_holiday.class).getLongname());
		}
		bizlog.method(" ApHolidayMnt.modifyHoliday end <<<<<<<<<<<<<<<<");
	}

	/**
	 * 单个新增节假日信息
	 */
	private static int addHolidaySingle(ApHolidayWithInd holiday) {
		bizlog.method(" ApHolidayMnt.modifyHolidaySingle begin >>>>>>>>>>>>>>>>");

		validHolidayField(holiday);

		app_holiday holidayOld = App_holidayDao.selectOne_odb1(holiday.getHoliday_code(), holiday.getHoliday_class(), holiday.getHoliday_date(), false);
		if (holidayOld != null) {
			ApPubErr.APPUB.E0019(OdbFactory.getTable(app_holiday.class).getLongname(), holiday.getHoliday_code() + ApConst.KEY_CONNECTOR + holiday.getHoliday_class()
					+ ApConst.KEY_CONNECTOR + holiday.getHoliday_date());
		}

		app_holiday holidayNew = BizUtil.getInstance(app_holiday.class);
		holidayNew.setHoliday_code(holiday.getHoliday_code()); // 假日代码
		holidayNew.setHoliday_class(holiday.getHoliday_class()); // 假日分类
		holidayNew.setHoliday_date(holiday.getHoliday_date()); // 假日日期
		holidayNew.setHoliday_ind(holiday.getHoliday_ind()); // 节假日标志
		holidayNew.setRemark(holiday.getRemark()); // 备注

		int i = App_holidayDao.insert(holidayNew);

		ApDataAuditApi.regLogOnInsertParameter(holidayNew);

		bizlog.method(" ApHolidayMnt.modifyHolidaySingle end <<<<<<<<<<<<<<<<");
		return i;
	}

	/**
	 * 单个删除节假日信息
	 */
	private static int deleteHolidaySingle(ApHolidayWithInd holiday) {
		bizlog.method(" ApHolidayMnt.deleteHolidaySingle begin >>>>>>>>>>>>>>>>");

		validHolidayField(holiday);

		app_holiday holidayOld = App_holidayDao.selectOne_odb1(holiday.getHoliday_code(), holiday.getHoliday_class(), holiday.getHoliday_date(), false);
		if (holidayOld == null) {
			throw ApPubErr.APPUB.E0025(OdbFactory.getTable(app_holiday.class).getLongname(), SysDict.A.holiday_code.getLongName(), holiday.getHoliday_code(),
					SysDict.A.holiday_class.getLongName(), holiday.getHoliday_class().getLongName(), SysDict.A.holiday_date.getLongName(), holiday.getHoliday_date());
		}

		int i = App_holidayDao.deleteOne_odb1(holiday.getHoliday_code(), holiday.getHoliday_class(), holiday.getHoliday_date());

		ApDataAuditApi.regLogOnDeleteParameter(holidayOld);
		bizlog.method(" ApHolidayMnt.deleteHolidaySingle end <<<<<<<<<<<<<<<<");
		return i;
	}

	/**
	 * 单个修改节假日信息
	 */
	private static int modifyHolidaySingle(ApHolidayWithInd holiday) {
		bizlog.method(" ApHolidayMnt.addHolidaySingle begin >>>>>>>>>>>>>>>>");

		validHolidayField(holiday);

		// 先检查是否存在
		app_holiday holidayOld = App_holidayDao.selectOne_odb1(holiday.getHoliday_code(), holiday.getHoliday_class(), holiday.getHoliday_date(), false);
		if (holidayOld == null) {
			throw ApPubErr.APPUB.E0025(OdbFactory.getTable(app_holiday.class).getLongname(), SysDict.A.holiday_code.getLongName(), holiday.getHoliday_code(),
					SysDict.A.holiday_class.getLongName(), holiday.getHoliday_class().getLongName(), SysDict.A.holiday_date.getLongName(), holiday.getHoliday_date());
		}

		BizUtil.fieldNotNull(holiday.getData_version(), SysDict.A.data_version.getId(), SysDict.A.data_version.getLongName());

		if (CommUtil.compare(holiday.getData_version(), holidayOld.getData_version()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_holiday.class).getLongname());
		}

		app_holiday holidayNew = BizUtil.clone(app_holiday.class, holidayOld);
		holidayNew.setHoliday_code(holiday.getHoliday_code()); // 假日代码
		holidayNew.setHoliday_class(holiday.getHoliday_class()); // 假日分类
		holidayNew.setHoliday_date(holiday.getHoliday_date()); // 假日日期
		holidayNew.setHoliday_ind(holiday.getHoliday_ind()); // 节假日标志
		holidayNew.setRemark(holiday.getRemark()); // 备注

		int i = ApDataAuditApi.regLogOnUpdateParameter(holidayOld, holidayNew);
		//		if (i == 0) {
		//			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_holiday.class).getLongname());
		//		}

		App_holidayDao.updateOne_odb1(holidayNew);

		bizlog.method(" ApHolidayMnt.addHolidaySingle end <<<<<<<<<<<<<<<<");
		return i;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月4日-下午2:40:18</li>
	 *         <li>功能说明：节假日信息浏览</li>
	 *         </p>
	 * @param holidayCode
	 * @param holidayClass2
	 * @param holidayClass
	 * @return
	 */
	public static Options<ApHolidayInfo> scanHolidaty(String holidayCode, E_HOLIDAYCLASS holidayClass, String holidayYear, String holidayDate) {
		bizlog.method(" ApHolidayMnt.scanHolidaty begin >>>>>>>>>>>>>>>>");

		BizUtil.fieldNotNull(holidayCode, SysDict.A.holiday_code.getId(), SysDict.A.holiday_code.getLongName());
		BizUtil.fieldNotNull(holidayYear, SysDict.A.holiday_year.getId(), SysDict.A.holiday_year.getLongName());

		Options<ApHolidayInfo> holidayList = new DefaultOptions<ApHolidayInfo>();

		if (CommUtil.isNotNull(holidayDate)) {

			// 非*且查询日期不为空
			ApHolidayInfo holidayWithDate = ApBasicDao.selHolidayWithDate(holidayClass,holidayCode, holidayDate, false);

			// *且查询日期不为空
			ApHolidayInfo holidayWithDateByDefault = ApBasicDao.selHolidayWithDate(holidayClass,ApConst.WILDCARD, holidayDate, false);
			
			if(holidayWithDate!=null){
				
				holidayList.add(holidayWithDate);
				
			}
				
			if(holidayWithDateByDefault!=null){
			
				holidayList.add(holidayWithDateByDefault);
			}

			return holidayList;

		}

		List<ApHolidayInfo> base = ApBasicDao.selHolidayList(ApConst.WILDCARD, holidayYear, holidayClass, false);

		// 非*的要和*的组装在一起
		if (!CommUtil.equals(holidayCode, ApConst.WILDCARD)) {

			Map<String, ApHolidayInfo> holidayMap = new HashMap<String, ApHolidayInfo>();
			for (ApHolidayInfo holiday : base) {
				String key = getKey(holiday);
				holiday.setHoliday_code(holidayCode);
				holidayMap.put(key, holiday);
			}

			List<ApHolidayInfo> pageNew = ApBasicDao.selHolidayList(holidayCode, holidayYear, holidayClass, false);
			for (ApHolidayInfo holidayNew : pageNew) {
				holidayMap.put(getKey(holidayNew), holidayNew);
			}

			holidayList.addAll(holidayMap.values());
		}
		else {
			holidayList.setValues(base);
		}

		// 排序
		BizUtil.listSort(holidayList, true, SysDict.A.holiday_date.getId());

		// 分页处理
		Options<ApHolidayInfo> holidayPageList = new DefaultOptions<ApHolidayInfo>();
		holidayPageList.setValues(BizUtil.list2Page(holidayList));

		bizlog.method(" ApHolidayMnt.scanHolidaty end <<<<<<<<<<<<<<<<");
		return holidayPageList;
	}

	private static String getKey(ApHolidayInfo holiday) {
		return holiday.getHoliday_code() + ApConst.KEY_CONNECTOR + holiday.getHoliday_class().getId() + ApConst.KEY_CONNECTOR + holiday.getHoliday_date();
	}

	private static void validHolidayField(ApHolidayWithInd holiday) {
		BizUtil.fieldNotNull(holiday.getHoliday_code(), SysDict.A.holiday_code.getId(), SysDict.A.holiday_code.getLongName());
		BizUtil.fieldNotNull(holiday.getHoliday_class(), SysDict.A.holiday_class.getId(), SysDict.A.holiday_class.getLongName());
		BizUtil.fieldNotNull(holiday.getHoliday_date(), SysDict.A.holiday_date.getId(), SysDict.A.holiday_date.getLongName());
	}
	
	/**
	 * @Author weixiang
	 *         <p>
	 *         <li>2017年10月31日-下午4:56:25</li>
	 *         <li>功能说明：使用list的循环</li>
	 *         </p>
	 * @param cplIn
	 * @return
	 */
	public static String getWorkDate(ApWorkdayInfo cplIn) {
		bizlog.method(" ApHolidayMnt.getWorkDate begin >>>>>>>>>>>>>>>>");
		bizlog.debug("ApWorkdayInfo=[%s]", cplIn);

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		BizUtil.fieldNotNull(cplIn.getAssign_days(), SysDict.A.assign_days.getId(), SysDict.A.assign_days.getLongName());

		E_CUSTOMERTYPE custType = E_CUSTOMERTYPE.CORPORATE;
		String workDay = ApHolidayApi.getNextWorkerDay(cplIn.getTrxn_date(),custType,Integer.parseInt(String.valueOf(cplIn.getAssign_days())),runEnvs.getTrxn_branch()); 

		bizlog.method(" ApHolidayMnt.getWorkDate begin >>>>>>>>>>>>>>>>");
		return workDay;

	}
	
	
	
	public static  ApHolidayQueryOut queryHoliday(String holiday_date) {

		BizUtil.fieldNotNull(holiday_date, SysDict.A.holiday_date.getId(), SysDict.A.holiday_date.getLongName());
		
		bizlog.method(" StChequeQuery.queryHoliday begin >>>>>>>>>>>>>>>>");
		bizlog.debug("cplIn=[%s]", holiday_date);
		ApHolidayQueryOut cplOut = BizUtil.getInstance(ApHolidayQueryOut.class);
		// 指定日期
		String workDate = holiday_date;
		String branchId = BizUtil.getTrxRunEnvs().getTrxn_branch();
		String holidayCode = ApBranchApi.getHolidayCode(branchId);

		// 获取指定日下个工作日  如果指定日是工作日 不变化
		while(ApHolidayApi.isHoliday(workDate,holidayCode, E_CUSTOMERTYPE.CORPORATE)){
			workDate = BizUtil.dateAdd("dd", workDate, 1);
		}
		cplOut.setHoliday_date(workDate);
		
		bizlog.method(" ApHolidayMnt.queryHoliday end >>>>>>>>>>>>>>>>");

		return cplOut;
	}
}
