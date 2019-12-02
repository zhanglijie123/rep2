package cn.sunline.icore.ap.rule;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_OPERATOR;
import cn.sunline.icore.ap.rule.ApBaseBuffer;
import cn.sunline.icore.ap.rule.ApBaseRule;
import cn.sunline.icore.ap.tables.TabApRule.App_ruleDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_dataDao;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_data;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;


public class ApRuleTest extends UnitTest{
	
	@Before
	public void init() throws Exception{
		Map<String, Object> commReq = new HashMap<String, Object>();
    	commReq.put("busi_org_id", "99");
    	commReq.put("trxn_code", "1010");
    	commReq.put("trxn_desc", "测试");
    	commReq.put("trxn_date", "20161208");
    	newCommReq(commReq);
    	
    	Statement st = LttsCoreBeanUtil.getDBConnectionManager().getConnection().createStatement();
    	String sql = "delete from app_rule";
    	String sql2 = "delete from app_rule_data";
    	st.execute(sql);
    	st.execute(sql2);
    	st.close();
	}

	@Test
	public void testClearBuffer() {
		Map<String,Object> data  = new HashMap<String,Object>();
		data.put("aaa", "1");
		data.put("bbb", "110");
		data.put("ccc", "21");
		data.put("ddd", "11");
		ApBaseBuffer.getBuffer().putAll(data);
		
		ApBaseBuffer.getBuffer().clear();
		
		Assert.assertTrue(ApBaseBuffer.getBuffer().isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddDataToBuffer() {
		Map<String,Object> data  = new HashMap<String,Object>();
		data.put("aaa", "1");
		data.put("bbb", "110");
		data.put("ccc", "21");
		data.put("ddd", "11");
		ApBaseBuffer.addData("RUN_ENVS", data);
		
		Map<String,Object> map = ApBaseBuffer.getBuffer();
		Assert.assertNotNull(map.get("RUN_ENVS"));
	}

	/**
	 * 正常mapping
	 */
	@Test
	public void testMapping() {
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("INPUT");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)(long)1);
		ruleInfo.setMapping_result("mapping1");
		ruleInfo.setException_rule_id("002");
		ruleInfo.setData_version((long)1);		
		App_ruleDao.insert(ruleInfo);		
		//数据一
		app_rule_data ruleData = BizUtil.getInstance(app_rule_data.class);
		ruleData.setOrg_id("99");
		ruleData.setRule_id("001");
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)(long)1);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("123");
		ruleData.setData_version((long)1);
		App_rule_dataDao.insert(ruleData);
		//数据二
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)2);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("B");
		ruleData.setData_mapping_operator(E_OPERATOR.NO_EQUAL);
		ruleData.setData_mapping_value("123");
		App_rule_dataDao.insert(ruleData);
		//数据三
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)3);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("C");
		ruleData.setData_mapping_operator(E_OPERATOR.IN);
		ruleData.setData_mapping_value("abcde");
		App_rule_dataDao.insert(ruleData);
		//数据四
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)4);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("D");
		ruleData.setData_mapping_operator(E_OPERATOR.NO_IN);
		ruleData.setData_mapping_value("abcde");
		App_rule_dataDao.insert(ruleData);
		//数据五
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)5);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("E");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		//数据六
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)6);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("F");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN_OR_EQUAL);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);		
		//数据七
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)7);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("G");
		ruleData.setData_mapping_operator(E_OPERATOR.LESS_THAN);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		//数据八
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)8);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("H");
		ruleData.setData_mapping_operator(E_OPERATOR.LESS_THAN_OR_EQUAL);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);		
		//数据九
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)9);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("I");
		ruleData.setData_mapping_operator(E_OPERATOR.REQULAR_EXPRESSION);
		ruleData.setData_mapping_value("^TEST.*$");
		App_rule_dataDao.insert(ruleData);	
        //例外规则
		ruleData.setOrg_id("99");
		ruleData.setRule_id("002");
		ruleData.setRule_group_no((long)2);
		ruleData.setRule_sort((long)(long)1);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("456");
		ruleData.setData_version((long)1);
		App_rule_dataDao.insert(ruleData);
		
		Map<String,Object> data  = new HashMap<String,Object>();
		data.put("A", 123.000000);//相等  123
		data.put("B", "");//不相等 123
		data.put("C", "bcd");//包含 abcde
		data.put("D", "fg");//不包含abcde
		data.put("E", "100.000001");//大于 100
		data.put("F", "100.000000");//大于等于 100
		data.put("G", "");//小与100
		data.put("H", "");//小与等于100
		data.put("I", "TESTAAAAA");//正则表达式
		ApBaseBuffer.addData("INPUT", data);
		
		boolean b = ApBaseRule.mapping("001");
		Assert.assertTrue(b);
	}
	
	/**
	 * 反例
	 * 满足例外，mapping失败
	 */
	@Test
	public void testMapping2(){
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("RUN_ENVS");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)(long)1);
		ruleInfo.setMapping_result("mapping1");
		ruleInfo.setException_rule_id("002");
		ruleInfo.setData_version((long)1);		
		App_ruleDao.insert(ruleInfo);		
		//数据一
		app_rule_data ruleData = BizUtil.getInstance(app_rule_data.class);
		ruleData.setOrg_id("99");
		ruleData.setRule_id("001");
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)(long)1);
		//ruleData.setData_mart("RUN_ENVS");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN);
		ruleData.setData_mapping_value("100");
		ruleData.setData_version((long)1);
		App_rule_dataDao.insert(ruleData);
        //例外规则
		ruleData.setOrg_id("99");
		ruleData.setRule_id("002");
		ruleData.setRule_group_no((long)2);
		ruleData.setRule_sort((long)(long)1);
		//ruleData.setData_mart("RUN_ENVS");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("456");
		ruleData.setData_version((long)1);
		App_rule_dataDao.insert(ruleData);
		
		Map<String,Object> data  = new HashMap<String,Object>();
		data.put("A", 456);//相等  123
		ApBaseBuffer.addData("RUN_ENVS", data);
		
		boolean b = ApBaseRule.mapping("001");
		Assert.assertFalse(b);
	}
	
	/**
	 * 正例
	 * 有多个组，group1、group2、group3
	 * group1 group2 校验失败
	 * group3 校验成功
	 */
	@Test
	public void testMapping3(){
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("INPUT");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)1);
		ruleInfo.setMapping_result("mapping1");
		ruleInfo.setData_version((long)1);		
		App_ruleDao.insert(ruleInfo);	
		//group1
		app_rule_data ruleData = BizUtil.getInstance(app_rule_data.class);
		ruleData.setOrg_id("99");
		ruleData.setRule_id("001");
		ruleData.setRule_group_no((long)1);
		ruleData.setRule_sort((long)1);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN);
		ruleData.setData_mapping_value("100");
		ruleData.setData_version((long)1);
		App_rule_dataDao.insert(ruleData);
		//group2
		ruleData.setRule_group_no((long)2);
		ruleData.setRule_sort((long)1);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("B");
		ruleData.setData_mapping_operator(E_OPERATOR.LESS_THAN);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		//group3
		ruleData.setRule_group_no((long)3);
		ruleData.setRule_sort((long)1);
		//ruleData.setData_mart("INPUT");
		ruleData.setField_name("C");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		//group3
		ruleData.setRule_group_no((long)3);
		ruleData.setRule_sort((long)2);
		//ruleData.setData_mart(ApConst.RUN_ENVS);
		ruleData.setField_name("busi_org_id");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("99");
		App_rule_dataDao.insert(ruleData);
        
		Map<String,Object> data  = new HashMap<String,Object>();
		data.put("A", 99);//大于  100
		data.put("B", 100.00001);//小于  100
		data.put("C", 100.000000);//相等  100
		ApBaseBuffer.addData("INPUT", data);
		
		boolean b = ApBaseRule.mapping("001");
		Assert.assertTrue(b);
	}
	
	/**
	 * 反例
	 * 
	 */
	public void testMapping4(){
		
	}

	/**
	 * 正例
	 * 正常获取
	 */
	@Test
	public void testGetFirstResultBySceneType() {
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("RUN_ENVS");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)(long)1);
		ruleInfo.setMapping_result("001");
		ruleInfo.setData_version((long)1);		
		App_ruleDao.insert(ruleInfo);
		
		ruleInfo.setRule_id("002");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)2);
		ruleInfo.setMapping_result("001");
		ruleInfo.setData_version((long)2);		
		App_ruleDao.insert(ruleInfo);
		
		//group1
		app_rule_data ruleData = BizUtil.getInstance(app_rule_data.class);
		ruleData.setOrg_id("99");
		ruleData.setRule_id("001");
		ruleData.setRule_group_no((long)(long)1);
		ruleData.setRule_sort((long)(long)1);
		//ruleData.setData_mart("RUN_ENVS");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN);
		ruleData.setData_mapping_value("100");
		ruleData.setData_version((long)1);
		App_rule_dataDao.insert(ruleData);
		//group2
		ruleData.setRule_group_no((long)2);
		ruleData.setRule_sort((long)(long)1);
		//ruleData.setData_mart("RUN_ENVS");
		ruleData.setField_name("B");
		ruleData.setData_mapping_operator(E_OPERATOR.LESS_THAN);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		
		String result = ApBaseRule.getFirstResultByScene("code1");
		Assert.assertTrue(result.equals("001"));
	}

	/**
	 * 正例
	 * 正常获取
	 */
	@Test
	public void testGetAllResultBySceneType() {
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("RUN_ENVS");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)(long)1);
		ruleInfo.setMapping_result("mapping1");
		ruleInfo.setData_version((long)1);		
		App_ruleDao.insert(ruleInfo);
		
		ruleInfo.setRule_id("002");
		ruleInfo.setRule_scene_code("code1");
		ruleInfo.setMapping_sort((long)2);
		ruleInfo.setMapping_result("mapping2");
		ruleInfo.setData_version((long)2);		
		App_ruleDao.insert(ruleInfo);

		List<String> result = ApBaseRule.getAllResultByScene("code1");
		Assert.assertTrue(result.get(0).equals("mapping1"));
		Assert.assertTrue(result.get(1).equals("mapping2"));
	}

    @Test
	public void test1(){
		App_ruleDao.selectOne_odb2("LN_GRADE_LEVEL_001", false);
	}
    
    
    
}
