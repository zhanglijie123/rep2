package cn.sunline.icore.ap.audit;

import cn.sunline.icore.ap.test.UnitTest;

public class ApDataAuditTest extends UnitTest{
//	
//	@Before
//	public void init() throws Exception{
//		Map<String, Object> commReq = new HashMap<String, Object>();
//    	commReq.put("trxn_seq", "00000001");
//    	commReq.put("busi_seq", "00000002");
//    	commReq.put("busi_org_id", "99");
//    	commReq.put("trxn_code", "1010");
//    	commReq.put("trxn_desc", "测试");
//    	commReq.put("trxn_date", "20161208");
//    	newCommReq(commReq);
//    	
//    	Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
//		Statement statement = conn.createStatement();
//		//先删除其他数据，防止干扰测试
//		String sql1 = "delete from apl_parameter_audit";
//		String sql2 = "delete from apl_parameter_audit_sub";
//		String sql3 = "delete from apl_business_audit";
//		String sql4 = "delete from apl_business_audit_sub";
//		statement.executeUpdate(sql1);
//		statement.executeUpdate(sql2);
//		statement.executeUpdate(sql3);
//		statement.executeUpdate(sql4);
//		statement.close();
//	}
//
//	/**
//	 * 正例
//	 * 正常操作
//	 */
//    @Test
//	public void testRegLogOnInsertParameter() {		
//		app_organization app_organization1 = BizUtil.getInstance(app_organization.class);
//		app_organization1.setBusi_org_id("8888");
//		app_organization1.setOrg_name("单元测试法人");
//		app_organization1.setDefault_org_ind(E_YESORNO.NO);
//		app_organization1.setRef_org_id("99");
//		
//		ApDataAudit.regLogOnInsertParameter(app_organization1);
//	}
//
//    /**
//	 * 反例
//	 * 随便填一个类
//	 */
//    @Test
//	public void testRegLogOnInsertParameter2() {		
//		String s = "123";
//		
//		try {
//			ApDataAudit.regLogOnInsertParameter(s);
//		}
//		catch (Exception e) {
//			Assert.assertNotNull(e);
//			return ;
//		}
//		Assert.fail("case fail");
//	}
//    
//	@Test
//	public void testRegLogOnDeleteParameter() {		
//		app_organization app_organization1 = BizUtil.getInstance(app_organization.class);
//		app_organization1.setBusi_org_id("001");
//		app_organization1.setOrg_name("单元测试法人");
//		app_organization1.setDefault_org_ind(E_YESORNO.NO);
//		app_organization1.setRef_org_id("99");
//		
//		ApDataAudit.regLogOnDeleteParameter(app_organization1);
//	}
//	
//	/**
//	 * 反例
//	 * 随便填一个类
//	 */
//    @Test
//	public void testRegLogOnDeleteParameter2() {		
//		String s = "123";
//		
//		try {
//			ApDataAudit.regLogOnInsertParameter(s);
//		}
//		catch (Exception e) {
//			Assert.assertNotNull(e);
//			return ;
//		}
//		Assert.fail("case fail");
//	}
//
//    /**
//     * 正例
//     * 正常操作
//     */
//	@Test
//	public void testRegLogOnUpdateParameter() {
//		app_organization app_organization1 = BizUtil.getInstance(app_organization.class);
//		app_organization1.setBusi_org_id("001");
//		app_organization1.setOrg_name("单元测试法人");
//		app_organization1.setDefault_org_ind(E_YESORNO.NO);
//		app_organization1.setRef_org_id("99");
//		
//		app_organization app_organization2 = BizUtil.getInstance(app_organization.class);
//		app_organization2.setBusi_org_id("002");
//		app_organization2.setOrg_name("单元测试法人ddd");
//		app_organization2.setDefault_org_ind(E_YESORNO.YES);
//		app_organization2.setRef_org_id("99911");
//		
//		ApDataAudit.regLogOnUpdateParameter(app_organization1, app_organization2);
//	}
//	
//	/**
//	 * 反例
//	 * 修改前和修改后的表对象不一致
//	 */
//	@Test
//	public void testRegLogOnUpdateParameter2() {
//		app_organization app_organization1 = BizUtil.getInstance(app_organization.class);
//		app_organization1.setBusi_org_id("8888");
//		app_organization1.setOrg_name("单元测试法人");
//		app_organization1.setDefault_org_ind(E_YESORNO.NO);
//		app_organization1.setRef_org_id("99");
//		
//		app_parameter_reference app_parameter_reference = BizUtil.getInstance(app_parameter_reference.class);
//		
//		try {
//			ApDataAudit.regLogOnUpdateParameter(app_organization1, app_parameter_reference);
//		}
//		catch (LttsBusinessException e) {
//			Assert.assertTrue(e.getCode().equals(ApBaseErr.ApBase.F_E0005));
//		}
//	}
//	
//	/**
//	 * 反例
//	 * 修改前和修改后的表对象不一致
//	 */
//	@Test
//	public void testRegLogOnUpdateParameter3() {
//		app_organization app_organization1 = BizUtil.getInstance(app_organization.class);
//		app_organization1.setBusi_org_id("001");
//		app_organization1.setOrg_name("单元测试法人");
//		app_organization1.setDefault_org_ind(E_YESORNO.NO);
//		app_organization1.setRef_org_id("99");
//		
//		app_parameter_reference app_parameter_reference = BizUtil.getInstance(app_parameter_reference.class);
//		
//		try {
//			ApDataAudit.regLogOnUpdateParameter(app_organization1, app_parameter_reference);
//		}
//		catch (LttsBusinessException e) {
//			Assert.assertTrue(e.getCode().equals(ApBaseErr.ApBase.F_E0005));
//		}
//	}
//
//	@Test
//	public void testRegLogOnUpdateBusiness() {		
//		app_organization app_organization1 = BizUtil.getInstance(app_organization.class);
//		app_organization1.setBusi_org_id("001");
//		app_organization1.setOrg_name("单元测试法人");
//		app_organization1.setDefault_org_ind(E_YESORNO.NO);
//		app_organization1.setRef_org_id("99");
//		
//		app_organization app_organization2 = BizUtil.getInstance(app_organization.class);
//		app_organization2.setBusi_org_id("002");
//		app_organization2.setOrg_name("单元测试法人ddd");
//		app_organization2.setDefault_org_ind(E_YESORNO.YES);
//		app_organization2.setRef_org_id("99911");
//		
//		ApDataAudit.regLogOnUpdateBusiness(app_organization1, app_organization2);
//	}
//	
//	@After
//	public void commit(){
//		DaoUtil.commitTransaction();
//	}
//
}
