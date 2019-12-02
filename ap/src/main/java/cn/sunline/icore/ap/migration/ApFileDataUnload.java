package cn.sunline.icore.ap.migration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.edsp.ds.manager.plugin.DBDataSourceFactory;
import cn.sunline.edsp.ds.manager.plugin.provider.JdbcDataSourceProvider;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.namedsql.ApFileBaseDao;
import cn.sunline.icore.ap.parm.ApBaseSystemParm;
import cn.sunline.icore.ap.tables.TabApDataMigration.Apl_unload_dataDao;
import cn.sunline.icore.ap.tables.TabApDataMigration.App_unload_dataDao;
import cn.sunline.icore.ap.tables.TabApDataMigration.apl_unload_data;
import cn.sunline.icore.ap.tables.TabApDataMigration.app_unload_data;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_UNLOAD_DATA_TYPE;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApFileDataUnload {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApFileDataUnload.class);
	
	public static void dataUnload() {
		
		//获取app_unload_data待处理记录数
		List<app_unload_data> processList = ApFileBaseDao.selUnloadData(E_YESORNO.YES, false);
		//已处理对象计数
		int processedItem = 0;
		//循环卸数处理
		for( app_unload_data item:processList ){
			callShellSingle(item);
			processedItem++;
		}
		
		if( processedItem != processList.size() ){
			bizlog.error("program execution abort， processed items are not match");
			throw ApPubErr.APPUB.E0039();
		}
		//生成.ok 文件
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		String fileName = runEnvs.getLast_date()+".ok";
		String fileLoaclPath = ApBaseFile.getFullPath("DWH_FILE_LOCAL_PATH");
		final LocalFileProcessor process = new LocalFileProcessor(fileLoaclPath, fileName);
		bizlog.debug("fileLoaclPath[%s], fileName[%s]", fileLoaclPath, fileName);
		process.open(true);
		process.close();
		
	}
	
	/**
	 * 
	 * @throws ParseException 
	 * @Author Dengyu
	 *         <p>
	 *         <li>2018年7月26日-下午2:22:43</li>
	 *         <li>功能说明：主程序</li>
	 *         </p>
	 */
	
	public static void callShellSingle(app_unload_data unloadCfg) {

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		//列出所有待卸载的记录：生效标志为Y
		//List<app_unload_data> unloadList = ApFileDao.selUnloadData(E_YESORNO.NO, E_YESORNO.YES, false);
		bizlog.method("excshell begin >>>>>>>>>>>>>>>>>>>>");
		if( CommUtil.isNull(unloadCfg.getUnload_data_type()) ){
			throw ApPubErr.APPUB.E0001(SysDict.A.unload_data_type.getId(), SysDict.A.unload_data_type.getDescription());
		}
		
		try {
			exc(unloadCfg.getProc_statement(), unloadCfg.getTable_name(),unloadCfg.getUnload_data_type());
		} catch (Exception e) {
			bizlog.error("execute program abort ,the error code is [%s] the error message is [%s]", e,e.getStackTrace(),e.getMessage());
			throw ApPubErr.APPUB.E0039( e);
		}
		bizlog.method("excshell end >>>>>>>>>>>>>>>>>>>>");
		//上传文件
		bizlog.method("uploadfile begin >>>>>>>>>>>>>>>>>>>>");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	Date date;
		try {
			date = sdf.parse(runEnvs.getLast_date());
		} catch (Exception e) {
			bizlog.error("execute program abort ,the error code is [%s] the error message is [%s]", e,e.getStackTrace(),e.getMessage());
			throw ApPubErr.APPUB.E0039( e);
		}
    	sdf.applyPattern("yyyy-MM-dd");
    	String lastDate = sdf.format(date);
    	//文件名
		String dataFile = "MIRAI_"+unloadCfg.getTable_name()+"_"+lastDate+"_235959.000000"+".dat";
		String ctlFile = "MIRAI_"+unloadCfg.getTable_name()+"_"+lastDate+"_235959.000000"+".ctl";
		bizlog.debug("dataFile[%s], dataCtlFile[%s]", dataFile, ctlFile);
		
		//文件路径
		String dataFileLocalPath = ApBaseFile.getFullPath("DWH_FILE_LOCAL_PATH", dataFile);
		String ctlFileLocalPath = ApBaseFile.getFullPath("DWH_FILE_LOCAL_PATH", ctlFile);
		bizlog.debug("dataFileLocalPath[%s], ctlFileLocalPath[%s]", dataFileLocalPath, ctlFileLocalPath);
		
		//上传
		/*
		String dataFileRemotePath = ApFile.getFullPath("DWH_FILE_REMOTE_PATH", dataFile);
		String ctlFileRemotePath = ApFile.getFullPath("DWH_FILE_REMOTE_PATH", ctlFile);
		bizlog.debug("dataFileRemotePath[%s], ctlFileRemotePath[%s]", dataFileRemotePath, ctlFileRemotePath);
		ApFile.createClient("DWH_FILE_REMOTE_PATH").upload(dataFileLocalPath, dataFileRemotePath);
		ApFile.createClient("DWH_FILE_REMOTE_PATH").upload(ctlFileLocalPath,ctlFileRemotePath);
		bizlog.method("uploadfile end >>>>>>>>>>>>>>>>>>>>");
		*/
		//卸数完毕，更新状态为已处理S
		unloadCfg.setProc_ind(E_YESORNO.YES);
		App_unload_dataDao.updateOne_odb1(unloadCfg);

		apl_unload_data unloadLog = BizUtil.getInstance(apl_unload_data.class);
		unloadLog.setTable_name(unloadCfg.getTable_name());
		unloadLog.setUnload_data_date(runEnvs.getLast_date());
		unloadLog.setProc_statement(unloadCfg.getProc_statement());
		unloadLog.setUpload_date(runEnvs.getTrxn_date());
		
		apl_unload_data hisUnloadLog = Apl_unload_dataDao.selectFirst_odb2(unloadCfg.getTable_name(), runEnvs.getLast_date(), false);
		if (CommUtil.isNotNull(hisUnloadLog)) {
			unloadLog.setUnload_times(hisUnloadLog.getUnload_times() + 1L);
		}
		
		Apl_unload_dataDao.insert(unloadLog);
	}
	
	/**
	 * 
	 * @Author Dengyu
	 *         <p>
	 *         <li>2018年7月26日-下午3:36:19</li>
	 *         <li>功能说明：执行shell脚本</li>
	 *         </p>
	 * @throws Exception
	 */
	
    public static void exc( String sqlStatement,String tabName,E_UNLOAD_DATA_TYPE datatype ) throws Exception {
    	
    	RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
    	
    	//卸数文件本地路径
//		String unloadFilePath = ApFile.getFullPath("DWH_FILE_LOCAL_PATH");
		String unloadFilePath = ApBaseFile.getLocalHome()+ApBaseFile.getFullPath("DWH_FILE_LOCAL_PATH");
		bizlog.debug("unloadFilePath[%s]", unloadFilePath);
		
		//卸数shell脚本地址
		String shellPath = ApBaseSystemParm.getValue("DATA_UNLOAD", "SHELL_DIR_CODE");
		//卸数shell文件名
		String shellName = ApBaseSystemParm.getValue("DATA_UNLOAD", "SHELL_FILE_NAME");
		//卸数shell全路径		
//		String shellFullPath = shellPath+"/"+shellName;		
		//DBinfo
		Map<String, Object> dbInfo = getDbInfo();
		String user = (String) dbInfo.get("db_user");
		String password = (String) dbInfo.get("db_password");
		String ip = (String) dbInfo.get("db_ip");	
		String port = (String) dbInfo.get("db_port");
		String schema = (String) dbInfo.get("db_schema");
		
		//文件名称
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	Date date = sdf.parse(runEnvs.getLast_date());
    	sdf.applyPattern("yyyy-MM-dd");
    	String lastDate = sdf.format(date);
    	//文件名
		String dataFile = "MIRAI_"+tabName+"_"+lastDate;
		String ctlFile = "MIRAI_"+tabName+"_"+lastDate;
		bizlog.debug("dataFile[%s], dataCtlFile[%s]", dataFile, ctlFile);
		//如果是全量，则起始日期为系统上线日期，从app_system_parameter中取得
		String sysOnlDate = ApBaseSystemParm.getValue("DATA_UNLOAD", "SYS_ONLINE_DATE");
		//执行  ./test app_date /app/baydit/cbs/bin/ sqlStatement
		String[] cmd = {"/bin/bash" ,shellName, tabName, unloadFilePath, sqlStatement,user,password,ip,port,schema,lastDate,sysOnlDate,datatype.getValue()};
    	ProcessBuilder pb = new ProcessBuilder(cmd);
    	Map<String, String> env = pb.environment();
    	for (String key : env.keySet())
    		bizlog.debug("key[%s] env.get(key)[%s]", key,env.get(key));
    	pb.directory(new File(shellPath));
    	Process process =null;
    	//脚本执行结果
    	int exitValue;
        try {
        	
        	process = pb.start();
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while((line = input.readLine()) != null)
            	bizlog.debug("line[%s] :", line);
                System.out.println(line);
            input.close();
            ir.close();
            exitValue = process.waitFor();
            bizlog.debug("exitValue[%s]", exitValue);
            if ( 0 != exitValue ){
            	throw ApPubErr.APPUB.E0039();
            }          	
            
        } catch (Exception e) {
        	bizlog.error("execute program abort ,the error code is [%s] the error message is [%s]", e,e.getStackTrace(),e.getMessage());
        	throw ApPubErr.APPUB.E0039(e);
        }finally{
        		if ( process != null )
        			process.destroy();
//        		try {
//					
//				} catch (NullPointerException e) {
////					e.printStackTrace();
//					bizlog.error("execute program abort ,the error code is [%s] the error message is [%s]", e,e.getStackTrace(),e.getMessage());
//		        	throw ApPubErr.APPUB.E0039(e);
//				}
        	
        }
    }
    
    private static Map<String, Object> getDbInfo() {
    	
    	Map<String, Object> dbInfo = new HashMap<>();
    	//数据库用户名
    	String dbUser = ((JdbcDataSourceProvider)DBDataSourceFactory.getDefaultDataSource()).getUserName();
    	//数据库密码
    	String dbPassword = ((JdbcDataSourceProvider)DBDataSourceFactory.getDefaultDataSource()).getPassword();
    	//数据库url
    	List<String> URL = new ArrayList<>();
		String connection = ((JdbcDataSourceProvider)DBDataSourceFactory.getDefaultDataSource()).getUrl();
		String regEx="[0-9.]{1,}|(\\d{1,})+|\\/([a-zA-Z_+]{1,})";
		Pattern pattern = Pattern.compile(regEx); 
		Matcher match = pattern.matcher(connection);
		while (match.find()) {
			String urlInfo=null;
			urlInfo=match.group();
			//加break只提取string中的一个IP
			URL.add(urlInfo.replaceAll("/", ""));
		}
		//空值检查
		BizUtil.fieldNotNull(dbUser, "db_user", "database user");
		BizUtil.fieldNotNull(dbPassword, "db_password", "database password");
		BizUtil.fieldNotNull(URL.get(0), "db_ip", "database ip");
		BizUtil.fieldNotNull(URL.get(1), "db_port", "database port");
		BizUtil.fieldNotNull(URL.get(2), "db_schema", "database db_schema");
		
		
    	dbInfo.put("db_user", dbUser);
    	dbInfo.put("db_password", dbPassword);
    	dbInfo.put("db_ip", URL.get(0));
    	dbInfo.put("db_port", URL.get(1));
    	dbInfo.put("db_schema", URL.get(2));
    	
		return dbInfo;
	}
    
}
