
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.rule.mnt.ApRuleMnt;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleFunc;
import cn.sunline.ltts.core.api.model.dm.Options;

 /**
  * function and paramter query
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApFuncAndParmImpl", longname="function and paramter query", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApFuncAndParmImpl implements cn.sunline.icore.ap.servicetype.SrvApFuncAndParm{

	@Override
	public Options<ApRuleFunc> queryInformation(String func_name){
		return ApRuleMnt.queryInformation(func_name);	
	}






}

