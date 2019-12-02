package cn.sunline.icore.ap.encrypt;
//package cn.sunline.ltts.ap.encrypt;
//
//import java.io.UnsupportedEncodingException;
//
//import cn.sunline.ltts.ap.parm.ApSystemParm;
//import cn.sunline.ltts.ap.util.ApConst;
//import cn.sunline.ltts.ap.util.EncryptUtil;
//import cn.sunline.ltts.core.api.encstring.EncStringSecurity;
//import cn.sunline.ltts.core.api.logging.BizLog;
//import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.sys.errors.ApBaseErr;
//
//public class EncStringSecurityImpl4AES implements EncStringSecurity{
//	private static final BizLog bizlog = BizLogUtil.getBizLog(EncStringSecurityImpl4AES.class);
//	
//	@Override
//	public String decrypt(String str) {
//		String ret = null;
//		try {
//			ret = new String(EncryptUtil.AESDecrypt(getKey(),EncryptUtil.hexStringToBytes(str)),ApConst.DEFAULT_CHARACTER);
//		} catch (Exception e) {
//			bizlog.error("decrypt error", e);
//			ApBaseErr.ApBase.E0116(e.getMessage());
//		} 
//		return ret;
//	}
//
//	@Override
//	public String encrypt(String str) {
//		String ret = null;
//		try {
//			ret = EncryptUtil.bytesToHexString(EncryptUtil.AESEncrypt(getKey(),str.getBytes(ApConst.DEFAULT_CHARACTER)));
//		} catch (Exception e) {
//			bizlog.error("encrypt error", e);
//			ApBaseErr.ApBase.E0115(e.getMessage());
//		} 
//		return ret;
//	}
//		
//	
//	private byte[] getKey() throws UnsupportedEncodingException{
//		
//		byte[] keyByte = ApSystemParm.getValue("AP_ENCRYPT", "AES_KEY").getBytes(ApConst.DEFAULT_CHARACTER);
//
//		return keyByte;
//	}
//}
