
package cn.sunline.icore.ap.batchtran;

import cn.sunline.icore.ap.sms.ApSmsFile;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
	 /**
	  * writing sms file
	  * @author 
	  * @Date 
	  */

public class ap10DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap10.Input, cn.sunline.icore.ap.batchtran.intf.Ap10.Property> {
  
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap10DataProcessor.class);
	
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap10.Input input, cn.sunline.icore.ap.batchtran.intf.Ap10.Property property) {
		
		 	bizlog.method("ap10DataProcessor begin>>>>>>>");
		
			try {
				ApSmsFile.smsEventUploadMain();
			}
			catch (Exception e) {
				bizlog.debug("ap10DataProcessor Exception Message [%s]",e.getMessage());
				e.printStackTrace();
				DBUtil.rollBack();
			}
			
			bizlog.method("ap10DataProcessor>>>>>>>>end>>>>>>>>>");
	}

}


