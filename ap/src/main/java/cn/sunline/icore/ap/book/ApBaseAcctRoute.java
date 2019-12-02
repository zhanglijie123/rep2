package cn.sunline.icore.ap.book;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_CASHTRXN;
import cn.sunline.icore.ap.tables.TabApBook.Apb_account_routeDao;
import cn.sunline.icore.ap.tables.TabApBook.apb_account_route;
import cn.sunline.icore.ap.type.ComApBasic.ApAccountRouteInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_ACCOUTANALY;
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
public class ApBaseAcctRoute {

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

		apb_account_route acctRoute = BizUtil.getInstance(apb_account_route.class);

		acctRoute.setAcct_no(acctNo);
		acctRoute.setAcct_route_type(type);

		Apb_account_routeDao.insert(acctRoute);
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

		//
		// apb_account_route acctRoute =
		// Apb_account_routeDao.selectOne_odb1(acctNo, false);
		// return (acctRoute == null) ? E_ACCTROUTETYPE.NONE :
		// acctRoute.getAcct_route_type();

		ApAccountRouteInfo routeInfo = BizUtil.getAccountRouteInfo(acctNo, E_CASHTRXN.TRXN, null);

		if (routeInfo.getAcct_analy() == E_ACCOUTANALY.BUSINESE) {
			return E_ACCTROUTETYPE.BUSINESSCODE;
		}
		else if (routeInfo.getAcct_analy() == E_ACCOUTANALY.DEPOSIT) {
			return E_ACCTROUTETYPE.DEPOSIT;
		}
		else if (routeInfo.getAcct_analy() == E_ACCOUTANALY.INSIDE) {
			return E_ACCTROUTETYPE.INSIDE;
		}
		else if (routeInfo.getAcct_analy() == E_ACCOUTANALY.NOSTRO) {
			return E_ACCTROUTETYPE.NOSTRO;
		}
		else if (routeInfo.getAcct_analy() == E_ACCOUTANALY.SUSPENSE) {
			return E_ACCTROUTETYPE.SUSPENSE;
		}
		
		return E_ACCTROUTETYPE.NONE;
	}
}
