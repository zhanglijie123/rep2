package cn.sunline.icore.aptran.systemParm;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.engine.data.DataArea;
public class SystemParmTest extends UnitTest{

	private static final BizLog bizlog = BizLogUtil.getBizLog(SystemParmTest.class);
	
	@Before
	public void insertInfo() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
	}
	
	@Test
	public void test1030(){
		DataArea requestDataArea = newTranReq("1030");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111112"));

		requestDataArea.getInput().put("main_key", "APP");
		requestDataArea.getInput().put("parm_desc", "下");

		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}
	
	private Map<String, Object> genCommReq(String trxnSeq) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("trxn_seq", trxnSeq);
		m.put("busi_teller_id", "t1");
		m.put("busi_seq", trxnSeq);
		m.put("busi_org_id", "99");
		m.put("initiator_system", "LOCAL");
		m.put("channel_id", "102");
		m.put("sponsor_system", "COUNTER");
		m.put("jiaoyigy", "交易柜员");
		m.put("branch_id", "99");
		return m;
	}
}
