package cn.sunline.icore.ap.batchtran;

import cn.sunline.icore.ap.api.ApMailApi;
import cn.sunline.icore.ap.namedsql.ApMailBaseDao;
import cn.sunline.icore.ap.tables.TabApMail.apb_mail_book;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
	 /**
	  * send mail
	  *
	  */

public class ap09DataProcessor extends
  AbstractBatchDataProcessor<cn.sunline.icore.ap.batchtran.intf.Ap09.Input, cn.sunline.icore.ap.batchtran.intf.Ap09.Property, cn.sunline.icore.ap.tables.TabApMail.apb_mail_book> {
	  
		private static final BizLog bizlog = BizLogUtil.getBizLog(ap09DataProcessor.class);
	  
	  /**
		 * 批次数据项处理逻辑。
		 * 
		 * @param job 批次作业ID
		 * @param index  批次作业第几笔数据(从1开始)
		 * @param dataItem 批次数据项
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 */
		@Override
		public void process(String jobId, int index, cn.sunline.icore.ap.tables.TabApMail.apb_mail_book dataItem, cn.sunline.icore.ap.batchtran.intf.Ap09.Input input, cn.sunline.icore.ap.batchtran.intf.Ap09.Property property) {
			
			bizlog.method("ap09DataProcessor begin>>>>>>>");
			
			try {
				ApMailApi.sendMail(dataItem);
			} catch (Exception e) {
				bizlog.error("ap09DataProcessor Exception Message [%s]", e.getMessage());
			}
			
			bizlog.method("ap09DataProcessor end>>>>>>>");
			
		}
		
		/**
		 * 获取数据遍历器。
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 * @return 数据遍历器
		 */
		@Override
		public BatchDataWalker<cn.sunline.icore.ap.tables.TabApMail.apb_mail_book> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap09.Input input, cn.sunline.icore.ap.batchtran.intf.Ap09.Property property) {
			
			Params parm = new Params();
			parm.add("org_id", BizUtil.getTrxRunEnvs().getBusi_org_id());

			return new CursorBatchDataWalker<apb_mail_book>(ApMailBaseDao.namedsql_selUnSendMailInfo, parm);
			
		}

}


