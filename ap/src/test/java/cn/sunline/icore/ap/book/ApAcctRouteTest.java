package cn.sunline.icore.ap.book;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.book.ApBaseAcctRoute;
import cn.sunline.icore.ap.tables.TabApBook.Apb_account_routeDao;
import cn.sunline.icore.ap.tables.TabApBook.apb_account_route;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_ACCTROUTETYPE;

public class ApAcctRouteTest extends UnitTest{

	@Before
	public void init() throws Exception{
		Map<String, Object> commReq = new HashMap<String, Object>();
    	commReq.put("trxn_seq", "00000001");
    	commReq.put("busi_seq", "00000002");
    	commReq.put("busi_org_id", "99");
    	commReq.put("trxn_code", "Junit");
    	commReq.put("trxn_desc", "测试");
    	commReq.put("trxn_date", "20161208");
    	newCommReq(commReq);
	}
	
	@Test
	public void registerTest() {
		ApBaseAcctRoute.register("100010001", E_ACCTROUTETYPE.CARD);
		apb_account_route acctRoute = Apb_account_routeDao.selectOne_odb1("100010001", false);
		Assert.assertNotNull(acctRoute);
	}
	
	@Test
	public void getRouteTypeTest() {
		apb_account_route acctRoute = BizUtil.getInstance(apb_account_route.class);
		acctRoute.setAcct_no("100010001");
        acctRoute.setAcct_route_type(E_ACCTROUTETYPE.CARD);
        Apb_account_routeDao.insert(acctRoute);
        
        E_ACCTROUTETYPE type = ApBaseAcctRoute.getRouteType("100010001");
        Assert.assertTrue(type==E_ACCTROUTETYPE.CARD);
	}

}
