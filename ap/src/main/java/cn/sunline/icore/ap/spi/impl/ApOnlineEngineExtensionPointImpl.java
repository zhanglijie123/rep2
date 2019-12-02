package cn.sunline.icore.ap.spi.impl;

import java.math.BigDecimal;

import cn.sunline.clwj.msap.biz.spi.MsTransactionProcessCallBackSPIImpl;
import cn.sunline.clwj.msap.biz.transaction.MsTrxn;
import cn.sunline.clwj.msap.core.spi.MsPointExtensionSPI;
import cn.sunline.clwj.msap.core.type.MsCoreComplexType.MsDateInfo;
import cn.sunline.clwj.msap.iobus.type.IoMsBizComplex.IoMsTrxnDefine;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DEBITCREDIT;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_TRXNTYPE;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.edsp.engine.online.plugin.engine.InServiceController.ServiceCategory;
import cn.sunline.edsp.microcore.spi.ExtensionLoader;
import cn.sunline.edsp.microcore.util.ExtensionUtil;
import cn.sunline.icore.ap.api.ApBusinessParmApi;
import cn.sunline.icore.ap.api.ApChannelApi;
import cn.sunline.icore.ap.api.ApRuleApi;
import cn.sunline.icore.ap.spi.CtCheckPermisWorkflow;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.parm.TrxEnvs.GlEntryInfo;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.base.RequestData;
import cn.sunline.ltts.base.ResponseData;
import cn.sunline.ltts.base.biz.HeaderDataConstants;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.common.net.util.JsonUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.engine.data.DataArea;

public class ApOnlineEngineExtensionPointImpl extends MsTransactionProcessCallBackSPIImpl {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApOnlineEngineExtensionPointImpl.class);

	@Override
	public void beforeBizEnv(RequestData requestData, ServiceCategory category) {
		
		super.beforeBizEnv(requestData, category);
		
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		String reconCode = runEnvs.getRecon_code();

		// 柜面的交易码特殊处理
		if (CommUtil.isNotNull(reconCode)) {
			runEnvs.setRecon_code(reconCode);
		}

		String language = requestData.getRequestHeader().getString("country");
		if (CommUtil.isNotNull(language)) {
			runEnvs.setUi_language(language);
		} else {
			runEnvs.setUi_language(ApConst.DEFAULT_LANGUAGE);
		}

		
		
		//初始化交易类型信息
		IoMsTrxnDefine trxnDef = MsTrxn.getTrxnDefine(runEnvs.getTrxn_code());
		if (CommUtil.isNotNull(trxnDef)) {
			
			runEnvs.setTrxn_type(trxnDef.getTrxn_type());
		}
		
		
		
		// 时间检查：日切时检查预期日期和交易日期是否相符
		boolean isCounter = ApChannelApi.isCounter(runEnvs.getChannel_id());
		
		String expectDate = requestData.getCommReq().getString("expect_date"); // 预期受理日期
		if (CommUtil.isNotNull(expectDate) && CommUtil.compare(expectDate, runEnvs.getTrxn_date()) != 0) {

			if (isCounter) {
				// COUNTER_EXIT 指柜面签退的交易码。
				bizlog.debug("counter exit code is " + ApBusinessParmApi.getValue("COUNTER_EXIT_CODE"));
				String COUNTER_EXIT = ApBusinessParmApi.getValue("COUNTER_EXIT_CODE");

				if (!COUNTER_EXIT.equals(runEnvs.getTrxn_code())) {
					// 核心已日切, 需要签退后重新登录
					throw ApErr.AP.E0123();
				}
			} else {
				// 预期受理日期和交易日期不相等，抛错
				throw ApErr.AP.E0028(expectDate, runEnvs.getTrxn_date());
			}
		}

		// 柜员session状态检查，如果状态为LOGOUT,则说明柜员已经被签退，给出提示重新登录
		// 是柜面渠道才进行session管理
		if (isCounter) {

			// 是工作流审批且不为空，则不进行session管理
			if (CommUtil.equals(requestData.getCommReq().getString("workflow_ind"), "Y")) {
				return;
			} else {
				String oldSessionId = runEnvs.getSession_id();

//				SrvIoCtUser srvIoCtUser = BizUtil.getInstance(SrvIoCtUser.class);
//				IoCtQryUserSessionInfoIn ioCtQryUserSessionInfoIn = BizUtil.getInstance(IoCtQryUserSessionInfoIn.class);
//
//				ioCtQryUserSessionInfoIn.setSession_id(oldSessionId); // 根据当前柜员sessionID获取session信息
//				srvIoCtUser.mntUserSessionInfo(ioCtQryUserSessionInfoIn);
			}
		}
		
		bizlog.debug("调用服务前beforeBizServiceEnv=====start");
		MsPointExtensionSPI process = ExtensionUtil.getExtensionPointImpl(MsPointExtensionSPI.POINT);
		runEnvs.setComputer_date(BizUtil.getComputerDate());
		runEnvs.setComputer_time(BizUtil.getComputerTime());
		MsDateInfo dateInfo = process.getDateInfo(runEnvs.getBusi_org_id());
		/* 如果是金融类交易，本地日期和上送日期不一致则报错退出 */
//		bizlog.debug("上送日期:[%s]",runEnvs.getInput_date());
		if ((E_TRXNTYPE.FINANCIAL == runEnvs.getTrxn_type())&& (!CommUtil.equals(dateInfo.getTrxn_date(),runEnvs.getTrxn_date()))) {
			bizlog.error("金融类交易，本地日期和上送日期不一致");
			throw ApErr.AP.E0128(dateInfo.getTrxn_date(), runEnvs.getTrxn_date());
		}
		bizlog.debug("调用服务前beforeBizServiceEnv=====end");
	}


	@Override
	public void afterBizEnv(RequestData requestData, ResponseData responseData,
			ServiceCategory category) {
		super.afterBizEnv(requestData, responseData, category);
		
		bizlog.debug("afterBizEnv start");
		/*
		 * 需在super.afterBizEnv之后处理： 交易编码包含在杂项参数中则回滚
		 */
//		if (ApSystemParmApi.getValue("TRXN_CHECK").contains(BizUtil.getTrxRunEnvs().getTrxn_code())) {
//			DBUtil.rollBack(); // 回滚事务
//			clearTrxRunEnvs(); // 清理缓存
//		}
		
		bizlog.debug("afterBizEnv end");
	}

	@Override
	public void beforeProcess(DataArea dataArea, ServiceCategory category) {
		
		super.beforeProcess(dataArea, category);
		
		checkEmployeeAcct(dataArea);

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		boolean isCounter = ApChannelApi.isCounter(runEnvs.getChannel_id());
		if (isCounter) {
			CtCheckPermisWorkflow client = ExtensionLoader.getExtensionLoader(CtCheckPermisWorkflow.class).getDefaultExtension();
			
			/**
			 * 用于检查不通过ct2100交易进行转发的交易授权处理
			 */
			if (!CommUtil.isNull(client) && runEnvs.getAuth_checked_ind() != E_YESORNO.YES) {
				// 已授权
				client.checkPermis(null);
				runEnvs.setAuth_checked_ind(E_YESORNO.YES);
			}
			
			/**
			 * 授权柜员验密后检查授权权限是否通过
			 */
			if (!CommUtil.isNull(client) && !runEnvs.getAuth_reason().isEmpty() && CommUtil.isNotNull(runEnvs.getAuth_user_id())) {
				client.chkAuthorization();
			}
			
		}
		
	}

	@Override
	public void afterProcess(DataArea dataArea, ServiceCategory category) {
		
		super.afterProcess(dataArea, category);
		
		bizlog.debug("the  RunEnvs Auto_chrg_info is =[%s]", BizUtil.getTrxRunEnvs().getAuto_chrg_info());

//		boolean isCounter = ApChannelApi.isCounter(BizUtil.getTrxRunEnvs().getChannel_id());
//		String josnInput = BizUtil.toJson(dataArea.getInput());;
		// 柜面工作流重复请求检查
//		if (isCounter) {
//			SrvIoCtWorkflow srvIoCtWorkflow = BizUtil.getInstance(SrvIoCtWorkflow.class);
//			srvIoCtWorkflow.promptApprovalInfo(josnInput);
//		}
//		// 告警处理
//		ApRuleApi.popupWarning();
//
//		// 场景收费处理
//		SrvIoCmChrg srvIoCmChrg = BizUtil.getInstance(SrvIoCmChrg.class);
//		srvIoCmChrg.prcAutoChrgAccounting(BizUtil.getTrxRunEnvs().getAuto_chrg_info());
//
//		// 账务平衡检查
//		if (BizUtil.getTrxRunEnvs().getAccounting_event_seq().longValue() > 1) {
//			BizUtil.getInstance(SrvIoTaAccounting.class).checkBalance();
//		}
//
//		// 柜面渠道进行权限检查
//		if (isCounter) {
//			
//			// 场景授权处理
//			ApRuleApi.popupSceneAuth();
//
//			SrvIoCtWorkflow srvIoCtWorkflow = BizUtil.getInstance(SrvIoCtWorkflow.class);
//			
//			// 工作流权限检查
//			srvIoCtWorkflow.chkWorkFlowPermission(josnInput);		
//		}
		
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		boolean isCounter = ApChannelApi.isCounter(runEnvs.getChannel_id());
		
		
		/*workflow_ind 审批完成标志
		*workflow_checked_ind 已检查工作流标志 在ct2100
		*workflow_error_ind 工作流抛错标志
		*/
		
		if (isCounter) {
			if (runEnvs.getWorkflow_approving_ind() == E_YESORNO.YES) {
				throw ApErr.AP.E0129();
			}
		}
		
		// 账务平衡检查  ----暂不检查，等清泉回来调shareenv
// 		if (!runEnvs.getGl_entries().isEmpty()) {
// 			checkAccountingBalance();
// 		}

		if (isCounter) {
			// 场景授权处理
			ApRuleApi.popupSceneAuth();
			if (runEnvs.getWorkflow_error_ind() == E_YESORNO.YES && runEnvs.getWorkflow_ind() != E_YESORNO.YES) {
				throw ApErr.AP.E0130();
			}
		}
		
		/*
		 * 增加柜面子系统交易的工作流检查
		 * */
		
		if (isCounter) {
			CtCheckPermisWorkflow client = ExtensionLoader.getExtensionLoader(CtCheckPermisWorkflow.class).getDefaultExtension();
			/**
			 * 用于检查不通过ct2100交易进行转发的交易工作流处理
			 */
			if (!CommUtil.isNull(client) && runEnvs.getWorkflow_checked_ind() != E_YESORNO.YES) {
				if (runEnvs.getWorkflow_ind() != E_YESORNO.YES) {
					String josnInput = BizUtil.toJson(dataArea.getInput());
					client.checkWorkflow(josnInput);
				}
			}
		}
		
	}

	@Override
	public void exceptionProcess(DataArea dataArea, Throwable e,
			ServiceCategory category) {
		super.exceptionProcess(dataArea, e, category);
	}

	@Override
	public void beforePkgFormat(ResponseData responseData, ServiceCategory category) {
		
		
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		boolean isCounter = ApChannelApi.isCounter(runEnvs.getChannel_id());
		
		if (isCounter) {
			final CtCheckPermisWorkflow client = ExtensionLoader.getExtensionLoader(CtCheckPermisWorkflow.class).getDefaultExtension();
			if (!CommUtil.isNull(client) && runEnvs.getWorkflow_checked_ind() != E_YESORNO.YES) {
				bizlog.info("responseData:[" + JsonUtil.toJson(responseData.getData()) + "]");
				String status = responseData.getData().getSystem().getString(HeaderDataConstants.NAME_RET_STATUS);
				if (status.equals("S")) {
					client.registerTrxnTellerSeq();
					
				}
			}
		}
		
		super.beforePkgFormat(responseData, category);
	}

	/**
	 * 检查当前柜员是否可以操作员工账户
	 * 
	 * @param dataArea
	 */
	private void checkEmployeeAcct(DataArea dataArea) {
		/**
		 * 控制只针对柜面的交易
		 */
		if (!ApChannelApi.isCounter((String) dataArea.getCommReq().get("channel_id"))) {
			return;
		}

		Object roleCollects = dataArea.getCommReq().get("role_collection");

		if (CommUtil.isNull(roleCollects)) {
			return;
		}

		String roles = ApBusinessParmApi.getValue("ROLE_OPR_EMP");// 可操作员工账号柜员的角色权限,多个以逗号隔开

		if (roles.split(",").length > 1) {
			for (String r : roles.split(",")) {
				if (roleCollects.toString().contains(r)) {
					return;
				}
			}
		} else if (roleCollects.toString().contains(roles)) {
			return;
		}

//		String acctType = dataArea.getInput().getString(SysDict.A.acct_type.getId());
//		String empAcctType = ApBusinessParmApi.getValue("EMP_ACCT_TYPE");// 员工账户的账号类型
//
//		if (CommUtil.isNotNull(acctType) && acctType.equals(empAcctType)) {
//			throw DpErr.Dp.E0461();
//		}
//
//		String acctNo = dataArea.getInput().getString(SysDict.A.acct_no.getId());
//
//		if (CommUtil.isNull(acctNo)) {
//			acctNo = dataArea.getInput().getString(SysDict.A.card_no.getId());
//			if (CommUtil.isNull(acctNo)) {
//				return;
//			}
//		}
//
//		IoDpQueryMainAcctIn acctIn = BizUtil.getInstance(IoDpQueryMainAcctIn.class);
//		acctIn.setAcct_no(acctNo);
//		acctIn.setAcct_type(acctType);
//
//		SrvIoDpQueryAcct ioDpQueryAcct = BizUtil.getInstance(SrvIoDpQueryAcct.class);
//		IoDpQueryMainAcctOut acctOut = ioDpQueryAcct.qryAcctMainInfo(acctIn);
//
//		if (acctOut.getAcct_type().equals(empAcctType)) {
//			throw DpErr.Dp.E0461();
//		}
	}
	
	
	/**
	 * 
	 * @Author maold
	 *         <p>
	 *         <li>2018年7月12日-下午4:20:38</li>
	 *         <li>功能说明：缓存清理</li>
	 *         </p>
	 */
	private static void clearTrxRunEnvs() {
		
		//短信
		BizUtil.getTrxRunEnvs().getSms_send_info().clear();
	
		//收费
		BizUtil.getTrxRunEnvs().setAuto_chrg_info(null);
		
		BizUtil.getTrxRunEnvs().setAuto_chrg_method(null);
		BizUtil.getTrxRunEnvs().setDeduct_chrg_amt(null);
		
		//客户信息
		BizUtil.getTrxRunEnvs().setCustom_parm(null);

		//加密信息
		BizUtil.getTrxRunEnvs().getEncrypted_info().clear();

		//支付方式
		BizUtil.getTrxRunEnvs().setPayment_mode(null);
		
		//操作柜员号
		BizUtil.getTrxRunEnvs().setOperator_cust_no(null);
	
		//语言
		BizUtil.getTrxRunEnvs().setUi_language(null);
		
		//警告信息
		BizUtil.getTrxRunEnvs().setWarning_confirm_ind(null);
		BizUtil.getTrxRunEnvs().setWarning_info(null);
		
		//工作流信息
		BizUtil.getTrxRunEnvs().setWorkflowInfo(null);
		
		//转账事件流水
		BizUtil.getTrxRunEnvs().setAccounting_event_seq(1L);	
		
	}
	
	private void checkAccountingBalance() {
		Options<GlEntryInfo> glEntries = BizUtil.getTrxRunEnvs().getGl_entries(); 
		BizUtil.listSort(glEntries, true, SysDict.A.trxn_ccy.getId());
		
		BigDecimal debitSumAmt = BigDecimal.ZERO;
		BigDecimal creditSumAmt = BigDecimal.ZERO;
		String ccyCode = glEntries.get(0).getTrxn_ccy();
		for (GlEntryInfo glEntry : glEntries) {
			if (E_YESORNO.NO == glEntry.getDouble_entry_ind()) { // 非复式记账不统计，如表外账
				continue;
			}
			
			// 币种相等，继续累加 
			if (CommUtil.equals(ccyCode, glEntry.getTrxn_ccy())) { 
				debitSumAmt = debitSumAmt.add(glEntry.getDebit_credit() == E_DEBITCREDIT.DEBIT ? glEntry.getTrxn_amt() : BigDecimal.ZERO);
				creditSumAmt = creditSumAmt.add(glEntry.getDebit_credit() == E_DEBITCREDIT.CREDIT ? glEntry.getTrxn_amt() : BigDecimal.ZERO);
			}
			// 币种不相等时，上一个币种已经累加完毕。如果借贷方相等，则下一个币种开始累加
			else if (debitSumAmt.compareTo(creditSumAmt) == 0) {
				ccyCode = glEntry.getTrxn_ccy();
				debitSumAmt = glEntry.getDebit_credit() == E_DEBITCREDIT.DEBIT ? glEntry.getTrxn_amt() : BigDecimal.ZERO;
				creditSumAmt = glEntry.getDebit_credit() == E_DEBITCREDIT.CREDIT ? glEntry.getTrxn_amt() : BigDecimal.ZERO;
			}
			// 同币种累积完毕，借贷方金额不一致时，账务不平
			else {
				bizlog.error("Balance check failed. Ccy_code[%s], the margin is [%s]", ccyCode, debitSumAmt.subtract(creditSumAmt).abs());
				throw ApBaseErr.ApBase.E0134(BizUtil.getTrxRunEnvs().getBusi_seq());
			}
		}
		
		// 最后一个币种的借贷方累积金额比较，不相等时，账务不平
		if (debitSumAmt.compareTo(creditSumAmt) != 0) {
			bizlog.error("Balance check failed. Ccy_code[%s], the margin is [%s]", ccyCode, debitSumAmt.subtract(creditSumAmt).abs());
			throw ApBaseErr.ApBase.E0134(BizUtil.getTrxRunEnvs().getBusi_seq());
		}
	}



	
}
