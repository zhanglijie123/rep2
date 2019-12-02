package cn.sunline.icore.ap.batchtran;
import cn.sunline.icore.ap.file.ApFile;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;

	 /**
	  * writing down file about ledger balance accounts
	  * @author 
	  * @Date 
	  */
public class ap23DataProcessor
		extends
		BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap23.Input, cn.sunline.icore.ap.batchtran.intf.Ap23.Property> {

	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 */
	@Override
	public void process(
			cn.sunline.icore.ap.batchtran.intf.Ap23.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap23.Property property) {
		
		ApFile.writeLedgerBalDataMain();
	}

}

