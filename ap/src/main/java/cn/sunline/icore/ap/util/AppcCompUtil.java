package cn.sunline.icore.ap.util;
import java.util.HashMap;
import java.util.Map;

import cn.sunline.icore.ap.component.AbstractComponent;
import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.engine.data.DataArea;

/**
 * 
 * <p>
 * 文件功能说明：通讯工具类
 *       			
 * </p>
 * 
 * @Author zhangsl
 *         <p>
 *         <li>2017年9月12日-上午10:18:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年9月12日-zhangsl：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class AppcCompUtil {

	private static final BizLog bizlog = BizLogUtil.getBizLog(AppcCompUtil.class);
	
	/**
	 * 
	 * @Author zhangsl
	 *         <p>
	 *         <li>2017年9月12日-上午11:12:14</li>
	 *         <li>功能说明：功能说明：外发交易（通过前置外呼第三方系统）</li>
	 *         </p>
	 * @param input 输入MAP对象
	 * @param trxnCode 交易码
	 * @param initiatorSystem 目标系统ID
	 * @return
	 */
	public static Map<String, Object> sendTrans(Map<String, Object> input, String trxnCode, String initiatorSystem){
		
		bizlog.debug("sendTrans==>input[%s]", input.toString());
		
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		ApBaseComp.TransExecutor tran = SysUtil.getInstance(ApBaseComp.TransExecutor.class, AbstractComponent.MessageTranExecutor);

		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, Object> system = new HashMap<String, Object>();
		Map<String, Object> commReq = new HashMap<String, Object>();

		// 系统头区
		system.put("prcscd", trxnCode);

		// 公共请求区
		commReq.put("trxn_seq", runEnvs.getTrxn_seq());
		commReq.put("busi_seq", runEnvs.getBusi_seq());
		commReq.put("busi_org_id", runEnvs.getBusi_org_id());
		commReq.put("channel_id", "101");
		commReq.put("sponsor_system", "800");
		commReq.put("initiator_system", initiatorSystem);
		commReq.put("busi_teller_id", runEnvs.getTrxn_teller());
		commReq.put("busi_branch_id", runEnvs.getTrxn_branch());
		
		request.put(DataArea.INPUT, input);
		request.put(DataArea.COMM_REQUEST, commReq);
		request.put(DataArea.SYSTEM, system);

		// 通过交易外发组件向前置发送请求
		Map<String, Object> response = tran.call(request);
		
		bizlog.debug("sendTrans==>response[%s]", response.toString());

		// 返回响应报文
		return response;
		
	}
	
}
