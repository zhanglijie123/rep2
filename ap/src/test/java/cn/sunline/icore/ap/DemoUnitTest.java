package cn.sunline.icore.ap;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.sunline.icore.ap.rule.ApBaseBuffer;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.test.UnitTest;

/**
 * <p>
 * 文件功能说明：验证平台是否正常运行的单元测试
 *       			
 * </p>
 * 
 * @Author jollyja
 *         <p>
 *         <li>2016年12月6日-上午11:22:47</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161206  jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class DemoUnitTest extends UnitTest {

	@Test
	public void test() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		commReq.put("trxn_date","20170504");
		newCommReq(commReq); 

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("acct_id_code", "11");
		map.put("ccy_num_code", "1567");
		map.put("acct_branch", "99101");
		map.put("acct_seq", "12345678");
		map.put("loan_object", "1");
		
		ApBaseBuffer.addData("PARM", map);
		ApBaseBuffer.addData("CURRENCY", map);
		
//		System.out.println("seq:"+ApSeq.genSeq("CONTRACT_NO"));
		System.out.println("seq:"+ApBaseSeq.genSeq("GL_ACCOUNT_NO"));
	}

}
