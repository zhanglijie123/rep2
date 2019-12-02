
package cn.sunline.icore.ap.batchtran;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;

	 /**
	  * update DWH configuration table
	  * @author 
	  * @Date 
	  */
public class ap89DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap89.Input, cn.sunline.icore.ap.batchtran.intf.Ap89.Property> {
  
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap89.Input input, cn.sunline.icore.ap.batchtran.intf.Ap89.Property property) {
		
		 ApFileDao.updDWHtable();
		 
	}

}


