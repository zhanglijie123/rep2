package cn.sunline.icore.ap.component.appc;
//package cn.sunline.odc.cbs.base.ap.component.appc;
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.sunline.ltts.ap.component.AbstractComponent;
//import cn.sunline.ltts.ap.component.ApBaseComp;
//import cn.sunline.ltts.ap.component.ApBaseComp.BlackListAppc;
//import cn.sunline.ltts.biz.global.CommUtil;
//import cn.sunline.ltts.biz.global.SysUtil;
//import cn.sunline.ltts.busi.cbs.ap.component.component.BlackListAppcImpl.ApacheBlackListAppc;
//import cn.sunline.ltts.cbs.iobus.type.cf.ComIoCfCustomer.IoCfBlackListCheckIn;
//import cn.sunline.ltts.cbs.iobus.type.cf.ComIoCfCustomer.IoCfBlackListCheckOut;
//import cn.sunline.ltts.core.api.exception.LttsServiceException;
//import cn.sunline.ltts.core.api.logging.BizLog;
//import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.core.api.model.dm.Options;
//public class ApacheBlackListAppcImpl extends ApacheBlackListAppc implements BlackListAppc {
//
//	private static final BizLog bizlog = BizLogUtil.getBizLog(ApacheBlackListAppcImpl.class);
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Options<IoCfBlackListCheckOut> blackListCheckAppc(IoCfBlackListCheckIn checkIn) throws LttsServiceException {
//	
//		ApBaseComp.TransExecutor tran = SysUtil.getInstance(ApBaseComp.TransExecutor.class, AbstractComponent.BlackListCheckAppc);
//
//		Map<String, Object> input = new HashMap<String, Object>();
//		
//		Map<String, Object> map=CommUtil.toMap(checkIn);
//		
//		input.put("comm_req", new HashMap<String, Object>());
//		
//		input.put("input", map);
//		
//		String prcscd = "0000";
//		Map<String, Object> system = new HashMap<String, Object>();
//
//	    input.put("sys", system);
//		  
//	    system.put("prcscd", prcscd);
//	    
//		Map<String, Object> response =tran.call(input);
//		
//		bizlog.debug("1", response.toString());
//		return null;
//		
//	}
//	
//}
