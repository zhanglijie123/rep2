package cn.sunline.icore.ap.api;

import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.icore.ap.parm.ApBaseIdentity;
import cn.sunline.icore.ap.tables.TabApBasic;
import cn.sunline.icore.ap.type.ComApBasic;
import cn.sunline.icore.ap.type.ComApBasic.ApIdentityInfo;
import cn.sunline.icore.sys.errors.ApErr.AP;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.type.EnumType.E_SUITABLECUSTTYPE;

/**
 * <p>
 * 文件功能说明：证件类型
 * </p>
 * 
 * @Author liucong
 *         <p>
 *         <li>2016年12月5日-下午10:14:15</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 liucong：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApIdentityApi {

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午1:58:50</li>
	 *         <li>功能说明：判断证件类型是否存在，无对应记录时返回false</li>
	 *         </p>
	 * @param idType
	 * @return 存在返回true，不存在返回false.
	 */
	public static boolean exists(String idType) {

		return ApBaseIdentity.exists(idType);
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:02:37</li>
	 *         <li>功能说明：获取一个证件类型定义对象</li>
	 *         </p>
	 * @param idType
	 * @return 不为空返回证件类型定义对象，否则抛出异常
	 * @throws APPUB.E0005
	 *             () 获取[${tableDesc}]失敗,无对应记录,数据键值[${keyValue}]
	 */
	public static ApIdentityInfo getItem(String idType) {

		return ApBaseIdentity.getItem(idType);
	}
	
	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:00:08</li>
	 *         <li>检查证件种类对是否合法，不合法则报错，合法则继续检查证件号码的格式是否有效，无效则报错。</li>
	 *         </p>
	 * @param idType
	 * @param idNo
	 * @throws APPUB.E0005
	 *             () 获取[${tableDesc}]失敗,无对应记录,数据键值[${keyValue}]
	 * @throws APPUB.E0001
	 *             () 信息域不允许为空,[${fieldName}-${fieldDesc}]
	 * @throws AP.E0006
	 *             () 证件号码的校验位错误
	 * @throws AP.E0003
	 *             () 证件号码不符合规则
	 */
	public static void isValid(String idType, String idNo) {

		ApBaseIdentity.isValid(idType, idNo);
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:04:43</li>
	 *         <li>功能说明：获取某个证件类型的名称</li>
	 *         </p>
	 * @param idType
	 * @return idDesc 返回证件描述
	 */
	public static String getName(String idType) {

		return ApBaseIdentity.getName(idType);
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:03:00</li>
	 *         <li>功能说明：获取全部证件类型定义对象</li>
	 *         </p>
	 * @return
	 */
	public static List<TabApBasic.app_identity> all() {
		return ApBaseIdentity.all();
	}
	
	/**
	 * @Author meizhian
	 *         <p>
	 *         <li>2019年4月28日-上午9:44:58</li>
	 *         <li>功能说明：根据客户类型、必须提供标志获取证件类型</li>
	 *         </p>
	 * @param custType	客户类型
	 * @param isProvide 必输标志
	 * @return 证件类型定义
	 */
	public static List<ComApBasic.ApIdentityInfo> getList(E_SUITABLECUSTTYPE custType,MsEnumType.E_YESORNO isProvide){
		return ApBaseIdentity.getIdentityList(custType, isProvide);
	}
}