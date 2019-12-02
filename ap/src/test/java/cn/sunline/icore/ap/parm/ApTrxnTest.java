package cn.sunline.icore.ap.parm;

import org.junit.Assert;
import org.junit.Test;

import cn.sunline.icore.ap.parm.ApBaseTrxn;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author Administrator
 *         <p>
 *         <li>2016年12月8日-下午8:09:09</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 Administrator：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApTrxnTest extends UnitTest {


	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午8:18:45</li>
	 *         <li>正例：获取交易参数信息</li>
	 *         </p>
	 */
	@Test
	public void testGetInfo() {

		// 正例
		Assert.assertTrue(ApBaseTrxn.getInfo("3020") == null ? false : true);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午8:18:45</li>
	 *         <li>反例：获取交易参数信息</li>
	 *         </p>
	 */
	@Test
	public void testGetInfoException() {

		// 反例
		try {
			ApBaseTrxn.getInfo("3333");
		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午8:24:45</li>
	 *         <li>正例：取交易序号</li>
	 *         </p>
	 */
	@Test
	public void testgetSerial() {

		// 正例：正数自增
		BizUtil.getTrxRunEnvs().setRuntime_seq(1L);

		// 正例：第一次取序号
		Long serBefore = ApBaseTrxn.getSerial();

		// 正例：第二次取序号
		Long serAfter = ApBaseTrxn.getSerial();

		Assert.assertEquals(++serBefore, serAfter);
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午8:24:45</li>
	 *         <li>反例：取交易序号</li>
	 *         </p>
	 */
	@Test
	public void testgetSerialException() {

		// 反例：null
		BizUtil.getTrxRunEnvs().setRuntime_seq(null);

		// 反例：第一次取序号
		Long serBefore1 = ApBaseTrxn.getSerial();

		// 反例：第二次取序号
		ApBaseTrxn.getSerial();

		// 反例：第三次取序号
		Long serAfter1 = ApBaseTrxn.getSerial();

		Assert.assertTrue(serAfter1 != ++serBefore1);
	}
}
