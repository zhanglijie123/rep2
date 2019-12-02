package cn.sunline.icore.ap.attr;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_OPERATOR;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.rule.ApBaseBuffer;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attributeDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_attribute_mutexDao;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute;
import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute_mutex;
import cn.sunline.icore.ap.tables.TabApRule.App_ruleDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_dataDao;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_data;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_OWNERLEVEL;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月12日-下午7:13:47</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 zhangql：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApAttributeTest extends UnitTest {

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月12日-下午7:14:46</li>
	 *         <li>功能说明：初始化数据</li>
	 *         </p>
	 */
	@Before
	public void insertParameter() {

		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);

		// 属性位定义表
		app_attribute parameter1 = BizUtil.getInstance(app_attribute.class);
		parameter1.setOrg_id("99");
		parameter1.setAttr_level(E_OWNERLEVEL.CARD);
		parameter1.setAttr_position(1L);
		parameter1.setAttr_desc("description1");
		parameter1.setAttr_expiry_ind(E_YESORNO.NO);
		parameter1.setRef_drop_list("ref_drop_list1");
		parameter1.setDefault_value(ApConst.WILDCARD);
		App_attributeDao.deleteOne_odb1(parameter1.getAttr_level(), parameter1.getAttr_position());
		App_attributeDao.insert(parameter1);

		// 属性（位）互斥定义表
		app_attribute_mutex parameter2 = BizUtil.getInstance(app_attribute_mutex.class);
		parameter2.setOrg_id("99");
		parameter2.setAttr_mutex_id("01");
		parameter2.setAttr_mutex_desc("description01");
		parameter2.setAttr_level(E_OWNERLEVEL.CARD);
		parameter2.setMapping_expression("[a-z]{3}");
		parameter2.setShow_error_info("E0007");
		App_attribute_mutexDao.deleteOne_odb1(parameter2.getAttr_mutex_id());
		App_attribute_mutexDao.insert(parameter2);
/*
		// 属性控制定义 属性控制运行条件为空
		app_attribute_control parameter3 = BizUtil.getInstance(app_attribute_control.class);
		parameter3.setOrg_id("99");
		parameter3.setAttr_ctrl_desc("hello");
		parameter3.setAttr_level(E_OWNERLEVEL.CARD);
		parameter3.setAttr_ctrl_id("02");
		parameter3.setMapping_expression("[a-z]{3}");
		parameter3.setShow_error_info("error_01");
		App_attribute_controlDao.deleteOne_odb1(parameter3.getAttr_ctrl_id());
		App_attribute_controlDao.insert(parameter3);*/
	}


	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月14日-下午1:14:26</li>
	 *         <li>测试将完整形态列表转换成“字符型态属性+到期日列表”</li>
	 *         </p>
	 */
	@Test
	// 正例，属性匹配表达式不匹配跳过扫描且控制运行条件全为空
	public void testCheckControl() {
		
		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		try {
			Statement statement = conn.createStatement();
			//先删除其他数据，防止干扰测试
			String sql1 = "delete from app_attribute_control";
			statement.execute(sql1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	/*	
		// 属性控制定义 属性控制运行条件为空
		app_attribute_control parameter3 = BizUtil.getInstance(app_attribute_control.class);
		parameter3.setOrg_id("99");
		parameter3.setAttr_ctrl_desc("hello");
		parameter3.setAttr_level(E_OWNERLEVEL.CARD);
		parameter3.setAttr_ctrl_id("02");
		parameter3.setMapping_expression("[a-z]{3}");
		parameter3.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter3);

		app_attribute_control parameter4 = BizUtil.getInstance(app_attribute_control.class);
		parameter4.setOrg_id("99");
		parameter4.setAttr_ctrl_desc("hello");
		parameter4.setAttr_level(E_OWNERLEVEL.CARD);
		parameter4.setAttr_ctrl_id("03");
		parameter4.setMapping_expression("[a-z]{3}");
		parameter4.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter4);

		ApAttribute.checkControl(E_OWNERLEVEL.CARD, "1");*/
	}

	@Test
	// 正例，属性匹配表达式不匹配跳过扫描且控制运行条件不为空
	public void testCheckControl1() {

		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		try {
			Statement statement = conn.createStatement();
			//先删除其他数据，防止干扰测试
			String sql1 = "delete from app_attribute_control";
			statement.execute(sql1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
/*
		// 属性控制定义 属性控制运行条件为空
		app_attribute_control parameter3 = BizUtil.getInstance(app_attribute_control.class);
		parameter3.setOrg_id("99");
		parameter3.setAttr_ctrl_desc("hello");
		parameter3.setAttr_level(E_OWNERLEVEL.CARD);
		parameter3.setAttr_ctrl_id("02");
		parameter3.setMapping_expression("[a-z]{3}");
		parameter3.setAttr_ctrl_run_cond("001");
		parameter3.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter3);

		app_attribute_control parameter4 = BizUtil.getInstance(app_attribute_control.class);
		parameter4.setOrg_id("99");
		parameter4.setAttr_ctrl_desc("hello");
		parameter4.setAttr_level(E_OWNERLEVEL.CARD);
		parameter4.setAttr_ctrl_id("03");
		parameter4.setMapping_expression("[a-z]{3}");
		parameter3.setAttr_ctrl_run_cond("001");
		parameter4.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter4);

		ApAttribute.checkControl(E_OWNERLEVEL.CARD, "1");*/
	}

	@Test
	// 反例，属性匹配表达式部分匹配且控制运行条件全为空
	public void testCheckControl2() {

		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		try {
			Statement statement = conn.createStatement();
			//先删除其他数据，防止干扰测试
			String sql1 = "delete from app_attribute_control";
			statement.execute(sql1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		/*// 属性控制定义 属性控制运行条件为空
		app_attribute_control parameter3 = BizUtil.getInstance(app_attribute_control.class);
		parameter3.setOrg_id("99");
		parameter3.setAttr_ctrl_desc("hello");
		parameter3.setAttr_level(E_OWNERLEVEL.CARD);
		parameter3.setAttr_ctrl_id("02");
		parameter3.setMapping_expression("[a-z]{3}");
		parameter3.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter3);

		app_attribute_control parameter4 = BizUtil.getInstance(app_attribute_control.class);
		parameter4.setOrg_id("99");
		parameter4.setAttr_ctrl_desc("hello");
		parameter4.setAttr_level(E_OWNERLEVEL.CARD);
		parameter4.setAttr_ctrl_id("03");
		parameter4.setMapping_expression("[a-z]");
		parameter4.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter4);
		try {
			ApAttribute.checkControl(E_OWNERLEVEL.CARD, "a");
			assertTrue(false);
		}
		catch (Exception e) {
			assertTrue(true);
		}*/

	}

	@Test
	// 正例，属性匹配表达式部分匹配且控制运行条件扫描失败(扫描时候不会抛出异常)
	public void testCheckControl3() {

		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		try {
			Statement statement = conn.createStatement();
			//先删除其他数据，防止干扰测试
			String sql1 = "delete from app_attribute_control";
			statement.execute(sql1);
			statement.execute("delete from app_rule");
			statement.execute("delete from app_rule_data");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		/*// 属性控制定义 属性控制运行条件不为空
		app_attribute_control parameter3 = BizUtil.getInstance(app_attribute_control.class);
		parameter3.setOrg_id("99");
		parameter3.setAttr_ctrl_desc("hello");
		parameter3.setAttr_level(E_OWNERLEVEL.CARD);
		parameter3.setAttr_ctrl_id("02");
		parameter3.setMapping_expression("[a-z]{3}");
		parameter3.setAttr_ctrl_run_cond("001");
		parameter3.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter3);

		app_attribute_control parameter4 = BizUtil.getInstance(app_attribute_control.class);
		parameter4.setOrg_id("99");
		parameter4.setAttr_ctrl_desc("hello");
		parameter4.setAttr_level(E_OWNERLEVEL.CARD);
		parameter4.setAttr_ctrl_id("03");
		parameter4.setMapping_expression("[a-z]");
		parameter4.setShow_error_info("error_01");
		parameter4.setAttr_ctrl_run_cond("001");
		App_attribute_controlDao.insert(parameter4);*/

		// Rule的mapping方法返回false
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("test");
		ruleInfo.setRule_scene_code("code1");
//		ruleInfo.setRule_sort((long) (long) 1);
		ruleInfo.setMapping_result("mapping1");
		ruleInfo.setException_rule_id("002");
		ruleInfo.setData_version((long) 1);
		App_ruleDao.insert(ruleInfo);
		// 数据一
		app_rule_data ruleData = BizUtil.getInstance(app_rule_data.class);
		ruleData.setOrg_id("99");
		ruleData.setRule_id("001");
		ruleData.setRule_group_no((long) (long) 1);
		ruleData.setRule_sort((long) (long) 1);
//		ruleData.setData_mart("TEST");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN);
		ruleData.setData_mapping_value("100");
		ruleData.setData_version((long) 1);
		App_rule_dataDao.insert(ruleData);
		// 例外规则
		ruleData.setOrg_id("99");
		ruleData.setRule_id("002");
		ruleData.setRule_group_no((long) 2);
		ruleData.setRule_sort((long) (long) 1);
//		ruleData.setData_mart("TEST");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("456");
		ruleData.setData_version((long) 1);
		App_rule_dataDao.insert(ruleData);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("A", 456);// 相等 123
		ApBaseBuffer.addData("TEST", data);

		//ApAttribute.checkControl(E_OWNERLEVEL.CARD, "a");
	}

	@Test
	// 反例，属性匹配表达式部分匹配且控制运行条件扫描成功(扫描时候会抛出异常)
	public void testCheckControl4() {

		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		try {
			Statement statement = conn.createStatement();
			//先删除其他数据，防止干扰测试
			String sql1 = "delete from app_attribute_control";
			statement.execute(sql1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		/*// 属性控制定义 属性控制运行条件不为空
		app_attribute_control parameter3 = BizUtil.getInstance(app_attribute_control.class);
		parameter3.setOrg_id("99");
		parameter3.setAttr_ctrl_desc("hello");
		parameter3.setAttr_level(E_OWNERLEVEL.CARD);
		parameter3.setAttr_ctrl_id("02");
		parameter3.setMapping_expression("[a-z]{3}");
		parameter3.setAttr_ctrl_run_cond("001");
		parameter3.setShow_error_info("error_01");
		App_attribute_controlDao.insert(parameter3);

		app_attribute_control parameter4 = BizUtil.getInstance(app_attribute_control.class);
		parameter4.setOrg_id("99");
		parameter4.setAttr_ctrl_desc("hello");
		parameter4.setAttr_level(E_OWNERLEVEL.CARD);
		parameter4.setAttr_ctrl_id("03");
		parameter4.setMapping_expression("[a-z]");
		parameter4.setShow_error_info("error_01");
		parameter4.setAttr_ctrl_run_cond("001");
		App_attribute_controlDao.insert(parameter4);*/

		// Rule的mapping方法返回true
		app_rule ruleInfo = BizUtil.getInstance(app_rule.class);
		ruleInfo.setOrg_id("99");
		ruleInfo.setRule_id("001");
		ruleInfo.setRule_desc("test");
		ruleInfo.setRule_scene_code("code1");
//		ruleInfo.setRule_sort((long) (long) 1);
		ruleInfo.setMapping_result("mapping1");
		ruleInfo.setData_version((long) 1);
		App_ruleDao.insert(ruleInfo);
		// group1
		app_rule_data ruleData = BizUtil.getInstance(app_rule_data.class);
		ruleData.setOrg_id("99");
		ruleData.setRule_id("001");
		ruleData.setRule_group_no((long) (long) 1);
		ruleData.setRule_sort((long) (long) 1);
//		ruleData.setData_mart("TEST");
		ruleData.setField_name("A");
		ruleData.setData_mapping_operator(E_OPERATOR.GREATER_THAN);
		ruleData.setData_mapping_value("100");
		ruleData.setData_version((long) 1);
		App_rule_dataDao.insert(ruleData);
		// group2
		ruleData.setRule_group_no((long) 2);
		ruleData.setRule_sort((long) (long) 1);
//		ruleData.setData_mart("TEST");
		ruleData.setField_name("B");
		ruleData.setData_mapping_operator(E_OPERATOR.LESS_THAN);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		// group3
		ruleData.setRule_group_no((long) 3);
		ruleData.setRule_sort((long) (long) 1);
//		ruleData.setData_mart("TEST");
//		ruleData.setField_name("C");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("100");
		App_rule_dataDao.insert(ruleData);
		// group3
		ruleData.setRule_group_no((long) 3);
		ruleData.setRule_sort((long) 2);
//		ruleData.setData_mart(ApConst.RUN_ENVS);
		ruleData.setField_name("busi_org_id");
		ruleData.setData_mapping_operator(E_OPERATOR.EQUAL);
		ruleData.setData_mapping_value("99");
		App_rule_dataDao.insert(ruleData);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("A", 99);// 大于 100
		data.put("B", 100.00001);// 小于 100
		data.put("C", 100.000000);// 相等 100
		ApBaseBuffer.addData("TEST", data);

		try {
			//ApAttribute.checkControl(E_OWNERLEVEL.CARD, "a");
			assertTrue(false);
		}
		catch (Exception e) {
			assertTrue(true);
		}

	}

}
