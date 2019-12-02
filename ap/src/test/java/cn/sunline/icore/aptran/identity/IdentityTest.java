package cn.sunline.icore.aptran.identity;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.engine.data.DataArea;
public class IdentityTest extends UnitTest{

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
	
	
/**
 *  证件种类参数列表查询交易测试
 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testAp1070() {
		DataArea requestDataArea = newTranReq("1070");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("7070"));
		requestDataArea.getCommReq().put("page_size", "20");
		requestDataArea.getCommReq().put("page_start", "0");
		requestDataArea.getInput().put("org_id", "99");
		requestDataArea.getInput().put("id_type", "1");
		requestDataArea.getInput().put("id_desc", "身份");
		//构造请求报文
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 
        
		Options list01 = (Options) res.getOutput().get("list01");
        for(Object list : list01){
        	Map m = (Map)list;
        	System.out.println(m.get("id_type") + ":" + m.get("id_desc"));
        }
	}
}
