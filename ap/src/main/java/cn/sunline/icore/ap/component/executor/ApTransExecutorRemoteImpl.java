package cn.sunline.icore.ap.component.executor;

import java.util.Map;

import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.component.TransExecutorImpl.ApTransExecutorRemote;
import cn.sunline.ltts.biz.global.BizConstant;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.engine.data.DataArea;

/**
 * 远程调用版本
 * @author caiqingqing
 *
 */
public class ApTransExecutorRemoteImpl extends ApTransExecutorRemote implements
		ApBaseComp.TransExecutor {

	private static final BizLog log = BizLogUtil.getBizLog(ApTransExecutorRemoteImpl.class);
	
	@SuppressWarnings({"rawtypes" })
	@Override
	public Map call(Map inputData) throws LttsServiceException {
		//组包
		ApBaseComp.Pack pack = SysUtil.getInstance(ApBaseComp.Pack.class, getPkgAbstId());
		if (pack == null) throw new IllegalArgumentException(/*ApHeadwords.ApWords.sWord013_1.getLongName() +*/ getPkgAbstId()/*+ ApHeadwords.ApWords.sWord013_2.getLongName()*/);
		String in = pack.format(inputData);
		
		ApBaseComp.PkgHeader pkgHeader = null;
		
		if (CommUtil.isNotNull(getHeaderAbstId())) {
			try {
				
				pkgHeader = SysUtil.getInstance(ApBaseComp.PkgHeader.class, getHeaderAbstId());
			} catch (Exception ignore) {
				if (log.isDebugEnabled())
					log.debug("初始化报文头对象失败。" + ignore.getMessage(), ignore);
			}
			
			if (pkgHeader != null) {
				String header = pkgHeader.processRequest(inputData);
				in = header + in;
			}
		}
		
		//发送请求
		ApBaseComp.Appc appc = SysUtil.getInstance(ApBaseComp.Appc.class, getAppcAbstId());
		
		if (appc == null) 
			throw new IllegalArgumentException(/*ApHeadwords.ApWords.sWord013_1.getLongName() + */getPkgAbstId()/*+ ApHeadwords.ApWords.sWord013_2.getLongName()*/);
		
		log.info("request[" + in + "]");
		
		String out = appc.call(in);
		
		log.info("response[" + out + "]");
		
		//报文头解析
		if (pkgHeader != null) {
			out = pkgHeader.processResponse(out);
		}
		
		Map ret;
		try {
			//报文解包
			ret = pack.parse(out);
		} catch (Exception e) {
			log.debug("package parse error! [%s]", e, e.getMessage());
			DataArea dataContext = DataArea.buildWithEmpty();
			dataContext.getSystem().put(BizConstant.erortx, /*ApHeadwords.ApWords.sWord014.getLongName()+*/out);
			dataContext.getSystem().put(BizConstant.erorcd, "0099");
			ret = dataContext.getData();
		}
		return ret;
	}

}
