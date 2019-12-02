//package cn.sunline.icore.ap.serviceimpl;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
//import cn.sunline.icore.ap.branch.ApBranchMnt;
//import cn.sunline.icore.ap.servicetype.SrvApBranch;
//import cn.sunline.icore.ap.tables.TabApBranch.Apb_branchDao;
//import cn.sunline.icore.ap.tables.TabApBranch.apb_branch;
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo;
//import cn.sunline.icore.ap.type.ComApBranch.ApBranchParmCondtion;
//import cn.sunline.icore.ap.type.ComApBranch.ApBranchRelSubWithOper;
//import cn.sunline.ltts.biz.global.CommUtil;
//import cn.sunline.ltts.core.api.model.dm.Options;
//import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
//import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
//import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
//import cn.sunline.icore.ap.util.BizUtil;
//
///**
// * <p>
// * 文件功能说明：机构测试类
// * </p>
// * 
// * @Author lanlf
// *         <p>
// *         <li>2016年12月24日-上午11:55:55</li>
// *         <li>修改记录</li>
// *         <li>-----------------------------------------------------------</li>
// *         <li>标记：修订内容</li>
// *         <li>20140228 lanlf：创建注释模板</li>
// *         <li>-----------------------------------------------------------</li>
// *         </p>
// */
//public class SrvApBranchImplTest extends UnitTest {
//	@Before
//	public void insertInfo() {
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "99");
//		newCommReq(commReq);
//	}
//
//	/**
//	 * @Author lanlf
//	 *         <p>
//	 *         <li>2016年12月26日-下午4:22:42</li>
//	 *         <li>功能说明：测试查询机构列表</li>
//	 *         </p>
//	 */
//	@Test
//	public void testQueryBranchList() {
//		for (int i = 0; i < 30; i++) {
//			apb_branch branch = BizUtil.getInstance(apb_branch.class);
//			branch.setBranch_id("78901234" + i);
//			branch.setBranch_name("测试" + i);
//			branch.setReal_branch_ind(E_YESORNO.NO);
//			branch.setBranch_address("深圳" + i);
//			branch.setBranch_function_class("A" + i);
//			branch.setBranch_phone("133222222" + i);
//			branch.setContacts_name("张三" + i);
//			branch.setContacts_phone("133333333" + i);
//			branch.setData_create_user("李四" + i);
//			branch.setPostcode("432132" + i);
//			branch.setSwift_no("131231321" + i);
//			branch.setHoliday_code("2313131");
//			branch.setData_create_user("王五");
//			Apb_branchDao.insert(branch);
//		}
//		ApBranchParmCondtion apBranchPram = BizUtil.getInstance(ApBranchParmCondtion.class);
//		apBranchPram.setOrg_id("99");
//		apBranchPram.setReal_branch_ind(E_YESORNO.NO);
//		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
//		runEnvs.setPage_start(1L);
//		runEnvs.setPage_size(20L);
//
//		SrvApBranch srv = BizUtil.getInstance(SrvApBranch.class);
//		Options<ApBranchInfo> op = srv.queryBranchList(apBranchPram);
//		List<ApBranchInfo> list = op.getValues();
//		assertNotNull(list);
//		assertTrue(list.size()<21);
//		for (ApBranchInfo info : list) {
//			assertEquals(E_YESORNO.NO, info.getReal_branch_ind());
//		}
//	}
//
//	/**
//	 * @Author lanlf
//	 *         <p>
//	 *         <li>2016年12月26日-下午4:23:32</li>
//	 *         <li>功能说明：测试添加机构</li>
//	 *         </p>
//	 */
//	@Test
//	public void testAddBranch() {
//		apb_branch branch = BizUtil.getInstance(apb_branch.class);
//		branch.setOrg_id("99");
//		branch.setBranch_id("123456");
//		branch.setBranch_name("测试3");
//		branch.setReal_branch_ind(E_YESORNO.NO);
//		ApBranchInfo newBranch = BizUtil.getInstance(ApBranchInfo.class);
//		CommUtil.copyProperties(newBranch, branch);
//		SrvApBranch srv = BizUtil.getInstance(SrvApBranch.class);
//		srv.addBranch(newBranch);
//		apb_branch b_branch = Apb_branchDao.selectOne_odb1(branch.getBranch_id(), true);
//		assertEquals(b_branch.getBranch_id(), branch.getBranch_id());
//		assertEquals(b_branch.getOrg_id(), branch.getOrg_id());
//		assertEquals(b_branch.getBranch_name(), branch.getBranch_name());
//		assertEquals(b_branch.getReal_branch_ind(), branch.getReal_branch_ind());
//	}
//
//	/**
//	 * @Author lanlf
//	 *         <p>
//	 *         <li>2016年12月26日-下午4:23:47</li>
//	 *         <li>功能说明：测试修改机构信息</li>
//	 *         </p>
//	 */
//	@Test
//	public void testModifyBranch() {
//		apb_branch bra = BizUtil.getInstance(apb_branch.class);
//		bra.setOrg_id("99");
//		bra.setBranch_id("7890123432");
//		bra.setBranch_name("测试2");
//		bra.setReal_branch_ind(E_YESORNO.NO);
//		Apb_branchDao.insert(bra);
//		ApBranchInfo apBranch = BizUtil.getInstance(ApBranchInfo.class);
//		apBranch.setBranch_id("7890123432");
//		apBranch.setBranch_name("ceshi22");
//		apBranch.setReal_branch_ind(E_YESORNO.YES);
//		apBranch.setSwift_no("111");
//		apBranch.setHoliday_code("111");
//		apBranch.setData_version((long)1);
//		SrvApBranch srv = BizUtil.getInstance(SrvApBranch.class);
//		srv.modifyBranch(E_DATAOPERATE.MODIFY,apBranch);
//		apb_branch branch = Apb_branchDao.selectOne_odb1(apBranch.getBranch_id(), true);
//		assertEquals(branch.getBranch_id(), apBranch.getBranch_id());
//		assertEquals(branch.getBranch_name(), apBranch.getBranch_name());
//		assertEquals(branch.getReal_branch_ind(), apBranch.getReal_branch_ind());
//	}
//	
//	/**
//	 * 
//	 * @Author lid
//	 *         <p>
//	 *         <li>2017年1月9日-下午3:34:12</li>
//	 *         <li>功能说明：机构关系信息维护测试</li>
//	 *         </p>
//	 */
//	@Test
//	public void testModifyBranchRelation() throws Exception{
//		ApBranchMnt.modifyBranchRelation("100", "*", new DefaultOptions<ApBranchRelSubWithOper>());
//		
//		LttsCoreBeanUtil.getDBConnectionManager().getConnection().commit();
//	}
//}
