package cn.sunline.icore.aptran.audit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.engine.data.DataArea;

public class AuditTranTest extends UnitTest {

	@Before
	public void insertInfo() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
	}

	@Test
	public void testAp1140() {
		DataArea requestDataArea = newTranReq("1140");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111112"));

		requestDataArea.getInput().put("audit_type", "P");

		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}

	@Test
	public void testAp1141() {
		DataArea requestDataArea = newTranReq("1141");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111112"));

		requestDataArea.getInput().put("audit_seq", "2017010100000203");
		requestDataArea.getInput().put("audit_type", "P");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}


	private Map<String, Object> genCommReq(String trxnSeq) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("trxn_seq", trxnSeq);
		m.put("busi_teller_id", "t1");
		m.put("busi_seq", trxnSeq);
		m.put("busi_org_id", "99");
		m.put("initiator_system", "100");
		m.put("channel_id", "102");
		m.put("sponsor_system", "100");
		m.put("jiaoyigy", "交易柜员");
		m.put("branch_id", "99");
		return m;
	}
}
