package cn.sunline.icore.ap.callback;

import java.util.HashMap;
import java.util.Map;

import cn.sunline.ltts.engine.data.DataArea;
import cn.sunline.ltts.srv.socket.protocol.pkgheader.PkgProcessor;

/**
 * 报文头处理器
 *
 */
public class BizPkgHeaderProcessor extends PkgProcessor{

	@Override
	public String processRequest(Map<String, Object> requestHeaderData,
			String requestMessage) {
		
		return requestMessage;
	}
	
	@Override
	public String processResponse(Map<String, Object> response, String responseMessage) {
		return responseMessage;
	}

	@Override
	public Map<String, Object> processResponseHeader(DataArea request,
			DataArea response, String responseMessage) {
        Map<String, Object> requestHeader = (request == null ? new HashMap<String, Object>() : request.getHeader());
		
		Map<String,Object> ret = new HashMap<String, Object>();
		ret.putAll(requestHeader);
		
		return ret;
	}

}
