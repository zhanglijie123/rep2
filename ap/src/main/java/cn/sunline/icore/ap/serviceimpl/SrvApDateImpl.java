package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.api.ApDateApi;
import cn.sunline.icore.ap.api.ApHolidayApi;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
 /**
  * system date service
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApDateImpl", longname="system date service", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApDateImpl implements cn.sunline.icore.ap.servicetype.SrvApDate{
 /**
  * query system date
  *
  */
	public void querySystemDate(final cn.sunline.icore.ap.servicetype.SrvApDate.querySystemDate.Output output){
		output.setResult(ApDateApi.getInfo(BizUtil.getTrxRunEnvs().getBusi_org_id()));
	}

	/**
	 * query next work date
	 */
	public void queryNextWorkDate( String trxnDate,  String branchId,  cn.sunline.icore.sys.type.EnumType.E_CUSTOMERTYPE custType,  final cn.sunline.icore.ap.servicetype.SrvApDate.queryNextWorkDate.Output output){
		BizUtil.fieldNotNull(trxnDate, SysDict.A.trxn_date.getId(), SysDict.A.trxn_date.getLongName());
		BizUtil.fieldNotNull(branchId, SysDict.A.branch_id.getId(), SysDict.A.branch_id.getLongName());
		
		output.setNext_date(ApHolidayApi.getNextWorkerDay(trxnDate, custType, 1, branchId));
	}
	/**
     * query account due date
     */
	public void queryDueDate( String start_inst_date,  String term_code,  final cn.sunline.icore.ap.servicetype.SrvApDate.queryDueDate.Output output){
		BizUtil.fieldNotNull(start_inst_date, SysDict.A.start_inst_date.getId(), SysDict.A.start_inst_date.getLongName());
		BizUtil.fieldNotNull(term_code, SysDict.A.term_code.getId(), SysDict.A.term_code.getLongName());
		output.setDue_date(BizUtil.calcDateByCycle(start_inst_date, term_code));
	}
}

