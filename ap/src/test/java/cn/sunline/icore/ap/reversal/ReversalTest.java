//package cn.sunline.icore.ap.reversal;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.ltts.busi.ltts.core.plugin.tables.KSysBizSvc.Ksys_fwbdxxDao;
//import cn.sunline.ltts.busi.ltts.core.plugin.tables.KSysBizSvc.ksys_fwbdxx;
//import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
//import cn.sunline.icore.ap.seq.ApBaseSeq;
//import cn.sunline.icore.ap.util.BizUtil;
//
//public class ReversalTest extends UnitTest {
//	
//	private static final String REVERSAL_EVENT_ID = "delAppSequence";
//	private static final String SERVICE_BIND_ID = "REV00001";
//
//	@Before
//	public void init() {
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "99");
//		newCommReq(commReq);
//
//		App_reversal_eventDao.deleteOne_odb1(REVERSAL_EVENT_ID);
//		app_reversal_event eventDef = BizUtil.getInstance(app_reversal_event.class);
//		eventDef.setReversal_event_id(REVERSAL_EVENT_ID);
//		eventDef.setReversal_event_desc("demo");
//		eventDef.setImplement_class("cn.sunline.icore.ap.servicetype.SrvApDemo");
//		eventDef.setImplement_method("delAppSequence");
//		System.out.println(app_sequence.class.getName());
//		eventDef.setInformation_object("cn.sunline.icore.ap.tables.TabApBasic$app_sequence");
//		App_reversal_eventDao.insert(eventDef);
//		
//		//登记服务绑定信息
//		ksys_fwbdxx serviceDef = BizUtil.getInstance(ksys_fwbdxx.class);
//		serviceDef.setBdid(SERVICE_BIND_ID);
//		serviceDef.setFwlxid("SrvApDemo");
//		serviceDef.setFwlxsxid("SrvApDemoImpl");
//		serviceDef.setZxqid("core.serviceType.executor.defaultImpl");
//		Ksys_fwbdxxDao.insert(serviceDef);
//	}
//
//	@Test
//	public void test1() {
//		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
//		runEnvs.setTrxn_seq(ApBaseSeq.genSeq("TRXN_SEQ"));
//		
//		app_sequence seqDef = BizUtil.getInstance(app_sequence.class);
//		seqDef.setSeq_org_id("99");
//		seqDef.setSeq_code("demo");
//		seqDef.setSeq_desc("测试");
//		seqDef.setSeq_init_value(1L);
//		seqDef.setSeq_cache_size(100L);
//		seqDef.setSeq_length(10L);
//		seqDef.setSeq_max_value(9999999999L);
//		seqDef.setModule("AP");
//		
//		//
////		ServiceUtil.getServiceProxyObject(SrvApDemo.class, SERVICE_BIND_ID);
//	//	demo.addAppSequence(seqDef);
//		
//		//
//		//ApReversal.process(runEnvs.getTrxn_seq());
//	}
//	
//	
//	
//	
//}
