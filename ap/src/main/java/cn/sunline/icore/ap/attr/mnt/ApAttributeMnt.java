package cn.sunline.icore.ap.attr.mnt;
import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDropList;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDropListDao;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.namedsql.ApAttributeDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attributeDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attribute_mutexDao;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute_mutex;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrInfo;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrInfoResult;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrInfoWithInd;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrMutexInfo;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrMutexInfoWithInd;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/**
 * 
 * <p>
 * 文件功能说明：
 *     属性维护类		
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2017年1月22日-下午2:13:44</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年1月22日-lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApAttributeMnt {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApAttributeMnt.class);

	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月7日-下午6:58:27</li>
	 *         <li>功能说明：属性信息查询</li>
	 *         </p>
	 * @param attr_level
	 * @return
	 */
	public static ApAttrInfoResult queryAttrInfo(E_OWNERLEVEL attrLevel) {
		bizlog.method(" ApAttributeMnt.queryAttrInfo begin >>>>>>>>>>>>>>>>");

		BizUtil.fieldNotNull(attrLevel, SysDict.A.attr_level.getId(), SysDict.A.attr_level.getLongName());

		List<ApAttrInfo> attrInfo = ApAttributeDao.selAttrInfo(MsOrg.getReferenceOrgId(app_attribute.class), attrLevel, false);
		List<ApAttrMutexInfo> attrMutexInfo = ApAttributeDao.selAttrMutexInfo(MsOrg.getReferenceOrgId(app_attribute_mutex.class), attrLevel, false);

		ApAttrInfoResult result = BizUtil.getInstance(ApAttrInfoResult.class);
		result.setAttrInfo(new DefaultOptions<ApAttrInfo>(attrInfo)); // 属性信息
		result.setAttrMutexInfo(new DefaultOptions<ApAttrMutexInfo>(attrMutexInfo)); // 属性互斥信息

		bizlog.method(" ApAttributeMnt.queryAttrInfo end <<<<<<<<<<<<<<<<");
		
		return result;
	}

	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月7日-下午6:58:45</li>
	 *         <li>功能说明：属性信息维护</li>
	 *         </p>
	 * @param attrLevel
	 * @param attrInfo
	 * @param attrMutexInfo
	 * @param attrCtlInfo
	 */
	public static void modifyAttrInfo(E_OWNERLEVEL attrLevel, ApAttrInfoWithInd attrInfo) {
		bizlog.method(" ApAttributeMnt.modifyAttrInfo begin >>>>>>>>>>>>>>>>");
		BizUtil.fieldNotNull(attrLevel, SysDict.A.attr_level.getId(), SysDict.A.attr_level.getLongName());
		
		int i = 0;
		
		i += modifyAttr(attrLevel,attrInfo); // 属性信息维护

		if (i == 0) {
			// throw
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_attribute.class).getLongname());
		}
		
		bizlog.method(" ApAttributeMnt.modifyAttrInfo end <<<<<<<<<<<<<<<<");
	}
	//属性互斥信息操作
	public static void modifyAttrMutexInfo(E_OWNERLEVEL attrLevel, ApAttrMutexInfoWithInd attrMutexInfo) {
		bizlog.method(" ApAttributeMnt.modifyAttrInfo begin >>>>>>>>>>>>>>>>");
		BizUtil.fieldNotNull(attrLevel, SysDict.A.attr_level.getId(), SysDict.A.attr_level.getLongName());

		int i = 0;

		i += modifyAttrMutex(attrLevel, attrMutexInfo); // 属性互斥信息维护

		if (i == 0) {
			// throw
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_attribute.class).getLongname());
		}

		bizlog.method(" ApAttributeMnt.modifyAttrInfo end <<<<<<<<<<<<<<<<");
	}

	/*
	 * 属性互斥信息维护
	 */
	private static int modifyAttrMutex(E_OWNERLEVEL attrLevel, ApAttrMutexInfoWithInd attrMutexInfo) {
		bizlog.method(" ApAttributeMnt.modifyAttrMutex begin >>>>>>>>>>>>>>>>");

		int i = 0;

		validAttrMutexInfo(attrMutexInfo);
		E_DATAOPERATE operater = attrMutexInfo.getOperater_ind();

		if (operater == E_DATAOPERATE.ADD) {
			addAttrMutexSingle(attrLevel, attrMutexInfo);
			i++;
		}
		else if (operater == E_DATAOPERATE.MODIFY) {
			i += modifyAttrMutexSingle(attrLevel, attrMutexInfo);
		}
		else if (operater == E_DATAOPERATE.DELETE) {
			App_attribute_mutexDao.deleteOne_odb1(attrMutexInfo.getAttr_mutex_id());
			i++;
		}
		
		bizlog.method(" ApAttributeMnt.modifyAttrMutex end <<<<<<<<<<<<<<<<");
		return i;
	}


	private static int modifyAttrMutexSingle(E_OWNERLEVEL attrLevel, ApAttrMutexInfoWithInd attrMutex) {
		BizUtil.fieldNotNull(attrMutex.getData_version(), SysDict.A.data_version.getId(), SysDict.A.data_version.getLongName());
		
		String mutexId = attrMutex.getAttr_mutex_id();
		
		app_attribute_mutex attrMutexInfo = App_attribute_mutexDao.selectOne_odb1(mutexId,  false);
		if(attrMutexInfo == null){
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_attribute.class).getLongname(), 
					SysDict.A.attr_mutex_id.getLongName(), mutexId);
		}
		
		if (CommUtil.compare(attrMutexInfo.getData_version(), attrMutex.getData_version()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_attribute_mutex.class).getLongname());
		}
		
		app_attribute_mutex newAttrMutexInfo = BizUtil.clone(app_attribute_mutex.class, attrMutexInfo);// 克隆OldObject，防止公共字段丢失
		newAttrMutexInfo.setAttr_mutex_id(mutexId);  //属性互斥ID
		newAttrMutexInfo.setAttr_mutex_desc(attrMutex.getAttr_mutex_desc());  //属性互斥描述
		newAttrMutexInfo.setAttr_level(attrLevel);  //属性层级
		newAttrMutexInfo.setMapping_expression(attrMutex.getMapping_expression());  //属性匹配表达式
		newAttrMutexInfo.setShow_error_info(attrMutex.getShow_error_info());  //错误异常展示信息

		// 先登记审计,如果没有字段修改，需要抛错
		int i = ApDataAuditApi.regLogOnUpdateParameter(attrMutexInfo, newAttrMutexInfo);

		App_attribute_mutexDao.updateOne_odb1(newAttrMutexInfo);
		
		return i;
	}

	private static void addAttrMutexSingle(E_OWNERLEVEL attrLevel, ApAttrMutexInfoWithInd attrMutex) {
        String mutexId = attrMutex.getAttr_mutex_id();
		
        app_attribute_mutex attrMutexInfo = App_attribute_mutexDao.selectOne_odb1(mutexId, false);
		if(attrMutexInfo != null){
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_attribute_mutex.class).getLongname(), mutexId);
		}
		
		attrMutexInfo = BizUtil.getInstance(app_attribute_mutex.class);
		attrMutexInfo.setAttr_mutex_id(mutexId);  //属性互斥ID
		attrMutexInfo.setAttr_mutex_desc(attrMutex.getAttr_mutex_desc());  //属性互斥描述
		attrMutexInfo.setAttr_level(attrLevel);  //属性层级
		attrMutexInfo.setMapping_expression(attrMutex.getMapping_expression());  //属性匹配表达式
		attrMutexInfo.setShow_error_info(attrMutex.getShow_error_info());  //错误异常展示信息
		
		App_attribute_mutexDao.insert(attrMutexInfo);
		
		ApDataAuditApi.regLogOnInsertParameter(attrMutexInfo);
	}
	
	private static void validAttrMutexInfo(ApAttrMutexInfoWithInd attrMutex) {
		BizUtil.fieldNotNull(attrMutex.getAttr_mutex_id(), SysDict.A.attr_mutex_id.getId(), SysDict.A.attr_mutex_id.getLongName());
		BizUtil.fieldNotNull(attrMutex.getAttr_mutex_desc(), SysDict.A.attr_mutex_desc.getId(), SysDict.A.attr_mutex_desc.getLongName());
		BizUtil.fieldNotNull(attrMutex.getMapping_expression(), SysDict.A.mapping_expression.getId(), SysDict.A.mapping_expression.getLongName());
		BizUtil.fieldNotNull(attrMutex.getShow_error_info(), SysDict.A.show_error_info.getId(), SysDict.A.show_error_info.getLongName());
	}


	/*
	 * 属性信息维护
	 */
	private static int modifyAttr(E_OWNERLEVEL attrLevel, ApAttrInfoWithInd attrInfo) {
		bizlog.method(" ApAttributeMnt.modifyAttr begin >>>>>>>>>>>>>>>>");

		int i = 0;		
		validAttrInfo(attrInfo);
		E_DATAOPERATE operater = attrInfo.getOperater_ind();
		if (operater == E_DATAOPERATE.ADD) {
			addAttrSingle(attrLevel, attrInfo);
			i++;
		}
		else if (operater == E_DATAOPERATE.MODIFY) {
			BizUtil.fieldNotNull(attrInfo.getAttr_position(), SysDict.A.attr_position.getId(), SysDict.A.attr_position.getLongName());
			i += modifyAttrSingle(attrLevel, attrInfo);
		}
		else {
			BizUtil.fieldNotNull(attrInfo.getAttr_position(), SysDict.A.attr_position.getId(), SysDict.A.attr_position.getLongName());
			App_attributeDao.deleteOne_odb1(attrLevel, attrInfo.getAttr_position());
			i++;
		}
		
		bizlog.method(" ApAttributeMnt.modifyAttr end <<<<<<<<<<<<<<<<");
		
		return i;
	}

	private static int modifyAttrSingle(E_OWNERLEVEL attrLevel, ApAttrInfoWithInd attr) {		
		BizUtil.fieldNotNull(attr.getData_version(), SysDict.A.data_version.getId(), SysDict.A.data_version.getLongName());
		
		Long position = attr.getAttr_position();
		
		app_attribute attrInfo = App_attributeDao.selectOne_odb1(attrLevel, position, false);
		if(attrInfo == null){
			throw ApPubErr.APPUB.E0024(OdbFactory.getTable(app_attribute.class).getLongname(), 
					SysDict.A.attr_level.getLongName(), attrLevel.getValue(),
					SysDict.A.attr_position.getLongName(),position.toString());
		}
		
		if (CommUtil.compare(attrInfo.getData_version(), attr.getData_version()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_attribute.class).getLongname());
		}
		List<MspDropList> dropList=MspDropListDao.selectAll_odb2(MsOrg.getReferenceOrgId(MspDropList.class), attr.getRef_drop_list(), false);
		if(dropList==null){
			throw ApErr.AP.E0065();
		}else{
			for (MspDropList dropListOne : dropList) {
				String dropValue=dropListOne.getDrop_list_value();
				if(dropValue.length()!=1){
					throw ApErr.AP.E0064();
				}
			}
		}
		app_attribute newAttrInfo = BizUtil.clone(app_attribute.class, attrInfo);// 克隆OldObject，防止公共字段丢失
		newAttrInfo.setAttr_level(attrLevel);  //属性层级
		newAttrInfo.setAttr_position(attr.getAttr_position());  //属性位置
		newAttrInfo.setAttr_desc(attr.getAttr_desc());  //属性位描述
		newAttrInfo.setAttr_expiry_ind(attr.getAttr_expiry_ind());  //属性位到期标志
		newAttrInfo.setRef_drop_list(attr.getRef_drop_list());  //引用下拉字典
		newAttrInfo.setDefault_value(attr.getDefault_value());  //缺省值

		// 先登记审计,如果没有字段修改，需要抛错
		int i = ApDataAuditApi.regLogOnUpdateParameter(attrInfo, newAttrInfo);
		App_attributeDao.updateOne_odb1(newAttrInfo);
		
		return i;
	}

	private static void addAttrSingle(E_OWNERLEVEL attrLevel, ApAttrInfoWithInd attr) {
		Long position = attr.getAttr_position();
		
		app_attribute attrInfo = App_attributeDao.selectOne_odb1(attrLevel, position, false);
		if(attrInfo != null){
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_attribute.class).getLongname(), attrLevel+ApConst.KEY_CONNECTOR+position);
		}
		List<MspDropList> dropList=MspDropListDao.selectAll_odb2(MsOrg.getReferenceOrgId(MspDropList.class), attr.getRef_drop_list(), false);
		if(dropList==null){
			throw ApErr.AP.E0065();
		}else{
			for (MspDropList dropListOne : dropList) {
				String dropValue=dropListOne.getDrop_list_value();
				if(dropValue.length()!=1){
					throw ApErr.AP.E0064();
				}
			}
		}
		long count=ApAttributeDao.selAttrDfeCount(attrLevel, false);
		attrInfo = BizUtil.getInstance(app_attribute.class);
		attrInfo.setAttr_level(attrLevel); // 属性层级
		attrInfo.setAttr_position(count+1); // 属性位置
		attrInfo.setAttr_desc(attr.getAttr_desc()); // 属性描述
		attrInfo.setAttr_expiry_ind(attr.getAttr_expiry_ind()); // 属性位到期标志
		attrInfo.setRef_drop_list(attr.getRef_drop_list()); // 引用下拉字典
		attrInfo.setDefault_value(attr.getDefault_value()); // 缺省值
		
		App_attributeDao.insert(attrInfo);
		
		ApDataAuditApi.regLogOnInsertParameter(attrInfo);
		
	}

	private static void validAttrInfo(ApAttrInfoWithInd attr) {
		BizUtil.fieldNotNull(attr.getAttr_desc(), SysDict.A.attr_desc.getId(), SysDict.A.attr_desc.getLongName());
		BizUtil.fieldNotNull(attr.getAttr_expiry_ind(), SysDict.A.attr_expiry_ind.getId(), SysDict.A.attr_expiry_ind.getLongName());
		BizUtil.fieldNotNull(attr.getRef_drop_list(), SysDict.A.ref_drop_list.getId(), SysDict.A.ref_drop_list.getLongName());
		BizUtil.fieldNotNull(attr.getDefault_value(), SysDict.A.default_value.getId(), SysDict.A.default_value.getLongName());
		
	}
	
	
}
