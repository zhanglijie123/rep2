package cn.sunline.icore.ap.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cn.sunline.common.util.StringUtil;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.BaseConst;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * </p>
 * 
 * @Author liucong
 *         <p>
 *         <li>2017.10.11-15:40:41</li>
 *         <li>Modify the record</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>Tags: revisions</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApSftpClient {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApSftpClient.class);
	private static final Integer DEFAULT_CONNECT_TIMEOUT_IN_MS = Integer.valueOf(5000);
	private static final Integer DEFAULT_SFTP_PORT = Integer.valueOf(21);

	private String ip;
	private Integer port = DEFAULT_SFTP_PORT;
	private String workingDirctory;

	private String user;
	private String password;
	private String keyFilePath;

	// sftp如何使用这些规则
	private Integer connectTimeoutInMs = DEFAULT_CONNECT_TIMEOUT_IN_MS;

	protected String localEncoding = "GB18030";
	protected String remoteEncoding = "UTF8";

	public ApSftpClient ip(String ip) {
		this.ip = ip;
		return this;
	}

	private String getFileName(String name) {
		try {
			return new String(name.getBytes(localEncoding), remoteEncoding);
		}
		catch (UnsupportedEncodingException e) {
			throw new LttsServiceException("9999","[" + name + "]" + "conversion failed", e);
		}
	}

	public ApSftpClient port(Integer port) {
		if (port != null) {
			this.port = port;
		}
		else {
			this.port = DEFAULT_SFTP_PORT;
		}
		return this;
	}

	public ApSftpClient user(String user) {
		this.user = user;
		return this;
	}

	public ApSftpClient password(String password) {
		this.password = password;
		return this;
	}
	
	public ApSftpClient keyFilePath(String keyFilePath) {
		this.keyFilePath = BizUtil.convertParmString(keyFilePath);
		return this;
	}
	public ApSftpClient workingDirctory(String workingDirctory) {
		this.workingDirctory = workingDirctory;
		return this;
	}

	public ApSftpClient connectTimeoutInMs(Integer connectTimeoutInMs) {
		if (connectTimeoutInMs != null) {
			this.connectTimeoutInMs = connectTimeoutInMs;
		}
		else {
			this.connectTimeoutInMs = DEFAULT_CONNECT_TIMEOUT_IN_MS;
		}
		return this;
	}

	private ChannelSftp login() {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			 if (keyFilePath != null) {
	             jsch.addIdentity(keyFilePath);// 设置私钥
	             bizlog.info("sftp connect,path of private key file：[%s]" , keyFilePath);
	         }
			Session sshSession = jsch.getSession(user, ip, port);
			bizlog.info("sftp connect by host:[%s] username:[%s]",ip,user);
	        if (password != null) {
	    		sshSession.setPassword(password);
	        }
			sshSession.setTimeout(connectTimeoutInMs);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(config); // 为Session对象设置properties
			sshSession.connect();
			sftp = (ChannelSftp) sshSession.openChannel("sftp");

	        bizlog.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", ip, port));
			sftp.connect();
			
			if (StringUtil.isNotEmpty(workingDirctory)) {
				sftp.cd(workingDirctory);
			}
		}
		catch (Exception e) {
			throw new LttsServiceException("9999","Sftp initialize failed:user=" + user + "ip=" + ip + "port=" + port, e);
		}
		return sftp;
	}

	private void disconnect(ChannelSftp sftp) {
		try {
			if (sftp != null) {
				sftp.disconnect();
				bizlog.debug("channel disconnect...");
				
				if (sftp.getSession() != null) {
					sftp.getSession().disconnect();
					bizlog.debug("session disconnect...");
				}
			}
			bizlog.debug("Success to close sftp connect!");
		}
		catch (Exception e) {
			throw new LttsServiceException("9999","Sftp disconnect failed:user=" + user + "ip=" + ip + "port=" + port, e);
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

	public void remotePrepare(ChannelSftp sftp, String remoteFileName) {
		if (new File(remoteFileName).exists()) {
			try {
				sftp.rmdir(getFileName(remoteFileName));

			}
			catch (SftpException e) {
				throw new LttsServiceException("9999","Deleting remote file [" + remoteFileName + "] failed", e);
			}
		}
		List<String> dirs = getDirectories(remoteFileName);
		StringBuilder sb;
		if ((dirs != null) && !dirs.isEmpty()) {
			sb = new StringBuilder();
			for (String dir : dirs) {
				dir = dir.replace('\\', '/');
				dir = dir.replace('/', '/');
				sb.append(dir).append("/");
				try {
					sftp.cd(sb.toString());
				} catch (SftpException e) {
					 bizlog.error("Remote directory [" + sb.toString() + "] not exits!", e, new Object[0]);
					 try {
							sftp.mkdir(getFileName(dir));
							sftp.cd(sb.toString());
						} catch (SftpException e1) {
							throw new LttsServiceException("9008","Remote directory [" + sb.toString() + "] creation failed", e1);
					}
				}
			}
		}
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2017.10.12-11:08:51</li>
	 *         <li>download file</li>
	 *         </p>
	 * @param remoteFileName
	 * @param localFileName
	 */
	public void download(String remoteFileName, String localFileName) {

		long start = System.currentTimeMillis();

		ChannelSftp sftp = null;
		FileOutputStream fos = null;
		try {
			sftp = login();
			localPrepare(localFileName);
			String remoteEncoderFileName = getFileName(remoteFileName);
			fos = new FileOutputStream(localFileName);
			sftp.get(remoteEncoderFileName, fos);
			
			bizlog.info(BaseConst.SimpleFTPClient04, new Object[] { ip, user, remoteEncoderFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });

		}
		catch (FileNotFoundException e1) {
			throw new LttsServiceException("9006","File download failed,the local file[" + localFileName + "] does not exist or can not be operated." + e1.getMessage(), e1);
		}
		catch (SftpException e) {
			throw new LttsServiceException("9013","Sftp download file failed!", e);
		}
		finally {
			disconnect(sftp);
			if (fos != null) {
				try {
					fos.close();
					fos.flush();
				}
				catch (IOException e) {
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
		}

	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2017.10.12-11:15:10</li>
	 *         <li>upload file</li>
	 *         </p>
	 */
	public void upload(String remoteFileName, String localFileName) {

		long start = System.currentTimeMillis();

		ChannelSftp sftp = null;
		FileInputStream in = null;
		try {
			sftp = login();
			remotePrepare(sftp, remoteFileName);
			in = new FileInputStream(new File(localFileName));
			sftp.put(in, remoteFileName);
			
			bizlog.info(BaseConst.SimpleFTPClient04, new Object[] { ip, user, remoteFileName, localFileName, Long.valueOf(System.currentTimeMillis() - start) });
		}
		catch (FileNotFoundException e) {
			throw new LttsServiceException("9006","File upload failed, the local file[" + localFileName + "] does not exist or can not be operated." + e.getMessage(), e);
		}
		catch (SftpException e) {
			throw new LttsServiceException("9013","Sftp upload file failed!", e);
		}
		finally {
			disconnect(sftp);
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					bizlog.error(BaseConst.SimpleFTPClient03, e, new Object[0]);
				}
			}
		}
	}
	/**
	 * 
	 * @Author liucong
	 *         <p>
	 *         <li>2017.10.12-20:16:09</li>
	 *         <li>Function Description:</li>
	 *         </p>
	 * @param pathname
	 * @param regs
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getRemoList(String pathname, final String regs) {
		ChannelSftp sftp = null;
		List<String> fileList;
		try {
			sftp = login();
			fileList = new ArrayList<String>();
			sftp.cd(workingDirctory);
			String tmppath = "";
			if (CommUtil.isNotNull(pathname)) {
				tmppath = pathname.replace('\\', '/');
				if(tmppath.startsWith("/"))
					tmppath.replaceFirst("", "");
				sftp.cd(tmppath);
			}
			Vector vste = sftp.ls(sftp.pwd());
			for( Object file: vste){
				if(CommUtil.isNotNull(file) && ((LsEntry) file).getFilename().endsWith(regs)){
					fileList.add(((LsEntry) file).getFilename());
				}
			}
			
			return fileList;
		}
		catch (SftpException e) {
			throw new LttsServiceException("9013","Remote directory[" + pathname + "] traversal failed", e);
		}
		finally {
			disconnect(sftp);
		}
	}
}
