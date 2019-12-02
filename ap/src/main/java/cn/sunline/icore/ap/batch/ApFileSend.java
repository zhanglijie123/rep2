package cn.sunline.icore.ap.batch;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchReceive;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchSend;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchSendDao;
import cn.sunline.icore.ap.type.ComApFile.ApBatchSend;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApFileSend {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApFileSend.class);

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月14日-下午7:23:59</li>
	 *         <li>功能说明：生成一个文件Id</li>
	 *         </p>
	 * @return fileId
	 */
	public static String genFileId() {

		return ApBaseSeq.genSeq("FILE_ID");
	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月14日-下午7:23:59</li>
	 *         <li>功能说明：登记文件发送簿</li>
	 *         </p>
	 * @param fileName
	 * @param remoteDirCode
	 * @param localDirCode
	 * @param appendOkInd
	 * @return
	 */
	public static String register(String fileName, String remoteDirCode, String localDirCode, E_YESORNO appendOkInd) {

		String fileId = ApBaseSeq.genSeq("FILE_ID");

		register(fileId, fileName, remoteDirCode, localDirCode, appendOkInd);

		return fileId;
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

		ApbBatchSend batchSend = BizUtil.getInstance(ApbBatchSend.class);

		String localPath = ApBaseFile.getFullPath(localDirCode);
		String serverPath = ApBaseFile.getFullPath(remoteDirCode);

		batchSend.setFile_id(fileId); // 文件ID
		batchSend.setFile_name(fileName); // 文件名称
		batchSend.setFile_server_path(serverPath); // 服务器路径
		batchSend.setSend_ind(E_YESORNO.NO); // 文件收妥标志
		batchSend.setFile_local_path(localPath); // 本地路径
		batchSend.setAppend_ok_ind(appendOkInd); // 是否追加Ok文件
		batchSend.setHash_value(BizUtil.getGroupHashValue("SEND_HASH_VALUE", fileId)); // 散列值
		batchSend.setSend_count(0L);

		// 登记文件发送登记薄
		ApbBatchSendDao.insert(batchSend);
	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月4日-上午9:44:02</li>
	 *         <li>功能说明：更新文件发送簿</li>
	 *         </p>
	 * @param fileId
	 * @param sendInd
	 */
	public static void modify(String fileId, E_YESORNO sendInd) {
		bizlog.method("modifySend begin >>>>>>>>>>>>>>>>");

		// 非空字段检查
		BizUtil.fieldNotNull(fileId, SysDict.A.file_id.getId(), SysDict.A.file_id.getDescription());

		// 更新文件收妥标志 、文件接收次数
		ApbBatchSend batchSend = ApbBatchSendDao.selectOneWithLock_odb1(fileId, true);

		batchSend.setSend_ind(sendInd);
		batchSend.setSend_count(batchSend.getSend_count() + 1); // 文件发送次数

		// 更新文件发送登记薄
		ApbBatchSendDao.updateOne_odb1(batchSend);

		bizlog.method("modifySend end <<<<<<<<<<<<<<<<<<<<");

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

		// 非空字段检查
		BizUtil.fieldNotNull(fileId, SysDict.A.file_id.getId(), SysDict.A.file_id.getDescription());

		ApbBatchSend batchSend = ApbBatchSendDao.selectOne_odb1(fileId, false);

		if (batchSend == null)
			throw APPUB.E0005(OdbFactory.getTable(ApbBatchReceive.class).getLongname(), SysDict.A.file_id.getLongName(), fileId);

		ApBatchSend sendInfo = BizUtil.getInstance(ApBatchSend.class);

		sendInfo.setFile_local_path(batchSend.getFile_local_path());
		sendInfo.setFile_name(batchSend.getFile_name());
		sendInfo.setFile_server_path(batchSend.getFile_server_path());
		sendInfo.setHash_value(batchSend.getHash_value());
		sendInfo.setSend_ind(batchSend.getSend_ind());
		sendInfo.setSend_count(batchSend.getSend_count());
		sendInfo.setAppend_ok_ind(batchSend.getAppend_ok_ind());

		return sendInfo;

	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月15日-下午5:23:49</li>
	 *         <li>功能说明：文件上传处理</li>
	 *         </p>
	 * @param fileName
	 * @param localPath
	 * @param serverPath
	 * @param appendOkInd
	 * @return boolean
	 */
	public static boolean upload(String fileName, String localPath, String serverPath, E_YESORNO appendOkInd) {

		// 获取本地文件路径
		String localFileName = ApBaseFile.getFileFullPath(localPath, fileName);
		// 获取服务器文件路径
		String remoteFileName = ApBaseFile.getFileFullPath(serverPath, fileName);
		boolean sucess = true;

		try {
			if (appendOkInd == E_YESORNO.YES) {
				// 上传文件，追加.ok 文件
//				ApBaseFile.createClient(null).upload(localFileName, remoteFileName, true);
				ApBaseFile.createClient(null).upload(ApBaseFile.
						getMsTransferFileInfo(localPath, serverPath, fileName, true));
			}
			else {
				// 上传文件
//				ApBaseFile.createClient(null).upload(localFileName, remoteFileName);
				ApBaseFile.createClient(null).upload(ApBaseFile.
						getMsTransferFileInfo(localPath, serverPath, fileName, false));
			}
		}
		catch (Exception e) {

			sucess = false;
			bizlog.error("File Upload fail,Exception :[%s] ", e);
		}

		return sucess;
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

		String fileId = ApBaseSeq.genSeq("FILE_ID");
		sendProcess(fileId, fileName, remoteDirCode, localDirCode, appendOkInd);
		return fileId;
	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月14日-下午7:23:59</li>
	 *         <li>功能说明：联机发送文件(已预先产生文件ID)</li>
	 *         </p>
	 * @param fileId
	 * @param fileName
	 * @param remoteDirCode
	 * @param localDirCode
	 * @param appendOkInd
	 * @return
	 */
	public static void sendProcess(String fileId, String fileName, String remoteDirCode, String localDirCode, E_YESORNO appendOkInd) {

		register(fileId, fileName, remoteDirCode, localDirCode, appendOkInd);

		ApbBatchSend fileInfo = ApbBatchSendDao.selectOne_odb1(fileId, true);

		if (upload(fileName, fileInfo.getFile_local_path(), fileInfo.getFile_server_path(), appendOkInd)) {

			ApFileSend.modify(fileId, E_YESORNO.YES);
		}
		else {
			throw ApBaseErr.ApBase.E0045(fileId, fileName);
		}
	}
}
