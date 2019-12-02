package cn.sunline.icore.ap.account;

import java.math.BigDecimal;
import java.util.List;

import cn.sunline.ap.tables.TabApBillInfo.ApsAccrueRecord;
import cn.sunline.ap.tables.TabApBillInfo.ApsAccrueRecordDao;
import cn.sunline.ap.tables.TabApBillInfo.ApsLedgerBal;
import cn.sunline.ap.tables.TabApBillInfo.ApsLedgerBalDao;
import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApBusinessParmApi;
import cn.sunline.icore.ap.api.ApCurrencyApi;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.api.ApDataSyncApi;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.ApSystemParmApi;
import cn.sunline.icore.ap.api.ApTrxnApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.namedsql.ApAccountingBaseDao;
import cn.sunline.icore.ap.tables.TabApAccounting.AppAccountingeventCtrl;
import cn.sunline.icore.ap.tables.TabApAccounting.AppAccountingeventCtrlDao;
import cn.sunline.icore.ap.tables.TabApAccounting.App_accounting_event_parmDao;
import cn.sunline.icore.ap.tables.TabApAccounting.ApsAccountingEvent;
import cn.sunline.icore.ap.tables.TabApAccounting.ApsAccountingEventDao;
import cn.sunline.icore.ap.tables.TabApAccounting.app_accounting_event_parm;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEvent;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEventCtrl;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEventIn;
import cn.sunline.icore.ap.type.ComApAccounting.ApGlParaCtrl;
import cn.sunline.icore.ap.type.ComApAccounting.ApRecordAccure;
import cn.sunline.icore.ap.type.ComApAccounting.ApRegLedgerBal;
import cn.sunline.icore.ap.type.ComApAccounting.ApTrxnNoBalancedData;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.GlEntryInfo;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_ACCOUNTINGSUBJECT;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

public class ApAccounting {
	
	/**余额属性main key**/
	public static final String BAL_ATTRIBUTE = "BAL_ATTRIBUTE"; 
	/**表外余额属性sub key**/
	public static final String OFF_BALANCE = "OFF_BALANCE"; 
	/**内部户余额属性sub key**/
	public static final String INSIDE = "INSIDE"; 
	/**核算参数检查机关 **/
	public static final String TA_ACCOUNTING_CHECK_IND = "TA_ACCOUNTING_CHECK_IND";
	/** 会计事件数据拆分散列值subKey */
	public static final String TA_ACCOUNTINGEVENT = "TA_ACCOUNTINGEVENT";
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApAccounting.class);
	/**
	 * @Author ThinkPad
	 *         <p>
	 *         <li>2017年1月19日-下午2:21:37</li>
	 *         <li>功能说明：登记会计事件</li>
	 *         </p>
	 * @param srvInput
	 * @param acctInfo
	 * @return
	 */
	public static void regAccountingEvent(ApAccountingEventIn srvInput) {

		// 交易运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		BizUtil.fieldNotNull(srvInput.getDebit_credit(), SysDict.A.debit_credit.getId(), SysDict.A.debit_credit.getLongName());
		BizUtil.fieldNotNull(srvInput.getAccounting_alias(), SysDict.A.accounting_alias.getId(), SysDict.A.accounting_alias.getLongName());
		BizUtil.fieldNotNull(srvInput.getAccounting_subject(), SysDict.A.accounting_subject.getId(), SysDict.A.accounting_subject.getLongName());
		BizUtil.fieldNotNull(srvInput.getDouble_entry_ind(), SysDict.A.double_entry_ind.getId(), SysDict.A.double_entry_ind.getLongName());
		BizUtil.fieldNotNull(srvInput.getTrxn_ccy(), SysDict.A.trxn_ccy.getId(), SysDict.A.trxn_ccy.getLongName());
		BizUtil.fieldNotNull(srvInput.getAcct_branch(), SysDict.A.acct_branch.getId(), SysDict.A.acct_branch.getLongName());
		BizUtil.fieldNotNull(srvInput.getAcct_no(), SysDict.A.acct_no.getId(), SysDict.A.acct_no.getLongName());
		BizUtil.fieldNotNull(srvInput.getTrxn_amt(), SysDict.A.trxn_amt.getId(), SysDict.A.trxn_amt.getLongName());

		// 如果是表外账户记账，则取默认余额属性
		if (srvInput.getDouble_entry_ind() == E_YESORNO.NO && CommUtil.isNull(srvInput.getBal_attributes())) {

			String balAttribute = ApBusinessParmApi.getValue(BAL_ATTRIBUTE, OFF_BALANCE);
			srvInput.setBal_attributes(balAttribute);
		}

		// 如果是内部户，则余额属性取默认值
		if (srvInput.getAccounting_subject() == E_ACCOUNTINGSUBJECT.INSIDE && CommUtil.isNull(srvInput.getBal_attributes())) {
			String insideBal = ApBusinessParmApi.getValue(BAL_ATTRIBUTE, INSIDE);
			srvInput.setBal_attributes(insideBal);
		}

		BizUtil.fieldNotNull(srvInput.getBal_attributes(), SysDict.A.bal_attributes.getId(), SysDict.A.bal_attributes.getLongName());

		if (CommUtil.compare(srvInput.getTrxn_amt(), BigDecimal.ZERO) == 0) {
			throw ApBaseErr.ApBase.E0133();
		}

		//判断核算别名和余额属性是否在参数表中
		String checkInd = ApBusinessParmApi.getValue(TA_ACCOUNTING_CHECK_IND);
		if (checkInd.equals(E_YESORNO.YES.getValue())) {
			ApAccountingEventCtrl eventCom = BizUtil.getInstance(ApAccountingEventCtrl.class);

			eventCom.setAccounting_alias(srvInput.getAccounting_alias()); // 核算别名
			eventCom.setBal_attributes(srvInput.getBal_attributes()); // 余额属性
			AppAccountingeventCtrl eventCtrlTabl = AppAccountingeventCtrlDao.selectOne_odb1(eventCom.getAccounting_alias(), eventCom.getBal_attributes(), false);
			if (eventCtrlTabl == null) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppAccountingeventCtrl.class).getId(), SysDict.A.accounting_alias.getLongName() + '-' + SysDict.A.bal_attributes,
						eventCom.getAccounting_alias() + '-' + eventCom.getBal_attributes());
			}
		}
		
		ApCurrencyApi.chkAmountByCcy(srvInput.getTrxn_ccy(), srvInput.getTrxn_amt());

		ApsAccountingEvent acctEvent = BizUtil.getInstance(ApsAccountingEvent.class);

		acctEvent.setTrxn_seq(runEnvs.getTrxn_seq());
		acctEvent.setTrxn_code(runEnvs.getTrxn_code());
		acctEvent.setData_sort(ApTrxnApi.getAccountingEventSerial());

		if (CommUtil.isNotNull(srvInput.getTrxn_date())) {
			acctEvent.setTrxn_date(srvInput.getTrxn_date());
		}
		else {
			acctEvent.setTrxn_date(runEnvs.getTrxn_date());
		}
		
		acctEvent.setMain_trxn_seq(runEnvs.getMain_trxn_seq());
		acctEvent.setDouble_entry_ind(srvInput.getDouble_entry_ind());
		acctEvent.setDebit_credit(srvInput.getDebit_credit());
		acctEvent.setTrxn_ccy(srvInput.getTrxn_ccy());
		acctEvent.setTrxn_amt(srvInput.getTrxn_amt());
		acctEvent.setAcct_branch(srvInput.getAcct_branch());
		acctEvent.setAccounting_alias(srvInput.getAccounting_alias());
		acctEvent.setAccounting_subject(srvInput.getAccounting_subject());
		acctEvent.setBal_attributes(srvInput.getBal_attributes());
		acctEvent.setData_sync_ind(E_YESORNO.NO);
		acctEvent.setAcct_no(srvInput.getAcct_no());
		acctEvent.setSub_acct_seq(srvInput.getSub_acct_seq());
		acctEvent.setGl_ref_code(srvInput.getGl_ref_code());
		acctEvent.setSummary_code(srvInput.getSummary_code());
		acctEvent.setSummary_name(srvInput.getSummary_name());
		acctEvent.setProd_id(srvInput.getProd_id());
		acctEvent.setAccrue_type(srvInput.getAccrue_type());
		acctEvent.setOpp_acct_no(acctEvent.getOpp_acct_no());
		acctEvent.setOut_opp_acct_no(acctEvent.getOut_opp_acct_no());

		long hash_value = BizUtil.getGroupHashValue(TA_ACCOUNTINGEVENT, runEnvs.getTrxn_seq());
		acctEvent.setHash_value(hash_value);

		// 登记会计事件
		ApsAccountingEventDao.insert(acctEvent);
		
		// 添加到公共运行区
		GlEntryInfo glEntry = BizUtil.getInstance(GlEntryInfo.class);				
		glEntry.setDouble_entry_ind(acctEvent.getDouble_entry_ind());		
		glEntry.setAccounting_alias(acctEvent.getAccounting_alias());		
		glEntry.setBal_attributes(acctEvent.getBal_attributes());		
		glEntry.setDebit_credit(acctEvent.getDebit_credit());		
		glEntry.setTrxn_ccy(acctEvent.getTrxn_ccy());		
		glEntry.setTrxn_amt(acctEvent.getTrxn_amt());
		
		BizUtil.getTrxRunEnvs().getGl_entries().add(glEntry);
		
	}
	
	
	/**
	 * @Author ThinkPad
	 *         <p>
	 *         <li>2017年1月18日-下午2:44:10</li>
	 *         <li>功能说明：财务平衡检查</li>
	 *         </p>
	 * @param srvInputList
	 */
	public static void checkBalance() {

		// 交易运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Long eventSeq = runEnvs.getAccounting_event_seq();

		if (CommUtil.isNull(eventSeq) || CommUtil.compare(eventSeq, 1L) == 0)
			return; // 没有登记会计流水,直接退出

		String orgId = MsOrg.getReferenceOrgId(ApsAccountingEvent.class);
		String trxnSeq = runEnvs.getTrxn_seq();

		List<ApTrxnNoBalancedData> lstNoBalance = ApAccountingBaseDao.selTrxnNoBalancedData(trxnSeq, orgId, false);

		if (lstNoBalance.size() > 0) {

			bizlog.debug("trxnSeq[%s]，it failed to check the  balance of accounts ...", trxnSeq);

			List<ApsAccountingEvent> accountingEvent = ApsAccountingEventDao.selectAll_odb4(trxnSeq, false);

			bizlog.debug("information of accounting balance[%s]:", accountingEvent);

			for (ApTrxnNoBalancedData noBalance : lstNoBalance) {
				bizlog.debug(">>>>>>Ccy_code[%s], the margin is [%s]", noBalance.getCcy_code(), noBalance.getTrxn_amt());
			}

			throw ApBaseErr.ApBase.E0134(trxnSeq);
		}
				
	}
	
	/**
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年3月23日-上午10:03:18</li>
	 *         <li>功能说明：登记分户账余额</li>
	 *         </p>
	 * @param input
	 */
	public static void regLedgerBal(ApRegLedgerBal srvInput) {

		// 检查非空
		BizUtil.fieldNotNull(srvInput.getAcct_branch(), SysDict.A.acct_branch.getId(), SysDict.A.acct_branch.getLongName());
		BizUtil.fieldNotNull(srvInput.getAccounting_alias(), SysDict.A.accounting_alias.getId(), SysDict.A.accounting_alias.getLongName());
		BizUtil.fieldNotNull(srvInput.getAccounting_subject(), SysDict.A.accounting_subject.getId(), SysDict.A.accounting_subject.getLongName());
		BizUtil.fieldNotNull(srvInput.getAccounting_alias(), SysDict.A.accounting_alias.getId(), SysDict.A.accounting_alias.getLongName());
		BizUtil.fieldNotNull(srvInput.getBal_attributes(), SysDict.A.bal_attributes.getId(), SysDict.A.bal_attributes.getLongName());
		BizUtil.fieldNotNull(srvInput.getAcct_bal(), SysDict.A.acct_bal.getId(), SysDict.A.acct_bal.getLongName());

		// 判断核算别名和余额属性是否在参数表中

		String checkInd = ApBusinessParmApi.getValue(TA_ACCOUNTING_CHECK_IND);
		if (checkInd.equals(E_YESORNO.YES.getValue())) {
			ApAccountingEventCtrl eventCom = BizUtil.getInstance(ApAccountingEventCtrl.class);

			eventCom.setAccounting_alias(srvInput.getAccounting_alias()); // 核算别名
			eventCom.setBal_attributes(srvInput.getBal_attributes()); // 余额属性
			AppAccountingeventCtrl eventCtrlTabl = AppAccountingeventCtrlDao.selectOne_odb1(eventCom.getAccounting_alias(), eventCom.getBal_attributes(), false);
			if (eventCtrlTabl == null) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppAccountingeventCtrl.class).getId(), SysDict.A.accounting_alias.getLongName() + '-' + SysDict.A.bal_attributes,
						eventCom.getAccounting_alias() + '-' + eventCom.getBal_attributes());
			}
		}

		// 公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		ApsLedgerBal balTable = BizUtil.getInstance(ApsLedgerBal.class);
		// 都是日切后执行,取上日日期
		balTable.setTrxn_date(runEnvs.getLast_date()); // 交易日期
		balTable.setAcct_branch(srvInput.getAcct_branch()); // 账务机构
		balTable.setCcy_code(srvInput.getCcy_code()); // 货币代码
		balTable.setAccounting_subject(srvInput.getAccounting_subject()); // 会计主体
		balTable.setAccounting_alias(srvInput.getAccounting_alias()); // 核算别名
		balTable.setBal_attributes(srvInput.getBal_attributes()); // 余额属性
		balTable.setBal_type(srvInput.getBal_type()); // 余额方向
		balTable.setAcct_bal(srvInput.getAcct_bal()); // 账户余额

		ApsLedgerBalDao.insert(balTable);

	}
	
	/**
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年3月22日-下午2:09:58</li>
	 *         <li>功能说明：登记计提</li>
	 *         </p>
	 * @param input
	 */
	public static void regAccure(ApRecordAccure srvInput) {

		// 检查非空
		BizUtil.fieldNotNull(srvInput.getAccrue_type(), SysDict.A.accrue_type.getId(), SysDict.A.accrue_type.getLongName());
		BizUtil.fieldNotNull(srvInput.getAcct_branch(), SysDict.A.acct_branch.getId(), SysDict.A.acct_branch.getLongName());
		BizUtil.fieldNotNull(srvInput.getAccounting_alias(), SysDict.A.accounting_alias.getId(), SysDict.A.accounting_alias.getLongName());
		BizUtil.fieldNotNull(srvInput.getCcy_code(), SysDict.A.ccy_code.getId(), SysDict.A.ccy_code.getLongName());
		BizUtil.fieldNotNull(srvInput.getBal_attributes(), SysDict.A.bal_attributes.getId(), SysDict.A.bal_attributes.getLongName());
		BizUtil.fieldNotNull(srvInput.getBudget_inst_amt(), SysDict.A.budget_inst_amt.getId(), SysDict.A.budget_inst_amt.getLongName());

		ApsAccrueRecord accureTable = BizUtil.getInstance(ApsAccrueRecord.class);

		// 判断核算别名和余额属性是否在参数表中

		String checkInd = ApBusinessParmApi.getValue(TA_ACCOUNTING_CHECK_IND);

		if (checkInd.equals(E_YESORNO.YES.getValue())) {

			ApAccountingEventCtrl eventCom = BizUtil.getInstance(ApAccountingEventCtrl.class);

			eventCom.setAccounting_alias(srvInput.getAccounting_alias()); // 核算别名
			eventCom.setBal_attributes(srvInput.getBal_attributes()); // 余额属性

			AppAccountingeventCtrl eventCtrlTabl = AppAccountingeventCtrlDao.selectOne_odb1(eventCom.getAccounting_alias(), eventCom.getBal_attributes(), false);
			
			if (eventCtrlTabl == null) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppAccountingeventCtrl.class).getId(), SysDict.A.accounting_alias.getLongName() + '-' + SysDict.A.bal_attributes,
						eventCom.getAccounting_alias() + '-' + eventCom.getBal_attributes());
			}
		}

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		// 日切后执行,取上日日期
		accureTable.setTrxn_date(runEnvs.getLast_date()); // 交易日期

		accureTable.setAccrue_type(srvInput.getAccrue_type()); // 计提种类
		accureTable.setAcct_branch(srvInput.getAcct_branch()); // 账务机构
		accureTable.setAccounting_alias(srvInput.getAccounting_alias()); // 核算别名
		accureTable.setCcy_code(srvInput.getCcy_code()); // 货币代码
		accureTable.setBal_attributes(srvInput.getBal_attributes()); // 余额属性
		accureTable.setBudget_inst_amt(srvInput.getBudget_inst_amt()); // 计提金额

		ApsAccrueRecordDao.insert(accureTable);

	}
	
	/**
	 * 
	 * @Title: getAccountingAliasInfo 
	 * @author zhangwh
	 * @Description:  查询核算别名列表信息
	 * @param aliasCode
	 * @return 
	 * @return List<ApAccountingEventCtrl>
	 * @date 2019年9月3日 下午5:46:11 
	 * @throws
	 */
	public static Options<ApAccountingEventCtrl> getAccountingAliasInfo(String aliasCode) {
		
		// 交易运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Options<ApAccountingEventCtrl> aliasInfo = new DefaultOptions<ApAccountingEventCtrl>();
		long start = runEnvs.getPage_start();
		long size = runEnvs.getPage_size();
		long total = runEnvs.getTotal_count();
		
		Page<ApAccountingEventCtrl> aliasList = ApAccountingBaseDao.selAcctAliasInfos(aliasCode, start, size, total, false);
		runEnvs.setTotal_count(aliasList.getRecordCount());
		aliasInfo.setValues(aliasList.getRecords());
		
		return aliasInfo;
	}
	
	
	/**
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年6月8日-上午9:38:15</li>
	 *         <li>功能说明：查询会计事件</li>
	 *         </p>
	 * @param trxnSeq
	 * @return
	 */
	public static Options<ApAccountingEvent> getAccountingEvent(String trxnSeq) {

		bizlog.debug("trxnSeq=[%s]", trxnSeq);
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Page<ApAccountingEvent> accountingEevent = ApAccountingBaseDao.selAccountingEvent(trxnSeq, MsOrg.getReferenceOrgId(ApsAccountingEvent.class), runEnvs.getPage_start(),
				runEnvs.getPage_size(), runEnvs.getTotal_count(), false);

		Options<ApAccountingEvent> accountingCom = new DefaultOptions<ApAccountingEvent>();

		runEnvs.setTotal_count(accountingEevent.getRecordCount());
		accountingCom.setValues(accountingEevent.getRecords());

		bizlog.debug("accountingCom=[%s]", accountingCom);
		return accountingCom;

	}
	
	
	/**
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年8月4日-上午9:36:22</li>
	 *         <li>功能说明：the maintenance of gl parameters</li>
	 *         </p>
	 * @param input
	 */
	public static void maintainingGlParmCtrl(ApGlParaCtrl input) {

		bizlog.method("TaAccounting.maintaningGlParmCtrl>>>>>begin>>>>>>>>>");
		bizlog.debug("input=[%s]", input);
		// 1.check the data
		BizUtil.fieldNotNull(input.getAccounting_alias(), SysDict.A.accounting_alias.getId(), SysDict.A.accounting_alias.getLongName());
		BizUtil.fieldNotNull(input.getBal_attributes(), SysDict.A.bal_attributes.getId(), SysDict.A.bal_attributes.getLongName());
		BizUtil.fieldNotNull(input.getOperater_ind(), SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());
		BizUtil.fieldNotNull(input.getAccounting_subject(), SysDict.A.accounting_subject.getId(), SysDict.A.accounting_subject.getLongName());
		BizUtil.fieldNotNull(input.getGl_code(), SysDict.A.gl_code.getId(), SysDict.A.gl_code.getLongName());

		// 2.set values
		AppAccountingeventCtrl accountingEventCtrl = BizUtil.getInstance(AppAccountingeventCtrl.class);
		accountingEventCtrl.setAccounting_alias(input.getAccounting_alias()); // accounting
																				// alias
		accountingEventCtrl.setBal_attributes(input.getBal_attributes()); // balance
																			// attributes
		accountingEventCtrl.setGl_code(input.getGl_code());																	// //
																			// attributes
		accountingEventCtrl.setRemark(input.getRemark()); // remark
		accountingEventCtrl.setAccounting_subject(input.getAccounting_subject());
		accountingEventCtrl.setData_version(input.getData_version());
		if (CommUtil.isNotNull(input.getLedger_check_ind())) {
			accountingEventCtrl.setLedger_check_ind(input.getLedger_check_ind());
		}
		else {
			accountingEventCtrl.setLedger_check_ind(E_YESORNO.YES);
		}
		
		final app_accounting_event_parm eventParm = BizUtil.getInstance(app_accounting_event_parm.class);
		
		eventParm.setSys_no(ApSystemParmApi.getValue("CORE_SYS_ID", "*"));  //system no
		eventParm.setAccounting_subject(input.getAccounting_subject());  //accounting subject
		eventParm.setAccounting_alias(input.getAccounting_alias());  //accounting alias
		eventParm.setRemark(input.getRemark());  //remark
		eventParm.setBal_attributes(input.getBal_attributes());  //balance attributes
		eventParm.setOrg_id(MsOrg.getReferenceOrgId(app_accounting_event_parm.class));  //organization id
		eventParm.setData_version(input.getData_version());
		
		// 3.add or delete or modify
		if (input.getOperater_ind() == E_DATAOPERATE.ADD) {

			AppAccountingeventCtrlDao.insert(accountingEventCtrl);

			// register audit
			ApDataAuditApi.regLogOnInsertParameter(accountingEventCtrl);
			
//			同步数据至总账			
			ApAccounting.accountingParmSync(accountingEventCtrl, E_DATAOPERATE.ADD);

		}
		else if (input.getOperater_ind() == E_DATAOPERATE.DELETE) {

			AppAccountingeventCtrl acountingCtrl = AppAccountingeventCtrlDao.selectOne_odb1(input.getAccounting_alias(), input.getBal_attributes(), false);
			
			if (acountingCtrl == null) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppAccountingeventCtrl.class).getLongname(), SysDict.A.accounting_alias.getLongName() + "-"
						+ SysDict.A.bal_attributes.getLongName(), input.getAccounting_alias() + "-" + input.getBal_attributes());
			}
			
			int busiCount = ApAccountingBaseDao.selBusiInfoCount(input.getAccounting_alias(), false);
			
			// 核算别名被业务编码引用时则报错
			if (busiCount >= 1) {
				throw ApBaseErr.ApBase.E0135(input.getAccounting_alias());
			}

			AppAccountingeventCtrlDao.deleteOne_odb1(input.getAccounting_alias(), input.getBal_attributes());
			
			// register audit
			ApDataAuditApi.regLogOnDeleteParameter(acountingCtrl);

//			同步数据至总账
			ApAccounting.accountingParmSync(accountingEventCtrl, E_DATAOPERATE.DELETE);
			
		}
		else if (input.getOperater_ind() == E_DATAOPERATE.MODIFY) {
			AppAccountingeventCtrl acountingCtrl = AppAccountingeventCtrlDao.selectOne_odb1(input.getAccounting_alias(), input.getBal_attributes(), false);
			AppAccountingeventCtrl newAcountingCtrl = BizUtil.clone(AppAccountingeventCtrl.class, acountingCtrl);

			accountingEventCtrl.setRemark(input.getRemark()); // remark
			accountingEventCtrl.setLedger_check_ind(input.getLedger_check_ind());
			accountingEventCtrl.setGl_code(input.getGl_code());
			//增加数据版本号
			//accountingEventCtrl.setData_version(input.getData_version());
			//维护时从数据库中取数据版本号
			accountingEventCtrl.setData_version(newAcountingCtrl.getData_version());
			// 登记业务审计
			int i = ApDataAuditApi.regLogOnUpdateParameter(newAcountingCtrl, accountingEventCtrl);
			if (i == 0) {
				throw ApPubErr.APPUB.E0023(OdbFactory.getTable(AppAccountingeventCtrl.class).getLongname());
			}
			AppAccountingeventCtrlDao.updateOne_odb1(accountingEventCtrl);
		}

		// 发布给
		registerGlCodeUpdateInfo(input);
		
		bizlog.method("TaAccounting.maintaningGlParmCtrl>>>>>end>>>>>>>>>");
		return;

	}
	
	/**
	 * 
	 * @Author Dengyu
	 *         <p>
	 *         <li>2018年4月3日-上午9:40:58</li>
	 *         <li>功能说明：使用list的循环</li>
	 *         </p>
	 * @param parm         核算参数
	 * @param operator	      操作标志
	 */
	public static void accountingParmSync(AppAccountingeventCtrl parm , E_DATAOPERATE operator ){
		
//		是否同步标志
		String syncInd = ApSystemParmApi.getValue("GL_DATA_SYNC_IND", "*");
	
		if( CommUtil.equals(syncInd, E_YESORNO.YES.getValue()) ) {
					
			final app_accounting_event_parm accountingParmParm = BizUtil.getInstance(app_accounting_event_parm.class);
			
			accountingParmParm.setSys_no(ApSystemParmApi.getValue("CORE_SYS_ID", "*"));  //system no
			accountingParmParm.setAccounting_subject(parm.getAccounting_subject());  //accounting subject
			accountingParmParm.setAccounting_alias(parm.getAccounting_alias());  //accounting alias
			accountingParmParm.setRemark(parm.getRemark());  //remark
			accountingParmParm.setBal_attributes(parm.getBal_attributes());  //balance attributes
			accountingParmParm.setOrg_id( MsOrg.getReferenceOrgId(app_accounting_event_parm.class) );  //organization id
			
			//删除
			if( CommUtil.equals(operator.getValue(), E_DATAOPERATE.DELETE.getValue() ) ){
				
				App_accounting_event_parmDao.deleteOne_odb1(accountingParmParm.getSys_no(), accountingParmParm.getAccounting_alias(), accountingParmParm.getBal_attributes());

				ApDataSyncApi.regDataOperateLog(new Runnable() {
					
					@Override
					public void run() {
						App_accounting_event_parmDao.deleteOne_odb1(accountingParmParm.getOrg_id(), accountingParmParm.getAccounting_alias(), accountingParmParm.getBal_attributes());
					}
				}, E_DATAOPERATE.DELETE);
			}
			//新核算参数，新增
			if( CommUtil.equals(operator.getValue(), E_DATAOPERATE.ADD.getValue() ) ){
				
				App_accounting_event_parmDao.deleteOne_odb1(accountingParmParm.getSys_no(), accountingParmParm.getAccounting_alias(), accountingParmParm.getBal_attributes());
				App_accounting_event_parmDao.insert(accountingParmParm);
				
				ApDataSyncApi.regDataOperateLog(new Runnable() {
					
					@Override
					public void run() {
						App_accounting_event_parmDao.deleteOne_odb1(accountingParmParm.getSys_no(), accountingParmParm.getAccounting_alias(), accountingParmParm.getBal_attributes());
						App_accounting_event_parmDao.insert(accountingParmParm);						
					}
				}, E_DATAOPERATE.ADD);
			}
			
		}
	}
	
	/**
	 * 
	 * @Author yanshp
	 *         <p>
	 *         <li>2019年1月23日-下午5:01:58</li>
	 *         <li>功能说明：查询核算别名,由标志errer_ctrl_ind控制是否报错</li>
	 *         </p>
	 * @param accounting_alias 
	 * @param bal_attributes
	 * @param errer_ctrl_ind
	 * @return
	 */
	public static ApGlParaCtrl qryGlParmCtrl(String accountingAlias, String balAttributes, E_YESORNO errerCtrlInd) {
		
		ApGlParaCtrl glParaCtrl = null;
		
		AppAccountingeventCtrl accountingCtrl = AppAccountingeventCtrlDao.selectOne_odb1(accountingAlias, balAttributes, false);
		
		//查询控制报错
		if (accountingCtrl == null) {
			
			if (errerCtrlInd == E_YESORNO.YES) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppAccountingeventCtrl.class).getLongname(), SysDict.A.accounting_alias.getLongName() + "-"+ SysDict.A.bal_attributes.getLongName(), accountingAlias + "-" + balAttributes);
			} else {
				return null;
			}
			
		} else {
			
			glParaCtrl = BizUtil.getInstance(ApGlParaCtrl.class);
			
			glParaCtrl.setAccounting_alias(accountingCtrl.getAccounting_alias());  //accounting alias
			glParaCtrl.setBal_attributes(accountingCtrl.getBal_attributes());  //balance attributes
			glParaCtrl.setRemark(accountingCtrl.getRemark());  //remark
			glParaCtrl.setLedger_check_ind(accountingCtrl.getLedger_check_ind());  //ledger check indication
			glParaCtrl.setAccounting_subject(accountingCtrl.getAccounting_subject());  //accounting subject
			glParaCtrl.setGl_code(accountingCtrl.getGl_code());  //gl code
			glParaCtrl.setData_version(accountingCtrl.getData_version());  //data version
		}

		return glParaCtrl;
		
	}
	
	
	public static void registerGlCodeUpdateInfo(ApGlParaCtrl input) {
		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
		String localRootPath = ApFileApi.getFullPath(ApConst.CORE_EVENT_PARM_REMOTE_DIR_CODE); // 远程的绝对路径部分
		
		// 文件名
		String fileName = String.format("CORE_EVENT_PARM_%s.txt", runEnvs.getTimestamp()+"_");// 文件名称

		bizlog.debug("localRootPath>>>>[%s] [%s]", localRootPath, fileName);
		
		// 创建文件处理对象
		LocalFileProcessor processor = new LocalFileProcessor(localRootPath, fileName, "UTF-8");
		try {
			// 打开文件
			processor.open(true);

			// 写文件
			processor.write(BizUtil.toJson(input));

			processor.close();// 关闭文件
			
		} catch (Exception e) {
			
		} 

		String fileId = ApFileSend.genFileId();
		
		ApFileSend.register(fileId, fileName, ApConst.CORE_EVENT_PARM_REMOTE_DIR_CODE, ApConst.CORE_EVENT_PARM_LOCAL_DIR_CODE, MsEnumType.E_YESORNO.YES);

	}
	
	
}
