package cn.sunline.icore.ap.parm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.parm.ApBaseHoliday;
import cn.sunline.icore.ap.tables.TabApBasic.App_holidayDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_holiday;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.type.EnumType.E_CUSTOMERTYPE;
import cn.sunline.icore.sys.type.EnumType.E_HOLIDAYCLASS;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月8日-下午5:29:42</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 zhangql：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApHolidayTest extends UnitTest {
	
	/**
	 * 初始化节假日信息
	 */
	@Test
	public void initHoliday(){
		String day = "20170101"; // 年份
		app_holiday holiday = BizUtil.getInstance(app_holiday.class);
		holiday.setHoliday_code(ApConst.WILDCARD); // 假日代码
				
		for(int i=0;i<365;i++){
			holiday.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
			String next = BizUtil.dateAdd("d", day, i);
			holiday.setHoliday_date(next);
			holiday.setHoliday_ind(E_YESORNO.NO);
			App_holidayDao.insert(holiday);
		}
		
		for(int i=0;i<365;i++){
			holiday.setHoliday_class(E_HOLIDAYCLASS.CORPORATE);
			String next = BizUtil.dateAdd("d", day, i);
			holiday.setHoliday_date(next);
			holiday.setHoliday_ind(E_YESORNO.NO);
			App_holidayDao.insert(holiday);
		}
		
		DBUtil.commit();
	}

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

		app_holiday parameter1 = BizUtil.getInstance(app_holiday.class);
		parameter1.setHoliday_code("A");
		parameter1.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter1.setHoliday_date("20000101");
		parameter1.setHoliday_ind(E_YESORNO.NO);
		parameter1.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter1.getHoliday_code(), parameter1.getHoliday_class(), parameter1.getHoliday_date());
		App_holidayDao.insert(parameter1);

		app_holiday parameter2 = BizUtil.getInstance(app_holiday.class);
		parameter2.setHoliday_code("A");
		parameter2.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter2.setHoliday_date("20000102");
		parameter2.setHoliday_ind(E_YESORNO.YES);
		parameter2.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter2.getHoliday_code(), parameter2.getHoliday_class(), parameter2.getHoliday_date());
		App_holidayDao.insert(parameter2);

		app_holiday parameter3 = BizUtil.getInstance(app_holiday.class);
		parameter3.setHoliday_code("A");
		parameter3.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter3.setHoliday_date("20000103");
		parameter3.setHoliday_ind(E_YESORNO.NO);
		parameter3.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter3.getHoliday_code(), parameter3.getHoliday_class(), parameter3.getHoliday_date());
		App_holidayDao.insert(parameter3);

		app_holiday parameter4 = BizUtil.getInstance(app_holiday.class);
		parameter4.setHoliday_code("A");
		parameter4.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter4.setHoliday_date("20000104");
		parameter4.setHoliday_ind(E_YESORNO.YES);
		parameter4.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter4.getHoliday_code(), parameter4.getHoliday_class(), parameter4.getHoliday_date());
		App_holidayDao.insert(parameter4);

		app_holiday parameter5 = BizUtil.getInstance(app_holiday.class);
		parameter5.setHoliday_code("A");
		parameter5.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter5.setHoliday_date("20000105");
		parameter5.setHoliday_ind(E_YESORNO.NO);
		parameter5.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter5.getHoliday_code(), parameter5.getHoliday_class(), parameter5.getHoliday_date());
		App_holidayDao.insert(parameter5);

		app_holiday parameter6 = BizUtil.getInstance(app_holiday.class);
		parameter6.setHoliday_code("A");
		parameter6.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter6.setHoliday_date("20000106");
		parameter6.setHoliday_ind(E_YESORNO.YES);
		parameter6.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter6.getHoliday_code(), parameter6.getHoliday_class(), parameter6.getHoliday_date());
		App_holidayDao.insert(parameter6);

		app_holiday parameter7 = BizUtil.getInstance(app_holiday.class);
		parameter7.setHoliday_code(ApConst.WILDCARD);
		parameter7.setHoliday_class(E_HOLIDAYCLASS.PERSONAL);
		parameter7.setHoliday_date("20000107");
		parameter7.setHoliday_ind(E_YESORNO.NO);
		parameter7.setRemark("备注");
		App_holidayDao.deleteOne_odb1(parameter7.getHoliday_code(), parameter7.getHoliday_class(), parameter7.getHoliday_date());
		App_holidayDao.insert(parameter7);

	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午5:55:12</li>
	 *         <li>正例：判断是否是节假日</li>
	 *         </p>
	 */
	@Test
	public void testIsHoliday() {

		// 正例
		Assert.assertTrue(ApBaseHoliday.isHoliday("20000102", "A", E_CUSTOMERTYPE.PERSONAL));
		Assert.assertTrue(ApBaseHoliday.isHoliday("20000104", "A", E_CUSTOMERTYPE.PERSONAL));
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午5:55:12</li>
	 *         <li>反例：判断是否是节假日</li>
	 *         </p>
	 */
	@Test
	public void testIsHolidayException() {

		// 反例
		Assert.assertTrue(!ApBaseHoliday.isHoliday("20000101", "A", E_CUSTOMERTYPE.PERSONAL));
		Assert.assertTrue(!ApBaseHoliday.isHoliday("20000103", "A", E_CUSTOMERTYPE.PERSONAL));
		Assert.assertTrue(!ApBaseHoliday.isHoliday("20000105", "A", E_CUSTOMERTYPE.PERSONAL));
		Assert.assertTrue(!ApBaseHoliday.isHoliday("20000107", "B", E_CUSTOMERTYPE.PERSONAL));
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午6:04:43</li>
	 *         <li>正例：获取指定日的第N个工作日</li>
	 *         </p>
	 */
	@Test
	public void testGetNextWorkerDay() {

		// 正例
		Assert.assertEquals("20000105", ApBaseHoliday.getNextWorkerDay("20000101", "A", E_CUSTOMERTYPE.PERSONAL, 2));
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午6:04:43</li>
	 *         <li>反例：获取指定日的第N个工作日</li>
	 *         </p>
	 */
	@Test
	public void testGetNextWorkerDayException() {

		// 反例
		Assert.assertTrue(!(ApBaseHoliday.getNextWorkerDay("20000101", "A", E_CUSTOMERTYPE.PERSONAL, 2).equals("20000103")));
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午6:14:14</li>
	 *         <li>正例：获取指定日期的前一个工作日</li>
	 *         </p>
	 */
	@Test
	public void testGetPriorWorkerDay() {

		// 正例
		Assert.assertEquals("20000101", ApBaseHoliday.getPriorWorkerDay("20000103", "A", E_CUSTOMERTYPE.PERSONAL));
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午6:14:14</li>
	 *         <li>反例：获取指定日期的前一个工作日</li>
	 *         </p>
	 */
	@Test
	public void testGetPriorWorkerDayException() {

		// 反例
		Assert.assertTrue(!"20000102".equals(ApBaseHoliday.getPriorWorkerDay("20000103", "A", E_CUSTOMERTYPE.PERSONAL)));
	}

}
