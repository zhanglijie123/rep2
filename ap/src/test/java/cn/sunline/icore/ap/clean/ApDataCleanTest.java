package cn.sunline.icore.ap.clean;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.clean.ApBaseDataClean;
import cn.sunline.icore.ap.test.UnitTest;

public class ApDataCleanTest extends UnitTest{
	
	@Before
	public void init(){
		//公共区写入业务法人代码
    	Map<String, Object> commReq = new HashMap<String, Object>();
    	commReq.put("busi_org_id", "99");
    	commReq.put("trxn_date", "20161225");
    	newCommReq(commReq);
	}
	
	@Test
	public void testParaAndClean() {
		ApBaseDataClean.paraAndClean();
	}

}
