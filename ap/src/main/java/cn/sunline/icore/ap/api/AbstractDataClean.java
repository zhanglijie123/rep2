package cn.sunline.icore.ap.api;

/**
 * 
 * <p>
 * 文件功能说明：
 * 数据清理业务处理抽象类      			
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2018年4月23日-下午3:52:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年4月23日-lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public abstract class AbstractDataClean {
	
	/** 数据保留天数 */
	private Long dataReserveDays = 0L;

	/** 表名*/
	private String tableName;


	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2018年4月23日-下午5:15:06</li>
	 *         <li>功能说明：删除实现</li>
	 *         </p>
	 */
	public abstract void process();
	
	
	public Long getDataReserveDays() {
		return dataReserveDays;
	}

	public void setDataReserveDays(Long dataReserveDays) {
		this.dataReserveDays = dataReserveDays;
	}

    public String getTableName() {
        return tableName;
    }


    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
