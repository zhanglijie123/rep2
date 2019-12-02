package cn.sunline.icore.ap.component.pack;

import java.util.Map;

import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.component.ApPackCompImpl.ApPackJson;
import cn.sunline.ltts.base.util.JsonUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;

public class ApPackJsonImpl extends ApPackJson implements ApBaseComp.Pack {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(Map buffer) throws LttsServiceException {
		return JsonUtil.formatPkg(buffer);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map parse(String buffer) throws LttsServiceException {
		return JsonUtil.parse(buffer);
	}

}
