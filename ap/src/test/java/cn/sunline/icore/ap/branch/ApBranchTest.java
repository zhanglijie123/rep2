package cn.sunline.icore.ap.branch;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDropList;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.branch.ApBaseBranch;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branchDao;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.loader.parmreload.DBChangeChecker;
import cn.sunline.ltts.core.loader.parmreload.OdbChangeListenerManager;

public class ApBranchTest extends UnitTest {
	
	// 初始化数据
	@Before
	public void initBranchData() throws Exception {
		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		commReq.put("trxn_date","20170504");
		newCommReq(commReq);


		apb_branch branchParameter1 = BizUtil.getInstance(apb_branch.class);
		apb_branch branchParameter2 = BizUtil.getInstance(apb_branch.class);

		// 第一组数据不含有假日代码和Swift号
		branchParameter1.setBranch_id("ap01");
		branchParameter1.setBranch_name("ap01");
		branchParameter1.setReal_branch_ind(E_YESORNO.YES);
		branchParameter1.setOrg_id("99");
		// 第二组数据含有假日代码和Swift号
		branchParameter2.setBranch_id("ap02");
		branchParameter2.setBranch_name("ap02");
		branchParameter2.setReal_branch_ind(E_YESORNO.YES);
		branchParameter2.setHoliday_code("GQJ");
		branchParameter2.setSwift_no("A-1");
		branchParameter2.setOrg_id("99");
        
		Apb_branchDao.deleteOne_odb1(branchParameter1.getBranch_id());
		Apb_branchDao.deleteOne_odb1(branchParameter2.getBranch_id());
		Apb_branchDao.insert(branchParameter1);
		Apb_branchDao.insert(branchParameter2);
	}

	/**
	 * 测试ApBaseBranch.exists(String branchId)
	 */
	@Test
	public void testExists() {
		// 正例
		Assert.assertTrue(ApBaseBranch.exists("ap01"));
		Assert.assertTrue(ApBaseBranch.exists("ap02"));
		// 反例
		Assert.assertFalse(ApBaseBranch.exists(" "));
		Assert.assertFalse(ApBaseBranch.exists("ap03"));

	}

	/**
	 * 测试ApBaseBranch.getHolidayCode(String branchId)
	 * 
	 */
	@Test
	public void testgetHolidayCode() {

		// 正例
		Assert.assertNotNull(ApBaseBranch.getHolidayCode("ap02"));

		// 反例
		try {
			ApBaseBranch.getHolidayCode("ap01");

		}
		catch (LttsBusinessException e) {
			Assert.assertEquals(ApBaseErr.ApBase.F_E0019, e.getCode());
		}

	}
	/**
	 * 测试ApBaseBranch.getItem(String branchId)
	 * 
	 */
	@Test
	public void testGetItem() {
		
		// 正例
		Assert.assertNotNull(ApBaseBranch.getItem("ap01"));
		Assert.assertNotNull(ApBaseBranch.getItem("ap02"));

		// 反例
		try {
			ApBaseBranch.getItem(" ");
			ApBaseBranch.getItem("ap03");
			Assert.assertFalse(true);
		}
		catch (LttsBusinessException e) {
			Assert.assertEquals(ApPubErr.APPUB.F_E0005, e.getCode());
		}
	}
	/**
	 * 测试ApBaseBranch.getSwiftNo(String branchId)
	 */
	@Test
	public void testGetSwiftNo() {

		// 正例
		Assert.assertNotNull(ApBaseBranch.getSwiftNo("ap02"));

		// 反例
		try {
			ApBaseBranch.getSwiftNo("ap01");

		}
		catch (LttsBusinessException e) {
			Assert.assertEquals(ApBaseErr.ApBase.F_E0014, e.getCode());
		}

	}
	
	@Test
	public void test1(){

//		System.out.println(ApBaseBranch.exists("11", "11"));	
		
		OdbChangeListenerManager.get().register(MspDropList.class, new DBChangeChecker(MspDropList.class));
		
		OdbChangeListenerManager.get().reloadAll(false);
		
		OdbChangeListenerManager.get().reloadAll(false);
	}

}
