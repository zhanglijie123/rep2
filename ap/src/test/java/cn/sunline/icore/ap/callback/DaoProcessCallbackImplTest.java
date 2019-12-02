//package cn.sunline.icore.ap.callback;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.fail;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Test;
//
//import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspGlobalParm;
//import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspGlobalParmDao;
//import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspParameter;
//import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspParameterDao;
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.ltts.core.api.exception.LTTSDaoDuplicateException;
//import cn.sunline.icore.ap.util.BizUtil;
//
///**
// * <p>
// * 文件功能说明：dao回调单元测试
// *       			
// * </p>
// * 
// * @Author jollyja
// *         <p>
// *         <li>2016年12月7日-下午12:23:36</li>
// *         <li>修改记录</li>
// *         <li>-----------------------------------------------------------</li>
// *         <li>标记：修订内容</li>
// *         <li>20161207  jollyja：创建</li>
// *         <li>-----------------------------------------------------------</li>
// *         </p>
// */
//public class DaoProcessCallbackImplTest extends UnitTest {
//
//	/**
//	 * 
//	 * @Author jollyja
//	 *         <p>
//	 *         <li>2016年12月7日-下午12:47:51</li>
//	 *         <li>功能说明：验证参数表、法人相关特性下，删除、新增、查询、更新特性</li>
//	 *         </p>
//	 */
//	@Test
//	public void testParameterOrg() {
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "99");
//		commReq.put("busi_teller_id", "junit tester");
//		newCommReq(commReq);
//		
////		MspParameterDao.deleteOne_odb1("Org", "*");
//
//		MspParameter p1 = BizUtil.getInstance(MspParameter.class);
//		p1.setOrg_id("99");
//		p1.setPrimary_key("Org");
//		p1.setParm_code("*");
//		p1.setRemark("nothing");
//		p1.setParm_value("123");
//		p1.setParm_desc("nothing");
//		p1.setModule("nothing");
//		MspParameterDao.insert(p1);
//		
//		MspParameter p11 = BizUtil.getInstance(MspParameter.class);
//		p11.setOrg_id("888");
//		p11.setPrimary_key("Org");
//		p11.setParm_code("*");
//		p11.setRemark("nothing");
//		p11.setParm_value("123");
//		p11.setParm_desc("nothing");
//		p11.setModule("nothing");
//		try {
//			MspParameterDao.insert(p11);
//			fail();
//		} catch (LTTSDaoDuplicateException e) {
//		} catch (Throwable e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		MspParameter p2 = MspParameterDao.selectOne_odb1("Org", "*", true);
//		assertNotNull(p2);
//		assertEquals("Org", p2.getPrimary_key());
//		assertNotNull(p2.getData_version());
//		assertEquals(1, p2.getData_version().longValue());
//		assertNotNull(p2.getData_create_time());
//		assertNotNull(p2.getData_create_user());
//		assertEquals("junit tester", p2.getData_create_user());
//		assertEquals("123", p2.getParm_value());
//		assertEquals("99", p2.getOrg_id());
//		
//		p2.setParm_value("newvalue");
//		MspParameterDao.updateOne_odb1(p2);
//		
//		MspParameter p3 = MspParameterDao.selectOne_odb1("Org", "*", true);
//		assertNotNull(p3);
//		assertEquals("Org", p3.getPrimary_key());
//		assertNotNull(p3.getData_version());
//		assertEquals(2, p3.getData_version().longValue());
//		assertNotNull(p3.getData_update_time());
//		assertNotNull(p3.getData_update_user());
//		assertEquals("junit tester", p3.getData_update_user());
//		assertEquals("newvalue", p3.getParm_value());
//		assertEquals("99", p3.getOrg_id());
//	}
//	
//	/**
//	 * 
//	 * @Author jollyja
//	 *         <p>
//	 *         <li>2016年12月7日-下午12:47:51</li>
//	 *         <li>功能说明：验证参数表、法人无关特性下，删除、新增、查询、更新特性</li>
//	 *         </p>
//	 */
//	@Test
//	public void testParameterNoOrg() {
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "99");
//		commReq.put("busi_teller_id", "junit tester");
//		newCommReq(commReq);
//		
//		MspGlobalParmDao.deleteOne_odb1("NoOrg", "*");
//
//		MspGlobalParm p1 = BizUtil.getInstance(MspGlobalParm.class);
//		p1.setMain_key("NoOrg");
//		p1.setSub_key("*");
//		p1.setParm_remark("nothing");
//		p1.setParm_value("123");
//		p1.setParm_desc("nothing");
//		p1.setModule("nothing");
//		
//		MspGlobalParmDao.insert(p1);
//		
//		MspGlobalParm p2 = MspGlobalParmDao.selectOne_odb1("NoOrg", "*", true);
//		assertNotNull(p2);
//		assertEquals("NoOrg", p2.getMain_key());
//		assertNotNull(p2.getData_version());
//		assertEquals(1, p2.getData_version().longValue());
//		assertNotNull(p2.getData_create_time());
//		assertNotNull(p2.getData_create_user());
//		assertEquals("junit tester", p2.getData_create_user());
//		assertEquals("123", p2.getParm_value());
//		
//		p2.setParm_value("newvalue");
//		MspGlobalParmDao.updateOne_odb1(p2);
//		
//		MspGlobalParm p3 = MspGlobalParmDao.selectOne_odb1("NoOrg", "*", true);
//		assertNotNull(p3);
//		assertEquals("NoOrg", p3.getMain_key());
//		assertNotNull(p3.getData_version());
//		assertEquals(2, p3.getData_version().longValue());
//		assertNotNull(p3.getData_update_time());
//		assertNotNull(p3.getData_update_user());
//		assertEquals("junit tester", p3.getData_update_user());
//		assertEquals("newvalue", p3.getParm_value());
//	}
//	
//	/**
//	 * 
//	 * @Author jollyja
//	 *         <p>
//	 *         <li>2016年12月7日-下午12:47:51</li>
//	 *         <li>功能说明：验证业务表、法人相关特性下，删除、新增、查询、更新特性</li>
//	 *         </p>
//	 */
//	@Test
//	public void testBusinessNoOrg() {
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "99");
//		commReq.put("busi_teller_id", "junit tester");
//		newCommReq(commReq);
//	
//	}
//
//}
