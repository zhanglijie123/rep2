package cn.sunline.icore.ap.rule;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.rule.ApBaseRule.BufferedDataVisitor;
import cn.sunline.icore.ap.rule.ApRuleFunc;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
public class ApRuleFuncTest extends UnitTest {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApRuleFuncTest.class);

	@Before
	public void init() throws Exception{
		Map<String, Object> commReq = new HashMap<String, Object>();
    	commReq.put("busi_org_id", "99");
    	commReq.put("trxn_code", "1010");
    	commReq.put("trxn_desc", "测试");
    	commReq.put("trxn_date", "20161208");
    	newCommReq(commReq);
	}
	
	@Test
	public void testSumFunc() {
		String funcFrontName = "Sum";
		String funcArgsExp = "number1=>${INPUT.principal},number2=>${INPUT.interest}";
		final Map<String, Object> argsContainer = new HashMap<>();
		Map<String, Object> input = new HashMap<>();
		input.put("principal", new BigDecimal("1000"));
		input.put("interest", new BigDecimal("0.123"));
		
		argsContainer.put("INPUT", input);
		
		Object ret = ApRuleFunc.eval(funcFrontName, funcArgsExp, new BufferedDataVisitor("") {
			protected Object getFieldValue(String dataMart, String columnName, boolean nullable) {
				return argsContainer.get(columnName);
			}
			
			protected Object getFieldValue(String fieldName, boolean nullable) {
				return argsContainer.get(fieldName);
			}
		});
		bizlog.debug("Object ret = %s", ret);
	}
	
	@Test
	public void testDateBetwFunc() {
		String funcFrontName = "Date_Between";
		String funcArgsExp = "date1=>${INPUT.trxn_date},date2=>${INPUT.start_intr_date}";
		final Map<String, Object> argsContainer = new HashMap<>();
		Map<String, Object> input = new HashMap<>();
		input.put("principal", new BigDecimal("1000"));
		input.put("interest", new BigDecimal("0.123"));
		input.put("trxn_date", "20180202");
		input.put("start_intr_date", "20180131");
		
		argsContainer.put("INPUT", input);
		
		Object ret = ApRuleFunc.eval(funcFrontName, funcArgsExp, new BufferedDataVisitor("") {
			protected Object getFieldValue(String dataMart, String columnName, boolean nullable) {
				return argsContainer.get(columnName);
			}
			
			protected Object getFieldValue(String fieldName, boolean nullable) {
				
				return argsContainer.get(fieldName);
			}
		});
		bizlog.debug("Object ret = %s", ret);
	}
}
