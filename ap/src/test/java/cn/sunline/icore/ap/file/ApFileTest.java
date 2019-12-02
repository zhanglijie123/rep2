package cn.sunline.icore.ap.file;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

//import cn.sunline.clwj.msap.file.api.FileClient;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.test.UnitTest;

public class ApFileTest extends UnitTest{
	
	/**
	 * 使用Sftp协议下载服务器文件到本地
	 */
	@Test
	public void testSFTPDownload(){
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "0033");
		newCommReq(commReq);
		
		String dirCode = "AP_FOR_TEST_SFTP";
		String localpath = "K:/download/aa.txt";
		String remotefile = "ICORESIBSCFEMPH20170714.txt";
//		FileClient createClient = ApBaseFile.createClient(dirCode);
//		createClient.download(localpath, remotefile);
	}
	
	/**
	 * 使用Ftp协议下载服务器文件到本地
	 */
	@Test
	public void testFTPDownload(){
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "0033");
		newCommReq(commReq);
		
		String dirCode = "AP_FOR_TEST_FTP";
		String localpath = "K:/download/bb.txt";
		String remotefile = "ICORESIBSCFMAINT20171009.txt";
//		FileClient createClient = ApBaseFile.createClient(dirCode);
//		createClient.download(localpath, remotefile);
	}
	
	/**
	 * 使用Sftp协议上传本地文件到服务器
	 */
	@Test
	public void testSftpUpload(){
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "0033");
		newCommReq(commReq);
		
		String dirCode = "AP_FOR_TEST_SFTP";
		String localpath = "K:/download/aa.txt";
		String remotefile = "ICORESIBSCFEMPH20170714_new.txt";
//		FileClient createClient = ApBaseFile.createClient(dirCode);
//		createClient.upload(localpath, remotefile);
	}
	
	/**
	 * 使用Ftp协议上传本地文件到服务器
	 */
	@Test
	public void testFtpUpload(){
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "0033");
		newCommReq(commReq);
		
		String dirCode = "AP_FOR_TEST_FTP";
		String localpath = "K:/download/bb.txt";
		String remotefile = "ICORESIBSCFMAINT20171009_new.txt";
//		FileClient createClient = ApBaseFile.createClient(dirCode);
//		createClient.upload(localpath, remotefile);
	}
	
	/**
	 * 使用Sftp协议上传本地OK文件到服务器
	 */
	@Test
	public void testSftpUploadWithOK(){
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "0033");
		newCommReq(commReq);
		
		String dirCode = "AP_FOR_TEST_SFTP";
		String localpath = "K:/download/aa.txt";
		String remotefile = "ICORESIBSCFEMPH20170714_new.txt";
//		FileClient createClient = ApBaseFile.createClient(dirCode);
//		createClient.upload(localpath, remotefile, true);
	}
	
	/**
	 * 使用Ftp协议上传本地OK文件到服务器
	 */
	@Test
	public void testFtpUploadWithOK(){
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "0033");
		newCommReq(commReq);
		
		String dirCode = "AP_FOR_TEST_FTP";
		String localpath = "K:/download/bb.txt";
		String remotefile = "ICORESIBSCFMAINT20171009_new.txt";
//		FileClient createClient = ApBaseFile.createClient(dirCode);
//		createClient.upload(localpath, remotefile, true);
	}
}
