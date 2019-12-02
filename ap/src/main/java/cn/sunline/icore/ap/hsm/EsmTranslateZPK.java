package cn.sunline.icore.ap.hsm;

/**
 * 
 * @author George Esguerra
 *
 */
public class EsmTranslateZPK extends EsmMsg {
	/*
	 *            MOVE X"40"                   TO WS-MPRCV-P1.
           MOVE X"00"                   TO WS-MPRCV-NUL1.
           MOVE "01010000001A"          TO WS-MPRCV-MSGHDR.
           MOVE "EE0403"                TO WS-MPRCV-CMD.
           MOVE "00"                    TO WS-MPRCV-FM.
           MOVE "02"                    TO WS-MPRCV-KIR-VAR.
           MOVE "00"                    TO WS-MPRCV-KIR-FORMAT.
           MOVE "0200"                  TO WS-MPRCV-KEY-FLAG.
           MOVE "10"                    TO WS-MPRCV-PPK-LEN.
			 MOVE WS-DE48-DATA             TO WS-MPRCV-PPK-KIR.

	
	 01  WS-MPRCV-PARAM1.
           03  WS-MPRCV-P1            PIC X(01) VALUE X"40".
           03  WS-MPRCV-NUL1          PIC X(01) VALUE X"00".
       01  WS-MPRCV-PARAM2.
           03  WS-MPRCV-MSGHDR        PIC X(12) VALUE "01010000001A".
           03  WS-MPRCV-CMD           PIC X(06) VALUE "EE0403".
           03  WS-MPRCV-FM            PIC X(02) VALUE "00".
           03  WS-MPRCV-KIR-SPEC.
               05 WS-MPRCV-KIR-VAR    PIC X(02) VALUE "02".
               05 WS-MPRCV-KIR-FORMAT PIC X(02) VALUE "00".
               05 WS-MPRCV-KIRI       PIC X(02) VALUE SPACES.
           03  WS-MPRCV-KEY-FLAG      PIC X(04) VALUE "0200".
           03  WS-MPRCV-PPK.
               05  WS-MPRCV-PPK-LEN   PIC X(02) VALUE "10".
               05  WS-MPRCV-PPK-KIR   PIC X(32) VALUE SPACES.

	 * 
	 * 
	 */
	
	public static String createMessage(String pwk,String sessionKey) {
		
//		if( tmk.length() != 2 )
//			log.warning("ConfErr:GenNewKey:Key len err:"+tmk.length());
		StringBuffer sb = new StringBuffer();
		sb.append("01010000001A"); //MSGHDR
		sb.append(genHeader("EE0403")); //CMD
		sb.append("00"); //FM
		sb.append("02"); //KIR-VAR
		sb.append("00"); //KIR-FORMAT
		sb.append(pwk.trim()); //KIRI
		//sb.append("0002"); //KEY-FLAG (Single length 16)
		//sb.append("0100"); //KEY-FLAG (Double length 32) <-- Wrong 0100		
		sb.append("0200"); //KEY-FLAG (Double length 32)
		sb.append("10"); //PPK-LEN
		sb.append(sessionKey);//WS-MPRCV-PPK-KIR 
//		log.info("EsmReqExtractZPK esm req="+sb.toString());
		return sb.toString();
	}
}
