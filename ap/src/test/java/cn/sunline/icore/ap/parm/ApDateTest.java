package cn.sunline.icore.ap.parm;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.icore.ap.parm.ApBaseDate;
import cn.sunline.icore.ap.tables.TabApSystem.app_date;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.type.ComApSystem.ApDateInfo;
import cn.sunline.icore.ap.util.BizUtil;

public class ApDateTest extends UnitTest{
	/**
	 * @throws SQLException 
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月9日-上午9:24:05</li>
	 *         <li>功能说明：初始化测试数据</li>
	 *         </p>
	 */
	@Before
	public void initSysDate() throws SQLException {
		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
		
		//使用数据库已有数据测试
		
		/*app_date date = BizUtil.getInstance(app_date.class);
		date.setBal_sheet_date("20161222");
		date.setBusi_org_id("99");
		date.setTrxn_date("20161208");
		date.setLast_date("20161207");
		date.setNext_date("20161209");
		App_dateDao.insert(date);*/
	}

	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月9日-上午9:26:44</li>
	 *         <li>功能说明：获取系统日期对象</li>
	 *         正例
	 *         </p>
	 */
	@Test
	public void testGetInfo() {

		String orgId = MsOrg.getReferenceOrgId(app_date.class);
		ApDateInfo appDate = ApBaseDate.getInfo(orgId);

		Assert.assertNotNull(appDate);
	}
	
	/**
	 * @Author HongBiao
	 *         <p>
	 *         <li>2016年12月9日-上午9:26:44</li>
	 *         <li>功能说明：日切系统时间修改</li>
	 *         正例
	 *         </p>
	 */
	@Test
	public void testSwith() {

		String orgId = MsOrg.getReferenceOrgId(app_date.class);
		
		ApDateInfo oldAppDate = ApBaseDate.getInfo(orgId); //获取日切之前的数据

		String lastDate = oldAppDate.getTrxn_date(); // 系统上日日期使用系统当前日期
		String trxnDate = oldAppDate.getNext_date(); // 系统当前日期使用系统下日日期
		String nextDate = BizUtil.dateAdd("day", trxnDate, 1); // 系统下日日期使用系统当前日期加一天。
		String balSheetDate = BizUtil.lastDay("Y", trxnDate); // 年节日使用当前年份最后一天
		
		ApBaseDate.swith();  //日切
		
		ApDateInfo appDate = ApBaseDate.getInfo(orgId); //获取日切之后的数据
		
		/* 原数据
		date.setBal_sheet_date("20161222");
		date.setBusi_org_id("888");
		date.setTrxn_date("20161208");
		date.setLast_date("20161207");
		date.setNext_date("20161209");
		 */
		
		Assert.assertNotNull(appDate);
		
		Assert.assertEquals(balSheetDate, appDate.getBal_sheet_date());
		Assert.assertEquals(trxnDate, appDate.getTrxn_date());
		Assert.assertEquals(lastDate, appDate.getLast_date());
		Assert.assertEquals(nextDate, appDate.getNext_date());

		
	}

}
