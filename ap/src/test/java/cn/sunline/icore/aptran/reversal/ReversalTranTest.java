//package cn.sunline.icore.aptran.reversal;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.icore.ap.type.ComApBook.ApReversalExceptionInfo;
//import cn.sunline.ltts.engine.data.DataArea;
//import cn.sunline.icore.ap.util.BizUtil;
//
//public class ReversalTranTest extends UnitTest {
//	private static final String ORIGINAL_TRXN_SEQ = "000001"; //原交易流水
//	private static final String TRXN_SEQ = "000002";          //冲正流水
//	private static final long TOTAL_COUNT = 100L;
//	@Before
//	public void init() {
//		super.newCommReq(new HashMap<String, Object>());
//		
//		aps_reversal_exception exp = BizUtil.getInstance(aps_reversal_exception.class);
//		exp.setOriginal_trxn_seq(ORIGINAL_TRXN_SEQ);
//		exp.setTrxn_seq(TRXN_SEQ);
//		exp.setTrxn_date(BizUtil.formatDate(new Date()));
//		exp.setReversal_event_id("event1");
//		exp.setError_stack("QQQ");
//		Aps_reversal_exceptionDao.insert(exp);
//		
//		for(int i=1; i < TOTAL_COUNT; i++) {
//			exp.setTrxn_seq((100000 + i) + "");
//			Aps_reversal_exceptionDao.insert(exp);
//		}
//		
//	}
//	
//	/**
//	 * 
//	 */
//	public void testAp1000() {
//		
//	}
//	
//	/**
//	 * 
//	 */
//	@Test
//	public void testAp1001() {
//		DataArea requestDataArea = newTranReq("1001");
//		//构造公共请求
//		requestDataArea.getCommReq().putAll(genCommReq("111111"));
//		requestDataArea.getCommReq().put("page_size", "20");
//		requestDataArea.getCommReq().put("page_start", "0");
//
//		//构造请求报文
//		requestDataArea.getInput().put("original_trxn_seq", "000001");
//
//        DataArea res = super.executeTran(requestDataArea);
//        super.isTranSuccess(res);
//        
//        assertEquals(Long.valueOf(TOTAL_COUNT), (Long) res.getCommRes().get("total_count"));
//        
//        @SuppressWarnings("unchecked")
//		List<ApReversalExceptionInfo>  list = (List<ApReversalExceptionInfo>) res.getOutput().get("queryList");
//        assertEquals(ORIGINAL_TRXN_SEQ, list.get(0).getOriginal_trxn_seq());
//        System.out.print(list.size());
//        assertTrue(20 == list.size());
//	}
//	
//	@Test
//	public void testAp1002() {
//		DataArea requestDataArea = newTranReq("1002");
//		//构造公共请求
//		requestDataArea.getCommReq().putAll(genCommReq("222222"));
//		//构造请求报文
//        requestDataArea.getInput().put("trxn_seq", TRXN_SEQ); //原交易流水
//
//
//        DataArea res = super.executeTran(requestDataArea);
//        super.isTranSuccess(res);
//        
//        assertEquals(ORIGINAL_TRXN_SEQ, res.getOutput().getString("original_trxn_seq"));
//        assertEquals(TRXN_SEQ, res.getOutput().getString("trxn_seq"));
//        assertEquals("QQQ", res.getOutput().getString("error_stack"));
//
//        
//	}
//	
//	private Map<String, Object> genCommReq(String trxnSeq) {
//		Map<String, Object> m = new HashMap<String, Object>();
//		m.put("trxn_seq", trxnSeq);
//		m.put("teller_id", "t1");
//        m.put("busi_seq", trxnSeq);
//        m.put("teller_id", "t1");
//        m.put("busi_org_id", "99");
//        m.put("initiator_system", "LOCAL");
//        m.put("channel_id", "102");
//        m.put("sponsor_system", "COUNTER");
//        m.put("jiaoyigy", "交易柜员");
//        m.put("branch_id", "99");
//        return m;
//	}
//}
