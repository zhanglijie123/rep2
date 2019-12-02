package cn.sunline.icore.ap.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import cn.sunline.common.util.StringUtil;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.ltts.BaseConst;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.SysLog;
import cn.sunline.ltts.core.api.logging.SysLogUtil;

/**
 * <p>
 * 文件功能说明： 实现FTP功能
 * </p>
 * 
 * @Author liuzf@sunline.cn
 *         <p>
 *         <li>2017年3月30日-下午1:17:19</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年3月30日-liuzf@sunline.cn：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApFtpClient {
	private static final SysLog log = SysLogUtil.getSysLog(ApFtpClient.class);

	private static final Integer DEFAULT_CONNECT_TIMEOUT_IN_MS = Integer.valueOf(5000);
	private static final Integer DEFAULT_DATA_TIMEOUT_IN_MS = Integer.valueOf(60000);
	private static final Integer DEFAULT_FTP_PORT = Integer.valueOf(21);

	private String ip;
	private Integer port = DEFAULT_FTP_PORT;

	private String user;
	private String password;
	private String workingDirctory;
	private Integer connectTimeoutInMs = DEFAULT_CONNECT_TIMEOUT_IN_MS;
	private Integer dataTimeoutInMs = DEFAULT_DATA_TIMEOUT_IN_MS;
	private Boolean binaryMode = Boolean.valueOf(true);
	protected String localEncoding = "GB18030";
	protected String remoteEncoding = "ISO8859-1";

	private Integer retryTime = Integer.valueOf(2);

	private Integer retryInterval = Integer.valueOf(2000);
	
	public ApFtpClient ip(String ip) {
		this.ip = ip;
		return this;
	}

	public ApFtpClient port(Integer port) {
		if (port != null) {
			this.port = port;
		}
		else {
			this.port = DEFAULT_FTP_PORT;
		}
		return this;
	}

	public ApFtpClient user(String user) {
		this.user = user;
		return this;
	}

	public ApFtpClient password(String password) {
		this.password = password;
		return this;
	}

	public ApFtpClient workingDirctory(String workingDirctory) {
		this.workingDirctory = workingDirctory;
		return this;
	}

	public ApFtpClient connectTimeoutInMs(Integer connectTimeoutInMs) {
		if (connectTimeoutInMs != null) {
			this.connectTimeoutInMs = connectTimeoutInMs;
		}
		else {
			this.connectTimeoutInMs = DEFAULT_CONNECT_TIMEOUT_IN_MS;
		}
		return this;
	}

	public ApFtpClient dataTimeoutInMs(Integer dataTimeoutInMs) {
		if (dataTimeoutInMs != null) {
			this.dataTimeoutInMs = dataTimeoutInMs;
		}
		else {
			this.dataTimeoutInMs = DEFAULT_DATA_TIMEOUT_IN_MS;
		}
		return this;
	}

	public ApFtpClient binaryMode(Boolean binaryMode) {
		if (binaryMode != null) {
			this.binaryMode = binaryMode;
		}
		else {
			this.binaryMode = Boolean.valueOf(true);
		}
		return this;
	}
	
	public void setRetryTime(int retryTime) {
		this.retryTime = Integer.valueOf(retryTime);
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = Integer.valueOf(retryInterval);
	}

	private String getFileName(String name) {
		try {
			return new String(name.getBytes(localEncoding), remoteEncoding);
		}
		catch (UnsupportedEncodingException e) {
			throw new LttsServiceException("9999","[" + name + "]" + "conversion failed", e);
		}
	}

	private FTPClient login() {
		FTPClient ftp = new FTPClient();

		try {
			connect(ftp);

			ftp.setSoTimeout(dataTimeoutInMs.intValue());
			ftp.setControlEncoding(remoteEncoding);

			if (!ftp.login(user, password)) {
				throw new LttsServiceException("9999","Logon FTP server failed：user=" + user + ",password=" + password);
			}

			if ((StringUtil.isNotEmpty(workingDirctory)) && (!ftp.changeWorkingDirectory(workingDirctory))) {
				throw new LttsServiceException("9999","FTP server working directory change failed：" + workingDirctory);
			}

			if ((binaryMode != null) && (!binaryMode.booleanValue())) {
				if (!ftp.setFileType(0)) {
					throw new LttsServiceException("9999","FTP file transfer type setting failed：0");
				}
			}
			else if (!ftp.setFileType(2)) {
				throw new LttsServiceException("9999","FTP file transfer type setting failed：2");
			}

			ftp.setBufferSize(1048576);
		}
		catch (SocketException e) {
			throw new LttsServiceException("9999","FTP connection error", e);
		}
		catch (IOException e) {
			throw new LttsServiceException("9999","FTP connection error", e);
		}

		return ftp;
	}

	private void connect(FTPClient ftp) {
		try {
			ftp.setDefaultTimeout(connectTimeoutInMs.intValue());
			ftp.setDataTimeout(dataTimeoutInMs.intValue());
			ftp.setConnectTimeout(connectTimeoutInMs.intValue());
			ftp.connect(ip, port.intValue());
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new LttsServiceException("9999","Can't Connect to :" + ip);
			}
			return;
		}
		catch (Exception e) {
			log.error("connect failed![%s]", e, e.getMessage());
			throw new LttsServiceException("9999", e.getMessage(), e);
		}
	}

	private void logoff(FTPClient ftp) {
		if (ftp.isConnected()) {
			try {
				ftp.logout();
			}
			catch (Exception e) {
				log.error("logout fail", e, new Object[0]);
			}
			try {
				ftp.disconnect();
			}
			catch (Exception e) {
				log.error(BaseConst.SimpleFTPClient01, e, new Object[0]);
			}
		}
	}

	public void download(String localFileName, String remoteFileName) {
		long start = System.currentTimeMillis();
		FTPClient ftp = login();

		localPrepare(localFileName);
		try {
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try {
				fos = new FileOutputStream(localFileName);
				bos = new BufferedOutputStream(fos);
				String remoteEncoderFileName = getFileName(remoteFileName);
                if (!ftp.retrieveFile(remoteEncoderFileName, bos)) {
                    throw new LttsServiceException("9999","File[" + remoteEncoderFileName + "] download failed," + ftp.getReplyString());
                }

				log.info(BaseConst.SimpleFTPClient02, new Object[] { ip, user, remoteEncoderFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });

			}
			catch (FileNotFoundException e) {
				throw new LttsServiceException("9006","File download failed,the local file[" + localFileName + "] does not exist or can not be operated." + e.getMessage(), e);
			}
			catch (Exception e) {
				throw new LttsServiceException("9999","File download failed", e);
			}
			finally {
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}

					if (fos != null) {
						fos.flush();
						fos.close();
					}
				}
				catch (Exception e) {
					log.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
		}
		finally {
			logoff(ftp);
		}
	}

	public void upload(String localFileName, String remoteFileName) {
		long start = System.currentTimeMillis();
		FTPClient ftp = login();

		remotePrepare(ftp, remoteFileName);
		try {
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(localFileName));
				if (!ftp.storeFile(getFileName(remoteFileName), in)) {
					throw new LttsServiceException("9013","Uploading files to FTP server failed: " + ftp.getReplyString());
				}

				log.info(BaseConst.SimpleFTPClient04, new Object[] { ip, user, remoteFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });

			}
			catch (FileNotFoundException e) {
				throw new LttsServiceException("9006","File upload failed, the local file[" + localFileName + "] does not exist or can not be operated." + e.getMessage(), e);
			}
			catch (Exception e) {
				throw new LttsServiceException("9999","UPloading files to FTP server failed," + e.getMessage(), e);
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (Exception e) {
						log.error(BaseConst.SimpleFTPClient05, e, new Object[0]);
					}
				}
			}
		}
		finally {
			logoff(ftp);
		}
	}

	public static List<String> getDirectories(String director) {
		String fileName = director.replace('\\', File.separatorChar).replace('/', File.separatorChar);
        
		List<String> ret = null;
        boolean abs = false;
        int idx = fileName.indexOf(File.separatorChar);
        if (idx != -1) {
            String realName = fileName;
            if (idx == 0) {
                realName = fileName.substring(idx + 1);
                abs = true;
            }

            idx = realName.lastIndexOf(File.separatorChar);
            if (idx != -1) {
                String tempName = realName.substring(0, idx);

                ret = StringUtil.split(tempName, File.separatorChar);
                ret.set(0, abs ? File.separatorChar + (String) ret.get(0)
                        : (String) ret.get(0));
            }
        }
		return ret;
	}

	public static void localPrepare(String localFileName) {
		List<String> dirs = getDirectories(localFileName);
		if ((dirs != null) && !dirs.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String dir : dirs) {
				sb.append(dir).append(File.separator);
			}
			new File(sb.toString()).mkdirs();
		}
	}

	public void remotePrepare(FTPClient client, String remoteFileName) {
		try {
			client.deleteFile(getFileName(remoteFileName));

		}
		catch (IOException e) {
			throw new LttsServiceException("9999","Deleting remote file[" + remoteFileName + "] failed", e);
		}

		List<String> dirs = getDirectories(remoteFileName);
		StringBuilder sb;
		if ((dirs != null) && !dirs.isEmpty()) {
			sb = new StringBuilder();
			for (String dir : dirs) {
				sb.append(dir).append("/");
				try {
					client.makeDirectory(getFileName(sb.toString()));

				}
				catch (IOException e) {
					throw new LttsServiceException("9008","Remote directory" + sb.toString() + "creation failed", e);
				}
			}
		}
	}

	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2017年4月7日-下午3:30:17</li>
	 *         <li>功能说明：根据文件规则遍历远程目录</li>
	 *         </p>
	 * @param pathname
	 * @param regs
	 * @return
	 */
	public List<String> getRemoList(String pathname, final String regs) {
		FTPClient ftpClient = login();
		FTPFile[] ftpFiles = null;

		try {
			String tmppath = "";
			if (CommUtil.isNotNull(pathname)) {
				tmppath = pathname.replace('\\', '/');
			}

			if (!ftpClient.changeWorkingDirectory(workingDirctory + tmppath)) {
				throw ApBaseErr.ApBase.E0039(workingDirctory + tmppath);
			}

			ftpFiles = ftpClient.listFiles(workingDirctory + tmppath, new FTPFileFilter() {

				@Override
				public boolean accept(FTPFile file) {
					// 获取OK文件
					if (file.getName().endsWith(regs)) {
						return true;
					}
					// 不是OK文件
					return false;
				}
			});
		}
		catch (IOException e) {
			throw new LttsServiceException("9013","Remote directory[" + pathname + "] traversal failed", e);
		}
		finally {
			logoff(ftpClient);
		}

		if (ftpFiles != null) {
			List<String> fileList = new ArrayList<>();
			for (FTPFile ftpFile : ftpFiles) {
				fileList.add(ftpFile.getName());
			}
			return fileList;
		}
		return Collections.emptyList();
	}
}