
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.attr.mnt.ApAttributeMnt;
import cn.sunline.icore.ap.type.ComApAttr.ApAttrInfoWithInd;
import cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL;


 /**
  * attribute information maintenance
  * 属性信息维护
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApAttrImpl", longname="attribute information maintenance", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApAttrImpl implements cn.sunline.icore.ap.servicetype.SrvApAttr{
 /**
  * query attribute information
  *
  */
	public cn.sunline.icore.ap.type.ComApAttr.ApAttrInfoResult queryAttrInfo(cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL attr_level){
		return ApAttributeMnt.queryAttrInfo(attr_level);
	}
 /**
  * maintenance attribute information
  *
  */
	@Override
	public void modifyAttrInfo(E_OWNERLEVEL attr_level, ApAttrInfoWithInd attrInfo) {
		ApAttributeMnt.modifyAttrInfo(attr_level, attrInfo);
		
	}
	public void modifyAttrMutexInfo( cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL attr_level,  final cn.sunline.icore.ap.type.ComApAttr.ApAttrMutexInfoWithInd attrMetx){
		ApAttributeMnt.modifyAttrMutexInfo(attr_level, attrMetx);
	}

	
}

