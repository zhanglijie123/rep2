package cn.sunline.icore.ap.hsm;

public class EsmVerifyCvvMsg extends EsmMsg {

	public static String createMsg(String cardNo, String expiry, String keyIndex, String serviceCode, String cvv) {
		StringBuffer sb = new StringBuffer();
//		String msgHdr = "010100000019";
//		sb.append(msgHdr); //MSGHDR
		sb.append(genHeader("EE0803")); //CMD
		sb.append("00"); //FM
		sb.append("02"); //FORMAT
		sb.append("00"); //CVK
		sb.append(keyIndex); //CVKIDX
		//CVV-DATA (length 16 hex) = PAN + YYMM + CRDTYPE + NUL5
		sb.append(cardNo); //PAN
		sb.append(expiry); // YYMM
		sb.append(serviceCode); // CRDTYPE ServerCode
		sb.append("000000000"); //NUL5
		sb.append(cvv); //CVV
		//COBOL got below values
		sb.append("F"); //PAD
		//sb.append("00"); //NUL2
				
		String cmdStr = sb.toString();
		return cmdStr;
	}
}
