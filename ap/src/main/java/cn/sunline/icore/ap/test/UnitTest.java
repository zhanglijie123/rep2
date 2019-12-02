package cn.sunline.icore.ap.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;

import cn.sunline.common.util.RandomGUID;
import cn.sunline.edsp.engine.online.plugin.config.OnlineEngineConfigManager;
import cn.sunline.edsp.engine.online.plugin.engine.InServiceController;
import cn.sunline.edsp.engine.online.plugin.engine.InServiceController.ServiceCategory;
import cn.sunline.edsp.engine.online.plugin.engine.OnlineEngineTemplate;
import cn.sunline.edsp.engine.online.plugin.util.OnlineEngineUtils;
import cn.sunline.edsp.microcore.boot.Bootstrap;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.base.RequestData;
import cn.sunline.ltts.base.ResponseData;
import cn.sunline.ltts.base.logging.LogConfigManager;
import cn.sunline.ltts.base.pkg.PkgFactory;
import cn.sunline.ltts.base.pkg.PkgMode;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.base.srv.GlobalServiceContext;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.ltts.core.api.util.ProfileSwitcher;
//import cn.sunline.ltts.core.startup.PluginServiceManager;
import cn.sunline.ltts.engine.biz.runtime.EngineContext;
import cn.sunline.ltts.engine.biz.runtime.EngineRuntimeContext;
import cn.sunline.ltts.engine.data.DataArea;
import cn.sunline.ltts.plugin.online.spi.IOnlineIDERunner;

/**
 * <p>
 * 文件功能说明： 交易及服务单元测试基类 需要做单元测试的交易，可以创建测试程序（java类），继承该类即可
 * 
 * 交易单元测试方法：newTranReq-->executeTran-->(auto
 * rollback)-->isTranSuccess/isTranFailure 服务单元测试方法：newCommReq-->call service in
 * unit test-->assert-->(auto rollback)
 * 
 * 注意，单元测试模式下，数据库采用的是回滚模式，每一个交易执行结束后回滚事务。
 * 
 * </p>
 * 
 * @Author Administrator
 *         <p>
 *         <li>2016年10月19日-上午9:09:09</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161019 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class UnitTest {

	private static final BizLog bizlog = BizLogUtil.getBizLog(UnitTest.class);

	static {
		try {
			loadPlatform();// 一个jvm下只加载一次
		} catch (Exception e) {
			bizlog.error("Load Platform Failed! [%s]", e, e.getMessage());
		}
	}

	private static void loadPlatform() throws Exception {
		new IOnlineIDERunner() {
			@Override
			public void run(String[] params) {
				// 空实现
			}
		}._run(new String[] { "true" }); // 设置当前运行模式为IDE_DEBUG
		System.setProperty("setting.file", "setting.dev.properties");
		System.setProperty("dmbClientFile", "dmb-client.dev.properties");
		System.setProperty("log4j.configurationFile", "ltts_log_dev.xml");

		System.setProperty("ltts.home", new File(System.getProperty("user.dir"), ".").getCanonicalPath());
		System.setProperty("ltts.log.home", new File(System.getProperty("user.dir"), ".").getCanonicalPath());
		System.setProperty("ltts.vmid", "UnitTestApp");

		LogConfigManager.get().setCurrentSystemType(LogConfigManager.SystemType.onl);

		Bootstrap.main(null);
		;

		ProfileSwitcher.get().useDebugListener = true;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					LttsCoreBeanUtil.getDBConnectionManager().close();
				} catch (Exception e) {
					bizlog.error("DBConnection close failed! [%s]", e, e.getMessage());
				}

				try {
					Thread.sleep(2 * 1000L);// 停止JVM,停止2秒，让后台线程池中的任务跑完
				} catch (Exception ex) {// 如果有异常则强制停止
					bizlog.error("Thread sleep exception! [%s]", ex, ex.getMessage());
				}

			}
		});
	}

	@After
	public void after() {
		LttsCoreBeanUtil.getDBConnectionManager().rollback();
	}

	/**
	 * 传入处理码，构造一个空的交易请求对象
	 * 
	 * @param prcscd
	 * @return
	 */
	public DataArea newTranReq(String prcscd) {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> input = new HashMap<String, Object>();
		Map<String, Object> sys = new HashMap<String, Object>();

		sys.put("prcscd", prcscd);

		req.put("sys", sys);
		req.put("input", input);

		return DataArea.buildWithData(req);
	}

	/**
	 * 交易执行入口
	 * 
	 * @param data
	 * @return
	 */
	public DataArea executeTran(DataArea requestDataArea) {
		if (requestDataArea == null)
			org.junit.Assert.fail("Request data is null.");

		String reqMessage = PkgFactory.get().getPkgWrapper().format(requestDataArea, null, PkgMode.request);
		bizlog.info("请求数据：%s", reqMessage);

		// 屏蔽掉报文中送的_debug标志
//		requestDataArea.getSystem().remove(PkgConfigConstants.NAME_DEBUG);
		// 添加事务控制的标志
		EngineRuntimeContext debugctx = OnlineEngineUtils.createRequestContext("");
//		debugctx.setisInnerSys(!("false".equals(requestDataArea.getSystem().get("isInnerSys"))));
		init(requestDataArea, debugctx);

		RequestData request = new RequestData("", requestDataArea);
		
		InServiceController serviceController;

		if ("s".equals(request.getRequestHeader().get("ServiceCategory"))) {
			serviceController = new InServiceController(request.getRequestHeader().getServiceCode(), null,
					ServiceCategory.S);
		} else {
			String flowTranId = request.getRequestHeader().getServiceCode();
			serviceController = new InServiceController(flowTranId);

		}
		serviceController.setInnerServiceCode("*");

		OnlineEngineTemplate engineTemplate = new OnlineEngineTemplate();

		ResponseData response = engineTemplate.process(new Date(), request, serviceController);

		String resMessage = PkgFactory.get().getPkgWrapper().format(response.getBody(), null, PkgMode.response);
		
		bizlog.info("返回数据：%s", resMessage);

		return response.getData();
	}

	private void init(DataArea area_, EngineRuntimeContext reqCtx) {
		// 生成系统调用流水号、业务流水号
		String sysSeq = getDebugSysCallSeq();
		String bizSeq = getDebugBizCallSeq();

		// 2014.12.24 别名需要处理下
//		if (reqCtx.isInnerSys()) {
			// 主交易流水
			area_.getCommReq().put("zjyilush", sysSeq);
			// 业务流水号
			area_.getCommReq().put("yewulush", bizSeq);
			// 交易流水
			area_.getCommReq().put("jiaoyils", sysSeq);
//		} else {
			// 主交易流水
			area_.getCommReq().put("SYS_REF_NO", sysSeq);
			// 业务流水号
			area_.getCommReq().put("BIZ_REF_NO", bizSeq);
			// 交易流水
			area_.getCommReq().put("TXN_REF_NO", sysSeq);
//		}

		// 设置主事务ID
//		reqCtx.setMainTsnId(sysSeq);

		/*RequestDataHelper rdh = RequestDataHelper.get(area_);
		boolean isFlowTran = true;
		if (StringUtil.isNotEmpty(rdh.getBizSrvId()))
			isFlowTran = false;
		reqCtx.setFlowTran(isFlowTran);

		if (isFlowTran) {
			// 设置事务传播方式
			TransactionServiceController ctrl = EngineExtensionManager.get().getTransactionProcessCallback()
					.getTransactionController(rdh.getTxnCd());
		} else {
			// 设置事务传播方式
			BizServiceController ctrl = EngineExtensionManager.get().getTransactionProcessCallback()
					.getBizServiceController(rdh.getBizSrvId());
		}*/
	}

	private static String getRandomGUID() {
		RandomGUID myGUID = new RandomGUID(true);
		return myGUID.getValueAfterMD5();
	}

	private static String getDebugSysCallSeq() {
		return getRandomGUID();
	}

	private static String getDebugBizCallSeq() {
		return getRandomGUID();
	}

	/**
	 * 判断交易是否成功
	 * 
	 * @param res
	 */
	public void isTranSuccess(DataArea res) {
		assertNotNull(res);
		assertNotNull(res.getSystem());
		assertEquals("S", res.getSystem().getString("status"));
	}

	/**
	 * 判断交易是否失败
	 * 
	 * @param res
	 */
	public void isTranFailure(DataArea res) {
		assertNotNull(res);
		assertNotNull(res.getSystem());
		assertEquals("F", res.getSystem().getString("status"));
	}

	/**
	 * 判断交易是否失败，并断言错误码
	 * 
	 * @param res
	 * @param errorcd
	 */
	public void isTranFailure(DataArea res, String errorcd) {
		assertNotNull(res);
		assertNotNull(res.getSystem());
		assertEquals("F", res.getSystem().getString("status"));
		assertNotNull(res.getSystem().getString("erorcd"));
		assertEquals(errorcd, res.getSystem().getString("erorcd"));
	}

	/**
	 * 传入公共请求数据，构造上下文
	 * 
	 * @param commReq
	 */
	public void newCommReq(Map<String, Object> commReq) {
		LttsCoreBeanUtil.getEngineResourceManager().clearThreadCache(false);

		assertNotNull(commReq);

		if (commReq.get("initiator_seq") == null)// 交易流水
			commReq.put("initiator_seq", getRandomGUID());
		if (commReq.get("busi_seq") == null)// 业务流水
			commReq.put("busi_seq", getRandomGUID());
		if (commReq.get("sponsor_system") == null)// 发起方系统编码
			commReq.put("sponsor_system", "100");
		if (commReq.get("initiator_system") == null)// 调用方系统编码
			commReq.put("initiator_system", "100");
		if (commReq.get("channel_id") == null)// 渠道ID
			commReq.put("channel_id", "101");

		DataArea dataArea = DataArea.buildWithEmpty().setCommReq(commReq);
		dataArea.getSystem().setString("prcscd", "JUNIT");

		EngineRuntimeContext debugctx = OnlineEngineUtils.createRequestContext("JUNIT");
		RequestData requestData = new RequestData("", dataArea);
		EngineContext.push(new EngineContext(requestData, debugctx));

		InServiceController controller = new InServiceController("");
		controller.setServiceCategory(ServiceCategory.T);
		
		OnlineEngineConfigManager.get().getOnlineEngineExtensionPoint().beforeBizEnv(requestData, controller);

		BizUtil.getTrxRunEnvs().setTrxn_code("JUNIT");
		BizUtil.getTrxRunEnvs().setTrxn_desc("JUNIT");
	}

}
