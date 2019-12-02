package cn.sunline.icore.aptran.organization;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.engine.data.DataArea;

public class OrganizationTranTest extends UnitTest{
	
	@Test
	public void testAp1010() {
		DataArea requestDataArea = newTranReq("1010");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq());

		//构造请求报文
		requestDataArea.getInput().put("busi_org_id", "test");
		requestDataArea.getInput().put("org_name", "测试");
		requestDataArea.getInput().put("ref_org_id", "99");
		requestDataArea.getInput().put("org_address", "深圳");
		requestDataArea.getInput().put("default_org_ind", "N");

        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res);
	}
	
	@Test
	public void testAp1011() {
		DataArea requestDataArea = newTranReq("1011");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq());

		//构造请求报文
		requestDataArea.getInput().put("busi_org_id", "99");
		requestDataArea.getInput().put("org_name", "测试1");
		requestDataArea.getInput().put("ref_org_id", "99");
		requestDataArea.getInput().put("org_address", "深圳");
		requestDataArea.getInput().put("default_org_ind", "N");
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res);        
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testAp1012() {
		DataArea requestDataArea = newTranReq("1012");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq());
		requestDataArea.getCommReq().put("page_size", "20");
		requestDataArea.getCommReq().put("page_start", "0");
		requestDataArea.getInput().put("busi_org_id", "01");
		requestDataArea.getInput().put("org_name", "农商");
		requestDataArea.getInput().put("org_address", "广东");
		requestDataArea.getInput().put("default_org_ind", "N");

		//构造请求报文
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res);        
        
        Options queryList = (Options) res.getOutput().get("list01");
        for (Object org : queryList) {
        	Map m = (Map) org;
        	System.out.println(m.get("busi_org_id") + ":" + m.get("org_name"));
        }
	}
	
	@Test
	public void testAp1013() {
		DataArea requestDataArea = newTranReq("1013");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq());

		//构造请求报文
		requestDataArea.getInput().put("org_id", "99");
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res);        
        
        assertEquals("99", res.getOutput().getString("org_id"));
        assertEquals("省联社", res.getOutput().getString("org_name"));
	}
	
	private Map<String, Object> genCommReq() {
		String trxnSeq = System.currentTimeMillis() + "";
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
