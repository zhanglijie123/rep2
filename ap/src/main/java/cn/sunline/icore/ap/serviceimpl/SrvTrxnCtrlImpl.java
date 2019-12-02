
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.trxnctrl.ApTrxnCtrlMnt;

 /**
  * SrvTrxnCtrlImpl
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvTrxnCtrlImpl", longname="SrvTrxnCtrlImpl", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvTrxnCtrlImpl implements cn.sunline.icore.ap.servicetype.SrvApTrxnCtrl{
 /**
  * query transaction info
  *
  */
	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApBasic.ApTrxnControl> queryTrxnControlInfo(final cn.sunline.icore.ap.type.ComApBasic.ApTrxnCtrlInput queryIn){
		return ApTrxnCtrlMnt.queryTrxnControlInfo(queryIn);
		
	}

	public void mntTrxnCtrl( final cn.sunline.icore.ap.type.ComApBasic.ApTrxnCtrlMntInfo modifyIn){
		ApTrxnCtrlMnt.mntTrxnCtrl(modifyIn);
	}
}

