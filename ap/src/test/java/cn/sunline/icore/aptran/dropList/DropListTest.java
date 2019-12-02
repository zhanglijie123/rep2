package cn.sunline.icore.aptran.dropList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
import cn.sunline.ltts.engine.data.DataArea;

public class DropListTest extends UnitTest {
	
	private Map<String, Object> genCommReq(String trxnSeq) {
		trxnSeq = System.currentTimeMillis() + "";
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
/**
 *  下拉字典参数列表查询交易测试方法
 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testAp1050() {
		DataArea requestDataArea = newTranReq("1050");
		//构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("5050"));
		requestDataArea.getCommReq().put("page_size", "20");
		requestDataArea.getCommReq().put("page_start", "0");
		requestDataArea.getInput().put("drop_list_type", "DATA_MART");
		requestDataArea.getInput().put("org_id", "99");
		//构造请求报文
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 
        
        Options list01 = (Options)res.getOutput().get("list01");
        for(Object list : list01){
        	Map m = (Map)list;
        	System.out.println(m.get("drop_list_type") + ":" + m.get("drop_list_value"));
        }
	}
	
	@Test
	public void testAp1051(){
		DataArea requestDataArea = newTranReq("1051");
		//构造公共请求
		//新增
		Options options = new DefaultOptions();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("drop_list_type", "JUNIT");
		map.put("drop_list_value", "TEST");
		map.put("drop_list_desc", "测试");
		map.put("data_sort", "1");
		map.put("module", "ap");
		map.put("operater_ind", E_DATAOPERATE.ADD.getValue());
		options.add(map);
		
		requestDataArea.getCommReq().putAll(genCommReq("50511"));
		requestDataArea.getInput().put("list01", options);

		//构造请求报文
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 
        
		//修改
		requestDataArea.getCommReq().putAll(genCommReq("50512"));
		map.put("drop_list_type", "DATA_MART");
		map.put("drop_list_value", "RUN_ENVS");
		map.put("drop_list_desc", "测试2");
		map.put("data_sort", "2");
		map.put("module", "ap");
		map.put("data_version", 1);
		map.put("operater_ind", E_DATAOPERATE.MODIFY.getValue());
		requestDataArea.getInput().put("list01", options);
		
		//构造请求报文
        DataArea res2 = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 
        
		//删除
		requestDataArea.getCommReq().putAll(genCommReq("50513"));
		map.put("drop_list_type", "DATA_MART");
		map.put("drop_list_value", "RUN_ENVS");
		map.put("drop_list_desc", "RUN_ENVS");
		map.put("data_sort", "2");
		map.put("module", "ap");
		map.put("operater_ind", E_DATAOPERATE.DELETE.getValue());
		requestDataArea.getInput().put("list01", options);
		//构造请求报文
        DataArea res3 = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 
	}
	
	@Test
	public void testAp1052(){
		DataArea requestDataArea = newTranReq("1052");
		//构造公共请		
		requestDataArea.getCommReq().putAll(genCommReq("50511"));
		requestDataArea.getInput().put("drop_list_type", "");
		requestDataArea.getInput().put("drop_list_name", "");
		requestDataArea.getInput().put("module", "");
		requestDataArea.getInput().put("diff_org_id", "");
		requestDataArea.getInput().put("dynamic_drop_list_type", "dynamicSelBrchRelayionCode");

		//构造请求报文
        DataArea res = super.executeTran(requestDataArea);
        super.isTranSuccess(res); 

        Options list01 = (Options)res.getOutput().get("list01");
        System.out.println(list01.size());
	}
}
