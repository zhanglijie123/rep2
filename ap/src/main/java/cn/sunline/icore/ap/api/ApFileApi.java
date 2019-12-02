package cn.sunline.icore.ap.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import cn.sunline.clwj.msap.oss.spi.MsTransfer;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.file.ApReadExcel;
import cn.sunline.icore.ap.type.ComApFile.ApBatchSend;

public class ApFileApi {

	
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
		return ApBaseFile.createClient(dirCode);
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
		return ApBaseFile.readFile(file);
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
		return ApBaseFile.getFullPath(dirCode);
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

		return ApBaseFile.getFullPath(dirCode, fileName);
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
		return ApBaseFile.getFileFullPath(rootDir, fileName);
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
		return ApBaseFile.getLocalHome();
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
		return ApBaseFile.getLocalHome(fileName);
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
	    ApBaseFile.syncRemoteFile2Local(busiBatchId);
	}
	
	/**
	 * 读取Excel表格文件头的内容
	 * @param headNum 文件头开始行号，excel是由下标0开始
	 * @param keys 字段名 作为返回map集合的键
	 * @param fieldCols 字段对应的列号集合，需要与keys一一对应，作为取返回集合的值
	 * @param filePath 文件路径
	 * @return
	 */
	public static Map<String,Object> readExcelFileTitle(int headNum,List<String> keys,List<Integer> fieldCols,String filePath) {
		return ApReadExcel.readExcelFileTitle(headNum, keys, fieldCols, filePath);
	}
	
	/**
	 * 读取Excel文件体数据内容
	 * @param bodyNum 文件体开始行号，excel是由下标0开始
	 * @param keys 字段名 作为返回map集合的键
	 * @param fields 字段对应的列号集合，需要与keys一一对应，作为取返回集合的值
	 * @param filepath  文件路径
	 * @return
	 */
	public static List<Map<String,Object>> readExcelFileContent(int bodyNum,List<String> keys,List<Integer> fields,String filepath) {
		return ApReadExcel.readExcelFileContent(bodyNum, keys, fields, filepath);
	}
	
	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月14日-下午7:23:59</li>
	 *         <li>功能说明：生成一个文件Id</li>
	 *         </p>
	 * @return fileId
	 */
	public static String genFileId() {
		return ApFileSend.genFileId();
	}
	
	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月14日-下午7:23:59</li>
	 *         <li>功能说明：登记文件发送簿(已预先产生文件ID)</li>
	 *         </p>
	 * @param fileId
	 * @param fileName
	 * @param remoteDirCode
	 * @param localDirCode
	 * @param appendOkInd
	 * @return
	 */
	public static void register(String fileId, String fileName, String remoteDirCode, String localDirCode, E_YESORNO appendOkInd) {
		ApFileSend.register(fileId, fileName, remoteDirCode, localDirCode, appendOkInd);
	}
	
	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月14日-下午7:23:59</li>
	 *         <li>功能说明：联机发送文件</li>
	 *         </p>
	 * @param fileName
	 * @param remoteDirCode
	 * @param localDirCode
	 * @param appendOkInd
	 * @return
	 */
	public static String sendProcess(String fileName, String remoteDirCode, String localDirCode, E_YESORNO appendOkInd) {
		return ApFileSend.sendProcess(fileName, remoteDirCode, localDirCode, appendOkInd);
	}
	
	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月15日-下午5:23:49</li>
	 *         <li>功能说明：获取文件发送薄信息</li>
	 *         </p>
	 * @param fileId
	 * @return ApBatchSend
	 */
	public static ApBatchSend getFileInfo(String fileId) {
		return ApFileSend.getFileInfo(fileId);
	}
}
