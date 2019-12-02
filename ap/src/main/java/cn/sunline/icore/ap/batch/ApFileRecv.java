package cn.sunline.icore.ap.batch;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchReceive;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchReceiveDao;
import cn.sunline.icore.ap.type.ComApFile.ApBatchRecv;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author yangdl
 *         <p>
 *         <li>2017年3月3日-下午5:07:56</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年3月3日-yangdl：文件接收相关</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */

public class ApFileRecv {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApFileRecv.class);

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月4日-上午9:44:02</li>
	 *         <li>功能说明：登记文件接收簿</li>
	 *         </p>
	 * @param fileName
	 * @param serverPath
	 * @param dirCode
	 */
	public static String register(String fileName, String serverPath, String localDirCode) {

		ApbBatchReceive batchRecv = BizUtil.getInstance(ApbBatchReceive.class);

		String fileId = ApBaseSeq.genSeq("FILE_ID");
		String localPath = ApBaseFile.getFullPath(localDirCode);
		//String serverPath = ApFile.getFullPath(remoteDirCode);

		batchRecv.setFile_id(fileId); // 文件ID
		batchRecv.setFile_name(fileName); // 文件名称
		batchRecv.setFile_server_path(serverPath); // 服务器路径
		batchRecv.setReceive_ind(E_YESORNO.NO); // 文件收妥标志
		batchRecv.setFile_local_path(localPath); // 本地路径
		batchRecv.setHash_value(BizUtil.getGroupHashValue("RECEIVE_HASH_VALUE", fileId)); // 散列值
		batchRecv.setRecevice_count(0L);

		// 登记文件批量接收登记薄
		ApbBatchReceiveDao.insert(batchRecv);

		return fileId;
	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月4日-上午9:44:02</li>
	 *         <li>功能说明：更新文件接收簿</li>
	 *         </p>
	 * @param fileId
	 * @param receiveInd
	 */
	public static void modify(String fileId, E_YESORNO receiveInd) {

		bizlog.method(" modifyRecv begin >>>>>>>>>>>>>>>>");

		// 非空字段检查
		BizUtil.fieldNotNull(fileId, SysDict.A.file_id.getId(), SysDict.A.file_id.getDescription());

		// 更新文件收妥标志 、文件接收次数
		ApbBatchReceive batcRecv = ApbBatchReceiveDao.selectOneWithLock_odb1(fileId, true);

		batcRecv.setReceive_ind(receiveInd);
		if(E_YESORNO.YES == receiveInd){
			batcRecv.setRecevice_count(batcRecv.getRecevice_count() + 1); // 文件接收成功次数
		}else{
			batcRecv.setFail_total_count(batcRecv.getFail_total_count() + 1); // 文件接收失败次数
		}

		ApbBatchReceiveDao.updateOne_odb1(batcRecv);

		bizlog.method("modifyRecv end <<<<<<<<<<<<<<<<<<<<");

	}

	/**
	 * 
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月15日-下午5:23:49</li>
	 *         <li>功能说明：获取文件接收薄信息</li>
	 *         </p>
	 * @param fileId
	 * @return ApBatchRecv
	 */
	public static ApBatchRecv getFileInfo(String fileId) {

		// 非空字段检查
		BizUtil.fieldNotNull(fileId, SysDict.A.file_id.getId(), SysDict.A.file_id.getDescription());

		ApbBatchReceive	batchRecv =ApbBatchReceiveDao.selectOne_odb1(fileId, false);
		
		if(batchRecv == null)
			throw APPUB.E0005(OdbFactory.getTable(ApbBatchReceive.class).getLongname(), SysDict.A.file_id.getLongName(), fileId);
		
		ApBatchRecv  recvInfo  =  BizUtil.getInstance(ApBatchRecv.class);
		
		recvInfo.setFile_local_path(batchRecv.getFile_local_path());
		recvInfo.setFile_name(batchRecv.getFile_name());
		recvInfo.setFile_server_path(batchRecv.getFile_server_path());
		recvInfo.setHash_value(batchRecv.getHash_value());
		recvInfo.setReceive_ind(batchRecv.getReceive_ind());
		recvInfo.setRecevice_count(batchRecv.getRecevice_count());

		return recvInfo;

	}
}
