package cn.sunline.icore.ap.spi.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETBeforeHandler;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETHandlerConstant;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETHandlerContext;
import cn.sunline.edsp.microcore.spi.SPIMeta;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

@SPIMeta(id = ApConstants.AP_SERVICE_PROCESS_BEFORE ,order = 1100, groups = {  OETHandlerConstant.SERVICE_ENGINE_TYPE })
public class ApServiceProcessBefore implements OETBeforeHandler{
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApServiceProcessBefore.class);
	
	@Override
	public void handler(OETHandlerContext context) {
		Stack<Map<String, Object>> buffer = BizUtil.getRuleBuffer();
		if (ApConst.UN_ONLINE_NODE.equals("")) { //TODO 非联机交易中的服务节点
			buffer.push(new HashMap<String, Object>());
			
		} else { // 联机
			if (BizUtil.isNeedSetupNewBuffer(context)) {
				buffer.push(new HashMap<String, Object>());
			}
		}
		
		if (BizUtil.isPartialReversalBuzi(context)) {
			BizUtil.getTrxRunEnvs().setChrg_context_ind(MsEnumType.E_YESORNO.YES);
		}
		
	}
	
}
