package cn.sunline.icore.ap.api;

import java.util.List;

import cn.sunline.icore.ap.parm.ApBaseSummary;
import cn.sunline.icore.ap.tables.TabApBasic.app_summary;

/**
 * <p>
 * 文件功能说明：摘要
 * </p>
 * 
 * @Author jollyja
 *         <p>
 *         <li>2016年12月5日-下午10:14:32</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApSummaryApi {

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午1:38:24</li>
	 *         <li>功能说明：判断摘要代码是否存在，不存返回false</li>
	 *         </p>
	 * @param summaryCode
	 * @return
	 */
	public static boolean exists(String summaryCode) {

		return ApBaseSummary.exists(summaryCode);
	}
	
	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月13日-下午12:02:40</li>
	 *         <li>功能说明：判断摘要代码是否存在</li>
	 *         </p>
	 * @param summaryCode
	 * @param flag
	 *          true 不存在报错
	 *          false 不抛错
	 * @return
	 */
	public static boolean exists(String summaryCode, boolean flag){
		return ApBaseSummary.exists(summaryCode, flag);
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午1:39:14</li>
	 *         <li>功能说明：获取摘要文字</li>
	 *         </p>
	 * @param summaryCode
	 * @return
	 */
	public static String getText(String summaryCode) {

		return ApBaseSummary.getText(summaryCode);
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月6日-下午1:43:07</li>
	 *         <li>功能说明：获取全部摘要定义</li>
	 *         </p>
	 * @return
	 */
	public static List<app_summary> all() {
		return ApBaseSummary.all();
	}
	
	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2017年11月27日-下午1:39:14</li>
	 *         <li>功能说明：获取摘要类型</li>
	 *         </p>
	 * @param summaryCode
	 * @return
	 */
	public static String getSummaryClass(String summaryCode) {

		return ApBaseSummary.getSummaryClass(summaryCode);
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月21日-下午1:39:14</li>
	 *         <li>功能说明：获取默认摘要代码</li>
	 *         </p>
	 * @param errFlag
	 *            报错标志 true-报错， false-不报错
	 * @return 摘要代码
	 */
	public static String getDefaultSummary(boolean errFlag) {
		
		return ApBaseSummary.getDefaultSummary(errFlag);
	}
	
	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月21日-下午1:39:14</li>
	 *         <li>功能说明：检查输入摘要代码与交易场景的适配性</li>
	 *         </p>
	 * @param summaryCode
	 *            摘要代码
	 */
	public static void checkTrxnSceneSummary(String summaryCode) {
		
		ApBaseSummary.checkTrxnSceneSummary(summaryCode);
	}
}
