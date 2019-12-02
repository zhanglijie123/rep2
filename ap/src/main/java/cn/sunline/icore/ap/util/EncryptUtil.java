package cn.sunline.icore.ap.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import cn.sunline.icore.ap.component.AbstractComponent;
import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.parm.ApBaseSystemParm;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.parm.TrxEnvs.EncryptedInfo;
import cn.sunline.icore.sys.type.EnumType.E_ENCRYPTEDTYPE;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;

/**
 * <p>
 * 文件功能说明：加密工具类
 * </p>
 * 
 * @Author Elton
 *         <p>
 *         <li>2016年1月6日-上午10:18:36</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228Elton：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class EncryptUtil {

    private static final BizLog bizlog = BizLogUtil.getBizLog(EncryptUtil.class);
    
    private static final String threeDESAlgorithm = "DESede"; 

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:19:18</li>
     *         <li>功能说明：验证柜员密码</li>
     *         </p>
     * @param sPasswdDB
     *            数据库密文
     * @param sKeyWords
     *            密钥
     * @param sPasswd
     *            输入柜员密码密文
     * @return 验证是否通过
     */
    public static boolean vfyTellerPINBlock(String sPasswdDB, String sKeyWords, String sPasswd) {
        bizlog.method("vfyTellerPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswdDB [%s], sKeyWords [%s], sPasswd [%s]", sPasswdDB, sKeyWords, sPasswd);

        boolean bRet = false;
        String newPasswd = sPasswd.toLowerCase(); // add by tiantl at 20160702 for 支持大写字母

        // 加密类型
        int iEncryType = chkEncryptTellerType();
        if (iEncryType == 0 || iEncryType == 2) { // 0-不加密不验密, 2-加密不验密
            bRet = true;
            bizlog.parm("bRet [%s]", bRet);
            bizlog.method("vfyTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return bRet;
        }
        else if (iEncryType == 1) { // 1-不加密验密
            if (newPasswd.equals(sPasswdDB))
                bRet = true;
            bizlog.parm("bRet [%s]", bRet);
            bizlog.method("vfyTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return bRet;
        }
        else {
            String pinOffSet = covTellerPINBlock(newPasswd, sKeyWords);

            bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

            // 将转加密后的密文与数据库密文进行比较
            if (pinOffSet.equals(sPasswdDB)) {
                bRet = true;
                bizlog.parm("bRet [%s]", bRet);
                bizlog.method("vfyTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
                return bRet;
            }
        }
        bRet = false;
        bizlog.parm("bRet [%s]", bRet);
        bizlog.method("vfyTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return bRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:21:34</li>
     *         <li>功能说明：转加密柜员密码</li>
     *         </p>
     * @param sPasswd
     *            新柜员密码密文
     * @param sKeyWords
     *            密钥
     * @return 加密后的密文
     */
    public static String covTellerPINBlock(String sPasswd, String sKeyWords) {
        bizlog.method("covTellerPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd [%s], sKeyWords [%s]", sPasswd, sKeyWords);

        String sRet;
        // 加密类型
        int iEncryType = chkEncryptTellerType();
        if (iEncryType == 0 || iEncryType == 1) {
            sRet = sPasswd;
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("covTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();

        // 将输入的密文进行转加密
        String pinOffSet = security.translatePin(sKeyWords, sKeyWords, sPasswd);

        sRet = pinOffSet;
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("covTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:23:42</li>
     *         <li>功能说明：明码加密柜员密码</li>
     *         </p>
     * @param sPasswd
     *            柜员密码明码
     * @param sKeyWords
     *            密钥
     * @return 加密后的密文
     */
    public static String genTellerPINBlock(String sPasswd, String sKeyWords) {
        bizlog.method("genTellerPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd [%s], sKeyWords [%s]", sPasswd, sKeyWords);

        String sRet;
        // 加密类型
        int iEncryType = chkEncryptTellerType();
        if (iEncryType == 0 || iEncryType == 1) {
            sRet = sPasswd;
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("genTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();

        // 调用加密服务，返回pinOffSet
        String pinOffSet = security.encryptPin(sKeyWords, sPasswd);

        bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

        sRet = pinOffSet;
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("genTellerPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:24:56</li>
     *         <li>功能说明：密码验证</li>
     *         </p>
     * @param sPasswdDB
     *            数据库密文
     * @param sKeyWords
     *            密钥
     * @param sPasswd
     *            输入密码密文
     * @return 验证结果
     */
    public static boolean vfyPINBlock(String sPasswdDB, String sKeyWords, String sPasswd) {
        bizlog.method("vfyPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswdDB [%s], sKeyWords [%s], sPasswd [%s]", sPasswdDB, sKeyWords, sPasswd);

        boolean bRet;
        String newPasswd = sPasswd.toLowerCase(); // add by tiantl at 20160702 for 支持大写字母

        // 加密类型
        int iEncryType = chkEncryptType();
        if (iEncryType == 0 || iEncryType == 2) {
            bRet = true;
            bizlog.parm("bRet [%s]", bRet);
            bizlog.method("vfyPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return bRet;
        }
        else if (iEncryType == 1) {
            if (newPasswd.equals(sPasswdDB)) {
                bRet = true;
                bizlog.parm("bRet [%s]", bRet);
                bizlog.method("vfyPINBlock end <<<<<<<<<<<<<<<<<<<<");
                return bRet;
            }
        }
        else {
            String pinOffSet = covPINBlock(newPasswd, sKeyWords);

            bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

            // 将转加密后的密文与数据库密文进行比较
            if (pinOffSet.equals(sPasswdDB)) {
                bRet = true;
                bizlog.parm("bRet [%s]", bRet);
                bizlog.method("vfyPINBlock end <<<<<<<<<<<<<<<<<<<<");
                return bRet;
            }
        }
        bRet = false;
        bizlog.parm("bRet [%s]", bRet);
        bizlog.method("vfyPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return bRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:26:42</li>
     *         <li>功能说明：密码转加密</li>
     *         </p>
     * @param sPasswd
     *            输入的密文密码
     * @param sKeyWords
     *            密钥
     * @return 加密后的密文
     */
    public static String covPINBlock(String sPasswd, String sKeyWords) {
        bizlog.method("covPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd [%s], sKeyWords [%s]", sPasswd, sKeyWords);

        String sRet;
        // 加密类型
        int iEncryType = chkEncryptType();
        if (iEncryType == 0 || iEncryType == 1) {
            sRet = sPasswd;
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("covPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();
        Options<EncryptedInfo> encryptedInfoList = BizUtil.getTrxRunEnvs().getEncrypted_info();
        if (encryptedInfoList == null || encryptedInfoList.isEmpty()) {
            bizlog.method("covPINBlock end <<<<<<<<<<<<<<<<<<<<");
            throw ApBaseErr.ApBase.E0049();
        }

        String fromKeyWords;
        // 约定：存在多个加密字段时，加密因子采用同一个，故此次获取第一个加密因子
        fromKeyWords = security.getEncFactor(encryptedInfoList);

        bizlog.debug("the source encryption key is:[%s]，the target encryption key is:[%s]，encrypted cipher text is:[%s]", fromKeyWords, sKeyWords, sPasswd);

        // 将输入的密文进行转加密
        String pinOffSet = security.translatePin(fromKeyWords, sKeyWords, sPasswd);

        bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

        sRet = pinOffSet;
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("covPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:28:15</li>
     *         <li>功能说明：明文密码加密</li>
     *         </p>
     * @param sPasswd
     *            明文密码
     * @param sKeyWords
     *            密钥
     * @return 加密后的密文
     */
    public static String genPINBlock(String sPasswd, String sKeyWords) {
        bizlog.method("genPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd [%s], sKeyWords [%s]", sPasswd, sKeyWords);

        String sRet;
        // 加密类型
        int iEncryType = chkEncryptType();
        if (iEncryType == 0 || iEncryType == 1) {
            sRet = sPasswd;
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("genPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();

        // 调用加密服务，返回pinOffSet
        String pinOffSet = security.encryptPin(sKeyWords, sPasswd);

        bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

        sRet = pinOffSet;
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("genPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:26:42</li>
     *         <li>功能说明：密码转加密</li>
     *         </p>
     * @param sPasswd
     *            输入的密文密码
     * @param sKeyWords
     *            密钥
     * @return 加密后的密文
     */
    public static String covPINBlockBase(String sPasswd, String sKeyWords) {
        bizlog.method("covPINBlockBase begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd [%s], sKeyWords [%s]", sPasswd, sKeyWords);

        String sRet;
        // 加密类型
        int iEncryType = chkEncryptType();
        if (iEncryType == 0 || iEncryType == 1) {
            sRet = sPasswd;
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("covPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();
        Options<EncryptedInfo> encryptedInfoList = BizUtil.getTrxRunEnvs().getEncrypted_info();
        if (encryptedInfoList == null || encryptedInfoList.isEmpty()) {
            bizlog.method("covPINBlock end <<<<<<<<<<<<<<<<<<<<");
            throw ApBaseErr.ApBase.E0049();
        }

        String fromKeyWords;
        // 约定：从输入的加密信息中获取加密因子
        fromKeyWords = security.getEncFactor(encryptedInfoList);

        bizlog.debug("the source encryption key is:[%s],the target encryption key is:[%s],encrypted cipher text is:[%s]", fromKeyWords, sKeyWords, sPasswd);

        // 将输入的密文进行转加密
        String pinOffSet = security.translatePin(fromKeyWords, sKeyWords, sPasswd);

        bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

        sRet = pinOffSet;
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("covPINBlockBase end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:28:15</li>
     *         <li>功能说明：明文密码加密</li>
     *         </p>
     * @param sPasswd
     *            明文密码
     * @param sKeyWords
     *            密钥
     * @return 加密后的密文
     */
    public static String genPINBlockBase(String sPasswd, String sKeyWords) {
        bizlog.method("genPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd [%s], sKeyWords [%s]", sPasswd, sKeyWords);

        String sRet;
        // 加密类型
        int iEncryType = chkEncryptType();
        if (iEncryType == 0 || iEncryType == 1) {
            sRet = sPasswd;
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("genPINBlock end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();

        // 调用加密服务，返回pinOffSet
        String pinOffSet = security.encryptPin(sKeyWords, sPasswd);

        bizlog.debug("enter the password cipher text to the encrypted result is:[%s]", pinOffSet);

        sRet = pinOffSet;
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("genPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:29:30</li>
     *         <li>功能说明：将数据库旧密码按新凭证信息转为新密码</li>
     *         </p>
     * @param sPasswd_old
     *            数据库旧密码
     * @param sKeyWords_old
     *            旧加密关键字
     * @param sKeyWords_new
     *            新加密关键字
     * @return 新密码密文
     */
    public static String repPINBlock(String sPasswd_old, String sKeyWords_old, String sKeyWords_new) {
        bizlog.method("repPINBlock begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sPasswd_old [%s]", sPasswd_old);
        bizlog.parm("sKeyWords_old [%s]", sKeyWords_old);
        bizlog.parm("sKeyWords_new [%s]", sKeyWords_new);

        ApBaseComp.Security security = getSecurityInstance();
        String pinOffSet = security.repPINBlock(sPasswd_old, sKeyWords_old, sKeyWords_new);

        bizlog.parm("pinOffSet [%s]", pinOffSet);
        bizlog.method("repPINBlock end <<<<<<<<<<<<<<<<<<<<");
        return pinOffSet;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:31:44</li>
     *         <li>功能说明：CVN验证</li>
     *         </p>
     * @param sCardNo
     *            卡号
     * @param sServiceCode
     *            服务代码
     * @param sMatDate
     *            有效期
     * @param sCVN
     *            CVN码
     * @return 校验结果
     */
    public static boolean vrfCardCVN(String sCardNo, String sServiceCode, String sMatDate, String sCVN) {
        bizlog.method("vrfCardCVN begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sCardNo [%s], sServiceCode [%s], sMatDate [%s], sCVN [%s]", sCardNo, sServiceCode, sMatDate, sCVN);

        // 获取组件对象实例
        ApBaseComp.Security security = getSecurityInstance();
        boolean bRet = security.cvvCheck(sCVN, sCardNo, sServiceCode, sMatDate);

        bizlog.parm("bRet [%s]", bRet);
        bizlog.method("vrfCardCVN end <<<<<<<<<<<<<<<<<<<<");
        return bRet;
    }

    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年8月6日-上午10:35:04</li>
     *         <li>功能说明：检查柜员加密验密类型</li>
     *         </p>
     * @return 加密验密类型
     */
    private static int chkEncryptTellerType() {
        bizlog.method("chkEncryptTellerType begin >>>>>>>>>>>>>>>>>>>>");
        int iEncrytype;
        
        //获取柜员加密解密类型
        String tellerEncDecType = ApBaseSystemParm.getValue(ApConst.TELLER_ENC_DEC_TYPE);

        // 不配置参数，默认加密验密
        if (CommUtil.isNull(tellerEncDecType)) {
            iEncrytype = 3;
        }
        else if (E_ENCRYPTEDTYPE.NO_ENC_NO_CHK.getValue().equals(tellerEncDecType)) {
            iEncrytype = 0; // 不加密不验密
        }
        else if (E_ENCRYPTEDTYPE.NO_ENC_CHK.getValue().equals(tellerEncDecType)) {
            iEncrytype = 1; // 不加密验密
        }
        else if (E_ENCRYPTEDTYPE.ENC_NO_CHK.getValue().equals(tellerEncDecType)) {
            iEncrytype = 2; // 加密不验密
        }
        else if (E_ENCRYPTEDTYPE.ENC_CHK.getValue().equals(tellerEncDecType)) {
            iEncrytype = 3; // 加密验密
        }
        else {
            iEncrytype = 3; // 其它值-加密验密
        }

        bizlog.parm("iEncrytype [%s]", iEncrytype);
        bizlog.method("chkEncryptTellerType end <<<<<<<<<<<<<<<<<<<<");
        return iEncrytype;
    }

    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:35:04</li>
     *         <li>功能说明：检查加密验密类型</li>
     *         </p>
     * @return 加密验密类型
     */
    private static int chkEncryptType() {
        bizlog.method("chkEncryptType begin >>>>>>>>>>>>>>>>>>>>");
        int iEncrytype;
        
        //获取加密解密类型
        String encDecType = ApBaseSystemParm.getValue(ApConst.ENC_DEC_TYPE);
        
        // 不配置参数，默认加密验密
        if (CommUtil.isNull(encDecType)) {
            iEncrytype = 3;
        }
        else if (E_ENCRYPTEDTYPE.NO_ENC_NO_CHK.getValue().equals(encDecType)) {
            iEncrytype = 0; // 不加密不验密
        }
        else if (E_ENCRYPTEDTYPE.NO_ENC_CHK.getValue().equals(encDecType)) {
            iEncrytype = 1; // 不加密验密
        }
        else if (E_ENCRYPTEDTYPE.ENC_NO_CHK.getValue().equals(encDecType)) {
            iEncrytype = 2; // 加密不验密
        }
        else if (E_ENCRYPTEDTYPE.ENC_CHK.getValue().equals(encDecType)) {
            iEncrytype = 3; // 加密验密
        }
        else {
            iEncrytype = 3; // 其它值-加密验密
        }

        bizlog.parm("iEncrytype [%s]", iEncrytype);
        bizlog.method("chkEncryptType end <<<<<<<<<<<<<<<<<<<<");
        return iEncrytype;
    }

    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:20:31</li>
     *         <li>功能说明：二进制转换为ASCII</li>
     *         </p>
     * @param val
     * @return 转换后的字符串
     */
    private static String binToAsc(byte[] val) {
        bizlog.method("binToAsc begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("val [%s]", val);
        StringBuilder str = new StringBuilder(val.length * 2);
        for (int i = 0; i < val.length; i++) {
            char hi = Character.forDigit((val[i] >> 4) & 0x0F, 16);
            char lo = Character.forDigit(val[i] & 0x0F, 16);
            str.append(Character.toUpperCase(hi));
            str.append(Character.toUpperCase(lo));
        }
        String sRet = str.toString();
        bizlog.parm("sRet [%s]", sRet);
        bizlog.method("binToAsc end <<<<<<<<<<<<<<<<<<<<");
        return sRet;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:21:09</li>
     *         <li>功能说明：ASCII转换为二进制</li>
     *         </p>
     * @param val
     * @return 转换后的字节数组
     */
    private static byte[] ascToBin(String val) {
        bizlog.method("ascToBin begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("val [%s]", val);
        byte interByte[] = val.getBytes();

        byte[] result = new byte[interByte.length / 2];
        for (int i = 0; i < result.length; i++) {
            byte hi = interByte[i * 2];
            byte lo = interByte[i * 2 + 1];
            int h;
            if (hi < 0x61) {
                h = hi > 0x40 ? 10 + hi - 0x41 : hi - 0x30;
            }
            else {
                h = 10 + hi - 0x61;
            }

            int l;
            if (lo < 0x61) {
                l = lo > 0x40 ? 10 + lo - 0x41 : lo - 0x30;
            }
            else {
                l = 10 + lo - 0x61;
            }

            result[i] = (byte) (h << 4 | l);
        }

        bizlog.parm("result [%s]", result);
        bizlog.method("ascToBin end <<<<<<<<<<<<<<<<<<<<");
        return result;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:19:09</li>
     *         <li>功能说明：获取秘钥长度</li>
     *         </p>
     * @param encryptKey
     * @return 秘钥长度
     */
    private static char getKeyLength(byte[] encryptKey) {
        bizlog.method("getKeyLength begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("encryptKey [%s]", encryptKey);
        String key = binToAsc(encryptKey);
        if (key.length() == 16) {// 单倍长
            bizlog.method("getKeyLength end <<<<<<<<<<<<<<<<<<<<");
            return 'Z';
        }
        else if (key.length() == 32) {// 双倍长
            bizlog.method("getKeyLength end <<<<<<<<<<<<<<<<<<<<");
            return 'X';
        }
        else if (key.length() == 48) {// 三倍长
            bizlog.method("getKeyLength end <<<<<<<<<<<<<<<<<<<<");
            return 'Y';
        }
        else {
            bizlog.method("getKeyLength end <<<<<<<<<<<<<<<<<<<<");
            return ' ';
        }
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:17:42</li>
     *         <li>功能说明：加密操作实现</li>
     *         </p>
     * @param plainText
     * @param desKey
     * @return 加密后的字节数组
     * @throws Exception
     */
    private static byte[] encryptDES(byte[] plainText, byte[] desKey) throws Exception {
        bizlog.method("encrypt_DES begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("plainText [%s],desKey [%s]", plainText, desKey);
        /* 用某种方法获得密匙数据 */
        byte rawKeyData[] = desKey;
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 正式执行加密操作
        byte[] encryptedData = cipher.doFinal(plainText);
        bizlog.parm("encryptedData [%s]", encryptedData);
        bizlog.method("encrypt_DES end <<<<<<<<<<<<<<<<<<<<");
        return encryptedData;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:18:57</li>
     *         <li>功能说明：加密</li>
     *         </p>
     * @param plainText
     * @param desKey
     * @return 加密后的字节数组
     * @throws Exception
     */
    private static byte[] encryptTDES(byte[] plainText, byte[] desKey) throws Exception {
        bizlog.method("encrypt_TDES begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("plainText,desKey", plainText, desKey);
        byte[] rawKeyData = new byte[24];
        if (desKey.length == 16) {
            System.arraycopy(desKey, 0, rawKeyData, 0, 16);
            System.arraycopy(desKey, 0, rawKeyData, 16, 8);
        }
        else {
            rawKeyData = desKey;
        }
        SecretKeySpec key = new SecretKeySpec(rawKeyData, 0, 24, "DESede");
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 正式执行加密操作
        byte[] encryptedData = cipher.doFinal(plainText);
        bizlog.parm("encryptedData [%s]", encryptedData);
        bizlog.method("encrypt_TDES end <<<<<<<<<<<<<<<<<<<<");
        return encryptedData;
    }
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:20:22</li>
     *         <li>功能说明：加密</li>
     *         </p>
     * @param encryptText
     * @param encryptKey
     * @return 加密后的字节数组
     * @throws Exception
     */
    public static byte[] doEncrypt(byte[] encryptText, byte[] encryptKey) throws Exception {
        bizlog.method("doEncrypt begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm(" encryptText [%s],  encryptKey [%s]", encryptText, encryptKey);
        byte[] dataBin;
        char keyLength = getKeyLength(encryptKey);
        if (keyLength == 'Z') {
            dataBin = encryptDES(encryptText, encryptKey);
        }
        else {
            dataBin = encryptTDES(encryptText, encryptKey);
        }
        bizlog.parm("dataBin [%s]", dataBin);
        bizlog.method("doEncrypt end <<<<<<<<<<<<<<<<<<<<");
        return dataBin;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:20:05</li>
     *         <li>功能说明：</li>
     *         </p>
     * @param encryptText
     * @param encryptKey
     * @return 加密后的字符串
     * @throws Exception
     */
    private static String doEncrypt(String encryptText, String encryptKey) throws Exception {
        bizlog.method("doEncrypt begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm(" encryptText [%s],  encryptKey [%s]", encryptText, encryptKey);
        byte[] dataBin = doEncrypt(ascToBin(encryptText), ascToBin(encryptKey));
        bizlog.method("doEncrypt end <<<<<<<<<<<<<<<<<<<<");
        return binToAsc(dataBin);
    }
    
    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:37:30</li>
     *         <li>功能说明：DES加密处理</li>
     *         </p>
     * @param sEncryptText
     *            被加密数据
     * @param sEncryptKey
     *            密钥
     * @return DES加密后数据
     */
    public static String doDesEncrypt(String sEncryptText, String sEncryptKey) {
        bizlog.method("doDesEncrypt begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sEncryptText [%s]", sEncryptText);
        bizlog.parm("sEncryptKey [%s]", sEncryptKey);
        try{
            String sRet = doEncrypt(sEncryptText, sEncryptKey);
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("doDesEncrypt end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }
        catch (Exception e) {
            bizlog.method("doDesEncrypt end <<<<<<<<<<<<<<<<<<<<");
            throw ApBaseErr.ApBase.E0050(e.getMessage(),e);
        }
    }

    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:16:34</li>
     *         <li>功能说明：解密操作实现</li>
     *         </p>
     * @param encryptText
     * @param desKey
     * @return 解码后的字节数组
     * @throws Exception
     */
    public static byte[] decryptDES(byte[] encryptText, byte[] desKey) throws Exception {
        bizlog.method("decrypt_DES begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("encryptText [%s], desKey[%s]", encryptText, desKey);

        /* 用某种方法获取原始密匙数据 */
        byte[] rawKeyData = desKey;
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 现在，获取数据并解密
        byte[] encryptedData = encryptText;/* 获得经过解密的数据 */
        // 正式执行解密操作
        byte[] decryptedData = cipher.doFinal(encryptedData);

        bizlog.parm("decryptedData [%s]", decryptedData);
        bizlog.method("decrypt_DES end <<<<<<<<<<<<<<<<<<<<");
        return decryptedData;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:18:17</li>
     *         <li>功能说明：解密</li>
     *         </p>
     * @param encryptText
     * @param desKey
     * @return 解码后的字节数组
     * @throws Exception
     */
    private static byte[] decryptTDES(byte[] encryptText, byte[] desKey) throws Exception {
        bizlog.method("decrypt_TDES begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("encryptText, desKey", encryptText, desKey);
        /* 用某种方法获取原始密匙数据 */
        byte[] rawKeyData = new byte[24];
        if (desKey.length == 16) {
            System.arraycopy(desKey, 0, rawKeyData, 0, 16);
            System.arraycopy(desKey, 0, rawKeyData, 16, 8);
        }
        else {
            rawKeyData = desKey;
        }
        SecretKeySpec key = new SecretKeySpec(rawKeyData, 0, 24, "DESede");
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 正式执行解密操作
        byte[] decryptedData = cipher.doFinal(encryptText);
        bizlog.parm("decryptedData [%s]", decryptedData);
        bizlog.method("decrypt_TDES end <<<<<<<<<<<<<<<<<<<<");
        return decryptedData;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:20:17</li>
     *         <li>功能说明：解密</li>
     *         </p>
     * @param encryptText
     * @param encryptKey
     * @return 解码后的字节数组
     * @throws Exception
     */
    public static byte[] doDecrypt(byte[] encryptText, byte[] encryptKey) throws Exception {
        bizlog.method("doDecrypt begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm(" encryptText [%s],  encryptKey [%s]", encryptText, encryptKey);
        byte[] dataBin;
        char keyLength = getKeyLength(encryptKey);
        if (keyLength == 'Z') {
            dataBin = decryptDES(encryptText, encryptKey);
        }
        else {
            dataBin = decryptTDES(encryptText, encryptKey);
        }
        bizlog.parm("dataBin [%s]", dataBin);
        bizlog.method("doDecrypt end <<<<<<<<<<<<<<<<<<<<");
        return dataBin;
    }
    
    /**
     * @Author <p>
     *         <li>2016年1月5日-下午6:19:31</li>
     *         <li>功能说明：解密</li>
     *         </p>
     * @param encryptText
     * @param encryptKey
     * @return 解码后的字符串
     * @throws Exception
     */
    private static String doDecrypt(String encryptText, String encryptKey) throws Exception {
        bizlog.method("doDecrypt begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm(" encryptText [%s],  encryptKey [%s]", encryptText, encryptKey);
        byte[] dataBin = doDecrypt(ascToBin(encryptText), ascToBin(encryptKey));

        bizlog.method("doDecrypt end <<<<<<<<<<<<<<<<<<<<");
        return binToAsc(dataBin);
    }
    
    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:38:58</li>
     *         <li>功能说明：DES解密处理</li>
     *         </p>
     * @param sEncryptText
     *            被解密数据
     * @param sEncryptKey
     *            密钥
     * @return DES解密后数据
     */
    public static String doDesDecrypt(String sEncryptText, String sEncryptKey) {
        bizlog.method("doDesDecrypt begin >>>>>>>>>>>>>>>>>>>>");
        bizlog.parm("sEncryptText [%s]", sEncryptText);
        bizlog.parm("sEncryptKey [%s]", sEncryptKey);
        try {
            String sRet = doDecrypt(sEncryptText, sEncryptKey);
            bizlog.parm("sRet [%s]", sRet);
            bizlog.method("doDesDecrypt end <<<<<<<<<<<<<<<<<<<<");
            return sRet;
        }
        catch (Exception e) {
            bizlog.method("doDesDecrypt end <<<<<<<<<<<<<<<<<<<<");
            throw ApBaseErr.ApBase.E0051(e.getMessage(), e);
        }
    }
    /**
     * @Author Elton
     *         <p>
     *         <li>2016年1月6日-上午10:53:00</li>
     *         <li>功能说明：获取安全组件接口对象</li>
     *         </p>
     * @return 安全组件接口对象
     */
    private static ApBaseComp.Security getSecurityInstance() {
        bizlog.method("getSecurityInstance begin >>>>>>>>>>>>>>>>>>>>");
        ApBaseComp.Security security = SysUtil.getInstance(ApBaseComp.Security.class, AbstractComponent.AbsSecurity);
        bizlog.parm("security [%s]", security);
        bizlog.method("getSecurityInstance end <<<<<<<<<<<<<<<<<<<<");
        return security;
    }
    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年3月1日-上午11:19:28</li>
     *         <li>功能说明：DES加密</li>
     *         </p>
     * @param key
     *            密钥
     * @param data
     *            待加密的数据
     * @return 密文
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] desEncrypt(byte[] key, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey seckey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, seckey, sr);

        // 正式执行加密操作
        return cipher.doFinal(data);
    }

    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年3月1日-上午11:19:28</li>
     *         <li>功能说明：DES解密</li>
     *         </p>
     * @param key
     *            密钥
     * @param data
     *            待解密的数据
     * @return 明文
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] desDecrypt(byte[] key, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey seckey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, seckey, sr);

        // 正式执行解密操作
        return cipher.doFinal(data);
    }

    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年3月1日-上午11:19:28</li>
     *         <li>功能说明：静态加密</li>
     *         </p>
     * @param data
     *            待加密的数据
     * @return 加密结果
     */
    public static byte[] staticEncrypt(byte[] data) {
        return data;
    }

    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年3月1日-上午11:19:28</li>
     *         <li>功能说明：静态解密</li>
     *         </p>
     * @param data
     *            待解密的数据
     * @return 解密结果
     */
    public static byte[] staticDecrypt(byte[] data) {
        return data;
    }

    /**
     * 
     * @Author T 
     *         <p>
     *         <li>2017年7月14日-上午10:34:46</li>
     *         <li>我们将byte[]转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。</li>
     *         </p>
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }
    
    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年3月24日-上午10:16:28</li>
     *         <li>功能说明：生成des密钥</li>
     *         </p>
     * @param
     * @return DES密钥(String)
     * @throws NoSuchAlgorithmException
     */
    public static byte[] genDesKey() throws NoSuchAlgorithmException {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 为我们选择的DES算法生成一个KeyGenerator对象
        KeyGenerator kg = KeyGenerator.getInstance("DES");

        kg.init(sr);

        // 生成密匙
        SecretKey key = kg.generateKey();

        // 获取密匙数据
        byte rawKeyData[] = key.getEncoded();

        return bytesToHexString(rawKeyData).getBytes();
    }

    /**
     * @Author Tiantl
     *         <p>
     *         <li>2016年3月24日-上午10:16:28</li>
     *         <li>功能说明：生成des密钥</li>
     *         </p>
     * @param enckey
     *            加密密钥（机密生成的密钥）
     * @return enckey加密后的DES密钥(String)
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public static byte[] genDesKey(byte[] enckey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] deskey = genDesKey();
        return desEncrypt(enckey, deskey);
    }
    
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            
        }
        return d;
    }
    
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    
    /** 
     * 加密方法 
     *  
     * @param keybyte 
     *            加密密钥，长度为24字节 
     * @param src 
     *            被加密的数据缓冲区（源） 
     * @return 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */  
    public static byte[] threeDESEncrypt(byte[] keybyte, byte[] src) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {  
        return do3DES(keybyte,src,Cipher.ENCRYPT_MODE);
    }  
  
    /** 
     * 解密 
     *  
     * @param keybyte 
     *            加密密钥，长度为24字节 
     * @param src 
     *            加密后的缓冲区 
     * @return 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */  
    public static byte[] threeDESDecrypt(byte[] keybyte, byte[] src) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {  
        return do3DES(keybyte,src,Cipher.DECRYPT_MODE);
    }
    
    private static byte[] do3DES(byte[] keybyte, byte[] src, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        // 生成密钥  
        SecretKey deskey = new SecretKeySpec(keybyte, "DESede");  

        // 解密  
        Cipher c1 = Cipher.getInstance("DESede");  
        c1.init(mode, deskey);  
        return c1.doFinal(src);
    }
    
    /**
     * AES加密
     * @param keybyte
     * @param src
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static byte[] AESEncrypt(byte[] keybyte, byte[] src) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
    	 return doAES(keybyte,src,Cipher.ENCRYPT_MODE);   	
    }
    
    /**
     * AES解密
     * @param keybyte
     * @param src
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public static byte[] AESDecrypt(byte[] keybyte, byte[] src) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{

        return doAES(keybyte,src,Cipher.DECRYPT_MODE); 
    	
    }
    
    private static byte[] doAES(byte[] keybyte, byte[] src, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
    	KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(keybyte));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);
        return cipher.doFinal(src); 
    }
    
}
