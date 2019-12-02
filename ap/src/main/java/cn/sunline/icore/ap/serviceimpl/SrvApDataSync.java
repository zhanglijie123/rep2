package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.util.DBUtil;

 /**
  * data sync
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApDataSync", longname="data sync", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApDataSync implements cn.sunline.icore.ap.servicetype.SrvApDataSync{
 /**
  * execute dml sql
  *
  */
	public void execDml(cn.sunline.ltts.core.api.model.dm.Options<String> dataOperateDml){
		for(String dml : dataOperateDml){
			DBUtil.executeDML(dml);
		}
	}
}

