package cn.sunline.icore.ap.util;
import junit.framework.Assert;

import org.junit.Test;

import cn.sunline.icore.ap.util.BizUtil;


public class BizUtilNewTest {
	
	@Test
	public void calcDateByReferenceTestD() {	
		
		String result = "";
		
		result = BizUtil.calcDateByReference(null, "20180228", "1D");
		Assert.assertEquals("20180301", result);
		System.out.println("null, 20180228, 1D, result = " + result);		
		
		result = BizUtil.calcDateByReference(null, "20180101", "31D");
		Assert.assertEquals("20180201", result);
		System.out.println("null, 20180101, 31D, result = " + result);
		
		result = BizUtil.calcDateByReference("20180131", "20180101", "31D");
		Assert.assertEquals("20180201", result);
		System.out.println("20180131, 20180101, 31D, result = " + result);	
		
		result = BizUtil.calcDateByReference(null, "20180101", "-1D");
		Assert.assertEquals("20171231", result);
		System.out.println("null, 20180101, -1D, result = " + result);	
		
		result = BizUtil.calcDateByReference("20180101", "20180201", "-5D");
		Assert.assertEquals("20180127", result);
		System.out.println("20180101, 20180201, -5D, result = " + result);	
	}
	
	
	@Test
	public void calcDateByReferenceTestW() {	
		
		String result = "";
		
		result = BizUtil.calcDateByReference(null, "20181229", "1W");
		Assert.assertEquals("20190105", result);
		System.out.println("null, 20181229, 1W, result = " + result);		
		
		result = BizUtil.calcDateByReference("MON", "20181229", "1W");
		Assert.assertEquals("20181231", result);
		System.out.println("MON, 20181229, 1W, result = " + result);
		
		result = BizUtil.calcDateByReference("TUE", "20181229", "1W");
		Assert.assertEquals("20190101", result);
		System.out.println("TUE, 20181229, 1W, result = " + result);		

		result = BizUtil.calcDateByReference("WED", "20181229", "1W");
		Assert.assertEquals("20190102", result);
		System.out.println("WED, 20181229, 1W, result = " + result);
		
		result = BizUtil.calcDateByReference("THU", "20181229", "1W");
		Assert.assertEquals("20190103", result);
		System.out.println("THU, 20181229, 1W, result = " + result);
		
		result = BizUtil.calcDateByReference("FRI", "20181229", "1W");
		Assert.assertEquals("20190104", result);
		System.out.println("FRI, 20181229, 1W, result = " + result);
		
		result = BizUtil.calcDateByReference("SAT", "20181229", "1W");
		Assert.assertEquals("20190105", result);
		System.out.println("SAT, 20181229, 1W, result = " + result);
		
		result = BizUtil.calcDateByReference("SUN", "20181229", "1W");
		Assert.assertEquals("20181230", result);
		System.out.println("SUN, 20181229, 1W, result = " + result);
		
		result = BizUtil.calcDateByReference(null, "20181229", "2W");
		Assert.assertEquals("20190112", result);
		System.out.println("null, 20181229, 2W, result = " + result);

		result = BizUtil.calcDateByReference("20010101", "20181229", "2W");
		System.out.println("20010101, 20181229, 2W, result = " + result);	
		Assert.assertEquals("20190112", result);		
		
		result = BizUtil.calcDateByReference("MON", "20181229", "2W");
		Assert.assertEquals("20190107", result);
		System.out.println("MON, 20181229, 2W, result = " + result);
		
		result = BizUtil.calcDateByReference("TUE", "20181229", "2W");
		Assert.assertEquals("20190108", result);
		System.out.println("TUE, 20181229, 2W, result = " + result);		

		result = BizUtil.calcDateByReference("WED", "20181229", "2W");
		Assert.assertEquals("20190109", result);
		System.out.println("WED, 20181229, 2W, result = " + result);
		
		result = BizUtil.calcDateByReference("THU", "20181229", "2W");
		Assert.assertEquals("20190110", result);
		System.out.println("THU, 20181229, 2W, result = " + result);
		
		result = BizUtil.calcDateByReference("FRI", "20181229", "2W");
		Assert.assertEquals("20190111", result);
		System.out.println("FRI, 20181229, 2W, result = " + result);
		
		result = BizUtil.calcDateByReference("SAT", "20181229", "2W");
		Assert.assertEquals("20190112", result);
		System.out.println("SAT, 20181229, 2W, result = " + result);
		
		result = BizUtil.calcDateByReference("SUN", "20181229", "2W");
		Assert.assertEquals("20190106", result);
		System.out.println("SUN, 20181229, 2W, result = " + result);
		
		result = BizUtil.calcDateByReference("FRI", "20181229", "-1W");
		Assert.assertEquals("20181228", result);
		System.out.println("FRI, 20181229, -1W, result = " + result);

		result = BizUtil.calcDateByReference("FRI", "20181229", "-2W");
		Assert.assertEquals("20181221", result);
		System.out.println("FRI, 20181229, -2W, result = " + result);
		
		result = BizUtil.calcDateByReference("FRI", "20181228", "-1W");
		Assert.assertEquals("20181221", result);
		System.out.println("FRI, 20181228, -1W, result = " + result);

	}
		
	@Test
	public void calcDateByReferenceTestM() {
		
		String result = "";
		
		result = BizUtil.calcDateByReference("0130", "20180131", "1M");
		Assert.assertEquals("20180228", result);
		System.out.println("0130, 20180131, 1M, result = " + result);	
		
		result = BizUtil.calcDateByReference("0129", "20180201", "1M");
		Assert.assertEquals("20180228", result);
		System.out.println("0129, 20180201, 1M, result = " + result);
				
		result = BizUtil.calcDateByReference("END", "20180101", "1M");
		Assert.assertEquals("20180131", result);
		System.out.println("END, 20180101, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180201", "1M");
		Assert.assertEquals("20180228", result);
		System.out.println("END, 20180201, 1M, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20160201", "1M");
		Assert.assertEquals("20160229", result);
		System.out.println("END, 20160201, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20160131", "1M");
		Assert.assertEquals("20160229", result);
		System.out.println("30, 20160131, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20180131", "1M");
		Assert.assertEquals("20180228", result);
		System.out.println("30, 20180131, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20180227", "1M");
		Assert.assertEquals("20180228", result);
		System.out.println("30, 20180227, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20180228", "1M");
		Assert.assertEquals("20180330", result);
		System.out.println("30, 20180228, 1M, result = " + result);		
		
		result = BizUtil.calcDateByReference("30", "20180331", "1M");
		Assert.assertEquals("20180430", result);
		System.out.println("30, 20180331, 1M, result = " + result);			

		result = BizUtil.calcDateByReference("31", "20180227", "1M");
		Assert.assertEquals("20180228", result);
		System.out.println("31, 20180227, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("31", "20160228", "1M");
		Assert.assertEquals("20160229", result);
		System.out.println("31, 20160228, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("31", "20160229", "1M");
		Assert.assertEquals("20160331", result);
		System.out.println("31, 20160229, 1M, result = " + result);		
		
		result = BizUtil.calcDateByReference("31", "20160331", "1M");
		Assert.assertEquals("20160430", result);
		System.out.println("31, 20160331, 1M, result = " + result);	
		
		result = BizUtil.calcDateByReference("31", "20180228", "1M");
		Assert.assertEquals("20180331", result);
		System.out.println("31, 20180228, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("31", "20180430", "1M");
		Assert.assertEquals("20180531", result);
		System.out.println("31, 20180430, 1M, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180101", "2M");
		Assert.assertEquals("20180228", result);
		System.out.println("END, 20180101, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180201", "2M");
		Assert.assertEquals("20180331", result);
		System.out.println("END, 20180201, 2M, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20160201", "2M");
		Assert.assertEquals("20160331", result);
		System.out.println("END, 20160201, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20160131", "2M");
		Assert.assertEquals("20160330", result);
		System.out.println("30, 20160131, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20180131", "2M");
		Assert.assertEquals("20180330", result);
		System.out.println("30, 20180131, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20180227", "2M");
		Assert.assertEquals("20180330", result);
		System.out.println("30, 20180227, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("30", "20180228", "2M");
		Assert.assertEquals("20180430", result);
		System.out.println("30, 20180228, 2M, result = " + result);		
		
		result = BizUtil.calcDateByReference("30", "20180331", "2M");
		Assert.assertEquals("20180530", result);
		System.out.println("30, 20180331, 2M, result = " + result);			

		result = BizUtil.calcDateByReference("31", "20180227", "2M");
		Assert.assertEquals("20180331", result);
		System.out.println("31, 20180227, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("31", "20180228", "2M");
		Assert.assertEquals("20180430", result);
		System.out.println("31, 20180228, 2M, result = " + result);		
		
		result = BizUtil.calcDateByReference("31", "20160228", "2M");
		Assert.assertEquals("20160331", result);
		System.out.println("31, 20160228, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("31", "20160229", "2M");
		Assert.assertEquals("20160430", result);
		System.out.println("31, 20160229, 2M, result = " + result);		
		
		result = BizUtil.calcDateByReference("31", "20160331", "2M");
		Assert.assertEquals("20160531", result);
		System.out.println("31, 20160331, 2M, result = " + result);	
		
		result = BizUtil.calcDateByReference("31", "20180430", "2M");
		Assert.assertEquals("20180630", result);
		System.out.println("31, 20180430, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("20180131", "20180430", "2M");
		Assert.assertEquals("20180630", result);
		System.out.println("20180131, 20180430, 2M, result = " + result);
		
		result = BizUtil.calcDateByReference("20180131", "20180430", "-1M");
		Assert.assertEquals("20180331", result);
		System.out.println("20180131, 20180430, -1M, result = " + result);
		
		result = BizUtil.calcDateByReference("20180131", "20180430", "-2M");
		Assert.assertEquals("20180228", result);
		System.out.println("20180131, 20180430, -2M, result = " + result);
		
		result = BizUtil.calcDateByReference("20180131", "20180501", "-2M");
		Assert.assertEquals("20180331", result);
		System.out.println("20180131, 20180501, -2M, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180501", "-2M");
		Assert.assertEquals("20180331", result);
		System.out.println("END, 20180501, -2M, result = " + result);
		
	}
	
	@Test
	public void calcDateByReferenceTest28() {
		
		String result = "";
		
		result = BizUtil.calcDateByReference28("0130", "20180131", "1M");
		Assert.assertEquals("20180301", result);
		System.out.println("0130, 20180131, 1M, result = " + result);	
		
		result = BizUtil.calcDateByReference28("0129", "20180201", "1M");
		Assert.assertEquals("20180301", result);
		System.out.println("0129, 20180201, 1M, result = " + result);
				
		result = BizUtil.calcDateByReference28("0131", "20180201", "1Q");
		Assert.assertEquals("20180501", result);
		System.out.println("0131, 20180201, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference28("0331", "20180401", "1H");
		Assert.assertEquals("20181001", result);
		System.out.println("0331, 20180401, 1H, result = " + result);
	}
	
	@Test
	public void calcDateByReferenceTestQ() {
		
		String result = "";
		
		result = BizUtil.calcDateByReference("END", "20180330", "1Q");
		Assert.assertEquals("20180331", result);
		System.out.println("END, 20180330, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180331", "1Q");
		Assert.assertEquals("20180630", result);
		System.out.println("END, 20180331, 1Q, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20180401", "1Q");
		Assert.assertEquals("20180630", result);
		System.out.println("END, 20180401, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180701", "1Q");
		Assert.assertEquals("20180930", result);
		System.out.println("END, 20180701, 1Q, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20181001", "1Q");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20181001, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20181231", "1Q");
		Assert.assertEquals("20190331", result);
		System.out.println("END, 20181231, 1Q, result = " + result);		
		
		result = BizUtil.calcDateByReference("0101", "20180101", "1Q");
		Assert.assertEquals("20180401", result);
		System.out.println("0101, 20180101, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("20180101", "20180101", "1Q");
		Assert.assertEquals("20180401", result);
		System.out.println("20180101, 20180101, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("01", "20180101", "1Q");
		Assert.assertEquals("20180401", result);
		System.out.println("01, 20180101, 1Q, result = " + result);		
		
		result = BizUtil.calcDateByReference("0331", "20180331", "1Q");
		Assert.assertEquals("20180630", result);
		System.out.println("0331, 20180331, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0330", "20180330", "1Q");
		Assert.assertEquals("20180630", result);		
		System.out.println("0330, 20180330, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0330", "20180330", "2Q");
		Assert.assertEquals("20180930", result);		
		System.out.println("0330, 20180330, 2Q, result = " + result);	
		
		result = BizUtil.calcDateByReference("0331", "20180331", "2Q");
		Assert.assertEquals("20180930", result);		
		System.out.println("0331, 20180331, 2Q, result = " + result);		
		
		result = BizUtil.calcDateByReference("0330", "20180330", "3Q");
		Assert.assertEquals("20181230", result);		
		System.out.println("0330, 20180330, 3Q, result = " + result);		
		
		result = BizUtil.calcDateByReference("0331", "20180331", "3Q");
		Assert.assertEquals("20181231", result);		
		System.out.println("0331, 20180331, 3Q, result = " + result);			
		
		result = BizUtil.calcDateByReference("1130", "20181130", "1Q");
		Assert.assertEquals("20190228", result);		
		System.out.println("1130, 20181130, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("1130", "20191130", "1Q");
		Assert.assertEquals("20200229", result);		
		System.out.println("1130, 20191130, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0228", "20180228", "1Q");
		Assert.assertEquals("20180528", result);		
		System.out.println("0228, 20180228, 1Q, result = " + result);	
		
		result = BizUtil.calcDateByReference("0229", "20200229", "1Q");
		Assert.assertEquals("20200529", result);		
		System.out.println("0229, 20200229, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0430", "20180430", "1Q");
		Assert.assertEquals("20180730", result);		
		System.out.println("0430, 20180430, 1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0430", "20180430", "2Q");
		Assert.assertEquals("20181030", result);		
		System.out.println("0430, 20180430, 2Q, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180430", "-1Q");
		Assert.assertEquals("20180331", result);		
		System.out.println("END, 20180430, -1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0430", "20180430", "-1Q");
		Assert.assertEquals("20180130", result);		
		System.out.println("0430, 20180430, -1Q, result = " + result);
		
		result = BizUtil.calcDateByReference("0430", "20180501", "-2Q");
		Assert.assertEquals("20180130", result);		
		System.out.println("0430, 20180501, -2Q, result = " + result);
		
	}
	
	@Test
	public void calcDateByReferenceTestH() {
		
		String result = "";	
		
		result = BizUtil.calcDateByReference("END", "20180101", "1H");
		Assert.assertEquals("20180630", result);
		System.out.println("END, 20180101, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180630", "1H");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20180701, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180701", "1H");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20180701, 1H, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20181230", "1H");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20181230, 1H, result = " + result);		
	
		result = BizUtil.calcDateByReference("END", "20181231", "1H");
		Assert.assertEquals("20190630", result);
		System.out.println("END, 20181231, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180101", "2H");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20180101, 2H, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180630", "2H");
		Assert.assertEquals("20190630", result);
		System.out.println("END, 20180701, 2H, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180701", "2H");
		Assert.assertEquals("20190630", result);
		System.out.println("END, 20180701, 2H, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20181230", "2H");
		Assert.assertEquals("20190630", result);
		System.out.println("END, 20181230, 2H, result = " + result);		
	
		result = BizUtil.calcDateByReference("END", "20181231", "2H");
		Assert.assertEquals("20191231", result);
		System.out.println("END, 20181231, 2H, result = " + result);		
		
		result = BizUtil.calcDateByReference("END", "20181231", "2H");
		Assert.assertEquals("20191231", result);
		System.out.println("END, 20181231, 2H, result = " + result);
		
		result = BizUtil.calcDateByReference("20180221", "20180101", "1H");
		Assert.assertEquals("20180221", result);
		System.out.println("20180221, 20180101, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("21", "20180101", "1H");
		Assert.assertEquals("20180121", result);
		System.out.println("21, 20180101, 1H, result = " + result);

		result = BizUtil.calcDateByReference("0321", "20180101", "1H");
		Assert.assertEquals("20180321", result);
		System.out.println("0321, 20180101, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0228", "20180701", "1H");
		Assert.assertEquals("20180828", result);
		System.out.println("0228, 20180701, 1H, result = " + result);		
		
		result = BizUtil.calcDateByReference("0828", "20180701", "1H");
		Assert.assertEquals("20180828", result);
		System.out.println("0828, 20180701, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0828", "20180829", "1H");
		Assert.assertEquals("20190228", result);
		System.out.println("0828, 20180829, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0828", "20180830", "1H");
		Assert.assertEquals("20190228", result);
		System.out.println("0828, 20180830, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0828", "20180831", "1H");
		Assert.assertEquals("20190228", result);
		System.out.println("0828, 20180831, 1H, result = " + result);			
		
		result = BizUtil.calcDateByReference("0829", "20180829", "1H");
		Assert.assertEquals("20190228", result);
		System.out.println("0829, 20180829, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0830", "20180829", "1H");
		Assert.assertEquals("20180830", result);
		System.out.println("0830, 20180829, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0830", "20180830", "1H");
		Assert.assertEquals("20190228", result);
		System.out.println("0830, 20180830, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0830", "20190830", "1H");
		Assert.assertEquals("20200229", result);
		System.out.println("0830, 20190830, 1H, result = " + result);		
		
		result = BizUtil.calcDateByReference("0830", "20190830", "2H");
		Assert.assertEquals("20200830", result);
		System.out.println("0830, 20190830, 2H, result = " + result);	
		
		result = BizUtil.calcDateByReference("0930", "20180930", "1H");
		Assert.assertEquals("20190330", result);
		System.out.println("0930, 20180930, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("1231", "20181231", "1H");
		Assert.assertEquals("20190630", result);
		System.out.println("1231, 20181231, 1H, result = " + result);
		
		result = BizUtil.calcDateByReference("0131", "20180131", "1H");
		Assert.assertEquals("20180731", result);
		System.out.println("0131, 20180131, 1H, result = " + result);	
		
		result = BizUtil.calcDateByReference("END", "20180131", "-1H");
		Assert.assertEquals("20171231", result);
		System.out.println("END, 20180131, -1H, result = " + result);	
		
		result = BizUtil.calcDateByReference("0201", "20180131", "-2H");
		Assert.assertEquals("20170201", result);
		System.out.println("0201, 20180131, -2H, result = " + result);	
	}	
	
	@Test
	public void calcDateByReferenceTestY() {
		
		String result = "";
		
		result = BizUtil.calcDateByReference("END", "20180101", "1Y");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20180101, 1Y, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20181230", "1Y");
		Assert.assertEquals("20181231", result);
		System.out.println("END, 20180228, 1Y, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20181231", "1Y");
		Assert.assertEquals("20191231", result);
		System.out.println("END, 20181231, 1Y, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20180101", "2Y");
		Assert.assertEquals("20191231", result);
		System.out.println("END, 20180101, 2Y, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20181230", "2Y");
		Assert.assertEquals("20191231", result);
		System.out.println("END, 20180228, 2Y, result = " + result);
		
		result = BizUtil.calcDateByReference("END", "20181231", "2Y");
		Assert.assertEquals("20201231", result);
		System.out.println("END, 20181231, 2Y, result = " + result);	
		
		result = BizUtil.calcDateByReference("0228", "20180101", "1Y");
		Assert.assertEquals("20180228", result);
		System.out.println("0228, 20180101, 1Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0228", "20160101", "1Y");
		Assert.assertEquals("20160228", result);
		System.out.println("0228, 20160101, 1Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0229", "20160101", "1Y");
		Assert.assertEquals("20160229", result);
		System.out.println("0229, 20160101, 1Y, result = " + result);	
		
		result = BizUtil.calcDateByReference("0229", "20180101", "1Y");
		Assert.assertEquals("20180228", result);
		System.out.println("0229, 20180101, 1Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0330", "20180329", "1Y");
		Assert.assertEquals("20180330", result);
		System.out.println("0330, 20180329, 1Y, result = " + result);			
		
		result = BizUtil.calcDateByReference("0330", "20180330", "1Y");
		Assert.assertEquals("20190330", result);
		System.out.println("0330, 20180330, 1Y, result = " + result);	
		
		result = BizUtil.calcDateByReference("0228", "20180101", "2Y");
		Assert.assertEquals("20190228", result);
		System.out.println("0228, 20180101, 2Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0228", "20160101", "2Y");
		Assert.assertEquals("20170228", result);
		System.out.println("0228, 20160101, 2Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0229", "20160101", "2Y");
		Assert.assertEquals("20170228", result);
		System.out.println("0229, 20160101, 2Y, result = " + result);	
		
		result = BizUtil.calcDateByReference("0229", "20150101", "2Y");
		Assert.assertEquals("20160229", result);
		System.out.println("0229, 20150101, 2Y, result = " + result);			
		
		result = BizUtil.calcDateByReference("0229", "20180101", "2Y");
		Assert.assertEquals("20190228", result);
		System.out.println("0229, 20180101, 2Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0330", "20180329", "2Y");
		Assert.assertEquals("20190330", result);
		System.out.println("0330, 20180329, 2Y, result = " + result);			
		
		result = BizUtil.calcDateByReference("0330", "20180330", "2Y");
		Assert.assertEquals("20200330", result);
		System.out.println("0330, 20180330, 2Y, result = " + result);	
		
		result = BizUtil.calcDateByReference("20180330", "20180330", "2Y");
		Assert.assertEquals("20200330", result);
		System.out.println("20180330, 20180330, 2Y, result = " + result);	
		
		result = BizUtil.calcDateByReference("END", "20180330", "-2Y");
		Assert.assertEquals("20161231", result);
		System.out.println("END, 20180330, -2Y, result = " + result);
		
		result = BizUtil.calcDateByReference("0201", "20180330", "-1Y");
		Assert.assertEquals("20180201", result);
		System.out.println("0201, 20180330, -1Y, result = " + result);
	}
	
	@Test
	public void calcNextDateByCaseDate() {
		
		String result = "";
		
		result = BizUtil.calcNextDateByCaseDate("20190201", "20190201", "1D", false);
		Assert.assertEquals("20190202", result);
		System.out.println("20190201, 20190201, 1D, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "2D", false);
		Assert.assertEquals("20190202", result);
		System.out.println("20190101, 20190201, 2D, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "-1D", false);
		Assert.assertEquals("20190131", result);
		System.out.println("20190101, 20190201, -1D, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "-2D", false);
		Assert.assertEquals("20190131", result);
		System.out.println("20190101, 20190201, -2D, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "1W", false);
		Assert.assertEquals("20190205", result);
		System.out.println("20190101, 20190201, 1W, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "2W", false);
		Assert.assertEquals("20190212", result);
		System.out.println("20190101, 20190201, 2W, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "-1W", false);
		Assert.assertEquals("20190129", result);
		System.out.println("20190101, 20190201, -1D, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "-2W", false);
		Assert.assertEquals("20190129", result);
		System.out.println("20190101, 20190201, -2W, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "1M", false);
		Assert.assertEquals("20190301", result);
		System.out.println("20190101, 20190201, 1M, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "2M", false);
		Assert.assertEquals("20190301", result);
		System.out.println("20190101, 20190201, 2M, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190201", "-1M", false);
		Assert.assertEquals("20190101", result);
		System.out.println("20190101, 20190201, -1M, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190601", "-2M", false);
		Assert.assertEquals("20190501", result);
		System.out.println("20190101, 20190601, -2M, result = " + result);
		
		result = BizUtil.calcNextDateByCaseDate("20190101", "20190701", "-2M", false);
		Assert.assertEquals("20190501", result);
		System.out.println("20190101, 20190701, -2M, result = " + result);
	}
}
