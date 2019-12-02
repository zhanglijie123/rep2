package cn.sunline.icore.aptran.businessParm;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.engine.data.DataArea;

public class BusinessParmTest extends UnitTest {

	// private static final BizLog bizlog = BizUtil.(BusinessParnTest.class);
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

	@Test
	public void testAp1020() {
		DataArea requestDataArea = newTranReq("1020");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("1020"));
		// 构造请求报文
		requestDataArea.getInput().put("org_id", "99");
		requestDataArea.getInput().put("main_key", "BANK_BASE_CCY");
		requestDataArea.getInput().put("parm_desc", "银行");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);

		// ApBusinessParmInfo info = (ApBusinessParmInfo)
		// res.getOutput().get("main_key");
		Options list01 = (Options)res.getOutput().get("list01");
		Map m = (Map)list01.get(0);
		// 断言结果
		assertEquals("银行基础货币", m.get("parm_desc").toString());
		assertEquals("CNY", m.get("parm_value").toString());
		// assertEquals("银行基础货币", info.getParm_desc());

	}


	public void testAp1021() {
		DataArea requestDataArea = newTranReq("1021");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("101121"));
		requestDataArea.getCommReq().put("page_size", "20");
		requestDataArea.getCommReq().put("page_start", "0");

		// 构造请求报文
		requestDataArea.getInput().put("org_id", "99");
		requestDataArea.getInput().put("main_key", "BANK_BASE_CCY");
		requestDataArea.getInput().put("sub_key", "*");
		requestDataArea.getInput().put("parm_desc", "银行基础货币");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);

		//@SuppressWarnings("unchecked")
		//List<ApBusinessParmInfo> listInfo = (List<ApBusinessParmInfo>) res.getOutput().get("queryList");
		//for (ApBusinessParmInfo list : listInfo) {
		//	System.out.println(list.getMain_key() + ":" + list.getParm_desc());
		//}

	}


	public void testAp1022() {
		DataArea requestDataArea = newTranReq("1022");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("10115555"));

		// 构造请求报文
		requestDataArea.getInput().put("org_id", "99");
		requestDataArea.getInput().put("main_key", "BANK_BASE_CCY");
		requestDataArea.getInput().put("sub_key", "*");
		requestDataArea.getInput().put("parm_desc", "hhh");
		requestDataArea.getInput().put("parm_value", "填写");
		requestDataArea.getInput().put("module", "AP");

		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
		// super.isTranFailure(res);
	}
}
