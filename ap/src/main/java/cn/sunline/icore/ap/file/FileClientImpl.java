package cn.sunline.icore.ap.file;

public class FileClientImpl { 
//	implements FileClient {
//
//	private static BizLog bizlog = BizLogUtil.getBizLog(FileClientImpl.class);
//
//	private MsTransfer transfer;
//
//	public FileClientImpl(String configId) {
//		MsTransfer transfer = OssFactory.get().create(configId);
//		this.transfer = transfer;
//	}
//
//	@Override
//	public boolean toReachable() {
//		return false;
//	}
//
//	@Override
//	public List<String> getRemoteFileList(String remotePath) {
//		return getRemoteFileList(remotePath, ".ok");
//	}
//
//	@Override
//	public List<String> getRemoteFileList(String remotePath, String paramString2) {
//		List<MsFileInfo> fileList = transfer.listAllFiles(false, remotePath);
//		List<String> ret = new ArrayList<String>();
//		for (MsFileInfo fileInfo : fileList) {
//			ret.add(fileInfo.getFileName());
//		}
//		return ret;
//	}
//
//	@Override
//	public String getRemoteHome() {
//		return transfer.getRemotePath();
//	}
//
//	@Override
//	public String download(String localPath, String remotePath) {
//
//		MsTransferFileInfo fileInfo = new MsTransferFileInfo();
//		fileInfo.setLocalFile(new MsFileInfo(localPath));
//		fileInfo.setRemoteFile(new MsFileInfo(remotePath));
//		transfer.download(fileInfo);
//
//		return getFullPathName(transfer.getLocalkPath(), localPath);
//	}
//
//	@Override
//	public void delete(String remotePath) {
//		transfer.delete(false, new MsFileInfo(remotePath));
//
//	}
//
//	@Override
//	public void upload(String localPath, String remotePath) {
//		upload(localPath, remotePath, true);
//	}
//
//	@Override
//	public void upload(String localPath, String remotePath, boolean uploadOk) {
//
//		MsTransferFileInfo fileInfo = new MsTransferFileInfo();
//		MsFileInfo localFileInfo = new MsFileInfo(localPath);
//		localFileInfo.setUpdateOk(uploadOk);
//		fileInfo.setLocalFile(localFileInfo);
//		fileInfo.setRemoteFile(new MsFileInfo(remotePath));
//
//		transfer.upload(fileInfo);
//
//	}
//
//	private String getFullPathName(String workDir, String fileName) {
//		if (MsStringUtil.isEmpty(workDir)) {
//			return fileName;
//		}
//
//		String file = workDir;
//		if (!isFileSeparator(file.charAt(file.length() - 1)))
//			file = file + '/';
//		if ((!MsStringUtil.isEmpty(fileName))
//				&& (isFileSeparator(fileName.charAt(0)))) {
//			file = file + fileName.substring(1);
//		} else
//			file = file + fileName;
//		return file;
//	}
//
//	private boolean isFileSeparator(char cha) {
//		if ((cha == '/') || (cha == '\\'))
//			return true;
//		return false;
//	}
}
