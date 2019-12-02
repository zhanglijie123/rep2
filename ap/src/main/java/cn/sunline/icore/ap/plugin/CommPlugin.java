package cn.sunline.icore.ap.plugin;

import cn.sunline.icore.ap.rule.common.CommConfig;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.ltts.core.plugin.PluginSupport;

public class CommPlugin extends PluginSupport {
    private static final BizLog bizlog = BizLogUtil.getBizLog(CommPlugin.class);

    private static CommPlugin instance;
    private CommConfig conf;
    
    public static CommPlugin get(){
        return instance;
    }
    
    @Override
    public boolean initPlugin(){
        instance = this;
        
        LttsCoreBeanUtil.getConfigManagerFactory().create(CommConfig.class, "conf/common-config-manager.xml");
        conf = LttsCoreBeanUtil.getConfigManagerFactory().getDefaultConfigManager().getConfig(CommConfig.class);
        
        if(conf == null){
            bizlog.error("Cann't find the CommonConfig configuration!");
            return false;
        }
        return super.initPlugin();
    }
    
    public CommConfig getConf() {
        return conf;
    }

    public void setConf(CommConfig conf) {
        this.conf = conf;
    }
}
