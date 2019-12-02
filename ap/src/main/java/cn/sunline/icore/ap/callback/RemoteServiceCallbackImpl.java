package cn.sunline.icore.ap.callback;
/*package cn.sunline.ltts.ap.callback;

import java.util.Map;

import cn.sunline.edsp.plugin.dtsp.custom.extension.CustomRPCServiceProcess;
import cn.sunline.edsp.service.type.service.ServiceRequest;

public class RemoteServiceCallbackImpl extends CustomRPCServiceProcess{

	@Override
	public void callRemoteBefore(ServiceRequest request) {
		super.callRemoteBefore(request);
		Map<String, Object> commReq = request.getCommReq();
		commReq.remove("rule_buffer");
		commReq.remove("flow_trxn_id");
		commReq.remove("consumerSystemId");// 为了远程调用服务，去掉该字段

	}
}
*/