package cn.sunline.icore.ap.limit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.limit.ApBaseLimit;
import cn.sunline.icore.ap.tables.TabApAttribute.Apb_custom_limitDao;
import cn.sunline.icore.ap.tables.TabApAttribute.Apb_limit_statisDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_limitDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_limit_driveDao;
import cn.sunline.icore.ap.tables.TabApAttribute.apb_custom_limit;
import cn.sunline.icore.ap.tables.TabApAttribute.apb_limit_statis;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit_drive;
import cn.sunline.icore.ap.tables.TabApRule.App_ruleDao;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_ADDSUBTRACT;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.icore.sys.type.EnumType.E_LIMITCTRLCLASS;

public class ApLimitTest extends UnitTest {
	
	@Before
	public void initLimit() throws SQLException {
		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);

		/**
		 * 1、限额控制分类为单次金额时,不做限额统计处理,因此案例未包含 2、传入的值amount,目前用于筛选限额定义中大于amount值的数据
		 * 3、查询数据时流程为 : 限额驱动定义 ==> 限额定义 ==> 限额有效验证 ==> 交易金额验证、币种换算 ==>是否自定义限额
		 * 限额对比 ==> 限额统计簿 ==> 限额统计信息为空或重置日期小于交易日期 ==> 添加限额统计簿信息到返回列表
		 */
	}
	
	@Test
	public void checkCumulativeAmt() {
		app_limit appLimit = BizUtil.getInstance(app_limit.class);
		appLimit.setOrg_id("99");
		appLimit.setLimit_no("2016121309204705");
		appLimit.setLimit_desc("累计金额限额");
		appLimit.setEffect_date("20161210");
		appLimit.setExpiry_date("20171210");
		appLimit.setLimit_ctrl_class(E_LIMITCTRLCLASS.CUMULATIVE_AMOUNT);
		appLimit.setLimit_reset_cycle(E_CYCLETYPE.MONTH);
		appLimit.setLimit_statis_no("2016121309204705");
		appLimit.setLimit_ccy("CNY");
		appLimit.setLimit_value(new BigDecimal("100000"));
		appLimit.setLimit_custom_allow(E_YESORNO.YES);
		appLimit.setLimit_sms_template_no("100001");

		// 自定义限额
		apb_custom_limit custLimit = BizUtil.getInstance(apb_custom_limit.class);
		custLimit.setCustom_limit_value(new BigDecimal("150000"));
		custLimit.setLimit_no("2016121309204705");
		custLimit.setLimit_owner_id("2016121309204705");
		custLimit.setOrg_id("99");

		Apb_custom_limitDao.insert(custLimit);
		App_limitDao.insert(appLimit);

		String trxnEvent = "TEST001";
		app_limit_drive limitDrive = BizUtil.getInstance(app_limit_drive.class);
		limitDrive.setOrg_id("99");
		limitDrive.setLimit_no("2016121309204705");
		limitDrive.setLimit_drive_cond("APP_LIMIT_TEST_001");
		limitDrive.setLimit_sum_way(E_ADDSUBTRACT.ADD);
		limitDrive.setTrxn_event_id(trxnEvent);
		App_limit_driveDao.insert(limitDrive);
		
		app_rule rule = BizUtil.getInstance(app_rule.class);
		rule.setOrg_id("99");
		rule.setRule_id("APP_LIMIT_TEST_001");
//		rule.setRule_type(E_RULETYPE.SIMPLE);
		rule.setRule_desc("test");
		rule.setRule_scene_code("APP_LIMIT_TEST");
//		rule.setRule_sort(1L);
		App_ruleDao.insert(rule);

		try {
			BizUtil.getTrxRunEnvs().setTrxn_date("20161220");
			BigDecimal use1 = new BigDecimal("100000");
			ApBaseLimit.process(trxnEvent, "CNY", use1);
			
			apb_limit_statis book = Apb_limit_statisDao.selectOne_odb1("2016121309204705", "2016121309204705", true);
			Assert.assertTrue(use1.compareTo(book.getUsed_limit()) == 0);
			
			BizUtil.getTrxRunEnvs().setTrxn_date("20161230");
			BigDecimal use2 = new BigDecimal("50000");
			ApBaseLimit.process(trxnEvent, "CNY", use2);
			
			book = Apb_limit_statisDao.selectOne_odb1("2016121309204705", "2016121309204705", true);
			Assert.assertTrue((use1.add(use2)).compareTo(book.getUsed_limit()) == 0);
			
			BizUtil.getTrxRunEnvs().setTrxn_date("20170110");
			BigDecimal use3 = new BigDecimal("150000");
			ApBaseLimit.process(trxnEvent, "CNY", use3);
			book = Apb_limit_statisDao.selectOne_odb1("2016121309204705", "2016121309204705", true);
			Assert.assertTrue(use3.compareTo(book.getUsed_limit()) == 0);
		}
		catch (Exception e) {
			Assert.fail();
		}
		
		// 超额
		try {
			BizUtil.getTrxRunEnvs().setTrxn_date("20170120");
			BigDecimal use = new BigDecimal("10");
			ApBaseLimit.process(trxnEvent, "CNY", use);
		}
		catch (Exception e) {
			return;
		}
		Assert.fail();
	}
	
	@Test
	public void checkAmountCounter() {
		app_limit appLimit = BizUtil.getInstance(app_limit.class);
		appLimit.setOrg_id("99");
		appLimit.setLimit_no("2016121309204711");
		appLimit.setLimit_desc("金额计数");
		appLimit.setEffect_date("20161210");
		appLimit.setExpiry_date("20171210");
		appLimit.setLimit_ctrl_class(E_LIMITCTRLCLASS.AMOUNT_COUNTER);
		appLimit.setLimit_statis_no("2016121309204711");
		appLimit.setLimit_ccy("CNY");

		App_limitDao.insert(appLimit);

		String trxnEvent = "TEST002";
		app_limit_drive limitDrive = BizUtil.getInstance(app_limit_drive.class);
		limitDrive.setOrg_id("99");
		limitDrive.setLimit_no("2016121309204711");
		limitDrive.setLimit_drive_cond("APP_LIMIT_TEST_002");
		limitDrive.setLimit_sum_way(E_ADDSUBTRACT.ADD);
		limitDrive.setTrxn_event_id(trxnEvent);
		App_limit_driveDao.insert(limitDrive);
		
		app_rule rule = BizUtil.getInstance(app_rule.class);
		rule.setOrg_id("99");
		rule.setRule_id("APP_LIMIT_TEST_002");
//		rule.setRule_type(E_RULETYPE.SIMPLE);
		rule.setRule_desc("test");
		rule.setRule_scene_code("APP_LIMIT_TEST");
//		rule.setRule_sort(2L);
		App_ruleDao.insert(rule);

		try {
			BizUtil.getTrxRunEnvs().setTrxn_date("20161220");
			BigDecimal use1 = new BigDecimal("100000");
			ApBaseLimit.process(trxnEvent, "CNY", use1);
			
			apb_limit_statis book = Apb_limit_statisDao.selectOne_odb1("2016121309204711", "2016121309204711", true);
			Assert.assertTrue(use1.compareTo(book.getUsed_limit()) == 0);
			
			BizUtil.getTrxRunEnvs().setTrxn_date("20170630");
			BigDecimal use2 = new BigDecimal("150000");
			ApBaseLimit.process(trxnEvent, "CNY", use2);
			
			book = Apb_limit_statisDao.selectOne_odb1("2016121309204711", "2016121309204711", true);
			Assert.assertTrue((use1.add(use2)).compareTo(book.getUsed_limit()) == 0);
			
			BizUtil.getTrxRunEnvs().setTrxn_date("20171030");
			BigDecimal use3 = new BigDecimal("200000");
			ApBaseLimit.process(trxnEvent, "CNY", use3);
			Assert.assertEquals(appLimit.getEffect_date(), book.getLimit_reset_date());
		}
		catch (Exception e) {
			Assert.fail();
		}
	}
	
	@Test
	public void checkTimesCounter() {
		app_limit appLimit = BizUtil.getInstance(app_limit.class);
		appLimit.setOrg_id("99");
		appLimit.setLimit_no("2016121309204721");
		appLimit.setLimit_desc("次数计数");
		appLimit.setEffect_date("20161210");
		appLimit.setExpiry_date("20171210");
		appLimit.setLimit_ctrl_class(E_LIMITCTRLCLASS.TIMES_COUNTER);
		appLimit.setLimit_statis_no("2016121309204721");
		appLimit.setLimit_ccy("CNY");

		App_limitDao.insert(appLimit);

		String trxnEvent = "TEST003";
		app_limit_drive limitDrive = BizUtil.getInstance(app_limit_drive.class);
		limitDrive.setOrg_id("99");
		limitDrive.setLimit_no("2016121309204721");
		limitDrive.setLimit_drive_cond("APP_LIMIT_TEST_003");
		limitDrive.setLimit_sum_way(E_ADDSUBTRACT.ADD);
		limitDrive.setTrxn_event_id(trxnEvent);
		App_limit_driveDao.insert(limitDrive);
		
		app_rule rule = BizUtil.getInstance(app_rule.class);
		rule.setOrg_id("99");
		rule.setRule_id("APP_LIMIT_TEST_003");
//		rule.setRule_type(E_RULETYPE.SIMPLE);
		rule.setRule_desc("test");
		rule.setRule_scene_code("APP_LIMIT_TEST");
//		rule.setRule_sort(3L);
		App_ruleDao.insert(rule);

		try {
			BizUtil.getTrxRunEnvs().setTrxn_date("20161220");
			BigDecimal use1 = new BigDecimal("100000");
			ApBaseLimit.process(trxnEvent, "CNY", use1);
			
			apb_limit_statis book = Apb_limit_statisDao.selectOne_odb1("2016121309204721", "2016121309204721", true);
			Assert.assertTrue(BigDecimal.ONE.compareTo(book.getUsed_limit()) == 0);
			
			BizUtil.getTrxRunEnvs().setTrxn_date("20170630");
			BigDecimal use2 = new BigDecimal("150000");
			ApBaseLimit.process(trxnEvent, "CNY", use2);
			
			book = Apb_limit_statisDao.selectOne_odb1("2016121309204721", "2016121309204721", true);
			Assert.assertTrue((new BigDecimal("2")).compareTo(book.getUsed_limit()) == 0);
			
			BizUtil.getTrxRunEnvs().setTrxn_date("20171030");
			BigDecimal use3 = new BigDecimal("200000");
			ApBaseLimit.process(trxnEvent, "CNY", use3);
			
			book = Apb_limit_statisDao.selectOne_odb1("2016121309204721", "2016121309204721", true);
			Assert.assertTrue((new BigDecimal("3")).compareTo(book.getUsed_limit()) == 0);
			
			Assert.assertEquals(appLimit.getEffect_date(), book.getLimit_reset_date());
		}
		catch (Exception e) {
			Assert.fail();
		}
	}

}
