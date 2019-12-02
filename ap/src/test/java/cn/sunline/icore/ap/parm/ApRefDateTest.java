package cn.sunline.icore.ap.parm;

import org.junit.Test;

import cn.sunline.icore.ap.parm.ApRefDate;

public class ApRefDateTest {

	@Test
	public void test() {
		ApRefDate.checkReferenceDate("W", "MON");
		ApRefDate.checkReferenceDate("W", "TUE");
		ApRefDate.checkReferenceDate("W", "WED");
		ApRefDate.checkReferenceDate("W", "THU");
		ApRefDate.checkReferenceDate("W", "FRI");
		ApRefDate.checkReferenceDate("W", "SAT");
		ApRefDate.checkReferenceDate("W", "SUN");
		
		ApRefDate.checkReferenceDate("M", "END");
		ApRefDate.checkReferenceDate("M", "CASEDATE");
		
		ApRefDate.checkReferenceDate("Q", "END");
		ApRefDate.checkReferenceDate("Q", "CASEDATE");
		
		ApRefDate.checkReferenceDate("H", "END");
		ApRefDate.checkReferenceDate("H", "CASEDATE");
		
		ApRefDate.checkReferenceDate("Y", "END");
		ApRefDate.checkReferenceDate("Y", "CASEDATE");
		
		ApRefDate.checkReferenceDate("M", "0101");
		ApRefDate.checkReferenceDate("M", "0131");
		ApRefDate.checkReferenceDate("Q", "0101");
		ApRefDate.checkReferenceDate("Q", "0131");
		ApRefDate.checkReferenceDate("Q", "0201");
		ApRefDate.checkReferenceDate("Q", "0231");
		ApRefDate.checkReferenceDate("Q", "0301");
		ApRefDate.checkReferenceDate("Q", "0331");
		
		ApRefDate.checkReferenceDate("H", "0101");
		ApRefDate.checkReferenceDate("H", "0631");
		
		ApRefDate.checkReferenceDate("Y", "0101");
		ApRefDate.checkReferenceDate("Y", "1231");
	}
}
