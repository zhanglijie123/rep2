package cn.sunline.icore.ap.hsm;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.sunline.icore.ap.hsm.HsmSecurity;
import cn.sunline.icore.ap.hsm.HsmSecurityFactory;
import cn.sunline.icore.ap.plugin.CommPlugin;
import cn.sunline.icore.ap.rule.common.CommConfig;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.sys.type.EnumType.E_KEYTYPE;

public class HsmConfigTest extends UnitTest{
    @Test
    public void testHsmConfig(){
        Map<String, Object> commReq = new HashMap<String, Object>();
        commReq.put("busi_org_id", "0033");
        newCommReq(commReq);
        
        // 从配置文件中取出index
        CommConfig hsmConf = CommPlugin.get().getConf();
        String pinVerificationKey = hsmConf.getPwkIndex();
        String zonePinKey = hsmConf.getPpkIndex();
        
        HsmSecurity hsmSecurity = HsmSecurityFactory.getInstance().getHsmSecurity();
        hsmSecurity.checkPin(E_KEYTYPE.DES, "", "",  pinVerificationKey, zonePinKey, "");
    }
}
