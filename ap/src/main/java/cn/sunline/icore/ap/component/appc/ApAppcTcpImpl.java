package cn.sunline.icore.ap.component.appc;

import java.io.IOException;
import java.util.Map;

import cn.sunline.icore.ap.component.ApAppcCompImpl;
import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.service.appc.LengthPrefixTcpClient;

public class ApAppcTcpImpl extends ApAppcCompImpl.AppcTcp implements ApBaseComp.Appc {
	private static final BizLog log = BizLogUtil.getBizLog(ApAppcTcpImpl.class);
	
	@Override
	@SuppressWarnings("rawtypes")
	public String call(String sendBuffer, Map properties)
			throws LttsServiceException {
		LengthPrefixTcpClient sc = new LengthPrefixTcpClient(this.getEncoding(), 
				this.getLeftPadding(),
				this.getLengthPrefixLength(), 
				this.getPaddingChar());
		
		try {
			sc.setConnectTimeout( this.getConnectTimeoutInMs() );
			sc.connect( getIp(), getPort() );
		} catch (Exception e) {
			throw new LttsServiceException("10", "ApHeadwords.ApWords.sWord012.getLongName()", e);
		}
		
		try {			
			return sc.call(sendBuffer);
		} finally {
			try {
				sc.disconnect();
			} catch (IOException e) {
				log.error("关闭连接失败", e);
			}
		}
	}
	@Override
	public String call(String sendBuffer) throws LttsServiceException {
		return call(sendBuffer, null);
	}	
}
