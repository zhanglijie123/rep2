
package cn.sunline.icore.ap.batchtran;
import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.icore.ap.file.ApFile;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.tables.TabApAccounting.ApsAccountingEvent;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

	 /**
	  * writing down file about accounting event
	  * @author 
	  * @Date 
	  */
public class ap21DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap21.Input, cn.sunline.icore.ap.batchtran.intf.Ap21.Property> {
  
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap21DataProcessor.class);
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap21.Input input, cn.sunline.icore.ap.batchtran.intf.Ap21.Property property) {
			
			String temp_data = ApFileDao.selSmallDateFromAccountingEvent(MsOrg.getReferenceOrgId(ApsAccountingEvent.class), false);
			
			if(CommUtil.isNull(temp_data)){
				temp_data = BizUtil.getTrxRunEnvs().getTrxn_date();
			}

			bizlog.debug("temp_data>>>>[%s]", temp_data);
			bizlog.method("taf01DataProcessor begin>>>>>>>");
			
			RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

			// 设置公共运行变量
			runEnvs.setTemp_data(temp_data);
		
			try {
				ApFile.accountingEventUploadMain();
			}
			catch (Exception e) {
				DBUtil.rollBack();
				bizlog.error("taf01DataProcessor Exception",e.getMessage());
				throw ApErr.AP.E0131();
			}
			
			bizlog.method("taf01DataProcessor>>>>>>>>end>>>>>>>>>");
	}

}


