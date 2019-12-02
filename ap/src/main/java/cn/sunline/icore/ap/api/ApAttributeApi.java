package cn.sunline.icore.ap.api;

import java.util.List;

import cn.sunline.icore.ap.attr.ApBaseAttribute;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrListIn;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrListResult;
import cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL;

public class ApAttributeApi {
	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月12日-下午4:40:03</li>
	 *         <li>功能说明：获取属性定义信息</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 */
	public static List<app_attribute> getAttrDefine(E_OWNERLEVEL attrLevel) {

		// 取属性位定义表
		return ApBaseAttribute.getAttrDefine(attrLevel);
	}

	/**
	 * @Author hongbiao
	 *         <p>
	 *         <li>2016年12月12日-下午4:40:03</li>
	 *         <li>功能说明：获取属性定义信息</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 */
	public static String getAttrDefineString(E_OWNERLEVEL attrLevel) {

		return ApBaseAttribute.getAttrDefineString(attrLevel);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月12日-下午4:40:03</li>
	 *         <li>功能说明：获取某个属性位的定义信息</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 * @param position
	 *            属性位
	 */
	public static app_attribute getAttrDefine(E_OWNERLEVEL attrLevel, long position) {

		// 取属性位定义表
		return ApBaseAttribute.getAttrDefine(attrLevel, position);
	}


	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月12日-下午6:14:20</li>
	 *         <li>功能说明：对属性数据做变更</li>
	 *         </p>
	 * @param attrLevel
	 * @param ownerId
	 * @param attrList
	 * @param oldValue
	 *            原有属性值
	 * @return ApAttrListResult 属性位列表形态查询结果
	 */
	public static ApAttrListResult setData(E_OWNERLEVEL attrLevel, String ownerId, List<ApAttrListIn> attrList, String oldValue) {

		return ApBaseAttribute.setData(attrLevel, ownerId, attrList, oldValue);
	}

	/**
	 * @Author tongxp
	 *         <p>
	 *         <li>2017年3月22日-下午4:40:03</li>
	 *         <li>功能说明：属性互斥检查</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 * @param attrValue
	 *            属性值
	 */
	public static void checkExclusion(E_OWNERLEVEL attrLevel, String attrValue) {
		ApBaseAttribute.checkExclusion(attrLevel, attrValue);
	}

	/**
	 * @Author wuqiang
	 *         <p>
	 *         <li>2017年1月13日-上午9:42:29</li>
	 *         <li>功能说明：判断属性位定位是否存在</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 * @param attrPosition
	 *            属性位置
	 * @return true-存在
	 */
	public static boolean existsAttribute(E_OWNERLEVEL attrLevel, Long attrPosition) {

		return ApBaseAttribute.existsAttribute(attrLevel, attrPosition);
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2017年10月19日-下午6:14:20</li>
	 *         <li>功能说明：获取刷新后的属性值</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 * @param ownerId
	 *            属主：账号、卡号、客户号、子账号
	 * @param oldAttrValue
	 *            原有属性值
	 * @return 新属性值
	 */
	public static String getNewestAttrValue(E_OWNERLEVEL attrLevel, String ownerId, String oldAttrValue) {

		return ApBaseAttribute.getNewestAttrValue(attrLevel, ownerId, oldAttrValue);
	}
}
