package cn.sunline.icore.ap.component.pack;

import java.util.Map;

import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.component.ApPackCompImpl.ApPackXml;
import cn.sunline.ltts.base.util.XmlUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;

public class ApPackXmlImpl extends ApPackXml implements ApBaseComp.Pack {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Map buffer) throws LttsServiceException {
		return XmlUtil.format(buffer);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map parse(String buffer) throws LttsServiceException {
		return XmlUtil.parse(buffer);
	}

}
