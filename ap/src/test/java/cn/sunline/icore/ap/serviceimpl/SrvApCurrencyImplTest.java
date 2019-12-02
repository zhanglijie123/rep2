//package cn.sunline.icore.ap.serviceimpl;
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
//import cn.sunline.icore.ap.servicetype.SrvApCurrency;
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfo;
//import cn.sunline.ltts.core.api.model.dm.Options;
//import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
//import cn.sunline.icore.ap.util.BizUtil;
//
///***
// * 
// * <p>
// * 文件功能说明：货币参数维护测试类
// *       			
// * </p>
// * 
// * @Author zhangjing2
// *         <p>
// *         <li>2016年12月26日-下午3:32:51</li>
// *         <li>修改记录</li>
// *         <li>-----------------------------------------------------------</li>
// *         <li>标记：修订内容</li>
// *         <li>2016年12月26日zhangjing2：创建</li>
// *         <li>-----------------------------------------------------------</li>
// *         </p>
// */
//public class SrvApCurrencyImplTest extends UnitTest {
//	@Before
//	public void init(){
//		Map<String, Object> m = new HashMap<String, Object>();
//		m.put("page_size", 20L);
//		m.put("page_start", 0L);
//		m.put("busi_org_id", "99");
//		super.newCommReq(new HashMap<String, Object>());
//	}
//	/**
//	 *  货币参数列表查询测试
//	 */
//	@Test
//	public void testQueryCurrencyList() {
//		RunEnvs env = BizUtil.getTrxRunEnvs();
//		env.setPage_size(20L);
//		env.setPage_start(0L);
//		env.setBusi_org_id("99");
//		env.setPage_start(1L);
//		//反射服务
//		SrvApCurrency srv = BizUtil.getInstance(SrvApCurrency.class);
//		
//		String ccyCode = "C";
//		String ccyName = "人";
//		String countryCode = "C";
//		//查询结果
//		Options<ApCurrencyInfo> op = srv.queryCurrencyList(ccyCode,ccyName,countryCode); 
//		List<ApCurrencyInfo> list = op.getValues();
//		assertTrue(list.size() < 21);
//		assertNotNull(op);
//	}
//}
