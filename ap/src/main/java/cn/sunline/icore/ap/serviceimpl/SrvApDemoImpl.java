package cn.sunline.icore.ap.serviceimpl;

import java.math.BigDecimal;

import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.icore.ap.api.ApReversalApi;
import cn.sunline.icore.ap.servicetype.SrvApDemo;
import cn.sunline.icore.sys.parm.TrxEnvs;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * 示例服务
 * 
 */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value = "SrvApDemoImpl", longname = "demo service", type = cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApDemoImpl implements
		cn.sunline.icore.ap.servicetype.SrvApDemo {
	/**
	 * 示例服务测试
	 * 
	 */
	private static final BizLog bizlog = BizLogUtil.getBizLog(SrvApDemoImpl.class);

	/**
	 * 示例服务测试
	 */
	public cn.sunline.icore.ap.servicetype.SrvApDemo.test.Output test(
			String remark) {
		bizlog.debug("input information[%s]", remark);

		TrxEnvs.RunEnvs envs = SysUtil.getTrxRunEnvs();
		SrvApDemo.test.Output output = SysUtil
				.getInstance(SrvApDemo.test.Output.class);
		output.setRemark(remark + ":" + envs.getBusi_org_id());

		bizlog.debug("input information[%s]", output.getRemark());
		return output;
	}
	
	/**
	 * 增加客户账
	 */
	public void addCustomerMoney( final cn.sunline.icore.ap.type.ComApDemo.DemoTranInfo tranInfo){
		//假设客户当前存款10000
		BigDecimal curAmt = new BigDecimal(10000);
		if (MsEnumType.E_DEBITCREDIT.DEBIT  == tranInfo.getDebit_credit()) {//存款
			bizlog.debug("account number[%s]increased[%s],currency[%s]", tranInfo.getTrxn_acct_no(),tranInfo.getTrxn_amt().toString(),tranInfo.getTrxn_ccy());
		} else {
			//取款
			if(BigDecimal.ZERO.compareTo(curAmt.subtract(tranInfo.getTrxn_amt())) == 1) {
				throw new LttsServiceException("9999", "insufficient deposit");
			}
			bizlog.debug("account number[%s]reduced[%s],currency[%s]", tranInfo.getTrxn_acct_no(),tranInfo.getTrxn_amt().toString(),tranInfo.getTrxn_ccy());
		}
		
		//登记冲正事件
		ApReversalApi.register("addCustomerMoney", tranInfo);
	}

	public void addCustomerMoneyReverse( final cn.sunline.icore.ap.type.ComApDemo.DemoTranInfo tranInfo){
		//假设客户当前存款10000
		BigDecimal curAmt = new BigDecimal(10000);
		if (MsEnumType.E_DEBITCREDIT.DEBIT  == tranInfo.getDebit_credit()) {//存款
			//原来是存款交易，冲正时减少客户存款额
			if(BigDecimal.ZERO.compareTo(curAmt.subtract(tranInfo.getTrxn_amt())) == 1) {
				throw new LttsServiceException("9999", "insufficient deposit");
			}
			bizlog.debug("account number[%s]reduced[%s],currency[%s]", tranInfo.getTrxn_acct_no(),tranInfo.getTrxn_amt().toString(),tranInfo.getTrxn_ccy());
		} else {
			//原来是取款交易，冲正时增加客户存款额
			bizlog.debug("account number[%s]increased[%s],currency[%s]", tranInfo.getTrxn_acct_no(),tranInfo.getTrxn_amt().toString(),tranInfo.getTrxn_ccy());
		}
	}

	public void addBankMoney( final cn.sunline.icore.ap.servicetype.SrvApDemo.addBankMoney.Input input){
		//假设银行当前存款100000000
		BigDecimal curAmt = new BigDecimal(100000000);
		if (MsEnumType.E_DEBITCREDIT.DEBIT  == input.getDebit_credit()) {//存款
			bizlog.debug("bank deposits increased[%s],currency[%s]", input.getTrxn_amt().toString(),input.getTrxn_ccy());
		} else {
			//取款
			if(BigDecimal.ZERO.compareTo(curAmt.subtract(input.getTrxn_amt())) == 1) {
				throw new LttsServiceException("9999", "insufficient deposit");
			}
			bizlog.debug("bank deposits reduced[%s],currency[%s]", input.getTrxn_amt().toString(),input.getTrxn_ccy());
		}
		//登记冲正事件
		ApReversalApi.register("addBankMoney", input);
	}

	public void addBankMoneyReverse( final cn.sunline.icore.ap.servicetype.SrvApDemo.addBankMoneyReverse.Input input){
		//假设银行当前存款10000
		BigDecimal curAmt = new BigDecimal(100000000);
		if (MsEnumType.E_DEBITCREDIT.DEBIT  == input.getDebit_credit()) {//存款
			//原来是存款交易，冲正时减少客户存款额
			if(BigDecimal.ZERO.compareTo(curAmt.subtract(input.getTrxn_amt())) == 1) {
				throw new LttsServiceException("9999", "insufficient deposit");
			}
			bizlog.debug("bank deposits reduced[%s],currency[%s]", input.getTrxn_amt().toString(),input.getTrxn_ccy());
		} else {
			//原来是取款交易，冲正时增加客户存款额
			bizlog.debug("bank deposits increased[%s],currency[%s]", input.getTrxn_amt().toString(),input.getTrxn_ccy());
		}
	}

}
