
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.api.ApTrxnApi;
import cn.sunline.icore.ap.parm.mnt.ApTrxnMnt;
import cn.sunline.icore.ap.report.mnt.ApReportMnt;


 /**
  * transaction flow
  * 交易流水服务实现
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApTrxnImpl", longname="transaction flow", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApTrxnImpl implements cn.sunline.icore.ap.servicetype.SrvApTrxn{
 /**
  * registration transaction flow
  *
  */
	public void registerApsTransaction( final cn.sunline.icore.ap.type.ComApBook.ApTransactionInfo trxn_info){
		ApTrxnApi.registerTrxn(trxn_info);		
	}

 /**
  * query report flow
  *
  */
	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApBook.ApQueryReportOut> searchReport( String trxDate,  String branchId,  String reportName, String reportId){
		return ApReportMnt.searchReport(trxDate, branchId, reportName, reportId);
	}
	
	/**
	 * generate transaction flow reconciliation 
	 */
    public cn.sunline.icore.ap.type.ComApBook.ApTransReconOut genTransReconFile( final cn.sunline.icore.ap.type.ComApBook.ApTransReconIn trxnIn){
    	
    	return  ApTrxnMnt.genTransReconFile(trxnIn);
    }
}

