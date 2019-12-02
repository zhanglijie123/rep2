package cn.sunline.icore.ap.batchtran;
import cn.sunline.icore.ap.api.ApDataCleanApi;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;

	 /**
	  * data clean
	  * @author 
	  * @Date 
	  */
public class ap98DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap98.Input, cn.sunline.icore.ap.batchtran.intf.Ap98.Property> {
  
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap98.Input input, cn.sunline.icore.ap.batchtran.intf.Ap98.Property property) {
		 ApDataCleanApi.paraAndClean();
	}

}


