package cn.sunline.icore.ap.util;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.sunline.edsp.dao.plugin.mybatis.MyBatisSqlProcessor;
import cn.sunline.ltts.base.util.LangUtil;
import cn.sunline.ltts.base.util.RunnableWithReturn;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;

/**
 * <p>
 * 文件功能说明：数据库相关操作
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2016年12月16日-上午9:11:09</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class DBUtil {

	private DBUtil(){}
	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月15日-下午6:38:59</li>
	 *         <li>功能说明：执行ddl</li>
	 *         <li>执行ddl的时候，慎重使用，执行ddl前后，数据库会自动提交事务</li>
     *         <li>该操作会在独立事务中执行，不用担心影响当前交易事务</li>
	 *         </p>
	 * @param ddl
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void executeDDL(final String ddl) {
		DaoUtil.executeInNewTransation(new RunnableWithReturn() {
			@Override
			public Object execute() {
				Statement st = null;
				try {
					st = LttsCoreBeanUtil.getDBConnectionManager().getConnection().createStatement();
					st.execute(ddl);
				}
				catch (Exception e) {
					throw LangUtil.wrapThrow("execute DDL error", e);
				}
				finally {
					try {
						if (st != null)
							st.close();
					}
					catch (SQLException e) {
						throw LangUtil.wrapThrow("close statement error", e);
					}
				}
				return null;
			}
		});
	}
	
	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月16日-上午10:04:59</li>
	 *         <li>功能说明：回滚当前事务</li>
	 *         </p>
	 */
	public static void rollBack() {
		LttsCoreBeanUtil.getDBConnectionManager().rollback();
	}
	
	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月16日-上午10:04:59</li>
	 *         <li>功能说明：提交当前事务</li>
	 *         </p>
	 */
	public static void commit() {
		LttsCoreBeanUtil.getDBConnectionManager().commit();
	}
	
	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月16日-上午10:04:59</li>
	 *         <li>功能说明：开启事务</li>
	 *         </p>
	 */
	public static void beginTransation() {
		LttsCoreBeanUtil.getDBConnectionManager().beginTransation();
	}
	
	/**
	 * @Author Administrator
	 *         <p>
	 *         <li>2015年4月8日-下午2:28:37</li>
	 *         <li>功能说明：调DAO操作返回sql接口</li>
	 *         </p>
	 * @param excuter
	 * @return
	 */
	public static List<String> retSqls(Runnable excuter) {
		List<String> sqls = null;
		try {
			MyBatisSqlProcessor.isRetSql.set(true);
			MyBatisSqlProcessor.isExecute.set(false);// 不执行，只返回对应SQL
			excuter.run();
			sqls = MyBatisSqlProcessor.retSqlSet.get();
		}
		catch (Exception e) {
			throw LangUtil.wrapThrow("DML generation failed", e);
		}
		finally {
			// 结束后设置回去
			MyBatisSqlProcessor.isRetSql.set(false);
			MyBatisSqlProcessor.isExecute.set(true);
			MyBatisSqlProcessor.retSqlSet.set(new ArrayList<String>());
		}
		return sqls;
	}
	
	/**
	 * 执行DML语句
	 * @param dml
	 */
	public static void executeDML(String dml) {
		try {
			Statement st = LttsCoreBeanUtil.getDBConnectionManager().getConnection().createStatement();
			st.execute(dml);
			st.close();
		}
		catch (Throwable e) {
			throw LangUtil.wrapThrow("DML failed to execute", e);
		}
	}
}
