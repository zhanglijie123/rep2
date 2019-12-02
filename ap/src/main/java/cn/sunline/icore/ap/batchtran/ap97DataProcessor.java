
package cn.sunline.icore.ap.batchtran;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;

	 /**
	  * batch encrypt
	  * @author 
	  * @Date 
	  */
public class ap97DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap97.Input, cn.sunline.icore.ap.batchtran.intf.Ap97.Property> {
  
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap97.Input input, cn.sunline.icore.ap.batchtran.intf.Ap97.Property property) {
		//List<Table> tables = OdbFactory.get().getOdbManager(Table.class).selectAll();
		
		//for(Table t : tables){
			
		//}
	}

}


