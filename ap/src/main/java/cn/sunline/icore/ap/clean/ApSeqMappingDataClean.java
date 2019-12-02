package cn.sunline.icore.ap.clean;

import cn.sunline.clwj.msap.biz.plugin.BizPlugin;
import cn.sunline.icore.ap.api.AbstractDataClean;
import cn.sunline.icore.ap.namedsql.ApSystemBaseDao;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.base.util.RunnableWithReturn;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApSeqMappingDataClean extends AbstractDataClean{
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseDataClean.class);
	
	private static final Long DEFAULT_COUNT = 2000L;

	@Override
	public void process() {
		bizlog.method("seq mapping data clean process start >>>>>>>>>>>>>>");
		
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Long days = this.getDataReserveDays();
		String delDate = BizUtil.dateAdd("dd", runEnvs.getTrxn_date(), 0 - days.intValue());
		
		if (BizPlugin.isReduSplitDao()){
			int count = BizPlugin.getReduNumber();
			for (int i=1; i <= count; i++){
				int c = doClean("_"+String.valueOf(i), DEFAULT_COUNT, delDate);
				bizlog.debug("del table seqMapping ,slice no[%s], count[%s]", i, c);
			}
		}else{
			int s = doClean("", DEFAULT_COUNT, delDate);
			bizlog.debug("del table seqMapping count[%s]", s);
		}
		
		bizlog.method("seq mapping data clean process end <<<<<<<<<<<<<<<<<");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int doClean(final String sliceNo,final Long count, final String delDate){
		
		return (Integer)DaoUtil.executeInNewTransation(new RunnableWithReturn(){
			@Override
			public Object execute() {
				int sum = 0;
				int i = 0;
				try {
					do{
						DaoUtil.beginTransaction();
						
						i = ApSystemBaseDao.delSeqMapping(sliceNo, count.longValue(), delDate);
						
						DaoUtil.commitTransaction();
						
						sum += i;
					}while(i == count.intValue());
				} catch (Exception e) {
					bizlog.error("seq mapping table clean error:", e);				
					DaoUtil.rollbackTransaction();
					throw e;									
				}
				
				return sum;
			}
			
		});
	}
}
