
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.parm.mnt.ApIdentityMnt;
import cn.sunline.icore.ap.type.ComApBasic.ApIdentityInfo;
import cn.sunline.ltts.core.api.model.dm.Options;


 /**
  * identity type parameter maintenance
  * 证件种类参数维护
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApIdentityImpl", longname="identity type parameter maintenance", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApIdentityImpl implements cn.sunline.icore.ap.servicetype.SrvApIdentity{
 /**
  * query the list of identity type parameter
  *
  */
	public Options<ApIdentityInfo> queryIdentityList(String idType,  String idDesc){		
		return ApIdentityMnt.queryIdentityList(idType, idDesc);		
	}
}

