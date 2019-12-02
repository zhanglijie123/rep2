
package cn.sunline.edsp.busi.ap.serviceimpl;

import cn.sunline.icore.ap.parm.ApBaseSummary;
import cn.sunline.icore.ap.type.ComApBasic.ApSummaryOptionalList;
import cn.sunline.ltts.core.api.model.dm.Options;

/**
  * summary service
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApSummaryImpl", longname="summary service", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApSummaryImpl implements cn.sunline.edsp.busi.ap.servicetype.SrvApSummary{

	@Override
	public Options<ApSummaryOptionalList> queryTrxnOptionalSummayList(String target_trxn_code,String target_recon_code) {
		  return ApBaseSummary.queryTrxnOptionalSummayList(target_trxn_code, target_recon_code);
	}


}

