package cn.sunline.icore.ap.hsm;

import cn.sunline.icore.ap.parm.ApBaseBusinessParm;
import cn.sunline.icore.ap.plugin.CommPlugin;
import cn.sunline.icore.ap.rule.common.CommConfig;
import cn.sunline.icore.sys.type.EnumType.E_CVVTYPE;
import cn.sunline.icore.sys.type.EnumType.E_KEYTYPE;
import cn.sunline.icore.sys.type.EnumType.E_PINFORMAT;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * 
 * <p>
 * 功能说明：
 *   广 东省农信科友加密平台常用安全校验方法    			
 * </p>
 * 
 * @Author heyong
 *         <p>
 *         <li>2017年5月3日-下午5:04:51</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年5月3日 heyong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class GdrcbKeYouHsmSecurityImpl implements HsmSecurity {
    
    private static final BizLog bizlog = BizLogUtil.getBizLog(GdrcbKeYouHsmSecurityImpl.class);
    
    // 测试：假的校验器，后续修改。
    private static ESMCommClient comm = null;
    private static CommConfig hsmConf = CommPlugin.get().getConf();
    
    
    // 公共密钥类型
    
    public static final String VBV_IND = "VBV_IND"; 
    public static final String CAVV_IDX = "CAVV_IDX"; 
    
//    public GdrcbKeYouHsmSecurityImpl() {
//        String ipAddress = ApSystemParm.getValue(HSM_PLAT, HSM_ADDRESS);
//        String sysId = ApSystemParm.getValue(HSM_PLAT, HSM_SYSID);
//        String appId = ApSystemParm.getValue(HSM_PLAT, HSM_APPID);
//        
//        List<String>  ipList = new ArrayList<String>();
//        List<Integer>  portList = new ArrayList<Integer>();
//        String[] addresses = ipAddress.split(":");
//        ipList.add(addresses[0]);
//        portList.add(Integer.parseInt(addresses[1]));
//        
//        unionApi = new UnionEsscAPI(ipList, portList, 7, sysId, appId, "1");
//    }
//    public void socketSecurityStart(){
//    	comm = new ESMCommClient(Integer.valueOf(hsmConf.getHsmPort()),hsmConf.getHsmIp());
//    }
    
    @Override
	public String getPublicKey(String pksIndex) {
    	//String keyIndex = hsmConf.getpksIndex();
    	//String keyIndex = "63";
    	
    	//生成头信息
    	String inputHeadMsg = EsmRetrivePubKey.GenPublicKey(pksIndex);
    	//生成报文头 
    	StringBuffer msgHdr = generateHead(inputHeadMsg);
    	//组装数据
    	String inputMsg = msgHdr + inputHeadMsg ;
    	
    	//process
    	String result = validationPCV(inputMsg);
    	
    	/*
    	 * Response
    	 * 010100000113EE3000
    	 * 00
8105808080C06CBB4F0F8FB62030D3A98F184DD09BF5EE9B9662CBF1D6130AD75C4630730EE1BE96DB07EB54E2DAFA7D93A0442FFC7ED9731E346BB5B16F5A3BD1586ECE528347A1F98FCBC7BD2E35677607924F82E648722D7FC68EDB2C5EEA4C865CB11A4E400361C32D986F551DB1F3C76120284B9B0AB28A29BF69073A2A14AB17542580800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003E5511422123BB182

    	 */
    	
//    	return result;
    	String CompareRes = result.substring(18, 20);
    	String keyData = result.substring(20);
    	EsmPublicKeyBean publicKey = new EsmPublicKeyBean(keyData);
    	if("00".equals(CompareRes)){ 
    		
    		
    		return publicKey.getPublicKey();
    		//Should also return in service the publicKey.getExponent()
    	}else{
    		bizlog.error("加密机校验失败", "");
    		throw new LttsBusinessException("异常");
    	}
	}
        
    @Override
    public String generateCvv(E_KEYTYPE cvk_type, String cardNo, String expiryDate, String serviceCode, E_CVVTYPE cvv_type) {
        
//        String isskeyId = null;
//        if (cvv_type == E_CVVTYPE.CVV)
//            isskeyId = ISSKEY_ID_CVK;
//        else if (cvv_type == E_CVVTYPE.ICVV)
//            isskeyId = ISSKEY_ID_ICVK;
//        else
//            isskeyId = ISSKEY_ID_CVK2;
//        
//        String cvkName = null;
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, isskeyId);
//        if (cvk_type == E_KEYTYPE.DES)
//            cvkName = issuerKey.getIsskey_name();
//        else if (cvk_type == E_KEYTYPE.SM)
//            cvkName = issuerKey.getIsskey_name1();
		if (cvv_type == E_CVVTYPE.CVV2) {
			String expiryYY = expiryDate.substring(0, 2);
			String expiryMM = expiryDate.substring(2, 4);
			expiryDate = expiryMM + expiryYY;
		}
    	
        String keyIndex = hsmConf.getCvkIndex();

        // 截取后的数据 
        String inputHeadMsg = EsmGenerateCVV.createMsg(cardNo, expiryDate, keyIndex, serviceCode);
        
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);
        //010100000006EE080200480F
        
        String CompareRes = result.substring(18, 20);
        // 组装数据 送到加密机
        if("00".equals(CompareRes)){
        	return result.substring(20,23);
        }else{
        	bizlog.error("加密机校验失败", "");
        	throw new LttsBusinessException("错误信息");
        }
//           
//        return null;
              
        // 密钥名称，有效期，服务代码，账号
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE133(cvkName, expiryDate, serviceCode, cardNo);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getCVV();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E133");
//        }
    }

    @Override
    public boolean checkCvv(E_KEYTYPE cvk_type, String cardNo, String expiryDate, String serviceCode, E_CVVTYPE cvv_type, String checkCvv) {
//    	MapListDataContext responseData = EngineContext.getRequestData().getInput();
//		System.err.println(responseData.getMap().get("track_2_data"));
    	
//        String isskeyId = null;
//        if (cvv_type == E_CVVTYPE.CVV)
//            isskeyId = ISSKEY_ID_CVK;
//        else if (cvv_type == E_CVVTYPE.ICVV)
//            isskeyId = ISSKEY_ID_ICVK;
//        else
//            isskeyId = ISSKEY_ID_CVK2;
//
//        String cvkName = null;
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, isskeyId);
//        if (cvk_type == E_KEYTYPE.DES)
//            cvkName = issuerKey.getIsskey_name();
//        else if (cvk_type == E_KEYTYPE.SM)
//            cvkName = issuerKey.getIsskey_name1();
        
        //Handle CVV2 Generation / Verification
        if (cvv_type == E_CVVTYPE.CVV2) {
        	String expiryYY = expiryDate.substring(0, 2);
        	String expiryMM = expiryDate.substring(2, 4);
        	expiryDate = expiryMM + expiryYY;
        }
        
        
        String keyIndex = hsmConf.getCvkIndex();
        
        if (cvv_type == E_CVVTYPE.CAVV) {
        	keyIndex = ApBaseBusinessParm.getValue(VBV_IND, CAVV_IDX);
        }
        
        
        // 截取后的数据 
        String inputHeadMsg = EsmVerifyCvvMsg.createMsg(cardNo, expiryDate, keyIndex, serviceCode, checkCvv);
        // 010100000019  EE08030002000162262701000000112102201000000000678F
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);
        bizlog.info("result>>>>>>>>>>>>>>>[%s]",result);
//        String CompareRes = result.substring(6, 8); (18,20 is the correct one)
        String CompareRes = result.substring(18, 20);
        // 组装数据 送到加密机
        if("00".equals(CompareRes)){
        	return true;
        }else{
        	bizlog.error("加密机校验失败", "");
        	return false;
        }

    }
    
    // 加密密码
    @Override
    public String encryptPin(String accNO, String plainPin) {
        
        //广东省农信特殊处理： 如果pinNode不为空，则使用pinNode作为zpk进行加解密和转加密
//        if (CommUtil.isNotNull(runEnvs.getPin_node())) {
//            zpkName = "HOST." + runEnvs.getPin_node() + ".zpk";
//        } else {
//            // 获取zpk密钥类型
//            E_KEYTYPE zpk_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZPK_TYPE));
//            
//            dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZPK);
//            if (zpk_type == E_KEYTYPE.DES)
//                zpkName = issuerKey.getIsskey_name();
//            else if (zpk_type == E_KEYTYPE.SM)
//                zpkName = issuerKey.getIsskey_name1();
//        }
        
        StringBuffer sb = new StringBuffer();
        String pinLength = "06";
		String genHeader = EsmMsg.genHeader("EE0600");
		String keyIndex = hsmConf.getCvkIndex();
        sb.append(genHeader); //CMD 
		sb.append("00"); //FM
		sb.append(pinLength); //PIN-Length
		sb.append(plainPin); // Pin
		sb.append(accNO); // ANB
		sb.append(keyIndex); //PPK-Spec
		String cmdStr = sb.toString();
        
		// 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(cmdStr);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + cmdStr ;
        
        String result = validationPCV(inputMsg);

//        String CompareRes = result.substring(6, 8);
        String CompareRes = result.substring(18, 20);
        // 组装数据 送到加密机
        if("00".equals(CompareRes)){
        	return "a39gd5";
        }else{
        	bizlog.error("加密机校验失败", "");
        	throw new LttsBusinessException("错误信息");
        }
        
//        
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE140(plainPin, accNO, 2, "01", zpkName);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getPinBlock();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E140");
//        }
    }
    
    
    @Override
    public boolean checkSimplePassword(String accNo, String mobileNo, String idNo, String checkData1, String checkData2,
            E_PINFORMAT pin_format, String pinBlock) {
        
//        String zpkName = null;
//        
//        RunEnvs runEnvs = SysUtil.getTrxRunEnvs();
//        
//        // 广东省农信特殊处理： 如果pinNode不为空，则使用pinNode作为zpk进行加解密和转加密
//        if (CommUtil.isNotNull(runEnvs.getPin_node())) {
//            zpkName = "HOST." + runEnvs.getPin_node() + ".zpk";
//        } else {
//            // 获取zpk密钥类型
//            E_KEYTYPE zpk_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZPK_TYPE));
//            
//            dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZPK);
//            if (zpk_type == E_KEYTYPE.DES)
//                zpkName = issuerKey.getIsskey_name();
//            else if (zpk_type == E_KEYTYPE.SM)
//                zpkName = issuerKey.getIsskey_name1();
//            
//            if (pin_format == E_PINFORMAT.WITHOUTACCTNO)  accNo = DEFAULT_ACCTNO;
//        }
        
        //-----
        //测试 丁美坤 0803
        return true;
        //-----
        
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE149(zpkName, "01", accNo, mobileNo, idNo, checkData1, checkData2, pinBlock);
//        if (1 == transInfo.getIsSuccess()) {
//            return (transInfo.getReturnBody().getResultFlag().equals("1"));
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E149");
//        }
    }

    @Override
    public String generatePin(E_KEYTYPE  pvk_type, String cardNO, String pinVerificationKey, String zonePinKey, String pinBlock) {

        // 获取密钥名称
//        String pvkName = null;
//        String zpkName = null;
//        dcp_issuerkey issuerKey = null;
//        TUnionTransInfo transInfo = null;
        
        
        // 广东省农信特殊处理： 如果pinNode不为空，则使用pinNode作为zpk进行加解密和转加密
//        if (CommUtil.isNotNull(runEnvs.getPin_node())) {
//            zpkName = "HOST." + runEnvs.getPin_node() + ".zpk";
//        } else {
//            // 获取zpk密钥类型
//            E_KEYTYPE zpk_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZPK_TYPE));
//            
//            issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZPK);
//            if (zpk_type == E_KEYTYPE.DES)
//                zpkName = issuerKey.getIsskey_name();
//            else if (zpk_type == E_KEYTYPE.SM)
//                zpkName = issuerKey.getIsskey_name1();
//            
//            // 广 东省农信，外围pinBlock为无账号加密，这里转成有账号加密
//            if (pin_format == E_PINFORMAT.WITHOUTACCTNO) {
//                transInfo = unionApi.unionAPIServiceE142(pinBlock, zpkName, zpkName, DEFAULT_ACCTNO, accNO, 
//                        "01", "01", 0, null, 0, null);
//                if (1 == transInfo.getIsSuccess()) {
//                    pinBlock = transInfo.getReturnBody().getPinBlock();
//                } else {
//                    bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//                    throw ApBaseErr.ApBase.E0059("E142");
//                }
//            }
//        }
        
//        issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_PVK);
//        if (pvk_type == E_KEYTYPE.DES)
//            pvkName = issuerKey.getIsskey_name();
//        else if (pvk_type == E_KEYTYPE.SM)
//            pvkName = issuerKey.getIsskey_name1();
      
        // 截取后的数据 
        String inputHeadMsg = EsmGeneratePinOffMsg.createPinOffMsg(pinVerificationKey, cardNO, zonePinKey, pinBlock);
        
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);

//        String CompareRes = result.substring(6, 8);
        String CompareRes = result.substring(18, 20);
        
        // 组装数据 送到加密机
        if("00".equals(CompareRes)){
        	return result.substring(20,32);
        }else{
        	bizlog.error("加密机校验失败", "");
        	throw new LttsBusinessException("错误信息");
        }
        
//        return "8187A7";
        // PVK密钥名称，PIN密文，minPinLen，PIN保护方式 ，ZPK密钥名称，账号，校验标识，PIN校验数据，PIN的弱密码判断标识，PIN块的格式，弱密码规则
//        transInfo = unionApi.unionAPIServiceE132(pvkName, pinBlock,
//                6, 2, zpkName, accNO, 0, null, 1, "01", 0);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getPinOffset();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E132");
//        }
        
    }

    @Override
    public boolean checkPin(E_KEYTYPE  pvk_type, String cardNO, String pinBlock, String pinVerificationKey, String zonePinKey, String pinOffset) {
        
        // 获取密钥名称
//        String pvkName = null;
//        String zpkName = null;
//        dcp_issuerkey issuerKey = null;
//        TUnionTransInfo transInfo = null;
        
        
        // 广东省农信特殊处理： 如果pinNode不为空，则使用pinNode作为zpk进行加解密和转加密
//        if (CommUtil.isNotNull(runEnvs.getPin_node())) {
//            zpkName = "HOST." + runEnvs.getPin_node() + ".zpk";
//        } else {
//            // 获取zpk密钥类型
//            E_KEYTYPE zpk_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZPK_TYPE));
//            
//            issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZPK);
//            if (zpk_type == E_KEYTYPE.DES)
//                zpkName = issuerKey.getIsskey_name();
//            else if (zpk_type == E_KEYTYPE.SM)
//                zpkName = issuerKey.getIsskey_name1();
//            
//        issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_PVK);
//        if (pvk_type == E_KEYTYPE.DES)
//            pvkName = issuerKey.getIsskey_name();
//        else if (pvk_type == E_KEYTYPE.SM)
//            pvkName = issuerKey.getIsskey_name1();
//        }
        
//        String keyIndex = hsmConf.getCvkIndex();
        // 截取后的数据 
        String inputHeadMsg = EsmVerifyPinMsg.createPinMsg(pinVerificationKey, cardNO, zonePinKey, pinBlock, pinOffset);
        
        // 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(inputHeadMsg);
		
		// 拼接数据 开始校验
		String inputMsg = msgHdr + inputHeadMsg ;
        
        String result = validationPCV(inputMsg);

//        String CompareRes = result.substring(6, 8);
        String CompareRes = result.substring(18, 20);
        // PIN密文，PIN格式，账号，ZPK名称，模式 1:IBM PinOffset验证，PVK名称，PIN Offset， 检验标识
        // 组装数据 送到加密机
        if("00".equals(CompareRes)){
        	return true;
        }else{
        	bizlog.error("加密机密码校验失败", "");
        	return false;
        }
    }

    @Override
    public boolean checkARQC(E_KEYTYPE mdk_type, String algorithmID, String cardNo, String atc, String arqcData, String arqc) {
        
        // 获取mc-ac密钥名称及密钥版本
//        String mdkAcName = null; 
        
        // 当卡为双算法卡时，则通过9F10中的算法标识来识别当前加密算法为国际还不国密
//        if (mdk_type == E_KEYTYPE.BOTH)
//        {
//            if ("01".equals(algorithmID))  mdk_type = E_KEYTYPE.DES;
//            else if ("04".equals(algorithmID)) mdk_type = E_KEYTYPE.SM;
//        }
//                
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_MDK_AC);
//        if (mdk_type == E_KEYTYPE.DES)
//            mdkAcName = issuerKey.getIsskey_name();
//        else if (mdk_type == E_KEYTYPE.SM)
//            mdkAcName = issuerKey.getIsskey_name1();
//        
//        // mk-ac密钥名称，密钥的版本号，卡号，离散过程因子，计算ARQC使用的数据，ARQC，iccType（0：PBOC2.0规范IC卡）
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE301(
//            mdkAcName,
//            "00000001",
//            cardNo,
//            atc,
//            arqcData,
//            arqc, 
//            0);
//        if (0 == transInfo.getResponseCode()) {
//            return true;
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            return false;
//        }
    	return true;
    }

    /**
     * 校验ARQC并生成ARPC
     * @param E_KEYTYPE  mdk_type
     * @param algorithmID  01-3DES 04-SM4
     * @param cardNo  卡号
     * @param atc   离散过程因子(即55域中9F36)
     * @param arqcData  ARQC数据源
     * @param arqc      要校验的ARQC
     * @param arc       授权响应码(即8583中的39域，这里可默认为00)
     * @return arpc
     */
    @Override								
    public String checkARQCAndGenerateARPC(E_KEYTYPE mdk_type, String algorithmID,String cmdStr) {
        
        // 获取mc-ac密钥名称及密钥版本
//        String mdkAcName = null;
        
        // 当卡为双算法卡时，则通过9F10中的算法标识来识别当前加密算法为国际还不国密
//        if (mdk_type == E_KEYTYPE.BOTH)
//        {
//            if ("01".equals(algorithmID))  mdk_type = E_KEYTYPE.DES;
//            else if ("04".equals(algorithmID)) mdk_type = E_KEYTYPE.SM;
//        }
//        
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_MDK_AC);
//        if (mdk_type == E_KEYTYPE.DES)
//            mdkAcName = issuerKey.getIsskey_name();
//        else if (mdk_type == E_KEYTYPE.SM)
//        
//    	mdkAcName = issuerKey.getIsskey_name1();
    
		// 开始计算生成 head 数据
        StringBuffer msgHdr = generateHead(cmdStr);
        
        // 拼接数据 开始校验
		String inputMsg = msgHdr + cmdStr ;
        
        String result = validationPCV(inputMsg);
	        
        //Add logic to see what is the result
		String CompareRes = result.substring(18, 20);
		System.out.println("Result:" + result);
		bizlog.info("result>>>>>>>>>>>>>>>[%s]",result);
		//CMD(3) + RC(1) + FILLER(1) + ARPC(8)
		//6+2+2+16
		if ("00".equals(CompareRes)) {
			if (result.length() < 38)
				return null;
			else
				return result.substring(22, 38);
//			return "0101"; // Placeholder
		} else {
			return null;
		}
    }

    @Override
    public String translatePinOffsetWith2AccNo(E_KEYTYPE pvk_type, String accNo, String accNo1, String pinOffset) {
        
        // 获取PVK密钥名称及密钥版本
//        String pvkName = null;
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_PVK);
//        if (pvk_type == E_KEYTYPE.DES)
//            pvkName = issuerKey.getIsskey_name();
//        else if (pvk_type == E_KEYTYPE.SM)
//            pvkName = issuerKey.getIsskey_name1();
//        
//        // 最小PIN长度，PIN Offset，源PVK密钥名称，目的PVK密钥名称，源账号，目的账号，检验标识，PIN校验数据，检验标识，PIN校验数据
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE144("06", pinOffset,
//                pvkName, pvkName, accNo, accNo1, 0, null, 0, null);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getPinOffset();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E144");
//        }    
    	return "";
    }

    @Override
    public String translatePinBlockWith2AccNo(String accNo, String accNo1, String pinBlock) {
        
        // 获取ZPK密钥名称及密钥版本
//        String zpkName = null;
//        
//        // 获取zpk密钥类型
//        E_KEYTYPE zpk_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZPK_TYPE));
//        
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZPK);
//        if (zpk_type == E_KEYTYPE.DES)
//            zpkName = issuerKey.getIsskey_name();
//        else if (zpk_type == E_KEYTYPE.SM)
//            zpkName = issuerKey.getIsskey_name1();
//        
//        // 密码密文，账号1，账号2，密钥名称1，密钥名称2
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE142(pinBlock, zpkName, zpkName, accNo, accNo1, 
//                "01", "01", 0, null, 0, null);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getPinBlock();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E142");
//        }
    	return "";
    }

    @Override
    public String encryptData(int encryptType, String plainData) {
        
//        String keyName = null;
//        
//        E_KEYTYPE key_type = null;
//        dcp_issuerkey issuerKey = null;
//        if (encryptType == 1) { // 1-敏感数据 
//            issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_EDK);
//            key_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, EDK_TYPE));
//        } else if (encryptType == 2) { // 2-磁条数据
//            issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZEK);
//            key_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZEK_TYPE));
//        }
//        
//        if (key_type == E_KEYTYPE.DES)
//            keyName = issuerKey.getIsskey_name();
//        else if (key_type == E_KEYTYPE.SM)
//            keyName = issuerKey.getIsskey_name1();
//        
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE160(1, keyName, null, 0, 1, plainData, null, 2);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getData();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E160");
//        }
    	return "";
    }

    @Override
    public String decryptData(int encryptType, String cipherData) {
        
//        String keyName = null;
//        
//        E_KEYTYPE key_type = null;
//        dcp_issuerkey issuerKey = null;
//        if (encryptType == 1) { // 1-敏感数据 
//            issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_EDK);
//            key_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, EDK_TYPE));
//        } else if (encryptType == 2) { // 2-磁条数据
//            issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_ZEK);
//            key_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, ZEK_TYPE));
//        }
//        
//        if (key_type == E_KEYTYPE.DES)
//            keyName = issuerKey.getIsskey_name();
//        else if (key_type == E_KEYTYPE.SM)
//            keyName = issuerKey.getIsskey_name1();
//        
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE161(1, keyName, null, 0, 1, cipherData, null, 2);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getData();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E161");
//        }
    	return "";
    }
    
    @Override
    public String generateOfflinePin(String accNo) {
        
        // 随机产生密码密文
//        String pinBlock = null;
//        TUnionTransInfo transInfo = unionApi.unionAPIServiceE130(6, accNo, 1, null, null);
//        if (1 == transInfo.getIsSuccess()) {
//            pinBlock = transInfo.getReturnBody().getPinBlock();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E130");
//        }
//        
//        // 获取opvk密钥类型
//        E_KEYTYPE opvk_type = E_KEYTYPE.valueOf(ApSystemParm.getValue(HSM_PLAT, OPVK_TYPE));
//        
//        String keyName = null;
//        dcp_issuerkey issuerKey = DcIssuerKey.getKeyName(CUP_KEYSET_ID, ISSKEY_ID_OFFL_PVK);
//        if (opvk_type == E_KEYTYPE.DES)
//            keyName = issuerKey.getIsskey_name();
//        else if (opvk_type == E_KEYTYPE.SM)
//            keyName = issuerKey.getIsskey_name1();
//        
//        transInfo = unionApi.unionAPIServiceE142(pinBlock, null, keyName, accNo, accNo, "01", "15", 0, null, 0, null);
//        if (1 == transInfo.getIsSuccess()) {
//            return transInfo.getReturnBody().getPinBlock();
//        } else {
//            bizlog.error("安全平台失败，错误吗:%s,错误信息:%s", transInfo.getResponseCode(), transInfo.getResponseRemark());
//            throw ApBaseErr.ApBase.E0059("E142");
//        }
    	//数据组装
    	StringBuffer   msg =  new StringBuffer();//EE0E04 pin-generate  rand 
    	msg.append("EE0E04");
    	msg.append("00");
    	msg.append("06");//PIN-length
    	msg.append("01");//PIN-Block  Format option:01 10 13
    	msg.append(accNo);//ANB
    	msg.append("11");//PPK-Spec
    	
    	comm = new ESMCommClient(Integer.valueOf(hsmConf.getHsmPort()),hsmConf.getHsmIp());
    	String resultstr = null;
    	try{
    		byte[] strMsgAfterSend = comm.send(EsmStringUtil.byteToBcd(msg.toString()));
            resultstr = EsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
        }catch(Exception ex){
        	bizlog.error("密码重置随机密码生成失败[%s]",ex);
		} finally {
			comm.closeSocket();
        }
    	
    	return resultstr;
        
    }
    
    // process CVV verifiction
    public String validationPCV(String inputMsg){
    	
    	comm = new ESMCommClient(Integer.valueOf(hsmConf.getHsmPort()),hsmConf.getHsmIp());
    	
//    	ESMCommClient comm = null;
    	String resultstr = null;
    	try{
    		bizlog.info("Request Message:[%s]", inputMsg);
			byte[] out=EsmStringUtil.byteToBcd(inputMsg);
			bizlog.info("Message Length:[%s]", out.length);
			byte[] strMsgAfterSend = comm.send(out);
			bizlog.info("Receive Size:[%s]", strMsgAfterSend.length);
			String receiveMsg = EsmStringUtil.dump(strMsgAfterSend,0,strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
			bizlog.info("resultSet:[%s]", receiveMsg);
            resultstr = EsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
            bizlog.info("resultStr:[%s]", resultstr);
            
        }catch(Exception except){
        	bizlog.error(except.getMessage(), except);
        	try {
	        	comm = new ESMCommClient(Integer.valueOf(hsmConf.getHsmPort()),hsmConf.getHsmIp());
	        	bizlog.info("Request Message:[%s]", inputMsg);
				byte[] out=EsmStringUtil.byteToBcd(inputMsg);
				bizlog.info("Message Length:[%s]", out.length);
				byte[] strMsgAfterSend = comm.send(out);
				bizlog.info("Receive Size:[%s]", strMsgAfterSend.length);
				String receiveMsg = EsmStringUtil.dump(strMsgAfterSend,0,strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
				bizlog.info("resultSet:[%s]", receiveMsg);
	            resultstr = EsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
	            bizlog.info("resultStr:[%s]", resultstr);
        	} catch (Exception e) {
        		bizlog.info("RETRY FAILED");
        	}
		} finally {
			comm.closeSocket();
        }
		return resultstr;
    }
    
    
    /**
     * @Author wangtk
     *         <p>
     *         <li>2017年8月31日-下午1:29:19</li>
     *         <li>功能说明：加密机校验串头部生成</li>
     *         </p>
     * @param inputHeadMsg
     * @return
     */
    public StringBuffer generateHead(String inputHeadMsg){
    	//0101 00000019
    	StringBuffer msgHdr = new StringBuffer();
		int halfSize = inputHeadMsg.length() / 2;
		msgHdr.append("0101");
		msgHdr.append(EsmStringUtil.getPadLeft(Integer.toHexString(halfSize), 8, '0'));
		
		return msgHdr;
    }

   
	@Override
	public String translateRSABlock(String RSAIndex, String C, String P,
			String RN, String PPK, String CardNo) {
//		EsmRSAPinTranslate  hsm = new EsmRSAPinTranslate();
		String cmdStr = EsmRSAPinTranslate.createMessage(RSAIndex, C, P, RN,
				PPK, CardNo);

		StringBuffer msgHdr = generateHead(cmdStr);
	        
	        // 拼接数据 开始校验
		String inputMsg = msgHdr + cmdStr;
		
		String result = validationPCV(inputMsg);
	        
		String CompareRes = result.substring(18, 20);
		//01010000000CEE301900DF500266422C8AB7
	        // 组装数据 送到加密机
		if ("00".equals(CompareRes)) {
			return result.substring(20);
		} else {
			bizlog.error("加密机校验失败", "");
			throw new LttsBusinessException("错误信息");
		}
//		return null;
	}

	@Override
	public String exchangeSessionKey(String sessionKey, String pwk) {
		
		
		String cmdStr = EsmTranslateZPK.createMessage(pwk, sessionKey);
		StringBuffer msgHdr = generateHead(cmdStr);

		// Splicing data
		String inputMsg = msgHdr + cmdStr;
		
		String result = validationPCV(inputMsg);

		String CompareRes = result.substring(18, 20);
		// 01010000000CEE301900DF500266422C8AB7
		// Assembly data to the encryption machine
		if ("00".equals(CompareRes)) {
			return result.substring(20);
		} else {
			bizlog.error("Encryptor verification failed", "");
			throw new LttsBusinessException("Error message");
		}
	}

}
