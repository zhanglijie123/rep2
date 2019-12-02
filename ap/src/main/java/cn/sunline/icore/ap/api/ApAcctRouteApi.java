package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.book.ApBaseAcctRoute;
import cn.sunline.icore.sys.type.EnumType.E_ACCTROUTETYPE;

/**
 * <p>
 * 文件功能说明： 账号路由登记
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2017年1月18日-下午5:33:53</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApAcctRouteApi {

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月18日-下午5:37:21</li>
	 *         <li>功能说明：注册</li>
	 *         </p>
	 * @param acctNO
	 * @param type
	 */
	public static void register(String acctNo, E_ACCTROUTETYPE type) {

		// 微服务下不再登记账户路由信息
		// ApBaseAcctRoute.register(acctNo, type);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月18日-下午5:37:39</li>
	 *         <li>功能说明：获取账号的路由分类</li>
	 *         </p>
	 * @param acctNo
	 * @return
	 */
	public static E_ACCTROUTETYPE getRouteType(String acctNo) {
		return ApBaseAcctRoute.getRouteType(acctNo);
	}
}
