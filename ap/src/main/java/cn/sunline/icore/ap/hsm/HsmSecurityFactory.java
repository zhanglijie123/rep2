package cn.sunline.icore.ap.hsm;

/**
 * 
 * <p>
 * 功能说明：
 *   安全管理工厂，方便后续扩展    			
 * </p>
 * 
 * @Author heyong
 *         <p>
 *         <li>2017年5月3日-下午5:07:26</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年5月3日 heyong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HsmSecurityFactory {

    private static HsmSecurityFactory instance = new HsmSecurityFactory();
    private HsmSecurity hsmSecurity = null;
    
    private HsmSecurityFactory() {
        hsmSecurity = new GdrcbKeYouHsmSecurityImpl();
    }
    
    public static HsmSecurityFactory getInstance() {
        return instance;
    }
    
    public HsmSecurity getHsmSecurity() {
        return hsmSecurity;
    }
}
