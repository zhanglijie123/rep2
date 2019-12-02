package cn.sunline.icore.ap.parm;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.branch.ApBaseBranch;
import cn.sunline.icore.ap.tables.TabApBasic.App_holidayDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_holiday;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_CUSTOMERTYPE;
import cn.sunline.icore.sys.type.EnumType.E_HOLIDAYCLASS;
import cn.sunline.ltts.biz.global.CommUtil;

/**
 * <p>
 * 文件功能说明：假日参数
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月8日-下午12:43:50</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 zhangql：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseHoliday {

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月9日-上午9:23:46</li>
	 *         <li>功能说明：将客户类型转换为假日类型</li>
	 *         </p>
	 * @param custType
	 * @return
	 */
	private static E_HOLIDAYCLASS convertType(E_CUSTOMERTYPE custType) {

		// 初始化假日分类
		E_HOLIDAYCLASS result = E_HOLIDAYCLASS.NONE;

		// 判断假日模式
		if (E_YESORNO.YES.getValue().equals(ApBaseBusinessParm.getValue("HOLIDAY_MODE"))) {

			if (E_CUSTOMERTYPE.PERSONAL == custType)
				result = E_HOLIDAYCLASS.PERSONAL;// 零售
			else
				result = E_HOLIDAYCLASS.CORPORATE;// 对公

		}

		return result;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午12:46:28</li>
	 *         <li>功能说明：判断指定日期是否节假日</li>
	 *         </p>
	 * @param trxnDate
	 *            假日日期
	 * @param holidayCode
	 *            假日代码
	 * @param custType
	 *            客户类型
	 * @return
	 */
	public static boolean isHoliday(String trxnDate, String holidayCode, E_CUSTOMERTYPE custType) {

		// 将客户类别转换为假日分类
		E_HOLIDAYCLASS holidayClass = convertType(custType);

		// 正常情况下获取假日参数表
		app_holiday holiday = App_holidayDao.selectOne_odb1(holidayCode, holidayClass, trxnDate, false);

		// 未获取到节假日记录、且不是缺省的节假日参数，则获取缺省节假日参数
		if (holiday == null && CommUtil.compare(holidayCode, ApConst.WILDCARD) != 0)
			holiday = App_holidayDao.selectOne_odb1(ApConst.WILDCARD, holidayClass, trxnDate, false);

		// 节假日标志
		E_YESORNO holidayInd = (holiday == null) ? E_YESORNO.NO : holiday.getHoliday_ind();

		return holidayInd == E_YESORNO.YES ? true : false;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月15日-下午4:09:10</li>
	 *         <li>功能说明：判断指定日期是否节假日</li>
	 *         </p>
	 * @param trxnDate
	 *            假日日期
	 * @param custType
	 *            客户类型
	 * @param branchId
	 *            机构号
	 * @return
	 */
	public static boolean isHoliday(String trxnDate, E_CUSTOMERTYPE custType, String branchId) {

		return isHoliday(trxnDate, ApBaseBranch.getHolidayCode(branchId), custType);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午1:57:11</li>
	 *         <li>功能说明：获取指定日的第N个工作日</li>
	 *         </p>
	 * @param trxnDate
	 *            指定日期
	 * @param holidayCode
	 *            假日代码
	 * @param custType
	 *            客户类型
	 * @param dayNum
	 *            第N个工作日
	 * @return
	 */
	public static String getNextWorkerDay(String trxnDate, String holidayCode, E_CUSTOMERTYPE custType, int dayNum) {

		// 指定日期
		String workDate = trxnDate;

		// 获取指定日的第N个工作日
		for (int i = 1; i <= dayNum;) {
			// 往后推一天
			workDate = BizUtil.dateAdd("dd", workDate, 1);

			// 遇节假日顺延
			if (!isHoliday(workDate, holidayCode, custType))
				i++;
		}

		return workDate;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午1:57:11</li>
	 *         <li>功能说明：获取指定日的第N个工作日</li>
	 *         </p>
	 * @param trxnDate
	 *            指定日期
	 * @param custType
	 *            客户类型
	 * @param dayNum
	 *            第N个工作日
	 * @param branchId
	 *            机构号
	 * @return
	 */
	public static String getNextWorkerDay(String trxnDate, E_CUSTOMERTYPE custType, int dayNum, String branchId) {

		return getNextWorkerDay(trxnDate, ApBaseBranch.getHolidayCode(branchId), custType, dayNum);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午2:14:34</li>
	 *         <li>功能说明：获取指定日期的前一个工作日</li>
	 *         </p>
	 * @param trxnDate
	 *            指定日期
	 * @param holidayCode
	 *            假日代码
	 * @param custType
	 *            客户类型
	 * @return
	 */
	public static String getPriorWorkerDay(String trxnDate, String holidayCode, E_CUSTOMERTYPE custType) {

		// 指定日期
		String workDate = trxnDate;

		// 往前倒推一日遇到节假日顺延
		do {
			workDate = BizUtil.dateAdd("dd", workDate, -1);
		}
		while (isHoliday(workDate, holidayCode, custType));

		return workDate;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午2:14:34</li>
	 *         <li>功能说明：获取指定日期的前一个工作日</li>
	 *         </p>
	 * @param trxnDate
	 *            指定日期
	 * @param custType
	 *            客户类型
	 * @param branchId
	 *            机构号
	 * @return
	 */
	public static String getPriorWorkerDay(String trxnDate, E_CUSTOMERTYPE custType, String branchId) {

		return getPriorWorkerDay(trxnDate, ApBaseBranch.getHolidayCode(branchId), custType);
	}

}
