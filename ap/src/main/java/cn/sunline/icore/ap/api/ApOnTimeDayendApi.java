package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.plugin.impl.ApBaseOnTimeDayend;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author liucong
 *         <p>
 *         <li>2018年1月4日-下午2:29:41</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年1月4日-liucong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApOnTimeDayendApi {


	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2018年1月4日-下午2:30:35</li>
	 *         <li>功能说明:On time day end</li>
	 *         </p>
	 */
	public static void onTimeDayend(String flowId, String orgId) {
		ApBaseOnTimeDayend.onTimeDayend(flowId, orgId);

	}

}
