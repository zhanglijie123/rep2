package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.reversal.ApBaseReversal;


/**
 * <p>
 * 文件功能说明：冲账处理
 * </p>
 * 
 * @Author chensy
 *         <p>
 *         <li>2016年12月5日-下午10:43:53</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApReversalApi {

	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月14日-下午2:07:00</li>
	 *         <li>功能说明：登记冲正事件</li>
	 *         </p>
	 * @param reversalEventID
	 *            冲正事件ID
	 * @param comObj
	 *            交易信息
	 */
	public static void register(String reversalEventID, Object comObj) {
		
		ApBaseReversal.register(reversalEventID, comObj);
	}
}
