package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.parm.ApBaseDate;
import cn.sunline.icore.ap.type.ComApSystem.ApDateInfo;

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
public class ApDateApi {

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
		return ApBaseDate.getInfo(orgId);
	}

	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月8日-下午5:32:01</li>
	 *         <li>功能说明:对当前业务法人做日切处理</li>
	 *         </p>
	 */
	public static void swith() {
		ApBaseDate.swith();
	}


}
