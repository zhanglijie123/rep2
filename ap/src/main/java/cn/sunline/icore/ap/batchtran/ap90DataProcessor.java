
package cn.sunline.icore.ap.batchtran;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.edsp.ds.manager.plugin.DBDataSourceFactory;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.namedsql.ApFileBaseDao;
import cn.sunline.icore.ap.tables.TabApDataMigration.app_unload_data;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.ltts.base.util.LangUtil;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

	 /**
	  * DWH table unload
	  * @author 
	  * @Date 
	  */
public class ap90DataProcessor extends
  AbstractBatchDataProcessor<cn.sunline.icore.ap.batchtran.intf.Ap90.Input, cn.sunline.icore.ap.batchtran.intf.Ap90.Property, cn.sunline.icore.ap.tables.TabApDataMigration.app_unload_data> {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap90DataProcessor.class);
	public static final String DATA_FILE_DIR_CODE = "DWH_FILE_LOCAL_PATH";
	private static final int DATA_FETCH_SIZE = 1024;
	private static final String DATA_COLUMN_SEPARATOR = "|";
	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String DATA_FILE_NAME_FORMAT = "MIRAI_%s_%s_235959.000000.dat";
	private static final String CTRL_FILE_NAME_FORMAT = "MIRAI_%s_%s_235959.000000.ctl";
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
	public void process(String jobId, int index, cn.sunline.icore.ap.tables.TabApDataMigration.app_unload_data dataItem, cn.sunline.icore.ap.batchtran.intf.Ap90.Input input, cn.sunline.icore.ap.batchtran.intf.Ap90.Property property) {
		bizlog.debug("Extract table[%s] data processing, fetch sql is [%s]", dataItem.getTable_name(), dataItem.getProc_statement());

		String SysLineSeparator = System.getProperty("line.separator");
		
		try (Connection conn = DBDataSourceFactory.getDefaultDataSource().getDataSource().getConnection();
			PreparedStatement ps = conn.prepareStatement(dataItem.getProc_statement(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			ps.setFetchSize(DATA_FETCH_SIZE);

			ResultSet rs = ps.executeQuery();
			int colCounts = rs.getMetaData().getColumnCount();

			LocalFileProcessor dataFileWriter = new LocalFileProcessor(ApFileApi.getFullPath(DATA_FILE_DIR_CODE), 
					String.format(DATA_FILE_NAME_FORMAT, dataItem.getTable_name(), BizUtil.getTrxRunEnvs().getLast_date()));
			dataFileWriter.open(true);

			int dataCounts = 0;
			String lineSeparator = "";
			String coluSeparator = "";
			StringBuffer content = new StringBuffer();
			while (rs.next()) { // write data file
				content.setLength(0);
				content.append(lineSeparator);

				coluSeparator = "";
				for (int idx = 1; idx <= colCounts; idx++) {
					rs.getMetaData().getColumnType(idx);
					content.append(coluSeparator).append(rs.getString(idx));
					coluSeparator = DATA_COLUMN_SEPARATOR;
				}
				dataFileWriter.writeLastLine(content.toString());
				lineSeparator = SysLineSeparator;
				dataCounts++;
			}
			dataFileWriter.close();

			genCtrlFile(dataItem.getTable_name(), dataCounts);
			bizlog.debug("Extract table[%s] data finished", dataItem.getTable_name());
		}
		catch (Exception e) {
			if (e instanceof LttsBusinessException) {
				bizlog.error("Extract table data failed, due to business defect[%s]. Please follow up.", e.getMessage());
			}
			else {
				bizlog.error("Extract table data failed, due to %s.", e.getMessage());
				throw LangUtil.wrapThrow(e);
			}
		}
	}

	private void genCtrlFile(String tableName, int dataCounts) {
		LocalFileProcessor ctrlFileWriter = new LocalFileProcessor(ApFileApi.getFullPath(DATA_FILE_DIR_CODE), 
				String.format(CTRL_FILE_NAME_FORMAT, tableName, BizUtil.getTrxRunEnvs().getLast_date()));
		ctrlFileWriter.open(true);
		ctrlFileWriter.writeLastLine(genCtrlContent(dataCounts));
		ctrlFileWriter.close();
	}

	/* format
	 * 2017-06-14 23:59:46.0000002017-06-14 00:00:00.0000002017-06-14 23:59:59.00000000000000010000008953
	 */
	private String genCtrlContent(int dataCounts) {
		String lastDate = BizUtil.getTrxRunEnvs().getLast_date();
		lastDate = lastDate.substring(0, 4) + "-" + lastDate.substring(4, 6) + "-" + lastDate.substring(6, 8);

		String fileCreateTime = dateTimeFormat.format(new Date()) + ".000000";
		String dataStartTime = lastDate + " 00:00:00.000000";
		String dataEndTime = lastDate + " 23:59:59.000000";
		String startIdx = String.format("%010d", 1);
		String endIdx = String.format("%010d", dataCounts);
		
		return fileCreateTime + dataStartTime + dataEndTime + startIdx + endIdx;
	}
	
	/**
	 * 获取数据遍历器。
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 * @return 数据遍历器
	 */
	@Override
	public BatchDataWalker<cn.sunline.icore.ap.tables.TabApDataMigration.app_unload_data> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap90.Input input, cn.sunline.icore.ap.batchtran.intf.Ap90.Property property) {
		bizlog.debug("[DWH]Data extraction of %s begin.", BizUtil.getTrxRunEnvs().getLast_date());
		Params parm = new Params();
		parm.put(SysDict.A.valid_ind.getId(), E_YESORNO.YES);
		return new CursorBatchDataWalker<app_unload_data>(ApFileBaseDao.namedsql_selUnloadData, parm);
	}

}


