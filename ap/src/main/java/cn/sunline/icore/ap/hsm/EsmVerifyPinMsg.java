package cn.sunline.icore.ap.hsm;


public class EsmVerifyPinMsg extends EsmMsg {

	
	public static String createPinMsg(String pinVerificationKey, String cardNo, String zonePinKey, String pinBlock, String pinOffset){
		
//		String msgHdr = "010100000028";
		
//		String wsLst12Pan = cardNo.substring(cardNo.length()-12,cardNo.length());
		String wsLst12Pan = cardNo.substring(3, 15);
		//String wsValidData = wsLst12Pan.substring(0, wsLst12Pan.length()-1) + "N";
		String wsValidData = cardNo.substring(0, 16);

		StringBuffer sb = new StringBuffer();
		String pvka = pinVerificationKey.substring(0, 2); //SAC20162381
		/*
		 * If cardBrand.CUP
   		sb.append(�010100000037�); //MSGHDR FOR CUP
		else
        sb.append(msgHdr); //MSGHDR 
		 */
//		if(brand.compareTo(CardBrand.CUP) == 0) {
//			sb.append("010100000037");
//		} else {
//			sb.append(msgHdr); //MSGHDR
//		}
		sb.append(genHeader("EE0603")); //CMD 
		sb.append("00"); //FM
		sb.append(pinBlock); //PIN-BLOCK
		//Conversion no need
		//sb.append(StringUtil.byteArrayToHexString(pinBlock.getBytes())); //PIN-BLOCK //Byte2bcd
		
//		if(brand.compareTo(CardBrand.CUP) == 0) {
//			sb.append("11"); //PPKI-VAR
//			sb.append("11"); //PPKI-FORMAT		
//		} else if (route == ProcMsg.BaseInterface.Nac) {
//			sb.append("09"); //PPKI-VAR
//			sb.append("11"); //PPKI-FORMAT	 //Double length if POS
//		}else {
		if(zonePinKey.length() > 2) {
			sb.append("11"); //PPKI-VAR
			sb.append("11"); //PPKI-FORMAT	
		} else {
			sb.append("02"); //PPKI-VAR
			sb.append("00"); //PPKI-FORMAT		
		}
//		}
		sb.append(zonePinKey); //PPKI
		sb.append("01"); //PFI
		sb.append(wsLst12Pan); //ANB
		sb.append("02"); //PVKI-VAR
		sb.append("00"); //PVKI-FORMAT
		sb.append(pvka); //PVKI		
		sb.append(wsValidData); //VALIDATION-DATA
		sb.append(EsmStringUtil.getPadRight(pinOffset, 12, '0')); //OFFSET
//		sb.append(twoDigit.format(pinLen)); //CHECK-LEN
		sb.append("06"); //CHECK-LEN
		String cmdStr = sb.toString();
		return cmdStr;
	}
}
