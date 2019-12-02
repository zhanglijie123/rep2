package cn.sunline.icore.aptran.branch;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branchDao;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.engine.data.DataArea;

public class BranchTranTest extends UnitTest {

	@Before
	public void insertInfo() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
	}

	public void testAp1040() {
		DataArea requestDataArea = newTranReq("1040");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111112"));

		requestDataArea.getInput().put("branch_id", "1234567");
		requestDataArea.getInput().put("org_id", "999");
		requestDataArea.getInput().put("branch_name", "测试机构");
		requestDataArea.getInput().put("real_branch_ind", "Y");
		requestDataArea.getInput().put("branch_address", "深圳");
		requestDataArea.getInput().put("branch_phone", "13333333333");
		requestDataArea.getInput().put("postcode", "500600");
		requestDataArea.getInput().put("contacts", "李四");
		requestDataArea.getInput().put("contacts_phone", "18888888888");
		requestDataArea.getInput().put("branch_function_class", "2");
		requestDataArea.getInput().put("swift_no", "121");
		requestDataArea.getInput().put("holiday_code", "20171010");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}

	public void testAp1041() {
		DataArea requestDataArea = newTranReq("1041");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111112"));

		// 构造请求报文
		apb_branch bra = BizUtil.getInstance(apb_branch.class);
		bra.setOrg_id("999");
		bra.setBranch_id("1234567");
		bra.setBranch_name("测试2");
		bra.setReal_branch_ind(E_YESORNO.NO);
		Apb_branchDao.insert(bra);

		requestDataArea.getInput().put("branch_id", "1234567");
		requestDataArea.getInput().put("org_id", "999");
		requestDataArea.getInput().put("branch_name", "测试机构");
		requestDataArea.getInput().put("real_branch_ind", "Y");
		requestDataArea.getInput().put("branch_address", "深圳");
		requestDataArea.getInput().put("branch_phone", "13333333333");
		requestDataArea.getInput().put("postcode", "500600");
		requestDataArea.getInput().put("contacts", "李四");
		requestDataArea.getInput().put("contacts_phone", "18888888888");
		requestDataArea.getInput().put("branch_function_class", "2");
		requestDataArea.getInput().put("swift_no", "121");
		requestDataArea.getInput().put("holiday_code", "20171010");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}

	@Test
	public void testAp1042() {
		DataArea requestDataArea = newTranReq("1042");
		BizUtil.getTrxRunEnvs().setBusi_org_id("99");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("111111"));

		// 构造请求报文
		apb_branch bra = BizUtil.getInstance(apb_branch.class);
		bra.setOrg_id("99");
		bra.setBranch_id("7890123432");
		bra.setBranch_name("测试2");
		bra.setReal_branch_ind(E_YESORNO.NO);
		Apb_branchDao.insert(bra);

		String branch_id = "7890123432";
		requestDataArea.getCommReq().put("page_start", "1");
		requestDataArea.getInput().put("branch_id", null);
		requestDataArea.getInput().put("branch_name", "A");
		requestDataArea.getInput().put("branch_query_scope", "R");
		requestDataArea.getInput().put("branch_function_class", null);
		
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}

	@Test
	public void testAp1043() {
		DataArea requestDataArea = newTranReq("1043");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("0000001043"));

		requestDataArea.getInput().put("brch_relation_code", "100");
		requestDataArea.getInput().put("ccy_code", "*");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);
	}
	
	@Test
	public void testAp1044() {
		DataArea requestDataArea = newTranReq("1044");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("0000001044"));

		requestDataArea.getInput().put("brch_relation_code", "100");
		requestDataArea.getInput().put("ccy_code", "*");
		
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
