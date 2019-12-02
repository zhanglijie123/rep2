package cn.sunline.icore.ap.parm;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.parm.ApBaseSummary;
import cn.sunline.icore.ap.tables.TabApBasic.App_summaryDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_summary;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;

public class ApSummaryTest extends UnitTest {
	
	@Before
	public void initlist() {
		
		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
		
		//插入测试数据
		app_summary summerList = BizUtil.getInstance(app_summary.class);
		
		summerList.setSummary_code("tset_01");
		summerList.setSummary_text("测试");
		summerList.setShort_text("测试");
		
		App_summaryDao.deleteOne_odb1(summerList.getSummary_code());
		App_summaryDao.insert(summerList);
	}
    //传入存在的摘要代码，并返回true
	@Test
	public void testExists() {
		Assert.assertTrue(ApBaseSummary.exists("tset_01"));
	}
    
	
	// 输入不存在的摘要代码，会返回false
	@Test
	public void testNotExists() {
		
			  Assert.assertFalse(ApBaseSummary.exists("tset_02"));
	}

	// 输入已存在的摘要代码，返回摘要文字
	@Test
	public void testGetText() {
			Assert.assertNotNull(ApBaseSummary.getText("tset_01"));
	}
	
	//输入不存在的摘要代码，返回的摘要文字为空
	@Test
	public void testNotGetText() {
		
		try {
			
			 ApBaseSummary.getText("tset_02");
			 Assert.assertNotNull(null);
		
		} catch (LttsBusinessException e) {
			
			 Assert.assertNull(null);
		}
		
	}

/*	@Test
	public void testAll() {
		fail("Not yet implemented");
	}
*/
}
