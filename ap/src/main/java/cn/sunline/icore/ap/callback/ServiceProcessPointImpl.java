package cn.sunline.icore.ap.callback;
/*package cn.sunline.ltts.ap.callback;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.edsp.service.type.api.ServiceUtil;
import cn.sunline.edsp.service.type.model.Service;
import cn.sunline.edsp.service.type.model.ServiceType;
//import cn.sunline.ltts.plugin.core.spi.ServiceProcessPointExtension;
//import cn.sunline.ltts.plugin.core.svc.Bind;
//import cn.sunline.ltts.plugin.core.svc.ServiceRequest;
//import cn.sunline.ltts.plugin.core.svc.ServiceResponse;
import cn.sunline.edsp.service.type.service.ServiceRequest;
import cn.sunline.edsp.service.type.service.ServiceResponse;
import cn.sunline.edsp.service.type.spi.ServiceProcessPointExtension;
import cn.sunline.ltts.ap.tables.TabApSystem.App_serviceDao;
import cn.sunline.ltts.ap.tables.TabApSystem.app_service;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.odc.cbs.base.ap.util.ApConst;
import cn.sunline.odc.cbs.base.ap.util.BizUtil;


public class ServiceProcessPointImpl implements ServiceProcessPointExtension {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ServiceProcessPointImpl.class);
	
	*//**
	 * partial reversal service list
	 *//*
	private static final String PARTIAL_REVERSAL_SERVICE_LST = "SrvIoCmChrg.prcAutoChrgAccounting,SrvIoCmChrg.prcManualChrgAccounting";

	@Override
	public void serviceProcessBefore(boolean isLocal, ServiceRequest request) {
		bizlog.method(" ServiceProcessPointImpl.bizServiceProcessBefore begin >>>>>>>>>>>>>>>>");
		bizlog.debug(">>>>>>>>>>>>>>>>> bindId:[%s],srvId:[%s], input:[%s]", request.getBind().getCallIdentity(), request.getController().getModelId(), request.getInput());
		
		Stack<Map<String, Object>> buffer = getRuleBuffer();
		if (ApConst.UN_ONLINE_NODE.equals(request.getNodeid())) { // 非联机交易中的服务节点
			buffer.push(new HashMap<String, Object>());
			
		} else { // 联机
			if (isNeedSetupNewBuffer(request)) {
				buffer.push(new HashMap<String, Object>());
			}
		}
		
		if (isPartialReversalBuzi(request)) {
			BizUtil.getTrxRunEnvs().setChrg_context_ind(MsEnumType.E_YESORNO.YES);
		}
		
		bizlog.method(" ServiceProcessPointImpl.bizServiceProcessBefore end <<<<<<<<<<<<<<<<");
	}
	
	private boolean isNeedSetupNewBuffer(ServiceRequest request) {
		Service service = ServiceUtil.getByFullId(request.getController().getModelId());
		ServiceType serviceType = service.getOwner();
		
		String serviceOwnerName = serviceType.getId();
		String serviceMethod = service.getId();
		app_service serviceCtrl = App_serviceDao.selectOne_odb1(serviceOwnerName, serviceMethod, false);
		
		if (serviceCtrl != null && serviceCtrl.getCreate_buffer_ind() == E_YESORNO.YES) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean isPartialReversalBuzi(ServiceRequest request) {
		Service service = ServiceUtil.getByFullId(request.getController().getModelId());
		String serviceId = service.getFullId();
		
		if (CommUtil.isNotNull(serviceId) && PARTIAL_REVERSAL_SERVICE_LST.contains(serviceId)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void serviceProcessAfter(boolean isLocal, ServiceRequest request, ServiceResponse response) {
		bizlog.method(" ServiceProcessPointImpl.bizServiceProcessAfter begin >>>>>>>>>>>>>>>>");
		bizlog.debug(">>>>>>>>>>>>>>>>> bindId:[%s],srvId:[%s], output:[%s]", request.getBind().getCallIdentity(), request.getController().getModelId(), response.getRespStr());
		
		Stack<Map<String, Object>> buffer = getRuleBuffer();
		if (ApConst.UN_ONLINE_NODE.equals(request.getNodeid())) { // 非联机交易中的服务节点
			buffer.pop();
		} else { // 联机服务节点暂时也开辟了一个新的缓冲区
			if (isNeedSetupNewBuffer(request)) {
				buffer.pop();
			}
		}
		
		if (isPartialReversalBuzi(request)) {
			BizUtil.getTrxRunEnvs().setChrg_context_ind(MsEnumType.E_YESORNO.NO);
		}
		
		bizlog.method(" ServiceProcessPointImpl.bizServiceProcessAfter end <<<<<<<<<<<<<<<<");
	}

	@Override
	public void serviceProcessException(boolean isLocal, boolean isCommit, ServiceRequest request, ServiceResponse serviceResponse, Exception ex) {
		bizlog.method(" ServiceProcessPointImpl.bizServiceProcessException begin >>>>>>>>>>>>>>>>");
		bizlog.debug(">>>>>>>>>>>>>>>>> bindId:[%s],srvId:[%s], exception:[%s]", request.getBind().getCallIdentity(), request.getController().getModelId(), ex);
		
		Stack<Map<String, Object>> buffer = getRuleBuffer();
		if (buffer.isEmpty()) {
			return;
		}
		
		if (ApConst.UN_ONLINE_NODE.equals(request.getNodeid())) { // 非联机交易中的服务节点
			buffer.pop();
		} else { // 联机服务节点
			if (isNeedSetupNewBuffer(request)) {
				buffer.pop();
			}
		}
		
		if (isPartialReversalBuzi(request)) {
			BizUtil.getTrxRunEnvs().setChrg_context_ind(MsEnumType.E_YESORNO.NO);
		}
		
		bizlog.method(" ServiceProcessPointImpl.bizServiceProcessException end <<<<<<<<<<<<<<<<");
	}
	
	@SuppressWarnings({ "unchecked" })
	private Stack<Map<String, Object>> getRuleBuffer() {
		Object ruleBufferObj = BizUtil.getTrxRunEnvs().getRule_buffer();
		
		Stack<Map<String, Object>> ruleBufferStack = null;
		if (ruleBufferObj == null) {
			ruleBufferStack = new Stack<Map<String, Object>>();
			BizUtil.getTrxRunEnvs().setRule_buffer(ruleBufferStack);
		} else {
			ruleBufferStack = (Stack<Map<String, Object>>) ruleBufferObj;
		}
		
		return ruleBufferStack;
	}
}
*/