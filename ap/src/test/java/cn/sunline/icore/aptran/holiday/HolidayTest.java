package cn.sunline.icore.aptran.holiday;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.sys.type.EnumType.E_HOLIDAYCLASS;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
import cn.sunline.ltts.engine.data.DataArea;
public class HolidayTest extends UnitTest{

	private static final BizLog bizlog = BizLogUtil.getBizLog(HolidayTest.class);

	
	@Before
	public void insertInfo() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
	}
	
	private Map<String, Object> genCommReq(String trxnSeq) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("trxn_seq", trxnSeq);
		m.put("busi_teller_id", "t1");
		m.put("busi_seq", trxnSeq);
		m.put("busi_org_id", "99");
		m.put("initiator_system", "100");
		m.put("channel_id", "102");
		m.put("sponsor_system", "100");
		m.put("jiaoyigy", "交易柜员");
		m.put("branch_id", "99");
		return m;
	}
	
	@Test
	public void testAp1100() {
		DataArea requestDataArea = newTranReq("1100");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("1100"));
		// 构造请求报文
		requestDataArea.getInput().put("holiday_code", "*");
		requestDataArea.getInput().put("holiday_class", E_HOLIDAYCLASS.PERSONAL);
		requestDataArea.getInput().put("holiday_year", "2016");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);

		Options list01 = (Options)res.getOutput().get("list01");

	}
	
	@Test
	public void testAp1101() {
		DataArea requestDataArea = newTranReq("1101");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("1101"));
		// 构造请求报文
		Options list01 = new DefaultOptions();
		int i;
		for(i=0;i<5;i++){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("holiday_code", "*");
			map.put("holiday_class", E_HOLIDAYCLASS.PERSONAL);
			map.put("holiday_date", "2016010"+i);
			map.put("holiday_ind", E_YESORNO.YES);		
			map.put("operater_ind", E_DATAOPERATE.ADD);	
			list01.add(map);
		}
		
		requestDataArea.getInput().put("list01", list01);

		DataArea res = super.executeTran(requestDataArea);
		
		super.isTranSuccess(res);

	}
	
	@Test
	public void testAp1102() {
		DataArea requestDataArea = newTranReq("1102");
		// 构造公共请求
		requestDataArea.getCommReq().putAll(genCommReq("1102"));
		// 构造请求报文
		requestDataArea.getInput().put("holiday_code", "test001");
		requestDataArea.getInput().put("holiday_class", E_HOLIDAYCLASS.PERSONAL);
		requestDataArea.getInput().put("holiday_year", "2017");
		DataArea res = super.executeTran(requestDataArea);
		super.isTranSuccess(res);

		Options list01 = (Options)res.getOutput().get("list01");
		System.out.println("1");
	}
}
