
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.trxnctrl.ApTrxnCtrlMnt;

 /**
  * scene auth service
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApSceneAuthImpl", longname="scene auth service", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApSceneAuthImpl implements cn.sunline.icore.ap.servicetype.SrvApSceneAuth{
 /**
  * querySceneAuth
  *
  */
	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApBasic.ApSceneAuthInfo> querySceneAuthInfo(final cn.sunline.icore.ap.type.ComApBasic.ApSceneAuthInfo queryIn){
		return ApTrxnCtrlMnt.querySceneAuthInfo(queryIn);
	}
 /**
  * mntmntSceneAuthInfo
  *
  */
	public void mntmntSceneAuthInfo(final cn.sunline.icore.ap.type.ComApBasic.ApSceneAuthInfo modifyIn, final cn.sunline.icore.ap.servicetype.SrvApSceneAuth.mntSceneAuth.Output output){
		ApTrxnCtrlMnt.mntmntSceneAuthInfo(modifyIn);
	}
}

