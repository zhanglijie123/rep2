
package cn.sunline.icore.ap.report.trans;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.icore.ap.tables.TabApSystem.app_date;
import cn.sunline.icore.ap.type.ComApReport.ApTranFlowReportOut;
import cn.sunline.icore.cbs.ap.namedsql.ApReportDao;
import cn.sunline.ltts.dao.Params;
import cn.sunline.ltts.report.LttsReportQueryExecuter;


	 /**
	  * Transaction Flow Report
	  *
	  */
public class ap1110TranFlowReportReportProcessor implements cn.sunline.ltts.report.LttsReportDataProcessor<cn.sunline.icore.ap.type.ComApReport.ApTranFlowReportOut, cn.sunline.icore.ap.report.intf.Ap1110TranFlowReport.Input, cn.sunline.icore.ap.type.ComApReport.ApTileOfTranFlow> {
	
	@Override
	public LttsReportQueryExecuter<cn.sunline.icore.ap.type.ComApReport.ApTranFlowReportOut> getMainDataQueryExecuter(cn.sunline.icore.ap.report.intf.Ap1110TranFlowReport.Input input) {
		//实现主数据集合获取
		
		Params para = new Params();
		para.add("org_id", MsOrg.getReferenceOrgId(app_date.class));
		para.add("trxn_seq",input.getTrxn_seq());
		para.add("trxn_date",input.getTrxn_date());
		para.add("busi_seq",input.getBusi_seq());
		para.add("channel_id",input.getChannel_id());
		para.add("trxn_teller",input.getTrxn_teller());
		para.add("branch_id",input.getBranch_id());
		para.add("trxn_code",input.getTrxn_code());
		para.add("recon_code",input.getRecon_code());
		para.add("cash_trxn_ind",input.getCash_trxn_ind());
		para.add("trxn_ccy",input.getTrxn_ccy());
		para.add("trxn_amt",input.getTrxn_amt());
		para.add("trxn_acbt_no",input.getTrxn_acct_no());
		para.add("trxn_status",input.getTrxn_status());
		
		return new LttsReportQueryExecuter<ApTranFlowReportOut>(ApReportDao.namedsql_selTrxnListReport,para);
	}
	@Override
	public boolean processSingleData(int index, cn.sunline.icore.ap.type.ComApReport.ApTranFlowReportOut item){
		return true;
	}
	
	@Override
	public cn.sunline.icore.ap.type.ComApReport.ApTileOfTranFlow getAssistentData(cn.sunline.icore.ap.report.intf.Ap1110TranFlowReport.Input input) {
		//实现辅助数据获取
		return null;
	}

}

