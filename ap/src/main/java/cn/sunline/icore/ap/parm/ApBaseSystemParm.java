package cn.sunline.icore.ap.parm;

import java.math.BigDecimal;

import cn.sunline.clwj.msap.core.parameter.MsGlobalParm;

/**
 * <p>
 * 文件功能说明：系统杂项参数
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
public class ApBaseSystemParm {

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

		return MsGlobalParm.exists(mainKey, subKey);
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

		return MsGlobalParm.exists(mainKey);
	}
	
	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:12:13</li>
	 *         <li>功能说明：根据mainKey和subKey获取获取参数值</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */
	public static String getValue(String mainKey, String subKey) {
 
		return MsGlobalParm.getValue(mainKey, subKey);
	}
	
	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:11:49</li>
	 *         <li>功能说明：根据mainKey获取参数值</li>
	 *         </p>
	 * @param mainKey
	 * @return
	 */
	public static String getValue(String mainKey) {

		// 取系统参数
		return MsGlobalParm.getValue(mainKey);
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

		return MsGlobalParm.getIntValue(mainKey, subKey);
	}
	
	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:13:37</li>
	 *         <li>功能说明：根据mainKey获取参数值，并转换为int输出</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */	
	public static int getIntValue(String mainKey) {

		return MsGlobalParm.getIntValue(mainKey);
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

		return MsGlobalParm.getDecimalValue(mainKey, subKey);

	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午1:13:37</li>
	 *         <li>功能说明：根据mainKey获取参数值，并转换为decimal输出</li>
	 *         </p>
	 * @param mainKey
	 * @param subKey
	 * @return
	 */	
	public static BigDecimal getDecimalValue(String mainKey) {

		return MsGlobalParm.getDecimalValue(mainKey);
	}
	
	/**
	 * @Author tangqg
	 *         <p>
	 *         <li>2017年8月10日-上午9:55:40</li>
	 *         <li>get summary code</li>
	 *         </p>
	 * @param subKey - sub-key in MspGlobalParm
	 * @return
	 */
	public static String getSummaryCode(String subKey){
		
		return getValue("CORE_SUMMARY_CODE", subKey);
	}

	/**
	 * 
	 * @param subKey
	 * @return
	 */
	public static boolean getOFF_ON(String subKey){
		
		return MsGlobalParm.getOFF_ON(subKey);
	}
}
