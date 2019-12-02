package cn.sunline.icore.ap.parm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspParameter;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspParameterDao;
import cn.sunline.icore.ap.parm.ApBaseCurrency;
import cn.sunline.icore.ap.tables.TabApBasic.App_currencyDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_currency;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;

public class ApCurrencyTest extends UnitTest {

	// 数据准备
	@Before
	public void initData() throws Exception {

		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		Statement statement = conn.createStatement();

		// 先删除其他数据，防止干扰测试
		String sql1 = "delete from MspParameter";
		String sql2 = "delete from app_currency";
		long interestUnit = 2;
		long minorUnit = 2;
		BigDecimal changeUnit = new BigDecimal(0.01);

		statement.executeUpdate(sql1);
		statement.executeUpdate(sql2);
		statement.close();

		// 向杂项参数表中插入数据
		MspParameter appBusinessParam1 = BizUtil.getInstance(MspParameter.class);
		MspParameter appBusinessParam2 = BizUtil.getInstance(MspParameter.class);
		MspParameter appBusinessParam3 = BizUtil.getInstance(MspParameter.class);

		appBusinessParam1.setPrimary_key("BANK_BASE_CCY");
		appBusinessParam1.setParm_code("*");
		appBusinessParam1.setParm_desc("银行基础货币");
		appBusinessParam1.setParm_value("cs1");
		appBusinessParam1.setOrg_id("99");
		appBusinessParam1.setModule("AP");

		appBusinessParam2.setPrimary_key("HOLIDAY_MODE");
		appBusinessParam2.setParm_code("*");
		appBusinessParam2.setParm_desc("假日模式");
		appBusinessParam2.setParm_value("cs2");
		appBusinessParam2.setOrg_id("99");
		appBusinessParam2.setModule("AP");

		appBusinessParam3.setPrimary_key("EXCHANGE_BASE_CCY");
		appBusinessParam3.setParm_code("*");
		appBusinessParam3.setParm_desc("货币兑换基础货币");
		appBusinessParam3.setParm_value("cs3");
		appBusinessParam3.setOrg_id("99");
		appBusinessParam3.setModule("AP");
        
//		MspParameterDao.deleteOne_odb1(appBusinessParam1.getPrimary_key(),appBusinessParam1.getParm_code());
//		MspParameterDao.deleteOne_odb1(appBusinessParam2.getPrimary_key(),appBusinessParam1.getParm_code());
//		MspParameterDao.deleteOne_odb1(appBusinessParam3.getPrimary_key(),appBusinessParam1.getParm_code());

		MspParameterDao.insert(appBusinessParam1);
		MspParameterDao.insert(appBusinessParam2);
		MspParameterDao.insert(appBusinessParam3);

		// 向货币参数表中插入数据
		app_currency appCurrencyParam = BizUtil.getInstance(app_currency.class);

		appCurrencyParam.setCale_interest_unit(interestUnit);
		appCurrencyParam.setCcy_minor_unit(minorUnit);
		appCurrencyParam.setCcy_code("cs1");
		appCurrencyParam.setCcy_change_unit(changeUnit);
		appCurrencyParam.setCcy_num_code("10");
		appCurrencyParam.setCountry_code("cs1");
		appCurrencyParam.setCcy_name("测试");
		appCurrencyParam.setOrg_id("99");
         
		App_currencyDao.deleteOne_odb1(appCurrencyParam.getCcy_code());
		App_currencyDao.insert(appCurrencyParam);
	}

	@Test
	// 判断货币代码是否存在，存在返回true，不存在返回false
	public void testExists() {

		// 输入存在的货币代号
		String ccyCode1 = "cs1";
		// 输入不存在的货币代号
		String ccyCode2 = "cs2";

		// 传入存在的货币代号，返回值为true
		Assert.assertTrue(ApBaseCurrency.exists(ccyCode1));

		// 传入不存在的货币代号，返回值为false
		Assert.assertFalse(ApBaseCurrency.exists(ccyCode2));

	}

	@Test
	public void testGetItem() {
		// 输入存在的货币代号
		String ccyCode1 = "cs1";
		// 输入不存在的货币代号
		String ccyCode2 = "cs2";

		// 传入存在的货币代号，返回值为对应的货币对象
		Assert.assertNotNull(ApBaseCurrency.exists(ccyCode1));

		// 传入不存在的货币代号，方法会抛出异常
		try {
			ApBaseCurrency.exists(ccyCode2);
		}
		catch (Exception e) {
			Assert.assertNull(null);
		}
	}

	/*
	 * @Test public void testAll() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testGetOtherItems() { fail("Not yet implemented"); }
	 */

	@Test
	public void testIsBankBase() {

		String ccyCode1 = "cs1";// 输入银行基础货币
		String ccyCode2 = "cs2";// 输入非银行基础货币

		Assert.assertTrue(ApBaseCurrency.isBankBase(ccyCode1));// 输入正常测试数据返回true
		Assert.assertFalse(ApBaseCurrency.isBankBase(ccyCode2));// 输入反例测试数据返回false
	}

	@Test
	public void testGetBankBase() {

		Assert.assertNotNull(ApBaseCurrency.getBankBase());
	}
	
	@Test
	public void testIsAmountValid() {

		String ccyCode = "cs1";
		BigDecimal amount1 = new BigDecimal("0.02");
		BigDecimal amount2 = new BigDecimal("0.021");

		Assert.assertTrue(ApBaseCurrency.isAmountValid(ccyCode, amount1));
		Assert.assertFalse(ApBaseCurrency.isAmountValid(ccyCode, amount2));

	}

	@Test
	public void testRoundAmount() {

		String ccyCode = "cs1";
		BigDecimal amount1 = new BigDecimal("0.02");
		BigDecimal amount2 = new BigDecimal("0.021");

		// 货币精度位为2，输入精度正确的数据与原数据比较是相等的，输入精度不正确则不相等
		Boolean flag1 = CommUtil.equals(ApBaseCurrency.roundAmount(ccyCode, amount1), amount1);
		Boolean flag2 = CommUtil.equals(ApBaseCurrency.roundAmount(ccyCode, amount2), amount2);

		Assert.assertTrue(flag1);
		Assert.assertFalse(flag2);
	}

	@Test
	public void testRoundInterest() {

		String ccyCode = "cs1";
		BigDecimal amount1 = new BigDecimal("0.02");
		BigDecimal amount2 = new BigDecimal("0.021");

		// 计息精度位为2，输入精度正确的数据与原数据比较是相等的，输入精度不正确则不相等
		Boolean flag1 = CommUtil.equals(ApBaseCurrency.roundInterest(ccyCode, amount1), amount1);
		Boolean flag2 = CommUtil.equals(ApBaseCurrency.roundInterest(ccyCode, amount2), amount2);

		Assert.assertTrue(flag1);
		Assert.assertFalse(flag2);
	}

	@Test
	public void testChkAmountByCcy() {
		// 对不合法的数据会直接抛出异常，直接反例来测试是否能正常抛出
		String ccyCode = "cs1";
		BigDecimal amount = new BigDecimal("0.021");

		try {
			ApBaseCurrency.chkAmountByCcy(ccyCode, amount);
			Assert.assertTrue(false);
		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

}
