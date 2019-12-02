
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.api.ApAccountApi;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEventIn;
import cn.sunline.ltts.core.api.model.dm.Options;

/**
  * internal accounts accounting service
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApTaAccoutingImpl", longname="internal accounts accounting service", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApAccoutingImpl implements cn.sunline.icore.ap.servicetype.SrvApAccouting{
 /**
  * checking balance
  *
  */
	public void checkBalance(){
		ApAccountApi.checkBalance();
	}
 /**
  * registering accounting events
  * 会计事件登记服务
  *
  */
	public void regAccountingEvent( final ApAccountingEventIn ioAccountingEventIn){
		ApAccountApi.regAccountingEvent(ioAccountingEventIn);
	}
 /**
  * registering accrue
  * 利息计提汇总登记服务
  *
  */
	public void regAccure( final cn.sunline.icore.ap.type.ComApAccounting.ApRecordAccure input){

		ApAccountApi.regAccure(input);
	}
 /**
  * registering ledger balance
  * 分户账余额登记服务
  *
  */
	public void regLedgerBal( final cn.sunline.icore.ap.type.ComApAccounting.ApRegLedgerBal input){
		ApAccountApi.regLedgerBal(input);
	}


	/**
	 * 
	 * @Title: selAliasInfos 
	 * @author zhangwh
	 * @Description: 查询核算别名列表信息
	 * @param accounting_alias
	 * @param aliasInfo 
	 * @date 2019年9月3日 下午8:21:09
	 */

	public Options<cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEventCtrl> selAliasInfos( String accounting_alias){
		return ApAccountApi.getAliasInfos(accounting_alias);
	}
	
	/**
	 * 
	 * @Title: getAccountingEvent 
	 * @author zhangwh
	 * @Description: 查询会计流水交易
	 * @param trxn_seq
	 * @return 
	 * @date 2019年9月23日 下午3:48:14
	 */
	public Options<cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEvent> getAccountingEvent( String trxn_seq){
		return ApAccountApi.getAccountingEvent(trxn_seq);
	}
	
	/**
	 * 
	 * @Title: maintainingGlParmCtrl 
	 * @author zhangwh
	 * @Description: 维护总账科目参数
	 * @param input 
	 * @date 2019年10月8日 上午10:55:02
	 */
	public void maintainingGlParmCtrl( final cn.sunline.icore.ap.type.ComApAccounting.ApGlParaCtrl input){
		ApAccountApi.maintainingGlParmCtrl(input);
	}
	
	/**
	 * 
	 * @Title: qryGlParmCtrl 
	 * @author zhangwh
	 * @Description: 查询总账科目参数 
	 * @param accounting_alias
	 * @param bal_attributes
	 * @param errer_ctrl_ind
	 * @return 
	 * @date 2019年10月8日 下午2:23:17
	 */
	public cn.sunline.icore.ap.type.ComApAccounting.ApGlParaCtrl qryGlParmCtrl( String accounting_alias,  String bal_attributes,  cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO errer_ctrl_ind){
		return ApAccountApi.qryGlParmCtrl(accounting_alias,bal_attributes,errer_ctrl_ind);
	}
	
}

