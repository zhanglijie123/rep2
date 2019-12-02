package cn.sunline.icore.ap.component.pkgheader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.component.ApPkgHeaderCompImpl.LttsPkgHeader;
import cn.sunline.ltts.biz.global.BizConstant;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.engine.data.DataArea;
import cn.sunline.ltts.srv.socket.protocol.pkgheader.PkgHeaderUtil;
import cn.sunline.ltts.srv.socket.protocol.pkgheader.SocketPkgHeaderHandler;
import cn.sunline.ltts.srv.socket.protocol.pkgheader.SocketPkgHeaderHandler.SocketPkgHeaderProperty;

/**
 * 
 * Ltts报文头处理组件实现
 * @author caiqq
 *
 */
public class LttsPkgHeaderImpl extends LttsPkgHeader implements ApBaseComp.PkgHeader {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(LttsPkgHeaderImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public String processRequest(Map request) throws LttsServiceException {
		
		SocketPkgHeaderHandler handler = createSocketPkgHeaderHandler();
		
		//报文头内容的配置
		Map<String,Object> header = new HashMap<String, Object>();
		Map<String,Object> commReq = CommUtil.toMap(request.get(DataArea.COMM_REQUEST));
		Map<String,Object> sys = CommUtil.toMap(request.get(DataArea.SYSTEM));
		
		//header.put(BizConstant.pkgsys, "800");   //系统标识号   
		//header.put(BizConstant.pkgdes, "016");   //目标系统标识号 
		header.put(BizConstant.pkgchn, commReq.get("sponsor_system")); //发起系统ID 
		header.put(BizConstant.pkgsys, commReq.get("sponsor_system")); //系统标识号   
		header.put(BizConstant.pkgdes, commReq.get("initiator_system")); //目标系统标识号 
		header.put(BizConstant.pkgsno, commReq.get("trxn_seq")); // 交易流水号 
		header.put(BizConstant.prcscd, sys.get("prcscd")); // 交易码
		
		if (request != null) {
			Object requestHeader = request.get(DataArea.HEADER);
			
			if (requestHeader instanceof Map) 
				header.putAll((Map<? extends String, ? extends Object>) request.get(DataArea.HEADER));
		}
		
		bizlog.debug("PkgHeader-->header[%s]", header.toString());
		bizlog.debug("PkgHeader-->handler[%s]", handler.toString());
		
		//报文头拼装
		byte[] bytes = handler.getHeaderData(header, this.getEncoding());
		
		bizlog.debug("PkgHeader-->bytes[%s]", Arrays.toString(bytes));
		
		try {
			return new String(bytes, this.getEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new LttsServiceException("9999", e.getMessage(), e);
		}
	}

	@Override
	public String processResponse(String response)
			throws LttsServiceException {
		SocketPkgHeaderHandler handler = createSocketPkgHeaderHandler();

		if (CommUtil.isNull(response)){
		}
		try {
			byte[] bs = response.getBytes(this.getEncoding());
			
			int headerLen = handler.getLength();
			
			if (bs.length < headerLen){
			}
			//获取报文体的字节数组
			byte[] pkg = new byte[bs.length - headerLen];
			PkgHeaderUtil.byteArrayCopy(bs, headerLen, pkg, 0, pkg.length);
			
			return new String(pkg, this.getEncoding());
			//报文头处理
		} catch (Exception e) {
			throw new LttsServiceException("9999", e.getMessage(), e);
		}
	
	}
	
	private SocketPkgHeaderHandler createSocketPkgHeaderHandler() {
		SocketPkgHeaderHandler handler = new SocketPkgHeaderHandler();
		handler.setProperties(new ArrayList<SocketPkgHeaderProperty>());
		String defaultPadding = " ";
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgmac", 16, "pkgmac", "                ", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgsno", 24, "pkgsno", "", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("prcscd", 8, "prcscd", "", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("timeout", 6, "timeout", "000000", "0", true));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgcli", 12, "pkgcli", "            ", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgchn", 3, "pkgchn", "", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgsys", 3, "pkgsys", "", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgdes", 3, "pkgdes", "", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgpwd", 1, "pkgpwd", "0", defaultPadding, false));
		handler.getProperties().add(new SocketPkgHeaderProperty("pkgsav", 40, "pkgsav", "                                        ", defaultPadding, false));
		
		return handler;
	}
	

}
