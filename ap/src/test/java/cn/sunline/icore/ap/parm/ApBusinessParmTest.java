package cn.sunline.icore.ap.parm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspParameter;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspParameterDao;
import cn.sunline.icore.ap.parm.ApBaseBusinessParm;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月7日-下午3:35:38</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 zhangql：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBusinessParmTest extends UnitTest {

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午2:00:11</li>
	 *         <li>功能说明：初始化数据</li>
	 *         </p>
	 */
	@Before
	public void insertParameter() {
		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);

		MspParameter parameter = BizUtil.getInstance(MspParameter.class);

		parameter.setOrg_id("99");
		parameter.setPrimary_key("mainKey");
		parameter.setParm_code(ApConst.WILDCARD);
		parameter.setParm_desc("测试");
		parameter.setParm_value("888");
		parameter.setRemark("测试测试");
		parameter.setModule("ap");
//		MspParameterDao.deleteOne_odb1(parameter.getMain_key(), parameter.getSub_key());
		MspParameterDao.insert(parameter);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午3:38:20</li>
	 *         <li>正例：根据mainKey和subKey获取获取参数值</li>
	 *         </p>
	 */
	@Test
	public void testGetValueStringString() {

		// 获取参数值
		String parameterValue = ApBaseBusinessParm.getValue("mainKey", ApConst.WILDCARD);
		assertTrue(parameterValue.equals("888"));// 正例
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午3:38:20</li>
	 *         <li>反例：根据mainKey和subKey获取获取参数值</li>
	 *         </p>
	 */
	@Test
	public void testGetValueStringStringException() {

		// 获取参数值
		String parameterValue = null;
		try {
			parameterValue = ApBaseBusinessParm.getValue("mainKey", "subKeyException");
		}
		catch (Exception e) {
			Assert.assertNull(parameterValue);// 反例
		}
		
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:16:30</li>
	 *         <li>正例：根据mainKey和subKey参数，并转换为整型输出</li>
	 *         </p>
	 */
	@Test
	public void testGetIntValueStringString() {

		// 获取参数值
		int parameterValue = ApBaseBusinessParm.getIntValue("mainKey", ApConst.WILDCARD);
		assertEquals(888, parameterValue);// 正例
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:16:30</li>
	 *         <li>反例：根据mainKey和subKey参数，并转换为整型输出</li>
	 *         </p>
	 */
	@Test
	public void testGetIntValueStringStringException() {

		// 获取参数值
		Integer parameterValue = null;
		try {
			parameterValue = ApBaseBusinessParm.getIntValue("mainKey", "subKeyException");
		}
		catch (Exception e) {
			Assert.assertNull(parameterValue);// 反例
		}
		
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:34:51</li>
	 *         <li>正例：根据mainKey和subKey获取唯一的一个业务参数值，不存在或超过一个都抛出异常</li>
	 *         </p>
	 */
	@Test
	public void testGetBigDecimalValueStringString() {

		// 获取参数值
		BigDecimal bigValue = new BigDecimal("888");
		BigDecimal parameterValue = ApBaseBusinessParm.getDecimalValue("mainKey", ApConst.WILDCARD);
		assertTrue(0==parameterValue.compareTo(bigValue));// 正例
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:34:51</li>
	 *         <li>反例：根据mainKey和subKey获取唯一的一个业务参数值，不存在或超过一个都抛出异常</li>
	 *         </p>
	 */
	@Test
	public void testGetBigDecimalValueException() {

		// 获取参数值
		BigDecimal parameterValue = null;
		try {
			parameterValue = ApBaseBusinessParm.getDecimalValue("mainKey", "subKeyException");
		}
		catch (Exception e) {
			Assert.assertNull(parameterValue);// 反例
		}	
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:35:20</li>
	 *         <li>正例：根据mainKey获取参数值</li>
	 *         </p>
	 */
	@Test
	public void testGetValueString() {

		// 获取参数值
		String parameterValue = ApBaseBusinessParm.getValue("mainKey");
		assertEquals("888", parameterValue);// 正例
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:35:20</li>
	 *         <li>反例：根据mainKey获取参数值</li>
	 *         </p>
	 */
	@Test
	public void testGetValueStringException() {

		// 获取参数值
		String parameterValue = null;
		try {
			parameterValue = ApBaseBusinessParm.getValue("mainKeyException");
		}
		catch (Exception e) {
			Assert.assertNull(parameterValue);//反例
		}
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:35:45</li>
	 *         <li>正例：根据mainKey获取参数值，并转换为整型输出</li>
	 *         </p>
	 */
	@Test
	public void testGetIntValueString() {
		// 获取参数值
		int parameterValue = ApBaseBusinessParm.getIntValue("mainKey");
		assertEquals(888, parameterValue);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:35:45</li>
	 *         <li>反例：根据mainKey获取参数值，并转换为整型输出</li>
	 *         </p>
	 */
	@Test
	public void testGetIntValueStringException() {
		// 获取参数值
		Integer parameterValue = null;
		try {
			parameterValue = ApBaseBusinessParm.getIntValue("mainKeyException");
		}
		catch (Exception e) {
			Assert.assertNull(parameterValue);
		}
		
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:36:10</li>
	 *         <li>正例：根据mainKey获取唯一的一个业务参数值，不存在或超过一个都抛出异常</li>
	 *         </p>
	 */
	@Test
	public void testGetBigDecimalValueString() {
		// 获取参数值
		BigDecimal bigValue = new BigDecimal("888");
		BigDecimal parameterValue = ApBaseBusinessParm.getDecimalValue("mainKey");
		assertTrue(0==parameterValue.compareTo(bigValue));
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月7日-下午4:36:10</li>
	 *         <li>反例：根据mainKey获取唯一的一个业务参数值，不存在或超过一个都抛出异常</li>
	 *         </p>
	 */
	@Test
	public void testGetBigDecimalValueStringException() {
		// 获取参数值
		BigDecimal parameterValue = null;
		try {
			parameterValue = ApBaseBusinessParm.getDecimalValue("mainKeyException");
		}
		catch (Exception e) {
			Assert.assertNull(parameterValue);// 反例
		}
		
	}

}
