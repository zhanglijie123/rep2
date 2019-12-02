package cn.sunline.icore.aptran.rule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
import cn.sunline.ltts.engine.data.DataArea;

public class RuleTranTest extends UnitTest{
	@Before
	public void insertInfo() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
	}

	/*
	 * 简单规则维护
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void TestAp1080(){
		DataArea requestDataArea = newTranReq("1080");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("108000001"));
		
		
		Options list01 = new DefaultOptions();
		Options list02 = new DefaultOptions();
		int i;
		for(i=0;i<5;i++){
			Map<String,Object> map01 = new HashMap<String,Object>();
			Map<String,Object> map02 = new HashMap<String,Object>();
			map01.put("operater_ind", E_DATAOPERATE.ADD);
			map01.put("rule_group_no", 1);
			map01.put("rule_sort", i);
			map01.put("data_mart", "RunEnvs");
			map01.put("field_name","Test");
			map01.put("data_mapping_operator","EQ");
			map01.put("data_mapping_value","Test");
			map01.put("data_version",1);
			
			map02.putAll(map01);
			list01.add(map01);
			list02.add(map02);
		}		

		requestDataArea.getInput().put("operater_ind", E_DATAOPERATE.ADD);
		requestDataArea.getInput().put("rule_id", "006");
		requestDataArea.getInput().put("rule_desc", "999");
		requestDataArea.getInput().put("rule_scene_code", "999");
		requestDataArea.getInput().put("data_version", "");
		requestDataArea.getInput().put("list01", list01);
		requestDataArea.getInput().put("list02", list02);

		DataArea res = super.executeTran(requestDataArea);
		
		super.isTranSuccess(res);
	}
	
	@Test
	public void TestAp1081(){
		DataArea requestDataArea = newTranReq("1081");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("108100001"));
				
		requestDataArea.getInput().put("rule_id", "001");
		requestDataArea.getInput().put("rule_desc", "1");
		requestDataArea.getInput().put("rule_scene_code", "999");

		DataArea res = super.executeTran(requestDataArea);
		
		super.isTranSuccess(res);
	}
	
	@Test
	public void TestAp1082(){
		DataArea requestDataArea = newTranReq("1082");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("108100001"));
				
		requestDataArea.getInput().put("rule_id", "001");

		DataArea res = super.executeTran(requestDataArea);
		
		super.isTranSuccess(res);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void TestAp1083(){
		DataArea requestDataArea = newTranReq("1083");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("108000001"));
		
		
		Options list01 = new DefaultOptions();
		Options list02 = new DefaultOptions();
		int i;
		for(i=0;i<5;i++){
			Map<String,Object> map01 = new HashMap<String,Object>();
			Map<String,Object> map02 = new HashMap<String,Object>();
			map01.put("operater_ind", E_DATAOPERATE.ADD);
			map01.put("rule_group_no", 1);
			map01.put("rule_sort", i);
			map01.put("data_mart", "RunEnvs");
			map01.put("field_name","Test");
			map01.put("data_mapping_operator","EQ");
			map01.put("data_mapping_value","Test");
			map01.put("data_version",1);
			
			map02.putAll(map01);
			list01.add(map01);
			list02.add(map02);
		}		

		requestDataArea.getInput().put("operater_ind", E_DATAOPERATE.ADD);
		requestDataArea.getInput().put("rule_desc", "999");
		requestDataArea.getInput().put("rule_scene_code", "abc");
		requestDataArea.getInput().put("rule_sort", "1");
		requestDataArea.getInput().put("mapping_result", "1");
		requestDataArea.getInput().put("module", "999");
		requestDataArea.getInput().put("data_version", "");
		requestDataArea.getInput().put("list01", list01);
		requestDataArea.getInput().put("list02", list02);

		DataArea res = super.executeTran(requestDataArea);
		
		super.isTranSuccess(res);
		
	}
	
	@Test
	public void TestAp1084(){
		DataArea requestDataArea = newTranReq("1084");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("108100001"));
				
		requestDataArea.getInput().put("rule_id", "");
		requestDataArea.getInput().put("rule_desc", "");
		requestDataArea.getInput().put("rule_scene_code", "");

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
