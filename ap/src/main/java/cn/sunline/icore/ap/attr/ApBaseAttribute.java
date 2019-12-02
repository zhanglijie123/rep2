package cn.sunline.icore.ap.attr;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.parm.ApBaseDropList;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attributeDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attribute_dueDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attribute_mutexDao;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute_due;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute_mutex;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrDate;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrListIn;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrListOut;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrListResult;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月12日-下午1:19:33</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 zhangql：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseAttribute {

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
		return App_attributeDao.selectAll_odb2(attrLevel, false);
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
		List<app_attribute> attriList = App_attributeDao.selectAll_odb2(attrLevel, false);

		String resultAttributeStr = "";

		for (app_attribute app_attribute : attriList) {

			// 防止下拉值修改,属性值未同步修改,对默认值也进行检查
			ApBaseDropList.exists(app_attribute.getRef_drop_list(), app_attribute.getDefault_value());

			resultAttributeStr += app_attribute.getDefault_value();
		}

		return resultAttributeStr;
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
		return App_attributeDao.selectOne_odb1(attrLevel, position, true);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月15日-上午10:17:01</li>
	 *         <li>功能说明: 在日期列表中查找属性位置对应记录，存在则返回该记录对应的下标</li>
	 *         </p>
	 * @param attrList
	 *            属性日期列表
	 * @param attrPosition
	 *            属性位置
	 * @return
	 */
	private static int getIndexByAttrDate(List<ApAttrDate> attrList, long attrPosition) {

		// 初始化
		int dataIndex = -1;
		int i = 0;

		// list中查找记录
		while (i < attrList.size()) {

			if (attrList.get(i).getAttr_position() == attrPosition) {

				dataIndex = i;
				break;
			}

			i++;
		}

		return dataIndex;
	}

	// 获取某个OwnerId的属性位到期日
	private static List<ApAttrDate> getOwnerDueDate(E_OWNERLEVEL attrLevel, String ownerId) {

		// 查询属性位到期日登记簿
		List<app_attribute_due> attributeList = App_attribute_dueDao.selectAll_odb2(attrLevel, ownerId, false);

		// 实例化属性日期
		ApAttrDate attrDate = BizUtil.getInstance(ApAttrDate.class);

		// 定义属性日期列表
		List<ApAttrDate> attrattrList = new ArrayList<ApAttrDate>();

		// 赋值
		for (app_attribute_due attribute : attributeList) {

			attrDate.setAttr_position(attribute.getAttr_position());// 属性位置
			attrDate.setAttr_date(attribute.getAttr_due_date());// 日期

			attrattrList.add(attrDate);
		}

		return attrattrList;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月12日-下午5:29:14</li>
	 *         <li>功能说明：组织属性数据</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 * @param ownerId
	 *            属主ID
	 * @param attrValue
	 *            属性值
	 * @param curtDate
	 *            当前日期
	 * @return
	 */
	private static List<ApAttrListIn> builderData(E_OWNERLEVEL attrLevel, String ownerId, String oldValue, String curtDate) {

		// 获取属性位到期数据
		List<ApAttrDate> attrList = getOwnerDueDate(attrLevel, ownerId);

		// 初始化
		List<ApAttrListIn> ret = new ArrayList<ApAttrListIn>();

		// 取属性位定义表
		List<app_attribute> attrDefList = getAttrDefine(attrLevel);

		// 属性位长度
		int attrValueLen = CommUtil.isNotNull(oldValue) ? oldValue.length() : 0;

		for (app_attribute attrDef : attrDefList) {

			ApAttrListIn record = BizUtil.getInstance(ApAttrListIn.class);

			record.setAttr_position(attrDef.getAttr_position());// 属性位置

			if (attrDef.getAttr_position() <= attrValueLen) {
				record.setAttr_position_value(oldValue.substring(attrDef.getAttr_position().intValue() - 1, attrDef.getAttr_position().intValue()));
			}
			else {
				record.setAttr_position_value(attrDef.getDefault_value());
			}

			// 在日期列表中查找属性位置对应记录
			int dataIndex = getIndexByAttrDate(attrList, attrDef.getAttr_position());

			if (dataIndex >= 0) {

				// 当前日期是空（无需舍弃）, 或是 当前日期 小于等于 attrList日期（未过期）
				if (CommUtil.isNull(curtDate) || CommUtil.compare(curtDate, attrList.get(dataIndex).getAttr_date()) <= 0) {
					// 未过期
					record.setAttr_date(attrList.get(dataIndex).getAttr_date());// 属性日期
				}
				else {
					// 已到期，值需要复原到默认值
					record.setAttr_position_value(attrDef.getDefault_value());// 属性日期
				}
			}

			ret.add(record);
		}

		return ret;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月15日-上午10:17:01</li>
	 *         <li>功能说明: 在结果列表中查找属性位置对应记录，存在则返回该记录对应的下标</li>
	 *         </p>
	 * @param resultList
	 *            属性日期列表
	 * @param attrPosition
	 *            属性位置
	 * @return
	 */
	private static int getIndexByAttrResult(List<ApAttrListIn> resultList, long attrPosition) {

		// 初始化s
		int dataIndex = -1;
		int i = 0;

		// list中查找记录
		while (i < resultList.size()) {

			if (resultList.get(i).getAttr_position() == attrPosition) {

				dataIndex = i;
				break;
			}

			i++;
		}

		return dataIndex;
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

		// 单个属性位更新处理
		for (ApAttrListIn record : attrList) {

			setAttrPosition(attrLevel, ownerId, record);
		}

		List<ApAttrListIn> result = merge(attrLevel, ownerId, attrList, oldValue);

		// 实例化属性位列表形态输出列表
		List<ApAttrListOut> cplOutList = new ArrayList<ApAttrListOut>();

		ApAttrListResult resultList = BizUtil.getInstance(ApAttrListResult.class);

		ApAttrListOut cplOut = null;

		// 循环获取新属性值
		StringBuffer buf = new StringBuffer();
		
		// 按属性位位置升序排列
		BizUtil.listSort(result, true, SysDict.A.attr_position.getId());

		for (ApAttrListIn record : result) {

			buf.append(record.getAttr_position_value());

			// 查询某位置属性定位信息
			app_attribute attribute = getAttrDefine(attrLevel, record.getAttr_position());

			// 实例化属性位列表形态输出
			cplOut = BizUtil.getInstance(ApAttrListOut.class);

			cplOut.setAttr_position(record.getAttr_position());
			cplOut.setAttr_desc(attribute.getAttr_desc());
			cplOut.setAttr_position_value(record.getAttr_position_value());
			cplOut.setRef_drop_list(attribute.getRef_drop_list());
			cplOut.setDrop_list_desc(ApBaseDropList.getText(attribute.getRef_drop_list(), record.getAttr_position_value(), true));// 下拉字典描述
			cplOut.setAttr_expiry_ind(attribute.getAttr_expiry_ind());
			cplOut.setAttr_date(record.getAttr_date());

			cplOutList.add(cplOut);
		}

		String attrValue = buf.toString();

		// 检查维护后的属性是否存在互斥
		checkExclusion(attrLevel, attrValue);

		resultList.setAttr_value(attrValue);

		resultList.setList01(new DefaultOptions<ApAttrListOut>(cplOutList));

		return resultList;
	}

	/**
	 * @Author wuqiang
	 *         <p>
	 *         <li>2017年3月22日-上午9:14:20</li>
	 *         <li>功能说明：设置属性位</li>
	 *         </p>
	 * @param attrLevel
	 *            属性层级
	 * @param ownerId
	 *            属主ID
	 * @param record
	 *            属性位信息
	 */
	private static void setAttrPosition(E_OWNERLEVEL attrLevel, String ownerId, ApAttrListIn record) {

		// 获取属性位定义信息
		app_attribute attribute = getAttrDefine(attrLevel, record.getAttr_position());

		// 属性定义中不存在属性层级，属性位对应的信息
		if (attribute == null) {
			throw ApBaseErr.ApBase.E0017(attrLevel, record.getAttr_position());
		}

		// 检查属性位值是否在下拉字典值之内
		ApBaseDropList.exists(attribute.getRef_drop_list(), record.getAttr_position_value());

		// 属性定义中属性位到期标志位NO,无需设置到期日
		if (attribute.getAttr_expiry_ind() == E_YESORNO.NO) {

			// 有设置到期日
			if (CommUtil.isNotNull(record.getAttr_date())) {

				// 属性层级，属性位无到期标志,无需设置到期日
				throw ApBaseErr.ApBase.E0037(attrLevel, record.getAttr_position());
			}

		}
		else {

			// 属性位值等于默认值, 说明是恢复初始数据，需要将到期记录删除
			if (CommUtil.equals(record.getAttr_position_value(), attribute.getDefault_value())) {

				App_attribute_dueDao.deleteOne_odb1(attrLevel, ownerId, record.getAttr_position());

				return;
			}

			// 属性位值 <> 默认值, 说明需要设置或者更新到期记录
			// 未提供到期日，报错
			if (CommUtil.isNull(record.getAttr_date())) {

				// 属性层级，属性位有到期标志,必须设置到期日
				throw ApBaseErr.ApBase.E0038(attrLevel, record.getAttr_position());
			}

			app_attribute_due dueInfo = App_attribute_dueDao.selectOneWithLock_odb1(attrLevel, ownerId, record.getAttr_position(), false);

			if (dueInfo == null) {

				app_attribute_due attributeDue = BizUtil.getInstance(app_attribute_due.class);

				attributeDue.setAttr_level(attrLevel);// 属性层级
				attributeDue.setAttr_owner_id(ownerId);// 属性属主ID
				attributeDue.setAttr_position(record.getAttr_position());// 属性位置
				attributeDue.setAttr_due_date(record.getAttr_date());// 属性位到期日

				App_attribute_dueDao.insert(attributeDue);

			}
			else {

				dueInfo.setAttr_due_date(record.getAttr_date());

				App_attribute_dueDao.updateOne_odb1(dueInfo);
			}

		}
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月12日-下午6:14:20</li>
	 *         <li>功能说明：将存量的内部形态属性与最新修改的的外部形态属性做合并处理，返回合并后的内部形态属性</li>
	 *         </p>
	 * @param attrLevel
	 * @param ownerId
	 * @param attrList
	 * @param oldValue
	 * @return List<ApAttrList>
	 */
	private static List<ApAttrListIn> merge(E_OWNERLEVEL attrLevel, String ownerId, List<ApAttrListIn> attrList, String oldValue) {

		// 获取系统日期
		String curtDate = BizUtil.getTrxRunEnvs().getTrxn_date();

		// 将原始形态转换为外部形态
		List<ApAttrListIn> result = builderData(attrLevel, ownerId, oldValue, curtDate);

		// 将attrList 合并到 ApAttrList
		for (ApAttrListIn record : attrList) {

			int idx = getIndexByAttrResult(result, record.getAttr_position());

			if (idx == -1) {

				// 属性定义中不存在属性层级，属性位对应的信息
				throw ApBaseErr.ApBase.E0017(attrLevel, record.getAttr_position());
			}
			else {
				result.set(idx, record);
			}
		}

		return result;

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

		// 取属性互斥表
		List<app_attribute_mutex> attriMutexList = App_attribute_mutexDao.selectAll_odb2(attrLevel, false);

		for (app_attribute_mutex attriMutex : attriMutexList) {

			// 检查匹配表达式是否匹配
			if (attrValue.matches(attriMutex.getMapping_expression())) {
				throw ApBaseErr.ApBase.E0007(attriMutex.getShow_error_info());
			}
		}

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

		app_attribute appAttr = App_attributeDao.selectOne_odb1(attrLevel, attrPosition, false);

		if (appAttr == null) {

			throw ApBaseErr.ApBase.E0132(attrLevel, attrPosition);
		}

		return true;
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

		// 获取系统日期
		String curtDate = BizUtil.getTrxRunEnvs().getTrxn_date();

		// 将原始形态转换为外部形态
		List<ApAttrListIn> result = builderData(attrLevel, ownerId, oldAttrValue, curtDate);
		
		// 按属性位位置升序排列
	    BizUtil.listSort(result, true, SysDict.A.attr_position.getId());

		// 循环获取新属性值
		StringBuffer buf = new StringBuffer();

		for (ApAttrListIn record : result) {
			
			buf.append(record.getAttr_position_value());
		}

		return buf.toString();
	}

}
