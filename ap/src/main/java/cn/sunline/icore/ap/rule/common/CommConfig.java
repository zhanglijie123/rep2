package cn.sunline.icore.ap.rule.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cn.sunline.ltts.frw.model.annotation.Index;

@Index
@XmlRootElement(name = "CommonConfig")
public class CommConfig {
    
    private String hsmIp;
    private String hsmPort;
    private String cvkIndex;
    private String coreZKURL;
	private String iwkIndex;
    private String pwkIndex;
    private String ppkIndex;
    private String rsaIndex;
    private String adurl;
    private String adadminUser;
	private String adadminPwd;
    private String aduserAttr;
    private String adbase;
    private String aduserFilter;
    private String adconnTimeOut;
    private String embossFileEncryptKey;
    
    @XmlAttribute
    public String getCoreZKURL() {
        return coreZKURL;
    }
    public void setCoreZKURL(String coreZKURL) {
        this.coreZKURL = coreZKURL;
    }
    
    @XmlAttribute
    public String getHsmIp() {
        return hsmIp;
    }
    public void setHsmIp(String hsmIp) {
        this.hsmIp = hsmIp;
    }
    
    @XmlAttribute
    public String getHsmPort() {
        return hsmPort;
    }
    public void setHsmPort(String hsmPort) {
        this.hsmPort = hsmPort;
    }
    
    @XmlAttribute
    public String getCvkIndex() {
        return cvkIndex;
    }
    public void setCvkIndex(String cvkIndex) {
        this.cvkIndex = cvkIndex;
    }
    
    @XmlAttribute
    public String getIwkIndex() {
		return iwkIndex;
	}
	public void setIwkIndex(String iwkIndex) {
		this.iwkIndex = iwkIndex;
	}
	
	@XmlAttribute
	public String getPwkIndex() {
		return pwkIndex;
	}
	public void setPwkIndex(String pwkIndex) {
		this.pwkIndex = pwkIndex;
	}
	
	@XmlAttribute
	public String getPpkIndex() {
		return ppkIndex;
	}
	public void setPpkIndex(String ppkIndex) {
		this.ppkIndex = ppkIndex;
	}
	
	@XmlAttribute
	public String getRsaIndex() {
		return rsaIndex;
	}
	
	public void setRsaIndex(String rsaIndex) {
		this.rsaIndex = rsaIndex;
	}
	
	@XmlAttribute
	public String getAdurl() {
		return adurl;
	}
	public void setAdurl(String adurl) {
		this.adurl = adurl;
	}
	@XmlAttribute
    public String getAdadminUser() {
		return adadminUser;
	}
	public void setAdadminUser(String adadminUser) {
		this.adadminUser = adadminUser;
	}
	@XmlAttribute
	public String getAdadminPwd() {
		return adadminPwd;
	}
	public void setAdadminPwd(String adadminPwd) {
		this.adadminPwd = adadminPwd;
	}
	@XmlAttribute
	public String getAduserAttr() {
		return aduserAttr;
	}
	public void setAduserAttr(String aduserAttr) {
		this.aduserAttr = aduserAttr;
	}
	@XmlAttribute
	public String getAdbase() {
		return adbase;
	}
	public void setAdbase(String adbase) {
		this.adbase = adbase;
	}
	@XmlAttribute
	public String getAduserFilter() {
		return aduserFilter;
	}
	public void setAduserFilter(String aduserFilter) {
		this.aduserFilter = aduserFilter;
	}
	@XmlAttribute
	public String getAdconnTimeOut() {
		return adconnTimeOut;
	}
	public void setAdconnTimeOut(String adconnTimeOut) {
		this.adconnTimeOut = adconnTimeOut;
	}
	public String getEmbossFileEncryptKey() {
		return this.embossFileEncryptKey;
	}
	public void setEmbossFileEncryptKey(String embossFileEncryptKey) { this.embossFileEncryptKey = embossFileEncryptKey; }
	
	
}
