package cn.sunline.icore.ap.parm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.parm.ApBaseIdentity;
import cn.sunline.icore.ap.tables.TabApBasic.App_identityDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_identity;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.type.ComApBasic.ApIdentityInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_CHECKBITTYPE;

public class ApIdentityTest extends UnitTest {

	@Before
	public void setData() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
		app_identity idInfo = BizUtil.getInstance(app_identity.class);
		idInfo.setOrg_id("99");
		idInfo.setDoc_type("30");
		idInfo.setDoc_desc("学生证");
		idInfo.setCheck_bit_rule(E_CHECKBITTYPE.CHNIDCARD18);
		idInfo.setData_version(1L);
		App_identityDao.insert(idInfo);

		app_identity idInfo1 = BizUtil.getInstance(app_identity.class);
		idInfo1.setOrg_id("99");
		idInfo1.setDoc_type("31");
		idInfo1.setDoc_desc("身份验证1");
		idInfo1.setData_version(1L);
		idInfo1.setCheck_bit_rule(E_CHECKBITTYPE.CHNIDCARD18);
		idInfo1.setCheck_rules1("^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$");
		App_identityDao.insert(idInfo1);

		app_identity idInfo2 = BizUtil.getInstance(app_identity.class);
		idInfo2.setOrg_id("99");
		idInfo2.setDoc_type("32");
		idInfo2.setDoc_desc("身份验证2");
		idInfo2.setData_version(1L);
		idInfo2.setCheck_bit_rule(E_CHECKBITTYPE.CHNIDCARD18);
		idInfo2.setCheck_rules2("^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$");
		App_identityDao.insert(idInfo2);

		app_identity idInfo3 = BizUtil.getInstance(app_identity.class);
		idInfo3.setOrg_id("99");
		idInfo3.setDoc_type("33");
		idInfo3.setDoc_desc("身份验证3");
		idInfo3.setData_version(1L);
		idInfo3.setCheck_bit_rule(E_CHECKBITTYPE.CHNIDCARD18);
		idInfo3.setCheck_rules3("^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$");
		App_identityDao.insert(idInfo3);
	}

	@Test
	public void testExists() {
		boolean test1 = ApBaseIdentity.exists("30");
		boolean test2 = ApBaseIdentity.exists("BB");
		assertTrue(test1);// 正例
		assertFalse(test2);// 反例
	}

	// 校验规则存在的可能性较多，故分多种成功和失败的案例进行
	// 检验规则代码的测试已经进行过，现在进行校验码的校验
	// 正例 校验三种校验规则都为空，满足校验规则且满足校验码校验
	@Test
	public void testIsValid() {
		ApBaseIdentity.isValid("30", "42138119951001941x");
	}

	// 反例 校验三种规则都为空，满足校验规则不满足校验码校验
	@Test
	public void testIsValid1() {
		try {
			ApBaseIdentity.isValid("30", "421381199510019411");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	// 正例 校验身份验证1，满足校验规则和校验码校验
	public void testIsValid2() {
		ApBaseIdentity.isValid("31", "42138119951001941X");
	}

	@Test
	// 正例 校验身份验证1，满足校验规则不满足校验码校验
	public void testIsValid3() {
		try {
			ApBaseIdentity.isValid("31", "421381199510019411");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	// 反例 校验身份验证2，满足校验规则满足校验码校验
	public void testIsValid4() {
		ApBaseIdentity.isValid("32", "42138119951001941x");
	}

	@Test
	// 正例 校验身份验证2，满足校验规则不满足校验码校验
	public void testIsValid5() {
		try {
			ApBaseIdentity.isValid("32", "420380199610048211");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	// 正例 校验身份验证3，满足校验规则和校验码校验
	public void testIsValid6() {
		ApBaseIdentity.isValid("33", "42138119951001941x");
	}

	@Test
	// 反例 校验身份验证3，满足校验规则但不满足校验规则
	public void testIsValid7() {
		try {
			ApBaseIdentity.isValid("33", "421381199510019417");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	// 反例 校验身份验证1，不满足校验规则且不满足校验规则
	public void testIsValid8() {
		try {
			ApBaseIdentity.isValid("31", "42138119951001941A");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	// 反例 校验身份验证2，不满足校验规则且不满足校验规则
	public void testIsValid9() {
		try {
			ApBaseIdentity.isValid("32", "42138119951001941A");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	// 反例 校验身份验证3，不满足校验规则且不满足校验规则
	public void testIsValid10() {
		try {
			ApBaseIdentity.isValid("33", "42138119951001941A");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	// 正例,存在此条数据
	@Test
	public void testGetName() {
		String name = ApBaseIdentity.getName("1B");
		assertNotNull(name);
	}

	// 反例，不存在此条数据
	@Test
	public void testGetName2() {
		try {
			ApBaseIdentity.getName("BB");
			Assert.assertFalse(true);
		}
		catch (Exception e) {
			Assert.assertFalse(false);
		}
	}

	// 正例
	@Test
	public void testGetItem() {
		ApIdentityInfo idInfo = ApBaseIdentity.getItem("30");
		assertNotNull(idInfo);
	}

	// 反例
	@Test
	public void testGetItem2() {
		try {
			ApBaseIdentity.getItem("BB");
			assertFalse(true);
		}
		catch (Exception e) {
			assertFalse(false);
		}
	}

	@Test
	public void testAll() {

	}

}
