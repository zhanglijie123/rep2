package cn.sunline.icore.ap.api;

import java.math.BigDecimal;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.parm.ApBaseBusinessParm;

/**
 * <p>
 * 文件功能说明：业务杂项参数
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月5日-下午10:13:13</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBusinessParmApi {

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:12:13</li>
	 *         <li>功能说明：判断参数是否存在</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return true or false
	 */
	public static boolean exists(String mainKey, String subKey) {
		return ApBaseBusinessParm.exists(mainKey, subKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:12:13</li>
	 *         <li>功能说明：判断参数是否存在,辅键默认为"*"</li>
	 *         </p>
	 * @param mainKey
	 * @return true or false
	 */
	public static boolean exists(String mainKey) {

		return ApBaseBusinessParm.exists(mainKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:12:13</li>
	 *         <li>功能说明：根据mainKey和subKey获取获取参数值</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return String
	 */
	public static String getValue(String mainKey, String subKey) {
		return ApBaseBusinessParm.getValue(mainKey, subKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:11:49</li>
	 *         <li>功能说明：根据mainKey获取参数值</li>
	 *         </p>
	 * @param mainKey
	 * @return String
	 */
	public static String getValue(String mainKey) {

		return ApBaseBusinessParm.getValue(mainKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:13:14</li>
	 *         <li>功能说明：根据mainKey和subKey参数，并转换为int输出</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */
	public static int getIntValue(String mainKey, String subKey) {

		return ApBaseBusinessParm.getIntValue(mainKey, subKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:20:13</li>
	 *         <li>功能说明：根据mainKey获取参数值，并转换为int输出</li>
	 *         </p>
	 * @param mainKey
	 * @return
	 */
	public static int getIntValue(String mainKey) {

		return ApBaseBusinessParm.getIntValue(mainKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:13:37</li>
	 *         <li>功能说明：根据mainKey和subKey获取参数值，并转换为decimal输出</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */
	public static BigDecimal getDecimalValue(String mainKey, String subKey) {

		return ApBaseBusinessParm.getDecimalValue(mainKey, subKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:22:24</li>
	 *         <li>功能说明：根据mainKey获取参数值，并转换为decimal输出</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */
	public static BigDecimal getDecimalValue(String mainKey) {

		return ApBaseBusinessParm.getDecimalValue(mainKey);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:22:24</li>
	 *         <li>功能说明：根据mainKey,subKey，返回E_YESORNO的枚举</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */
	public static E_YESORNO getYesOrNoValue(String mainKey, String subKey) {

		return ApBaseBusinessParm.getYesOrNoValue(mainKey, subKey);
	}

}
