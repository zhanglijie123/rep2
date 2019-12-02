package cn.sunline.icore.ap.callback;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.core.spi.MsPointExtensionSPI;
import cn.sunline.clwj.msap.core.type.MsCoreComplexType.MsDateInfo;
import cn.sunline.icore.ap.api.ApDateApi;
import cn.sunline.icore.ap.type.ComApSystem.ApDateInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;

public class MsPointExtensionSPIImpl implements MsPointExtensionSPI{

	@Override
	public String getCenterOrgId() {
		return MsOrg.getDefaultOrgId();
	}

	@Override
	public String getBrchnoByUserid(String userid) {
		
		return BizUtil.getTrxRunEnvs().getTrxn_branch();
	}

	@Override
	public MsDateInfo getDateInfo(String orgId) {
		ApDateInfo apDateInfo = ApDateApi.getInfo(orgId);
		MsDateInfo msDateInfo = BizUtil.getInstance(MsDateInfo.class);
		
		msDateInfo.setTrxn_date(apDateInfo.getTrxn_date());
		msDateInfo.setLast_date(apDateInfo.getLast_date());
		msDateInfo.setNext_date(apDateInfo.getNext_date());
		msDateInfo.setBal_sheet_date(apDateInfo.getBal_sheet_date());
		
		return msDateInfo;
	}

	@Override
	public void registerBusiTransaction() {
		
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
//		if (ApChannelApi.isCounter(runEnvs.getChannel_id())){
//			// 柜面渠道需要登记柜员流水
//			SrvIoCtBasic counterSrv = BizUtil.getInstance(SrvIoCtBasic.class);
//			
//			IoCtTransactionInfo inputInfo = BizUtil.getInstance(IoCtTransactionInfo.class);
//			
//			inputInfo.setCash_trxn_ind(runEnvs.getFinance_info().getCash_trxn_ind());
//			inputInfo.setCounterparty_acct_na(runEnvs.getFinance_info().getCounterparty_acct_na());
//			inputInfo.setCounterparty_acct_no(runEnvs.getFinance_info().getCounterparty_acct_no());
//			inputInfo.setCustomer_remark(runEnvs.getFinance_info().getCustomer_remark());
//			inputInfo.setTrxn_ccy(runEnvs.getFinance_info().getCcy_code());
//			inputInfo.setTrxn_amt(runEnvs.getFinance_info().getTrxn_amt());
//			inputInfo.setDebit_credit(runEnvs.getFinance_info().getDebit_credit());
//			inputInfo.setTrxn_acct_name(runEnvs.getFinance_info().getTrxn_acct_name());
//			inputInfo.setTrxn_acct_no(runEnvs.getFinance_info().getTrxn_acct_no());
//			inputInfo.setTrxn_remark(runEnvs.getFinance_info().getTrxn_remark());
//			
//			counterSrv.registerCtsTransaction(inputInfo);
//		}
	}
}
