package cn.sunline.icore.aptran.currency;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.engine.data.DataArea;

public class CurrencyTest extends UnitTest{
	
	@Before
	public void before() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
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
	
/***
 *  货币参数列表查询交易测试方法
 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testAp1060() {
		DataArea requestDataArea = newTranReq("1060");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("6060"));
		requestDataArea.getCommReq().put("page_size", "20");
		requestDataArea.getCommReq().put("page_start", "0");
		requestDataArea.getInput().put("org_id", "99");
		requestDataArea.getInput().put("ccy_code", "C");
		requestDataArea.getInput().put("ccy_name", "人");
		requestDataArea.getInput().put("country_code", "C");
		//构造请求报文
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 
        
		Options list01 = (Options) res.getOutput().get("list01");
        for(Object list : list01){
        	Map m = (Map)list;
        	System.out.println(m.get("ccy_code") + ":" + m.get("ccy_name"));
        }
	}
}
