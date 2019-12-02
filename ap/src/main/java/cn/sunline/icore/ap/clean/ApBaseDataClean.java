package cn.sunline.icore.ap.clean;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import cn.sunline.icore.ap.api.AbstractDataClean;
import cn.sunline.icore.ap.namedsql.ApSystemBaseDao;
import cn.sunline.icore.ap.parm.ApBaseSystemParm;
import cn.sunline.icore.ap.tables.TabApSystem.App_data_cleanDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_data_clean;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.icore.sys.type.EnumType.E_DATACLEANSTRATEGY;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.ltts.core.util.LangUtil;

/**
 * <p>
 * 文件功能说明：分区及清理策略定义
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2016年12月14日-下午3:18:54</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseDataClean {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseDataClean.class);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月14日-下午3:48:19</li>
	 *         <li>功能说明：分区跟清理</li>
	 *         <li>前提：表分区都是rang形式，并且分区名是"P+日期"的形式,建表分区的时候要按照约定建</li>
	 *         </p>
	 */
	@SuppressWarnings("deprecation")
	public static void paraAndClean() {
		bizlog.techMethod("paraAndClean begin <<<<<<<<<<<<<<<<<<<<");
		// 所有定义数据
		List<app_data_clean> dataCleanList = DaoUtil.selectAll(app_data_clean.class);
		String trnDate = BizUtil.dateAdd("dd", BizUtil.getTrxRunEnvs().getTrxn_date(), 1);// 要加1天

		bizlog.debug("trans date [%s]", trnDate);

		for (app_data_clean dataClean : dataCleanList) {
			String tableName = dataClean.getTable_name();// 表名
			Long partitionDay = 1L;// 分区天数,默认1天
			Long presetPartition = Long.valueOf(ApBaseSystemParm.getIntValue(ApConst.DATACLEAN_MAINKEY, ApConst.PRESETPARTITION_SUBKEY));// 预设分区个数
			Long dataReserveDays = dataClean.getData_reserve_days();// 数据保留天数
			E_DATACLEANSTRATEGY dataCleanStraegy = dataClean.getData_clean_strategy(); // 数据清理策略 A-delete B-partition

			bizlog.debug("table [%s] start >>>>>>>>>>>", tableName);
			bizlog.debug("data clean strategy [%s]", dataCleanStraegy.getLongName());
			bizlog.debug("preset partition [%s]", presetPartition);
			bizlog.debug("data reserve days [%s]", dataReserveDays);

			if(dataCleanStraegy == E_DATACLEANSTRATEGY.PARTITION){ // 分区处理
				List<String> partitionList = ApSystemBaseDao.selPartitions(tableName, false);// 数据已排序,按照从大到小的顺序
				
				if(partitionList.size() == 0){
					bizlog.debug("table [%s] has not partition",tableName);
				}

				// 分区处理
				long sumPara = paratition(trnDate, tableName, partitionList, partitionDay, presetPartition);

				// 清理处理
				if (sumPara > 0) {
					long cleanCnt = clean(trnDate, tableName, partitionList, sumPara, dataReserveDays);
					bizlog.debug("clean paratition count [%s]", cleanCnt);
				}
			}else if(dataCleanStraegy == E_DATACLEANSTRATEGY.DELETE){ // 删除处理
				deleteProcess(dataClean);
			}


			bizlog.debug("table [%s] stop >>>>>>>>>>>", tableName);
		}
		bizlog.techMethod("paraAndClean end <<<<<<<<<<<<<<<<<<<<");
	}

	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2018年4月23日-下午5:03:40</li>
	 *         <li>删除处理</li>
	 *         </p>
	 * @param dataClean
	 */
	private static void deleteProcess(app_data_clean dataClean) {
		long dataReserveDays = dataClean.getData_reserve_days();
		String implClass = dataClean.getDel_impl_class();
		
		Class<?> implClazz = null;
		try {
			if (CommUtil.isNull(implClass)) {
				implClazz = ApDefaultDataDeletor.class;
			}
			else {
				implClazz = Class.forName(implClass);
			}
			
			if(!AbstractDataClean.class.isAssignableFrom(implClazz)){
				// 删除实现类[%s]没有实现AbstractDataClean抽象类
				ApBaseErr.ApBase.E0111(implClass);
			}
			AbstractDataClean dataCleanObj = (AbstractDataClean)BizUtil.getInstance(implClazz);
			
			dataCleanObj.setDataReserveDays(dataReserveDays);
			dataCleanObj.setTableName(dataClean.getTable_name());
			dataCleanObj.process();		
			
			DaoUtil.commitTransaction();
		}
		catch (Exception e) {
			// 删除处理失败，错误原因[%s]
			bizlog.error("delete fail:", e);
			ApBaseErr.ApBase.E0112(e.getMessage());
		}	
	}

	/**
	 * 分区处理
	 * 
	 * @return 分区总数
	 */
	private static long paratition(String trnDate, String tableName, List<String> partitionList, long partitionDay, long presetPartition) {

		int cnt = partitionList.size();
		int sumPara = cnt;// 分区总数，后面可能要新增

		bizlog.debug("present partition count [%s]", cnt);

		if (cnt == 0) {
			bizlog.debug("table [%s] has not been partition", tableName);
			return -1;
		}

		long i = 0;// 现有预设分区个数
		for (String partition : partitionList) {
			String tmp = getPartitionDate(partition);
			if (CommUtil.compare(tmp, trnDate) > 0) {
				// 分区日期大于当前日期的均为预设分区
				bizlog.debug("preset partition [%s]", tmp);
				i++;
			}
		}
		bizlog.debug("presetPartition now [%s]", i);

		if (i >= presetPartition) {
			bizlog.debug("don't need to partition");
			return sumPara;
		}

		// 分区处理
		String maxPartition = getPartitionDate(partitionList.get(0));// 现有最大的分区日期
		
		// 支持跳跑，当前日期可能已经超出预留的分区
		if (CommUtil.compare(maxPartition, BizUtil.getTrxRunEnvs().getTrxn_date()) < 0){
		    maxPartition = BizUtil.getTrxRunEnvs().getTrxn_date();
		}
		
		long diff = presetPartition - i;// 跟预留相差个数
		// 添加预留分区
		for (int j = 0; j < diff; j++) {
			maxPartition = BizUtil.dateAdd("dd", maxPartition, (int) partitionDay);// 添加日期最大的日期+预留天数
			
			addPartition(tableName, maxPartition);
			sumPara++;		
		}

		return sumPara;
	}



	/**
	 * 清理处理
	 * 
	 * @return 清理表个数
	 */
	private static long clean(String trnDate, String tableName, List<String> partitionList, long sumPara, long dataReserveDays) {

		bizlog.debug("sumPara [%s]", sumPara);
		int dropCnt = 0;
		String reserveDate = BizUtil.dateAdd("dd", trnDate, (int)-dataReserveDays);// 需要清理的最大日期日期，小于改日期就要清理

		for (String dropPartition : partitionList) {
			if (CommUtil.compare(getPartitionDate(dropPartition), reserveDate) <= 0) {
				dropPartition(tableName, dropPartition);
				dropCnt++;
			}		
		}

		return dropCnt;

	}
	
	/**
	 * 
	 * @return
	 */
	private static String getPartitionDate(String partition) {
		return partition.replace("P", "").replace("p", "");
	}

	/**
	 * 增加分区
	 */
	private static void addPartition(String tableName, String date) {
	    String partitionDate = BizUtil.dateAdd("dd", date, 1);
		String sDDL = "ALTER TABLE " + tableName + " ADD PARTITION (PARTITION p" + date + " VALUES LESS THAN (\'" + partitionDate + "\'))";
		executeDDL(sDDL);
	}

	/**
	 * 删除分区
	 */
	private static void dropPartition(String tableName, String partition) {
		String sDDL = "ALTER TABLE " + tableName + " DROP PARTITION " + partition;
		executeDDL(sDDL);
	}

	/**
	 * 执行ddl
	 */
	private static void executeDDL(String ddl) {
		DBUtil.executeDDL(ddl);
	}

	/**
	 * 查询数据保留天数
	 */
	public static Integer getDataReserveDays(String tableName) {

		app_data_clean dataClean = App_data_cleanDao.selectOne_odb1(tableName, false);

		// 没有定义返回空值
		if (CommUtil.isNull(dataClean)) {
			return null;
		}

		return Integer.parseInt(String.valueOf(dataClean.getData_reserve_days()));
	}
	
	public static class ApDefaultDataDeletor extends AbstractDataClean {
		private static final int BATCH_DELETE_SIZE = 1000;
		
		@Override
		public void process() {
			String cleanDataDate = BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), BizUtil.getTrxRunEnvs().getTrxn_date(), -1 * getDataReserveDays().intValue());
			String lastDataKeptDate = BizUtil.dateAdd(E_CYCLETYPE.DAY.getValue(), cleanDataDate, 1);

			app_data_clean cleanConf = App_data_cleanDao.selectOne_odb1(getTableName(), true);
			String cleanSql = "Delete from " + getTableName() + " where " + cleanConf.getDate_column_name() + " < '" + lastDataKeptDate + "' limit " + BATCH_DELETE_SIZE;  

			bizlog.debug("Table [%s] [%s] data clean sql is [%s]", getTableName(), BizUtil.getTrxRunEnvs().getTrxn_date(), cleanSql);
			Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
			try (Statement sqlState = conn.createStatement();) {
				int deletedCount;
				do {
					deletedCount = sqlState.executeUpdate(cleanSql);
					DBUtil.commit();
				}
				while (deletedCount >= BATCH_DELETE_SIZE);
			} catch (Exception e) {
				throw LangUtil.wrapThrow("DML failed to execute", e);
			}
		}
	}

}
