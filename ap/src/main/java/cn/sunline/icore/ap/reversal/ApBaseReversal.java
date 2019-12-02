package cn.sunline.icore.ap.reversal;

import cn.sunline.clwj.msap.biz.transaction.MsEvent;
import cn.sunline.clwj.msap.iobus.type.IoMsReverseType.IoMsRegEvent;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.biz.global.SysUtil;


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
public class ApBaseReversal {

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
		
		IoMsRegEvent input = BizUtil.getInstance(IoMsRegEvent.class);
		
		input.setReversal_event_id(reversalEventID);
		input.setInformation_value(SysUtil.serialize(comObj));

		MsEvent.register(input, true);
	}
	 }
