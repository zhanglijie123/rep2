package cn.sunline.icore.ap.api;

import cn.sunline.clwj.msap.core.type.MsCoreComplexType.MsChannelInfo;
import cn.sunline.icore.ap.parm.ApBaseChannel;

/**
 * <p>
 * 文件功能说明：渠道参数相关操作
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
public class ApChannelApi {
	
	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月11日-下午4:23:11</li>
	 *         <li>功能说明: 判断渠道是否存在 </li>
	 *         </p>
	 * @param channelId
	 * @return
	 */	
	public static boolean exists(String channelId) {
		return ApBaseChannel.exists(channelId);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月11日-下午4:23:11</li>
	 *         <li>功能说明：获取渠道信息,渠道不存在时抛出异常</li>
	 *         </p>
	 * @param channelId
	 * @return
	 */
	public static MsChannelInfo getChannel(String channelId) {
		
		return ApBaseChannel.getChannel(channelId);
	}

	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月8日-下午5:32:01</li>
	 *         <li>功能说明：获取渠道信息</li>
	 *         </p>
	 * @param channelId
	 *            渠道编号
	 * @param isThrow
	 *            true:不存在抛异常 false:不存在不抛异常，返回null
	 * @return 渠道对象
	 */
	public static MsChannelInfo getChannel(String channelId, boolean isThrow) {

		return ApBaseChannel.getChannel(channelId, isThrow);
	}
	

	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月8日-下午5:32:01</li>
	 *         <li>功能说明：判断是否需要登记冲正事件</li>
	 *         </p>
	 * @param channelId
	 *            渠道编号
	 * @return true:需要登记 false:不需要登记
	 */
	public static boolean isRegisterReversalEvent(String channelId) {

		return ApBaseChannel.isRegisterReversalEvent(channelId);
	}
	
	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月8日-下午5:32:01</li>
	 *         <li>功能说明: 判断是否柜面渠道</li>
	 *         </p>
	 * @param channelId
	 *            渠道编号
	 * @return true 柜面 false 非柜面
	 */
	public static boolean isCounter(String channelId) {

		return ApBaseChannel.isCounter(channelId);
	}	
}
