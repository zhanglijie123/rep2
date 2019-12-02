package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.parm.ApBaseHoliday;
import cn.sunline.icore.sys.type.EnumType.E_CUSTOMERTYPE;

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
public class ApHolidayApi {


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

		return ApBaseHoliday.isHoliday(trxnDate, holidayCode, custType);
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

		return ApBaseHoliday.isHoliday(trxnDate, custType, branchId);
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

		return ApBaseHoliday.getNextWorkerDay(trxnDate, holidayCode, custType, dayNum);
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

		return ApBaseHoliday.getNextWorkerDay(trxnDate, custType, dayNum, branchId);
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

		return ApBaseHoliday.getPriorWorkerDay(trxnDate, holidayCode, custType);
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

		return ApBaseHoliday.getPriorWorkerDay(trxnDate, custType, branchId);
	}

}
