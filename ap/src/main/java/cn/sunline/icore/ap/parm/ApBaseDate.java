package cn.sunline.icore.ap.parm;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.namedsql.ApBasicBaseDao;
import cn.sunline.icore.ap.tables.TabApBook.Aps_dayend_infoDao;
import cn.sunline.icore.ap.tables.TabApBook.aps_dayend_info;
import cn.sunline.icore.ap.tables.TabApSystem.App_dateDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_date;
import cn.sunline.icore.ap.type.ComApBasic.ApSystemDate;
import cn.sunline.icore.ap.type.ComApSystem.ApDateInfo;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_SYSDATESOLUTION;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.SysUtil;

/**
 * <p>
 * 文件功能说明：系统日期相关操作
 * </p>
 * 
 * @Author HongBiao
 *         <p>
 *         <li>2016年12月8日-下午5:30:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 HongBiao：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseDate {
	private static final String AP_DATE_SOLUTION = "AP_DATE_SOLUTION";
	private static final String AP_IGNORE_CROSS_MULTIDAY = "AP_IGNORE_CROSS_MULTIDAY";
	private static final String AP_EOD_FLOW_ID = "CoreEOD";

	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月8日-下午5:32:01</li>
	 *         <li>功能说明:获取系统日期信息(业务模块禁止使用)</li>
	 *         </p>
	 * @param orgId
	 *            业务法人ID
	 * @return 系统日期对象
	 */
	public static ApDateInfo getInfo(String orgId) {
		ApSystemDate date = ApBasicBaseDao.selSystemDate(orgId, false);
		
		if (date == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_date.class).getLongname(), SysDict.A.busi_org_id.getLongName(), orgId);
		}

		E_SYSDATESOLUTION dateSolution = E_SYSDATESOLUTION.LOGICAL;
		if (ApBaseSystemParm.exists(ApConst.DAYEND_DATA, AP_DATE_SOLUTION)) {
			dateSolution = E_SYSDATESOLUTION.get(ApBaseSystemParm.getValue(ApConst.DAYEND_DATA, AP_DATE_SOLUTION));
		}
		
		ApDateInfo dateInfo = BizUtil.getInstance(ApDateInfo.class);
		dateInfo.setBusi_org_id(date.getBusi_org_id());
		dateInfo.setBal_sheet_date(date.getBal_sheet_date());
		dateInfo.setData_create_time(date.getData_create_time());
		dateInfo.setData_create_user(date.getData_create_user());
		dateInfo.setData_update_time(date.getData_update_time());
		
		dateInfo.setData_update_user(date.getData_update_user());
		dateInfo.setData_version(date.getData_version());
		dateInfo.setLast_date(date.getLast_date());
		dateInfo.setNext_date(date.getNext_date());
		dateInfo.setTrxn_date(date.getTrxn_date());
		
		if (dateSolution == E_SYSDATESOLUTION.LOGICAL) {
			return dateInfo;
		}
		else {
			/*
			 * When use physical date solution, can't jump day run EOD. 
			 * Use physical date to ensure system switch date on 00:00:00, need ensure EOD start later 00:00:00, like 00:05:00
			 */
			int diffDays = BizUtil.dateDiff(E_CYCLETYPE.DAY.getValue(), date.getTrxn_date(), date.getPhysical_date());
			
			if (diffDays < 0) { // When logical date large than physical date, cann't use physical date
				throw ApBaseErr.ApBase.E0130(date.getPhysical_date(), date.getTrxn_date());
			}
			else if (diffDays == 0) { // After 00:05:00, system date switched, logical date is equal to physical date, at this time. 
				return dateInfo;
			}
			else { // physical date > logical date
				E_YESORNO permCrosMultiDay = E_YESORNO.YES;
				if (ApBaseSystemParm.exists(ApConst.DAYEND_DATA, AP_IGNORE_CROSS_MULTIDAY)) {
					permCrosMultiDay = E_YESORNO.get(ApBaseSystemParm.getValue(ApConst.DAYEND_DATA, AP_IGNORE_CROSS_MULTIDAY));
				}
				
				aps_dayend_info lastEodInfo = Aps_dayend_infoDao.selectOne_odb1(orgId, AP_EOD_FLOW_ID, dateInfo.getLast_date(), false);
				if (permCrosMultiDay == E_YESORNO.YES) {
					// If system date switched, but the hole EOD failed. (Date switch is the first job of EOD)
					if (lastEodInfo != null && lastEodInfo.getDay_end_status() != E_DAYENDSTATUS.SUCCESS) {
						return dateInfo;
					}
					else {
						dateInfo.setLast_date(BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), date.getLast_date(), 1));
						dateInfo.setTrxn_date(BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), date.getTrxn_date(), 1));
						dateInfo.setNext_date(BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), date.getNext_date(), 1));
						dateInfo.setBal_sheet_date(BizUtil.lastDay(E_CYCLETYPE.YEAR.getValue(), dateInfo.getTrxn_date()));
						return dateInfo;
					}
				}
				else {// during 00:00:00 to 00:04:59
					if (diffDays == 1 && (lastEodInfo == null || lastEodInfo.getDay_end_status() == E_DAYENDSTATUS.SUCCESS)) {
						dateInfo.setLast_date(BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), date.getLast_date(), 1));
						dateInfo.setTrxn_date(BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), date.getTrxn_date(), 1)); // equal to physical date
						dateInfo.setNext_date(BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), date.getNext_date(), 1));
						dateInfo.setBal_sheet_date(BizUtil.lastDay(E_CYCLETYPE.YEAR.getValue(), dateInfo.getTrxn_date()));
						return dateInfo;
					}
					else {
						throw ApBaseErr.ApBase.E0131();
					}
				}
			}
		}
	}

	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月8日-下午5:32:01</li>
	 *         <li>功能说明:对当前业务法人做日切处理</li>
	 *         </p>
	 */
	public static void swith() {

		RunEnvs runEnvs = SysUtil.getTrxRunEnvs();

		// 当前系统日期信息
		String orgId = runEnvs.getBusi_org_id();
		app_date dateInfo = App_dateDao.selectOneWithLock_odb1(orgId, false);

		if (dateInfo == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_date.class).getLongname(), SysDict.A.busi_org_id.getLongName(), orgId);
		}

		String lastDate = dateInfo.getTrxn_date(); // 系统上日日期使用系统当前日期
		String trxnDate = dateInfo.getNext_date(); // 系统当前日期使用系统下日日期
		String nextDate = BizUtil.dateAdd("day", trxnDate, 1); // 系统下日日期使用系统当前日期加一天。
		String balSheetDate = BizUtil.lastDay("Y", trxnDate); // 年节日使用当前年份最后一天

		// 日切系统日期修改
		dateInfo.setLast_date(lastDate);
		dateInfo.setTrxn_date(trxnDate);
		dateInfo.setNext_date(nextDate);
		dateInfo.setBal_sheet_date(balSheetDate);
		
		App_dateDao.updateOne_odb1(dateInfo);
		
		// 更新环境变量日期
		runEnvs.setTrxn_date(trxnDate);
		runEnvs.setNext_date(nextDate);
		runEnvs.setLast_date(lastDate);
		runEnvs.setBal_sheet_date(balSheetDate);

	}


}
