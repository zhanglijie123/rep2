
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.parm.mnt.ApCurrencyMnt;
import cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfo;
import cn.sunline.ltts.core.api.model.dm.Options;


 /**
  * currency parameter maintenance
  * 货币参数维护
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApCurrencyImpl", longname="currency parameter maintenance", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApCurrencyImpl implements cn.sunline.icore.ap.servicetype.SrvApCurrency{
 /**
  * query the list of currency parameters
  *
  */
	public Options<ApCurrencyInfo> queryCurrencyList(String ccyCode,  String ccyName,  String countryCode){
		return ApCurrencyMnt.queryCurrencyList(ccyCode, ccyName, countryCode);
	}
	
	/**
	 * currency parameters maintanence
	 */
	public void mntCcyInfo( final cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfoMnt cplIn){
		ApCurrencyMnt.mntCurrencyInfo(cplIn);
	}

}

