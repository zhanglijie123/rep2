package cn.sunline.icore.ap.hsm;

public class EsmRSAPinTranslate extends  EsmMsg{

public static String createMessage(String RSAIndex, String C, String P, String RN, String PPK,String CardNo) {
		
		//Construct EPB
		StringBuffer epb = new StringBuffer();
		epb.append("0200");
		epb.append(RSAIndex);
		if(256 == C.length()/2) {
			epb.append("8"+EsmStringUtil.int2hex2(C.length()/2));
		} else {
			epb.append(EsmStringUtil.int2hex2(C.length()/2));
			epb.append(EsmStringUtil.int2hex2(C.length()/2));
		}
		epb.append(C);
		epb.append("18");
		epb.append("5A5A");
		epb.append("01");
		epb.append("04");//how change 
		epb.append("14");
		epb.append(EsmStringUtil.int2hex2(P.length()/2));
		epb.append(P);
		epb.append("A5A5");
		epb.append(EsmStringUtil.int2hex2(RN.length()/2));
		epb.append(RN);
		
		StringBuffer sb = new StringBuffer();
//		String msgHdr = "010100000019";
//		
//		sb.append(msgHdr); // MSGHDR
		sb.append("EE3019"); // CMD
		sb.append("00");
		sb.append(epb.toString());
		sb.append("0200");
		sb.append(PPK);
		sb.append("01");
		sb.append(CardNo.substring(3, 15));
		
		return sb.toString();
		// sb.append("00"); //NUL2
	}
}
