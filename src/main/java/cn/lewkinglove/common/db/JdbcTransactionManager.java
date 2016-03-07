package cn.lewkinglove.common.db;

import java.sql.Connection;
import java.sql.SQLException;

import cn.lewkinglove.common.util.LogUtil;

/**
 * 数据库连接管理器
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class JdbcTransactionManager {

	private JdbcTransactionManager() {
	}

	/**
	 * 当前线程持有的数据库连接
	 */
	private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

	/**
	 * 当前线程事务深度
	 */
	private static final ThreadLocal<Integer> transactionDepth = new ThreadLocal<Integer>();

	/**
	 * 从当前线程中获取数据库连接
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return connectionHolder.get();
	}

	/**
	 * 判断数据库连接是否是所在线程事务连接
	 * @param conn
	 * @return
	 */
	public static boolean isTxConnection(Connection conn) {
		return conn == connectionHolder.get();
	}

	/**
	 * 当前线程是否存在事务
	 * @return
	 */
	public static boolean isInTransaction() {
		return connectionHolder.get() != null;
	}

	/**
	 * 获取当前线程事务深度
	 * @return
	 */
	private static int getTransactionDepth() {
		if (transactionDepth.get() == null)
			return 0;
		return transactionDepth.get();
	}

	/**
	 * 开启事务(默认数据源)
	 * @return
	 */
	public static Connection beginTransaction() {
		return beginTransaction(DataSourcePool.DEFAULT_DS_ID);
	}

	/**
	 * 开启指定数据源的事务
	 * @param dsId
	 *            数据源标识
	 * @return
	 */
	public static Connection beginTransaction(String dsId) {
		Connection conn = null;
		try {
			conn = getConnection();
			if (conn == null) {
				conn = DataSourcePool.getMasterDataSource(dsId).getConnection();
				conn.setAutoCommit(false);
				connectionHolder.set(conn);
			}
			increaseTransactionDepth();
		} catch (SQLException e) {
			LogUtil.error(JdbcTransactionManager.class, "beginTransaction:数据库连接池获取连接出错", e);
		}
		return conn;
	}

	/**
	 * 递增当前事务深度
	 */
	private static void increaseTransactionDepth() {
		transactionDepth.set(getTransactionDepth() + 1);
	}

	/**
	 * 递减当前事务深度
	 */
	private static void decreaseTransactionDepth() {
		transactionDepth.set(getTransactionDepth() - 1);
	}

	/**
	 * 最外层事务
	 * @return
	 */
	private static boolean isLastTxDepth() {
		return getTransactionDepth() == 0;
	}

	/**
	 * 提交事务(不关闭数据库连接)
	 * @throws SQLException
	 * @date 2015年8月4日 上午10:03:17
	 */
	public static void commit() throws SQLException {
		commit(false);
	}

	/**
	 * 提交事务
	 * @param needCloseConn
	 *            是否关闭数据库连接
	 * @throws SQLException
	 */
	public static void commit(boolean needCloseConn) throws SQLException {
		decreaseTransactionDepth();
		if (!isLastTxDepth())
			return;
		Connection conn = getConnection();
		if (conn != null) {
			try {
				conn.commit();
				JdbcTransactionManager.resetTXDepth();
			} catch (SQLException e) {
				LogUtil.error(JdbcTransactionManager.class, "commit出错", e);
			} finally {
				if (needCloseConn) {
					closeConnection(conn);
				}
			}
		}
	}

	/**
	 * 回滚事务(不关闭数据库连接)
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		rollback(false);
	}

	/***
	 * 回滚事务
	 * @param needCloseConn
	 *            是否关闭数据库连接
	 * @throws SQLException
	 */
	public static void rollback(boolean needCloseConn) throws SQLException {
		/**
		 * 任何时候只要本方法被调用
		 * 则数据库事务将一定被回滚, 不考虑事务嵌套深度
		 */
		if(getTransactionDepth() == 0){
			throw new SQLException("当前事务已经回滚");
		}
		
		Connection conn = getConnection();
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				LogUtil.error(JdbcTransactionManager.class, "rollback出错", e);
			} finally {
				if (needCloseConn) {
					closeConnection(conn);
				}
			}
		}
	}

	/**
	 * 关闭当前数据库连接
	 * @throws SQLException
	 */
	public static void closeConnection() throws SQLException {
		Connection conn = getConnection();
		JdbcTransactionManager.closeConnection(conn);
	}
	
	/**
	 * 关闭关闭指定数据库连接
	 * @throws SQLException
	 */
	public static void closeConnection(Connection conn) throws SQLException {
		if (conn == null) 
			return ;
		
		if (!isLastTxDepth())
			return;

		try {
			conn.close();
			connectionHolder.remove();
		} catch (SQLException e) {
			LogUtil.error(JdbcTransactionManager.class, "close connection error", e);
		}
	}

	/**
	 * 重置(清除)事务深度
	 */
	private static void resetTXDepth(){
		transactionDepth.remove();
	}
}
