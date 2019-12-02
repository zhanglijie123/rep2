package cn.sunline.icore.ap.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.oss.api.OssFactory;
import cn.sunline.clwj.msap.oss.model.MsFileInfo;
import cn.sunline.clwj.msap.oss.model.MsTransferFileInfo;
import cn.sunline.clwj.msap.oss.spi.MsTransfer;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.batch.ApBatch;
import cn.sunline.icore.ap.namedsql.ApFileBaseDao;
import cn.sunline.icore.ap.parm.ApBaseDropList;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchReceive;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchReceiveDao;
import cn.sunline.icore.ap.tables.TabApFile.AppBatch;
import cn.sunline.icore.ap.tables.TabApFile.AppBatchDao;
import cn.sunline.icore.ap.tables.TabApFile.AppDirectory;
import cn.sunline.icore.ap.tables.TabApFile.AppDirectoryDao;
import cn.sunline.icore.ap.tables.TabApFile.AppRootDirectory;
import cn.sunline.icore.ap.tables.TabApFile.AppRootDirectoryDao;
import cn.sunline.icore.ap.type.ComApFile.ApFileIn;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.type.EnumType.E_DIRPARMTYPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.base.util.FileDataExecutor;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.FileUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApBaseFile {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseFile.class);

	private static final String BUSI_BATCH_CODE = "BUSI_BATCH_CODE";
	
	private static final String FILE_OK = ".ok";

	private static boolean exists(String dirCode, boolean flag) {

		ApBaseDropList.exists(ApConst.DIR_CODE, dirCode, flag);

		List<AppDirectory> dirInfo = AppDirectoryDao.selectAll_odb1(dirCode, false);
		if (dirInfo.size() <= 0 && flag) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppDirectory.class).getLongname(), SysDict.A.dir_code.getLongName(), dirCode);
		}
		return dirInfo.size() <= 0 ? false : true;
	}

	
	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2017年10月17日-下午5:46:28</li>
	 *         <li>功能说明：创建文件传输客户端</li>
	 *         </p>
	 * @param dirCode
	 * @return
	 */
	public static MsTransfer createClient(String dirCode){
		String protocolId = null;
		
		AppRootDirectory rootDir = AppRootDirectoryDao.selectOne_odb1(dirCode, false);
		if ( CommUtil.isNotNull(rootDir)){
			protocolId = rootDir.getRoot_dir_id();
		}
		return OssFactory.get().create(protocolId);
//		return FileServiceAPI.createClient(protocolId);
	}
	
	/**
	 * 构建文件传输对象
	 * <p>Title:getMsTransferFileInfo </p>
	 * <p>Description:	</p>
	 * @author jiangyaming
	 * @date   2019年8月5日 
	 * @param localFilePath
	 * @param remoteFilePath
	 * @param fileName
	 * @return
	 */
	public static MsTransferFileInfo getMsTransferFileInfo(String localFilePath, String remoteFilePath, String fileName,
			boolean createOk) {
		MsFileInfo localFile = new MsFileInfo(localFilePath, fileName);
		MsFileInfo remoteFile = new MsFileInfo(remoteFilePath, fileName);
		if (createOk) {
			remoteFile.setUpdateOk(true);
			localFile.setUpdateOk(true);
		}
		return new MsTransferFileInfo(localFile, remoteFile);
	}
	
	/**
	 * <p>
	 * <li>2016年1月6日-上午10:44:55</li>
	 * <li>功能说明：读文件</li>
	 * </p>
	 * 
	 * @param file
	 * @return 读取文件列表
	 */
	public static List<String> readFile(File file) {
		final List<String> lines = new ArrayList<String>();
		FileUtil.readFile(file.getAbsolutePath(), new FileDataExecutor() {

			@Override
			public void process(int index, String line) {
				lines.add(line);
			}

		});
		return lines;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午4:59:27</li>
	 *         <li>功能说明：根据目录编码获取目录路径</li>
	 *         </p>
	 * @param dirCode
	 * @return 目录路径
	 */
	public static String getFullPath(String dirCode) {
		return getFullPath(dirCode, "");
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午4:59:27</li>
	 *         <li>功能说明：根据目录编码、文件名获取带相对路径的文件名</li>
	 *         </p>
	 * @param dirCode
	 *            目录编码
	 * @param fileName
	 *            文件名
	 * @return 含路径的文件名
	 */
	public static String getFullPath(String dirCode, String fileName) {

		bizlog.method("getFileNameById begin >>>>>>>>>>>>>>>>>>>>");

		// 判断是否存在, 不存在抛错
		exists(dirCode, true);

		String filePath = genFileName(dirCode, fileName);

		bizlog.method("getFileNameById end >>>>>>>>>>>>>>>>>>>>");
		return filePath;
	}

	private static String genFileName(String dirCode, String fileName) {
		StringBuilder sb = new StringBuilder();
//		List<AppDirectory> directoryList = AppDirectoryDao.selectAll_odb1(dirCode, false);
		List<AppDirectory> directoryList = ApFileBaseDao.selDirectoryData(dirCode, true);
		
		for (AppDirectory directory : directoryList) {
			E_DIRPARMTYPE type = directory.getDir_parm_type();
			String parmValue = directory.getParm_value();

			if (type == E_DIRPARMTYPE.RUNENV) {// 公共运行变量
				sb.append(BizUtil.getTrxRunEnvsValue(parmValue));
				sb.append(File.separator);
			}
			else if (type == E_DIRPARMTYPE.FIXED) {// 固定值
				sb.append(parmValue);
				sb.append(File.separator);
			}
		}
		return sb.append(fileName).toString();
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午5:59:09</li>
	 *         <li>功能说明：获取文件全路径</li>
	 *         </p>
	 * @param rootDir
	 * @param fileName
	 * @return
	 */
	public static String getFileFullPath(String rootDir, String fileName) {
		bizlog.method("getFileFullPath begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.parm(" rootDir[%s],  fileName [%s]", rootDir, fileName);
		String sFullPath = FileUtil.getFullPath(rootDir, fileName);

		bizlog.parm("sFullPath [%s]", sFullPath);
		bizlog.method("getFileFullPath end >>>>>>>>>>>>>>>>>>>>");
		return sFullPath;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月2日-下午7:58:45</li>
	 *         <li>功能说明：获取本地的绝对路径部分/li>
	 *         </p>
	 * @return
	 */
	public static String getLocalHome() {
		return OssFactory.get().create().getLocalkPath();
	}
	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月2日-下午7:58:45</li>
	 *         <li>功能说明：获取本地的绝对路径部分/li>
	 *         </p>
	 * @return
	 */
	public static String getLocalHome(String fileName) {
		String localHome = getLocalHome();
		if (localHome != null && !localHome.endsWith(File.separator))
			localHome = localHome + File.separator;
		return localHome + fileName;

	}
	
	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2017年3月17日-下午6:05:36</li>
	 *         <li>功能说明：同步远程文件到本地</li>
	 *         </p>
	 * @param fileRecvId
	 * @return
	 */
	public static void syncRemoteFile2Local(String busiBatchId) {
		bizlog.method(" ApFile.syncRemoteFile2Local begin >>>>>>>>>>>>>>>>");
		// busiBatchId 需要在 AppBatch 有定义
		AppBatch appBatch = AppBatchDao.selectOne_odb1(busiBatchId, false);
		if (appBatch == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppBatch.class).getLongname(), SysDict.A.busi_batch_id.getLongName(), busiBatchId);
		}

		// 远程服务器是系统缺省的文件服务器
		String remoteDir = getFullPath(appBatch.getRemote_dir_code());
		String localPath = getFullPath(appBatch.getLocal_dir_code());
		String localDir = getLocalHome() + localPath;

		// 本地无local_dir_code 目录则自动创建，创建失败需要抛出
		File dirFile = new File(localDir);
		if (!dirFile.exists()) {
			if (!dirFile.mkdirs())
				throw ApBaseErr.ApBase.E0040(localDir);
		}

		// 获取远程 *.ok 列表中本地不存在的清单
//		List<String> apRemoteFileList = createClient(appBatch.getRemote_dir_code()).getRemoteFileList(remoteDir);
		List<String> apRemoteFileList = getRemoteFileList(appBatch.getRemote_dir_code(),remoteDir,true);

		for (String fileNames : apRemoteFileList) {
			File file = new File(getFileFullPath(localDir, fileNames.substring(0, fileNames.lastIndexOf(FILE_OK))));
			if (!file.exists()) {
				ApbBatchReceive tblApbBatchReceive = ApbBatchReceiveDao.selectFirst_odb3(file.getName(), remoteDir, E_YESORNO.NO, false);
				if (tblApbBatchReceive == null) {// 文件不存在且未请求下载
					bizlog.debug("fileName[%s] not exits, register now!", file.getName());
					// 生成文件下载请求
					ApFileIn apFileIn = BizUtil.getInstance(ApFileIn.class);
					apFileIn.setBusi_batch_code(ApBaseSeq.genSeq(BUSI_BATCH_CODE));// 文件批量号
					apFileIn.setBusi_batch_id(appBatch.getBusi_batch_id());// 文件批量业务ID
					apFileIn.setFile_name(file.getName());// 文件名称
					apFileIn.setFile_server_path(remoteDir);// 服务器路径
					apFileIn.setTiming_process_ind(E_YESORNO.NO);
					// 调用文件批量申请
					ApBatch.fileBatchApply(apFileIn);
				}
			}
		}
		bizlog.method(" ApFile.syncRemoteFile2Local end <<<<<<<<<<<<<<<<");
	}
	
	/**
	 * 获取远程服务器文件路径
	 * <p>Title:getRemoteFileList </p>
	 * <p>Description:	</p>
	 * @author jiangyaming
	 * @date   2019年8月5日 
	 * @param remoteDir
	 * @return
	 */
	private static List<String> getRemoteFileList(String remote_dir_code,String remoteDir) {
		bizlog.method("getRemoteFileList begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.parm(" remoteDir[%s]", remoteDir);
		MsTransfer msTransfer = createClient(remote_dir_code);
		List<MsFileInfo> listAllFiles = msTransfer.listAllFiles(false, remoteDir);

		List<String> listNames = new ArrayList<String>();
		for(MsFileInfo file:listAllFiles) {
			listNames.add(file.getFileName());
		}
		bizlog.parm(" listNames[%s]", listNames);
		bizlog.method("getRemoteFileList end >>>>>>>>>>>>>>>>>>>>");
		return listNames;
	}

	private static List<String> getRemoteFileList(String remote_dir_code,String remoteDir,boolean isOk) {
		List<String> listNames = getRemoteFileList(remote_dir_code, remoteDir);
		List<String> results = new ArrayList<>();
		for(String name : listNames) {
			if(name.endsWith(".ok")) {
				results.add(name);
			}
		}
		return results;
	}
}
