package cn.sunline.icore.ap.util;

/**
 * <p>
 * 文件功能说明：全局常量定义
 * </p>
 * 
 * @Author jollyja
 *         <p>
 *         <li>2016年11月30日-下午4:42:22</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public interface ApConst {

	// 测试用途的全局变量
	public static final String TEST_CONST = "test";

	// 联机的系统标识
	public static final String XITONGBS_ONL = "onlApp";

	// 参数通配符
	public static final String WILDCARD = "*";

	// 缺省的最大日期
	public static final String DEFAULT_MAX_DATE = "20991231";

	// dataMart字段公共运行变量固定写法
	public static final String RUN_ENVS = "RUN_ENVS";
	
	// 客户化参数信息
	public static final String ADDITIONAL_DATA_MART = "ADDITIONAL";

	// 数据集在 MspDroplist 中的定义
	public static final String DATA_MART = "DATA_MART";

	// 客户信息集
	public static final String CUST_DATA_MART = "CUSTOMER";
	
	// 客户信息集
	public static final String BUSI_DATA_MART = "BUSINESS_TYPE";

	// 输入区数据集
	public static final String INPUT_DATA_MART = "INPUT";

	// 参数数据集
	public static final String PARM_DATA_MART = "PARM";

	// 货币代码数据集
	public static final String CURRENCY_DATA_MART = "CURRENCY";

	// 账户数据集
	public static final String ACCOUNT_DATA_MART = "ACCOUNT";

	// 子账户数据集
	public static final String SUB_ACCOUNT_DATA_MART = "SUBACCT";

	// 借据信息集
	public static final String LOAN_DATA_MART = "LOAN";

	// 卡数据集
	public static final String CARD_DATA_MART = "CARD";
	
	// 反欺诈卡数据集
	public static final String AFA_CARD_DATA_MART = "AFA_CARD";
	// 反欺客户数据集
	public static final String AFA_CUST_DATA_MART = "AFA_CUST";
	// 反欺诈统计量数据集
	public static final String AFA_PARA_DATA_MART = "AFA_PARA";
	// 反欺诈金融交易数据集
	public static final String AFA_TRXN_DATA_MART = "AFA_TRXN";
	
	//金融交易数据集
	public static final String TRXN_FINANCE_AFRAUD_MART = "TRXN_FINANCE_AFRAUD";

	// 连接多个key的连接符 key1-key2-key3
	public static final String KEY_CONNECTOR = "-";

	// 对私证件类型下拉字典
	public static final String PERSON_CERT_TYPE = "PERSON_CERT_TYPE";

	// 对公证件类型下拉字典
	public static final String CORPOR_CERT_TYPE = "CORPOR_CERT_TYPE";

	// 国别代码下拉字典
	public static final String COUNTRY_CODE = "COUNTRY_CODE";

	// 目录代码下拉字典
	public static final String DIR_CODE = "DIR_CODE";

	// 场景授权下拉字典
	public static final String SCENE_AUTH_ID = "SCENE_AUTH_ID";

	// 批量的系统编码
	public static final String SYSTEM_BATCH = "999";

	// 文件换行符
	public static final String FILE_LINEFEEDS = "\r\n";

	// 上下级包名分隔符 "."
	public static final String PACKAGE_SEPERATE_DOT = ".";

	// 非联机交易中执行的服务
	public static final String UN_ONLINE_NODE = "NULL";

	// 冲账事件
	public static final String REVERSAL_SUMMARY = "EC";

	// 更新限额事件id
	public static final String LIMIT_REVERSAL_EVENT = "apUpdateLimit";

	// 柜员加密解密类型
	public static final String TELLER_ENC_DEC_TYPE = "TELLER_ENC_DEC_TYPE";

	// 加密解密类型
	public static final String ENC_DEC_TYPE = "ENC_DEC_TYPE";

	// 动态下拉字典
	public static final String DYNAMIC_DROP_LIST = "DYNAMIC_DROP_LIST";

	// 核心摘要码
	public static final String CORE_SUMMARY_CODE = "CORE_SUMMARY_CODE";

	// 获取手续费摘要代码辅key
	public static final String CHRG_SUMMARY_SUB_KEY = "CHARGE";

	// day end transaction branch
	public static final String DAYEND_TRAN_BRANCH = "DAYEND_TRAN_BRANCH";

	// day end channel id
	public static final String DAYEND_CHANNEL_ID = "DAYEND_CHANNEL_ID";

	// day end transaction teller
	public static final String DAYEND_TRAN_TELLER = "DAYEND_TRAN_TELLER";

	// day end ontime switch
	public static final String ONTIME_DAYEND_SWITCH = "ONTIME_DAYEND_SWITCH";

	// receive or update threshold count
	public static final String UPGRADE_THRESHOILD_COUNT = "UPGRADE_THRESHOILD_COUNT";

	// UTF-8编码常量
	public static final String UTF_ENCORDING = "UTF-8";

	// 客户号
	public static final String CUST_NO = "cust_no";

	// 黑名单系统ID
	public static final String BLACK_LIST_SYSTEM_ID = "233";

	// TAS系统ID
	public static final String TAS_SYSTEM_ID = "112";

	// 获取文件对账本地目录
	public static final String RECON_UPLOAD_LOCALFILE = "RECON_FILE_LOCAL";

	// 获取文件对账远程目录
	public static final String RECON_UPLOAD_REMOTEFILE = "RECON_FILE_REMOTE";

	// 客户号校验位类型
	public static final String CUSTNO_CHECKBIT_TYPE = "1";

	// 日终信息main key
	public static final String DAYEND_DATA = "DAYEND_DATA";

	// 自动跑批开关
	public static final String DAYEND_TRAN_SWITCH = "DAYEND_TRAN_SWITCH";
	
	// 参数id流水在app_sequence中的定义
    public static final String PARM_ID_SEQ = "PARM_ID_SEQ";

    // 信息流水定义
    public static final String AP_MSG_SEQ_ID = "MSG_SEQ_ID";
    
    // AP模块公共流水定义
    public static final String AP_COMM_SEQ = "AP_COMM_SEQ";
    
    // 交易控制错误ID
    public static final String TRXNCTRL_ERROR_ID = "AP.E0068";
    
    // 数据操作id定义
    public static final String DATA_OPERATE_ID = "DATA_OPERATE_ID";
     
    // 数据同步批次号
    public static final String DATASYNC_BATCH_NO = "DATASYNC_BATCH_NO";
    
    // 数据清理主key
	public static final String DATACLEAN_MAINKEY = "AP_DATACLEAN";
	
	// 预设分区个数辅key
	public static final String PRESETPARTITION_SUBKEY = "PRESET_PARTITION_DAYS";
	
	// 默认语种
	public static final String DEFAULT_LANGUAGE = "en";
	
	// default character
    public static final String DEFAULT_CHARACTER = "utf-8";
    
    // sms otp address
    public static final String SMS_OTP_ADDRESS="AP_OTP_ADDRESS";
	
    // mail rule mappiing
    public static final String RULE_MAIL_TEMPLATE = "MAIL_TEMPLATE_CODE";
    
    // 获取下拉字典本地目录
  	public static final String DROPLIST_UPLOAD_LOCALFILE = "AP_DROPLIST_LOCAL";

  	// 获取下拉字典远程目录
  	public static final String DROPLIST_UPLOAD_REMOTEFILE = "AP_DROPLIST_REMOTE";
	
  	/** 文件的目录编码 */
	public static final String ACCURE_LOCAL_DIR_CODE = "CORE_ACCURE_LOCAL"; // 计提文件的本地目录
	
	public static final String ACCURE_REMOTE_DIR_CODE = "CORE_ACCURE_REMOTE"; // 计提文件的远程目录
																
	public static final String ACCOUNTING_EVENT_LOCAL_DIR_CODE = "CORE_ACCOUNTING_LOCAL"; // 会计事件的本地目录编码
																						
	public static final String ACCOUNTING_EVENT_REMOTE_DIR_CODE = "CORE_ACCOUNTING_REMOTE"; // 会计事件的远程目录编码
	
	public static final String CORE_CHECK_RECORD_LOCAL = "CORE_CHECK_RECORD_LOCAL"; // 会计事件的本地目录编码
								
	public static final String CORE_CHECK_RECORD_REMOTE = "CORE_CHECK_RECORD_REMOTE"; // 会计事件的远程目录编码
	
	public static final String CORE_LEDGER_BAL_LOCAL_DIR_CODE = "CORE_LEDGER_BAL_LOCAL"; // 分户账余额文件的本地目录编码
	
	public static final String CORE_LEDGER_BAL_REMOTE_DIR_CODE = "CORE_LEDGER_BAL_REMOTE"; // 分户账余额文件的远程目录编码
	
	public static final String CORE_EVENT_PARM_LOCAL_DIR_CODE = "CORE_EVENT_PARM_LOCAL"; // 科目修改文件的远程目录编码
	
	public static final String CORE_EVENT_PARM_REMOTE_DIR_CODE = "CORE_EVENT_PARM_REMOTE"; // 科目修改文件的远程目录编码
	
	/** 文件笔数限制 **/
	public static final String FILE_COUNT = "FILE_COUNT"; 
	
}
