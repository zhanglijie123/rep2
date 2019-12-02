package cn.sunline.icore.ap.batchtran;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

	 /**
	  * inform DWH icore table unload successful
	  * @author 
	  * @Date 
	  */
public class ap91DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap91.Input, cn.sunline.icore.ap.batchtran.intf.Ap91.Property> {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap91DataProcessor.class);
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap91.Input input, cn.sunline.icore.ap.batchtran.intf.Ap91.Property property) {
		LocalFileProcessor okFileWriter = new LocalFileProcessor(ApFileApi.getFullPath(ap90DataProcessor.DATA_FILE_DIR_CODE), 
					BizUtil.getTrxRunEnvs().getLast_date() + ".ok");
		okFileWriter.open(true);
		okFileWriter.close();
		bizlog.debug("[DWH]Data extraction of %s over.", BizUtil.getTrxRunEnvs().getLast_date());

	}

}


