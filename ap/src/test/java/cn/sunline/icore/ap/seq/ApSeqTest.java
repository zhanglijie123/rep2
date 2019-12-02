package cn.sunline.icore.ap.seq;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.rule.ApBaseBuffer;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;

public class ApSeqTest extends UnitTest{
	
	@Before
	public void init(){
		initial();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initial() {
		super.newCommReq(new HashMap());
		BizUtil.getTrxRunEnvs().setBusi_org_id("99");
	}
	@Test 
	public void testGenSeq1() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("acct_id_code", "22");
		
		ApBaseBuffer.addData("INPUT", map);

		System.out.println("========================================"+ApBaseSeq.genSeq("ACCOUNT_NO"));	

		
		ApBaseBuffer.getBuffer().clear();
		/*
		for(int i=0; i < 1000; i++) {
			System.out.println(ApSeq.buildSeqNo("TRXN_SEQ"));			
		}*/
	}
	
	@Test
	public void testGenSeq2() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		List<Callable<String>> tasks = new ArrayList<Callable<String>>();
		Set<String> set = new HashSet<String>();
		
		int taskNum = 300;
		for(int i=0; i < taskNum; i++) {
			tasks.add(new SeqTask(set));
		}
		pool.invokeAll(tasks);
		assertEquals(taskNum, set.size());
	}
	
	private class SeqTask implements Callable<String> {

		private Set<String> set;
		
		public SeqTask(Set<String> set) {
			this.set = set;
		}

		@Override
		public String call() throws Exception {
			initial();
			String seq = ApBaseSeq.genSeq("TRXN_SEQ");
			System.out.println(seq);		
			set.add(seq);
			return seq;
		}
		
	}
	
	
	@Test
	public void testGenSeq3() {
		BizUtil.getTrxRunEnvs().setBusi_org_id("99");
		String batchNo0 = ApBaseSeq.genSeq("BATCH_NO");
		
		BizUtil.getTrxRunEnvs().setBusi_org_id("001");
		String batchNo1 = ApBaseSeq.genSeq("BATCH_NO");

		BizUtil.getTrxRunEnvs().setBusi_org_id("002");
		String batchNo2 = ApBaseSeq.genSeq("BATCH_NO");
		
		System.out.println(batchNo0);
		System.out.println(batchNo1);
		System.out.println(batchNo2);
		
		assertEquals(batchNo0.length(), 10);
		assertEquals(batchNo0.length(), batchNo1.length());
		assertEquals(batchNo1.length(), batchNo2.length());
		
		long l0 = Long.valueOf(batchNo0) + 2L;
		long l1 = Long.valueOf(batchNo1) + 1L;
		long l2 = Long.valueOf(batchNo2);	
		assertEquals(l0, l1);
		assertEquals(l1, l2);
	}
	
	@Test
	public void testGenSeq4() {
		BizUtil.getTrxRunEnvs().setTrxn_date("20161213");
		BizUtil.getTrxRunEnvs().setSponsor_system("COUNTER");

		BizUtil.getTrxRunEnvs().setBusi_org_id("01");
		String trxnSeq1 = ApBaseSeq.genSeq("TRXN_SEQ");
		
		BizUtil.getTrxRunEnvs().setBusi_org_id("02");
		String trxnSeq2 = ApBaseSeq.genSeq("TRXN_SEQ");
		
		System.out.println(trxnSeq1);
		System.out.println(trxnSeq2);

		String trxnDate1 = trxnSeq1.substring(0,8);
		String trxnDate2 = trxnSeq2.substring(0,8);
		assertEquals(trxnDate1, trxnDate2);
		assertEquals(trxnDate1, "20161213");

		String sponSys1 = trxnSeq1.substring(8,11);
		String sponSys2 = trxnSeq2.substring(8,11);
		assertEquals(sponSys1, sponSys2);
		assertEquals(sponSys1, "COU");
		
		String buisOrgId1 = trxnSeq1.substring(11, 13);
		String buisOrgId2 = trxnSeq2.substring(11, 13);
		assertEquals(buisOrgId1, "01");
		assertEquals(buisOrgId2, "02");

		String seq1 = trxnSeq1.substring(14);
		String seq2 = trxnSeq2.substring(14);	
		assertEquals(seq1.length(), 10);
		assertEquals(seq2.length(), 10);	
	}
	
	
}
