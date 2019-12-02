package cn.sunline.icore.ap.callback;



/*import java.util.Date;

import cn.sunline.clwj.msap.biz.spi.MsTransactionProcessCallBackSPIImpl;
import cn.sunline.edsp.dtsp.custom.plugin.tables.KsysSvcDefine.Ksys_ywfwdyDao;
import cn.sunline.edsp.dtsp.custom.plugin.tables.KsysSvcDefine.ksys_ywfwdy;
import cn.sunline.edsp.engine.online.plugin.engine.InServiceController;
import cn.sunline.edsp.engine.service.ServiceTypeEnum;
import cn.sunline.edsp.microcore.plugin.IProcess;
import cn.sunline.edsp.microcore.util.ExtensionUtil;
import cn.sunline.edsp.plugin.dtsp.custom.model.CustomServiceController;
import cn.sunline.edsp.plugin.dtsp.custom.util.CustomUtil;
import cn.sunline.edsp.service.type.controller.IServiceControllerExtension;
import cn.sunline.ltts.base.RequestData;
import cn.sunline.ltts.base.ResponseData;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.cbs.iobus.servicetype.cm.chrg.SrvIoCmChrg;
import cn.sunline.ltts.cbs.iobus.servicetype.ct.SrvIoCtBasic;
import cn.sunline.ltts.cbs.iobus.servicetype.ct.SrvIoCtUser;
import cn.sunline.ltts.cbs.iobus.servicetype.ct.SrvIoCtWorkflow;
import cn.sunline.ltts.cbs.iobus.servicetype.lt.dept.SrvIoDpQueryAcct;
import cn.sunline.ltts.cbs.iobus.servicetype.ta.SrvIoTaAccounting;
import cn.sunline.ltts.cbs.iobus.type.ct.ComIoCtBasic.IoCtTranCodeMappingIn;
import cn.sunline.ltts.cbs.iobus.type.ct.ComIoCtUser.IoCtQryUserSessionInfoIn;
import cn.sunline.ltts.cbs.iobus.type.ct.ComIoCtUser.IoCtQryUserSessionInfoOut;
import cn.sunline.ltts.cbs.iobus.type.lt.dept.ComIoDpQueryAcct.IoDpQueryMainAcctIn;
import cn.sunline.ltts.cbs.iobus.type.lt.dept.ComIoDpQueryAcct.IoDpQueryMainAcctOut;
import cn.sunline.ltts.cbs.sys.dict.SysDict;
import cn.sunline.ltts.cbs.sys.errors.DpErr;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.engine.biz.runtime.EngineContext;
import cn.sunline.ltts.engine.data.DataArea;
import cn.sunline.ltts.engine.sequence.PackageSequence;
import cn.sunline.ltts.sys.dict.ApBaseDict;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.odc.cbs.base.ap.api.ApBusinessParmApi;
import cn.sunline.odc.cbs.base.ap.api.ApChannelApi;
import cn.sunline.odc.cbs.base.ap.api.ApRuleApi;
import cn.sunline.odc.cbs.base.ap.api.ApSystemParmApi;
import cn.sunline.odc.cbs.base.ap.spi.ITransLogger;
import cn.sunline.odc.cbs.base.ap.util.ApConst;
import cn.sunline.odc.cbs.base.ap.util.BizUtil;
import cn.sunline.odc.cbs.base.ap.util.DBUtil;
import cn.sunline.odc.cbs.busi.sys.errors.ApErr;*/

/*public class TransactionProcessCallBackImpl extends MsTransactionProcessCallBackSPIImpl{

    private static final BizLog bizlog = BizLogUtil.getBizLog(TransactionProcessCallBackImpl.class);
	
	
	@Override
	public InServiceController getTransactionController(String prcscd) {
		bizlog.method("TransactionProcessCallbackImpl.getTransactionController begin>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if (bizlog.isDebugEnabled()){
			bizlog.debug(">>>>>>>>>>>>>>>>> prcscd: " + prcscd);
		}
		
		RunEnvs runEnvs = SysUtil.getTrxRunEnvs();

		// **柜面交易码映射处理（CT模块与核心部署在一起的特殊处理）
		SrvIoCtBasic counterSrv = BizUtil.getInstance(SrvIoCtBasic.class);

		IoCtTranCodeMappingIn inputParm = BizUtil.getInstance(IoCtTranCodeMappingIn.class);
		inputParm.setCt_trxn_code(prcscd);

		String trxnCode = counterSrv.queryCoreTransactionCode(inputParm).getBackend_trxn_code();
		String innerPrcscd = prcscd;
		if (CommUtil.isNotNull(trxnCode)) {
			runEnvs.setRecon_code(prcscd);// 柜面的交易码放到对账代码里面
			innerPrcscd = trxnCode; // 交易码变更为核心的
		}
		// **end
		
		EngineContext.peek().getReuqestDataArea().getSystem().setString("prcscd", innerPrcscd);
		
		return super.getTransactionController(innerPrcscd);		
	}

	*//**
	 * 覆盖交易前回调点实现，获取Controller
	 * @param serviceId
	 * @return
	 *//*
	@Override
	public IServiceControllerExtension getBizServiceController(String serviceId){
		bizlog.method("TransactionProcessCallbackImpl.getBizServiceController begin>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		bizlog.debug(">>>>>>>>>>>>>>>>> serviceId: " + serviceId);
		
		CustomServiceController controller;
		ksys_ywfwdy serviceInfo = Ksys_ywfwdyDao.selectOne_odb_uidx1(serviceId, CustomUtil.getSystemCode(), false);
		if (serviceInfo == null) {
			controller = new CustomServiceController(serviceId);
			controller.setModelId(serviceId);
			controller.setRunnable(true);
			controller.setServiceType(ServiceTypeEnum.CHECK_SERVICE);
		}else {
			controller = (CustomServiceController) super.getBizServiceController(serviceId);
		}
		
		bizlog.method("TransactionProcessCallbackImpl.getBizServiceController end>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		return controller;
	}

	*//**
	 * 交易前处理-初始化数据区
	 *//*
	@Override
	public void beforeBizEnv(DataArea dataArea) {
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
		String reconCode = runEnvs.getRecon_code();
		
		// 代码直接掉flowtran，直接走这里，不走initPackageSequence
		if (CommUtil.isNull(runEnvs.getTrxn_date())){
			this.initPackageSequence(dataArea);
		}

		super.beforeBizEnv(dataArea);

		// 柜面的交易码特殊处理
		if (CommUtil.isNotNull(reconCode)) {
			runEnvs.setRecon_code(reconCode);
		}		

		String language = dataArea.getSystem().getString("country");
		if(CommUtil.isNotNull(language)){
			runEnvs.setUi_language(language);
		}else{
			runEnvs.setUi_language(ApConst.DEFAULT_LANGUAGE);
		}
		
		//时间检查：日切时检查预期日期和交易日期是否相符

		String expectDate = dataArea.getCommReq().getString("expect_date"); // 预期受理日期
		if (CommUtil.isNotNull(expectDate)
				&& CommUtil.compare(expectDate, runEnvs.getTrxn_date()) != 0) {
			
			boolean isCounter = ApChannelApi.isCounter(runEnvs.getChannel_id());
			
			if (isCounter) {
				// COUNTER_EXIT 指柜面签退的交易码。
				bizlog.debug("counter exit code is " + ApBusinessParmApi.getValue("COUNTER_EXIT_CODE"));
				String COUNTER_EXIT=ApBusinessParmApi.getValue("COUNTER_EXIT_CODE");
				
				if (!COUNTER_EXIT.equals(runEnvs.getTrxn_code())) {
					//核心已日切, 需要签退后重新登录
					throw ApErr.AP.E0123();
				}
			} else {
				// 预期受理日期和交易日期不相等，抛错
				throw ApErr.AP.E0028(expectDate, runEnvs.getTrxn_date());
			}
		}
		
		// 柜员session状态检查，如果状态为LOGOUT,则说明柜员已经被签退，给出提示重新登录
		boolean isCounter = ApChannelApi.isCounter(BizUtil.getTrxRunEnvs().getChannel_id());
		// 是柜面渠道才进行session管理
		if (isCounter) {
		
			//是工作流审批且不为空，则不进行session管理
			if(CommUtil.equals(dataArea.getCommReq().getString("workflow_ind"), "Y")){
				return;
			}else {
				String oldSessionId = runEnvs.getSession_id();

				SrvIoCtUser srvIoCtUser = BizUtil.getInstance(SrvIoCtUser.class);
				IoCtQryUserSessionInfoIn ioCtQryUserSessionInfoIn = BizUtil.getInstance(IoCtQryUserSessionInfoIn.class);

				ioCtQryUserSessionInfoIn.setSession_id(oldSessionId); // 根据当前柜员sessionID获取session信息
				IoCtQryUserSessionInfoOut ioCtQryUserInfoOut = srvIoCtUser.mntUserSessionInfo(ioCtQryUserSessionInfoIn);
			}
		}
	}

	*//**
	 * 交易处理-获取交易文件 ? 返回公共运行区的FlowtranID
	 *//*
	@Override
	public String getTransactionCode(String prcscd, DataArea dataArea) {
		return super.getTransactionCode(prcscd, dataArea);
	}

	*//**
	 * 交易处理-前处理扩展点 暂无使用场景
	 *//*
	@Override
	public void beforeProcess(DataArea dataArea) {
		super.beforeProcess(dataArea);
		checkEmployeeAcct(dataArea);
	}

	*//**
	 * 交易处理-后处理扩展点 该扩展点可以做的工作如下，其他代码需要去除。 平衡检查
	 *//*
	@Override
	public void afterProcess(DataArea dataArea) {

		bizlog.debug("the  RunEnvs Auto_chrg_info is =[%s]", BizUtil.getTrxRunEnvs().getAuto_chrg_info());

		boolean isCounter = ApChannelApi.isCounter(BizUtil.getTrxRunEnvs().getChannel_id());
		String josnInput = BizUtil.toJson(dataArea.getInput());;
		// 柜面工作流重复请求检查
		if (isCounter) {
			SrvIoCtWorkflow srvIoCtWorkflow = BizUtil.getInstance(SrvIoCtWorkflow.class);
			srvIoCtWorkflow.promptApprovalInfo(josnInput);
		}
		// 告警处理
		ApRuleApi.popupWarning();

		// 场景收费处理
		SrvIoCmChrg srvIoCmChrg = BizUtil.getInstance(SrvIoCmChrg.class);
		srvIoCmChrg.prcAutoChrgAccounting(BizUtil.getTrxRunEnvs().getAuto_chrg_info());

		// 账务平衡检查
		if (BizUtil.getTrxRunEnvs().getAccounting_event_seq().longValue() > 1) {
			BizUtil.getInstance(SrvIoTaAccounting.class).checkBalance();
		}

		// 柜面渠道进行权限检查
		if (isCounter) {
			
			// 场景授权处理
			ApRuleApi.popupSceneAuth();

			SrvIoCtWorkflow srvIoCtWorkflow = BizUtil.getInstance(SrvIoCtWorkflow.class);
			
			// 工作流权限检查
			srvIoCtWorkflow.chkWorkFlowPermission(josnInput);		
		}
		
		super.afterProcess(dataArea);
	}

	*//**
	 * 交易处理-数据区处理 ? 将公共运行区数据映射到报文公共返回区
	 *//*
	@Override
	public void afterBizEnv(DataArea dataArea) {
		super.afterBizEnv(dataArea);
		
		
		 * 需在super.afterBizEnv之后处理：
		 * 交易编码包含在杂项参数中则回滚
		 
		if(ApSystemParmApi.getValue("TRXN_CHECK").contains(BizUtil.getTrxRunEnvs().getTrxn_code())){
			DBUtil.rollBack(); // 回滚事务
			clearTrxRunEnvs(); // 清理缓存
		}
	}

	*//**
	 * 交易后处理-异常处理 暂无使用场景
	 *//*
	@Override
	public void exceptionProcess(DataArea dataArea, Throwable e) {
		super.exceptionProcess(dataArea, e);
	}

	*//**
	 * 交易后处理-登记报文流水
	 *//*
	@Override
	public void registPackageSequence(final String pckgsq, final String pckgdt, final RequestData request, final ResponseData response, final Date beginTime, final Throwable cause, final boolean autonomous) {
		super.registPackageSequence(pckgsq, pckgdt, request, response, beginTime, cause, autonomous);
		  ExtensionUtil.executeExtensionPoint("Aplt.trans.logger", new IProcess<ITransLogger>() {
		        public void run(ITransLogger extensionObj) {
		          try {
		            extensionObj.logTransInfo(pckgsq, pckgdt, request, response, beginTime, cause, autonomous);
		          }
		          catch (Exception e) {
		        	  TransactionProcessCallBackImpl.bizlog.error("Log the transaction information failed by Logger[%s].", e, new Object[] { extensionObj.getClass().getSimpleName() });
		          }
		        }

		      });
	}

	*//**
	 * 交易后处理-返回前处理 返回前的一些拦截、重新赋值处理，例如错误码的标准化，暂未使用。
	 *//*
	@Override
	public void beforePkgFormat(ResponseData response) {
		super.beforePkgFormat(response);
	}
	*//**
	 * 
	 * @Author maold
	 *         <p>
	 *         <li>2018年7月12日-下午4:20:38</li>
	 *         <li>功能说明：缓存清理</li>
	 *         </p>
	 *//*
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
	
	
	
	
	*//**
	 * 检查当前柜员是否可以操作员工账户
	 * @param dataArea
	 *//*
	private void checkEmployeeAcct(DataArea dataArea) {
		*//**
		 * 控制只针对柜面的交易
		 *//*
		if(!ApChannelApi.isCounter((String) dataArea.getCommReq().get("channel_id"))) {
			return;
		}
		
		Object roleCollects = dataArea.getCommReq().get("role_collection");
		
		if(CommUtil.isNull(roleCollects)) {
			return;
		}

		String roles = ApBusinessParmApi.getValue("ROLE_OPR_EMP");//可操作员工账号柜员的角色权限,多个以逗号隔开
		
		if(roles.split(",").length > 1) {
			for(String r : roles.split(",")) {
				if(roleCollects.toString().contains(r)) {
					return;
				}
			}
		}else if(roleCollects.toString().contains(roles)) {
			return;
		}
		
		String acctType = dataArea.getInput().getString(SysDict.A.acct_type.getId());
		String empAcctType = ApBusinessParmApi.getValue("EMP_ACCT_TYPE");//员工账户的账号类型
		
		if(CommUtil.isNotNull(acctType) && acctType.equals(empAcctType)) {
			throw DpErr.Dp.E0461();
		}
		
		String acctNo = dataArea.getInput().getString(SysDict.A.acct_no.getId());
		
		if(CommUtil.isNull(acctNo)) {
			acctNo = dataArea.getInput().getString(SysDict.A.card_no.getId());
			if(CommUtil.isNull(acctNo)) {
				return;
			}
		}
		
		IoDpQueryMainAcctIn acctIn = BizUtil.getInstance(IoDpQueryMainAcctIn.class);
		acctIn.setAcct_no(acctNo);
		acctIn.setAcct_type(acctType);
		
		SrvIoDpQueryAcct ioDpQueryAcct = BizUtil.getInstance(SrvIoDpQueryAcct.class);
		IoDpQueryMainAcctOut acctOut = ioDpQueryAcct.qryAcctMainInfo(acctIn);
		
		if(acctOut.getAcct_type().equals(empAcctType)) {
			throw DpErr.Dp.E0461();
		}
	}
*/	
	/**
	 * 交易前处理-初始化包流水
	 */
//	@Override
//	public PackageSequence initPackageSequence(DataArea dataArea) {
//		
//		BizUtil.getTrxRunEnvs().setRuntime_seq(1L);// 运行期序号
//		BizUtil.getTrxRunEnvs().setAccounting_event_seq(1L); // 会计事件序号
//		
//		return super.initPackageSequence(dataArea);
//	}

//}
