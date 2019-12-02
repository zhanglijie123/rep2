package cn.sunline.icore.aptran.transaction;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.engine.data.DataArea;
public class TransactionSeqTest extends UnitTest{

	@Before
	public void initParamter(){
		Map<String,Object> commReq=new HashMap<>();
		commReq.put("bus_org_id", "99");
		newCommReq(commReq);
	}
	@Test
	public void testAP1110(){
		//获取DataArea对象
		DataArea requestDataArea = newTranReq("1110");
		BizUtil.getTrxRunEnvs().setBusi_org_id("99");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111111"));
		//构造查询条件
		requestDataArea.getInput().put("trxn_date", "20170101");
		
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}
	/**
	 * 构造公共请求
	 */
	private Map<String, Object> genCommReq(String trxnSeq) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("trxn_seq", trxnSeq);
		m.put("busi_teller_id", "t1");
		m.put("busi_seq", trxnSeq);
		m.put("busi_org_id", "99");
		m.put("caller_system", "LOCAL");
		m.put("channel_id", "102");
		m.put("sponsor_system", "COUNTER");
		m.put("jiaoyigy", "交易柜员");
		m.put("branch_id", "99");
		return m;
	}
}
