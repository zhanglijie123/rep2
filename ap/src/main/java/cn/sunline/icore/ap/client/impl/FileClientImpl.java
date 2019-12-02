package cn.sunline.icore.ap.client.impl;

import java.io.File;
import java.io.IOException;

import cn.sunline.icore.ap.api.FileClient;
import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.type.ComApFile.ApRemoteFileList;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class FileClientImpl implements FileClient {

	private static final BizLog bizlog = BizLogUtil.getBizLog(FileClientImpl.class);

	private ApBaseComp.FileTransfer abFileTransfer = null;

	private static final String FILE_OK = ".ok";
	
	public FileClientImpl() {
		super();
		//abFileTransfer = SysUtil.getInstance(ApBaseComp.FileTransfer.class, ApFile.DEFAULT_FILE_TRANSFER);
	}

	public FileClientImpl(String fileTransfer) {
		super();
		abFileTransfer = SysUtil.getInstance(ApBaseComp.FileTransfer.class, fileTransfer);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月2日-下午7:58:45</li>
	 *         <li>功能说明：获取远程的绝对路径部分/li>
	 *         </p>
	 * @return
	 */
	public String getRemoteHome() {
		return abFileTransfer.getRemoteDirectory();
	}

	public ApRemoteFileList getRemoteFileList(String remoteDir) {
		return abFileTransfer.getRemoteFileList(remoteDir, FILE_OK);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:35</li>
	 *         <li>功能说明：文件下载</li>
	 *         </p>
	 * @param localFileName
	 * @param remoteFileName
	 * @return
	 */
	public String download(String localFileName, String remoteFileName) {

		bizlog.method("download begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.parm(" localFileName [%s], remoteFileName [%s]", localFileName, remoteFileName);

		abFileTransfer.download(localFileName, remoteFileName);
		String fullPath = ApBaseFile.getFileFullPath(ApBaseFile.getLocalHome(), localFileName);

		bizlog.parm("fullPath [%s]", fullPath);
		bizlog.method("download end <<<<<<<<<<<<<<<<<<<<");
		return fullPath;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:51</li>
	 *         <li>功能说明：文件上传，无OK文件</li>
	 *         </p>
	 * @param localFileName
	 *            相对路径
	 * @param remoteFileName
	 *            相对路径
	 * @return
	 */
	public void upload(String localFileName, String remoteFileName) {
		upload(localFileName, remoteFileName, false);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:51</li>
	 *         <li>功能说明：文件上传</li>
	 *         </p>
	 * @param localFileName
	 *            相对路径
	 * @param remoteFileName
	 *            相对路径
	 * @return
	 */
	public void upload(String localFileName, String remoteFileName, boolean uploadOk) {
		bizlog.method("upload begin >>>>>>>>>>>>>>>>>>>>");
		bizlog.debug("localFileName [%s], remoteFileName [%s]", localFileName, remoteFileName);

		abFileTransfer.upload(localFileName, remoteFileName);

		if (uploadOk) {
			File okFile = new File(ApBaseFile.getLocalHome() + File.separator + localFileName + FILE_OK);
			if (!okFile.exists()) {
				try {
					okFile.createNewFile();
				}
				catch (IOException e) {
					throw ApBaseErr.ApBase.E0032(e);
				}
			}
			abFileTransfer.upload(localFileName + FILE_OK, remoteFileName + FILE_OK);
		}

		bizlog.method("upload end >>>>>>>>>>>>>>>>>>>>");
	}
}
