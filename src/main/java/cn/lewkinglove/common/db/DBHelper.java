package cn.lewkinglove.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import cn.lewkinglove.common.db.exception.DataSourceNotFoundException;
import cn.lewkinglove.common.util.LogUtil;
import cn.lewkinglove.common.util.StringUtil;
import cn.lewkinglove.gtl.GTLTransactionPool;

/**
 * JDBC查询数据辅助类
 * 
 * @param <T>
 *            具体数据库映射模型
 * @author liujing(lewkinglove@gmail.com)
 */
public class DBHelper<T> {

	/**
	 * 获取主库链接(使用默认数据源)
	 * 
	 * @return 主库链接
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static Connection getMasterConnection() throws SQLException, DataSourceNotFoundException {
		return getMasterConnection(DataSourcePool.DEFAULT_DS_ID);
	}

	/**
	 * 获取指定数据源的主库链接
	 * 
	 * @param dsId
	 *            数据源标识
	 * @return 主库链接
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static Connection getMasterConnection(String dsId) throws SQLException, DataSourceNotFoundException {
		if (JdbcTransactionManager.isInTransaction())
			return JdbcTransactionManager.getConnection();

		DataSource ds = DataSourcePool.getMasterDataSource(dsId);
		if (ds == null)
			throw new DataSourceNotFoundException("数据源标识[" + dsId + "]指向的数据源不存在");

		return ds.getConnection();
	}

	/**
	 * 获取从库数据库链接(使用默认数据源)
	 * 
	 * @return 从库链接
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static Connection getSlaveConnection() throws SQLException, DataSourceNotFoundException {
		return getSlaveConnection(DataSourcePool.DEFAULT_DS_ID);
	}

	/**
	 * 获取指定数据源的从库数据库链接
	 * 
	 * @param dsId
	 *            数据源标识
	 * @return 从库链接
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static Connection getSlaveConnection(String dsId) throws SQLException, DataSourceNotFoundException {
		if (JdbcTransactionManager.isInTransaction())
			return JdbcTransactionManager.getConnection();

		DataSource ds = DataSourcePool.getSlaveDataSource(dsId);
		if (ds == null)
			throw new DataSourceNotFoundException("数据源标识[" + dsId + "]指向的数据源不存在");

		return ds.getConnection();
	}

	/**
	 * 释放指定的服务器资源
	 * 
	 * @param rs
	 *            结果集
	 * @param stat
	 *            语句执行对象
	 * @param conn
	 *            连接对象
	 * @throws SQLException
	 */
	public static void disposeDBSource(ResultSet rs, PreparedStatement ps, Connection conn) throws SQLException {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				throw e;
			} finally {
				try {
					if (conn != null && !JdbcTransactionManager.isTxConnection(conn) // 非JDBC全局事务链接
					        && !GTLTransactionPool.isTXConnection(conn) // 非GTL事务链接
					)
						conn.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * 为PreparedStatement填充参数(非命名参数处理)
	 * 
	 * @param ps
	 *            PreparedStatement对象
	 * @param args
	 *            不定项参数列表
	 * @throws SQLException
	 */
	private static void fillSQLParameter(PreparedStatement ps, Object... args) throws SQLException {
		if (args == null || args.length < 1)
			return;
		for (int i = 0; i < args.length; i++) {
			ps.setObject(i + 1, args[i]);
		}
	}

	/**
	 * 为SQL语句填充参数数据(命名参数处理)
	 * 
	 * @param sql
	 *            要填充的SQL
	 * @param args
	 *            命名参数列表和参数数值
	 * @return 填充好的sql语句
	 */
	private static String fillSQLParameter(String sql, Map<String, Object> args) {
		if (args == null || args.size() < 1)
			return sql;
		StringBuilder result = new StringBuilder(sql);
		Iterator<Map.Entry<String, Object>> entries = args.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Object> entry = entries.next();
			StringUtil.replace(result, entry.getKey(), escapeSQLParam(entry.getValue()));
		}
		return result.toString();
	}

	/**
	 * 过滤用于SQL的命名参数数值
	 * 
	 * @param param
	 *            数值
	 * @return 过滤后的值
	 */
	private static String escapeSQLParam(Object param) {
		String result = null;
		if (param instanceof String)
			result = "'" + StringUtil.mysqlEscape(param.toString(), true) + "'";
		else
			result = param.toString();
		return result;
	}

	/**
	 * 执行增删改语句,使用默认connection,参数为map结构
	 * 
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            命名参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		return executeNonQuery(null, null, sql, args);
	}

	/**
	 * 执行增删改语句,使用默认connection,参数为map结构
	 * 
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            命名参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(GeneratedKey refAutoId, String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		return executeNonQuery(null, refAutoId, sql, args);
	}

	/**
	 * 执行增删改语句,指定connection,参数为map结构
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            命名参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(Connection conn, String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		return executeNonQuery(conn, null, sql, args);
	}

	/**
	 * 执行增删改语句,指定connection,参数为map结构
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            命名参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(Connection conn, GeneratedKey refAutoId, String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeNonQuery(conn, refAutoId, sql);
	}

	/**
	 * 执行增删改语句,使用默认connection,参数为不固定参数
	 * 
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            占位参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeNonQuery((Connection) null, sql, args);
	}

	/**
	 * 执行增删改语句,使用默认connection,参数为不固定参数
	 * 
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            占位参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(GeneratedKey refAutoId, String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeNonQuery(null, refAutoId, sql, args);
	}

	/**
	 * 执行增删改语句,指定connection,参数为不固定参数
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            占位参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(Connection conn, String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeNonQuery(conn, null, sql, args);
	}

	/**
	 * 执行增删改语句,指定connection,参数为不固定参数
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param sql
	 *            SQL语句
	 * @param args
	 *            占位参数
	 * @return 本次操作影响的行数. 失败为-1
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static int executeNonQuery(Connection conn, GeneratedKey refAutoId, String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		if (conn == null)
			conn = getMasterConnection();
		PreparedStatement ps = null;
		int res = -1;
		try {
			LogUtil.info(DBHelper.class, sql + "\t 参数:" + Arrays.toString(args));
			ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			fillSQLParameter(ps, args);
			res = ps.executeUpdate();

			// 填充自增Id参数
			if (refAutoId != null) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next())
					refAutoId.setValue(rs.getObject(1));
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			disposeDBSource(null, ps, conn);
		}
		return res;
	}

	/**
	 * 执行返回单行单列值的查询,使用默认connection,参数为map结构
	 * 
	 * @param sql
	 *            SQL
	 * @param args
	 *            命名参数
	 * @return 单行结果
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static String executeScalarQuery(String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		Connection conn = null;
		sql = fillSQLParameter(sql, args);
		return executeScalarQuery(conn, sql);
	}

	/**
	 * 执行返回单行单列值的查询,指定connection,参数为map结构
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            SQL
	 * @param args
	 *            命名参数
	 * @return 单行结果
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static String executeScalarQuery(Connection conn, String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeScalarQuery(conn, sql);
	}

	/**
	 * 执行返回单行单列值的查询,使用默认connection,参数为不固定参数
	 * 
	 * @param sql
	 * @param args
	 *            SQL参数
	 * @return 单行结果
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static String executeScalarQuery(String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeScalarQuery(null, sql, args);
	}

	/**
	 * 执行返回单行单列值的查询,指定connection,参数为不固定参数
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            SQL
	 * @param args
	 *            SQL参数
	 * @return 单行结果
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static String executeScalarQuery(Connection conn, String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		if (conn == null)
			conn = getSlaveConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String res = null;
		try {
			LogUtil.info(DBHelper.class, sql + "\t 参数:" + Arrays.toString(args));
			ps = conn.prepareStatement(sql);
			fillSQLParameter(ps, args);
			rs = ps.executeQuery();
			if (rs.next()) { // 如果有值的话
				res = rs.getString(1); // 获取第一行第一列的值
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			disposeDBSource(rs, ps, conn);
		}
		return res;
	}

	/**
	 * 执行查询, 返回已封装的对象,使用默认connection,参数为map结构
	 * 
	 * @param sql
	 *            SQL
	 * @param mapper
	 *            映射器
	 * @param args
	 *            命名参数
	 * @return 对象
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public T executeObjectQuery(String sql, IRowMaper<T> mapper, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeObjectQuery(null, sql, mapper);
	}

	/**
	 * 执行查询, 返回已封装的对象,指定connection,参数为map结构
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            SQL
	 * @param mapper
	 *            映射器
	 * @param args
	 *            命名参数
	 * @return 对象
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public T executeObjectQuery(Connection conn, String sql, IRowMaper<T> mapper, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeObjectQuery(conn, sql, mapper);
	}

	/**
	 * 执行查询, 返回已封装的对象,使用默认connection,参数为不固定参数
	 * 
	 * @param sql
	 * @param mapper
	 *            映射器
	 * @param args
	 *            SQL参数
	 * @return 具体的实体类
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public T executeObjectQuery(String sql, IRowMaper<T> mapper, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeObjectQuery(null, sql, mapper, args);

	}

	/**
	 * 执行查询, 返回已封装的对象,指定connection,参数为不固定参数
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            SQL
	 * @param mapper
	 *            映射器
	 * @param args
	 *            SQL参数
	 * @return 对象
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public T executeObjectQuery(Connection conn, String sql, IRowMaper<T> mapper, Object... args) throws SQLException, DataSourceNotFoundException {
		if (conn == null)
			conn = getSlaveConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		T res = null;
		try {
			LogUtil.info(this.getClass(), sql + "\t 参数:" + Arrays.toString(args));
			ps = conn.prepareStatement(sql);
			fillSQLParameter(ps, args);
			rs = ps.executeQuery();
			while (rs.next()) {
				res = mapper.mappingRow(rs);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			disposeDBSource(rs, ps, conn);
		}
		return res;
	}

	/**
	 * 执行查询, 返回一封装的集合,使用默认connection,参数为map结构
	 * 
	 * @param mapper
	 *            映射器
	 * @param sql
	 *            SQL
	 * @param args
	 *            命名参数
	 * @return 集合
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public List<T> executeListQuery(String sql, IRowMaper<T> mapper, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeListQuery(null, sql, mapper);
	}

	/**
	 * 执行查询, 返回一封装的集合,指定connection,参数为map结构
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param mapper
	 *            映射器
	 * @param sql
	 *            SQL
	 * @param args
	 *            命名参数
	 * @return 集合
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public List<T> executeListQuery(Connection conn, String sql, IRowMaper<T> mapper, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeListQuery(conn, sql, mapper);
	}

	/**
	 * 执行查询, 返回一封装的集合,使用默认connection,参数为不固定参数
	 * 
	 * @param mapper
	 *            映射器
	 * @param sql
	 * @param args
	 *            SQL参数
	 * @return
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public List<T> executeListQuery(String sql, IRowMaper<T> mapper, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeListQuery(null, sql, mapper, args);
	}

	/**
	 * 执行查询, 返回一封装的集合,指定connection,参数为不固定参数
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param mapper
	 *            映射器
	 * @param sql
	 *            SQL
	 * @param args
	 *            SQL参数
	 * @return 集合
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public List<T> executeListQuery(Connection conn, String sql, IRowMaper<T> mapper, Object... args) throws SQLException, DataSourceNotFoundException {
		if (conn == null)
			conn = getSlaveConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = null;
		try {
			LogUtil.info(this.getClass(), sql + "\t 参数:" + Arrays.toString(args));
			ps = conn.prepareStatement(sql);
			fillSQLParameter(ps, args);
			rs = ps.executeQuery();
			list = new ArrayList<T>();
			while (rs.next()) {
				list.add(mapper.mappingRow(rs));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			disposeDBSource(rs, ps, conn);
		}
		return list;
	}

	/**
	 * 查询没有实体的结果集,使用默认connection,参数为map结构
	 * 
	 * @param sql
	 *            需要执行的sql语句
	 * @param args
	 *            命名参数
	 * @return 集合保存多行, Map保存一行的记录
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static List<HashMap<String, Object>> executeFreeQuery(String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		Connection conn = null;
		sql = fillSQLParameter(sql, args);
		return executeFreeQuery(conn, sql);
	}

	/**
	 * 查询没有实体的结果集,指定connection,参数为map结构
	 * 
	 * @param conn
	 *            数据链接,不能为空
	 * @param sql
	 *            需要执行的sql语句
	 * @param args
	 *            命名查询参数
	 * @return 集合保存多行, Map保存一行的记录
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static List<HashMap<String, Object>> executeFreeQuery(Connection conn, String sql, Map<String, Object> args) throws SQLException, DataSourceNotFoundException {
		sql = fillSQLParameter(sql, args);
		return executeFreeQuery(conn, sql);
	}

	/**
	 * 查询没有实体的结果集,使用默认connection,参数为不固定参数
	 * 
	 * @param sql
	 *            需要执行的sql语句
	 * @param args
	 *            sql查询参数
	 * @return 集合保存多行, Map保存一行的记录
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static List<HashMap<String, Object>> executeFreeQuery(String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		return executeFreeQuery(null, sql, args);
	}

	/**
	 * 查询没有实体的结果集,指定connection,参数为不固定参数
	 * 
	 * @param conn
	 *            数据库链接对象
	 * @param sql
	 *            需要执行的sql语句
	 * @param args
	 *            sql查询参数
	 * @return 集合保存多行, Map保存一行的记录
	 * @throws SQLException
	 * @throws DataSourceNotFoundException
	 */
	public static List<HashMap<String, Object>> executeFreeQuery(Connection conn, String sql, Object... args) throws SQLException, DataSourceNotFoundException {
		if (conn == null)
			conn = getSlaveConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try {
			LogUtil.info(DBHelper.class, sql + "\t 参数:" + Arrays.toString(args));
			ps = conn.prepareStatement(sql);
			fillSQLParameter(ps, args);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = rsmd.getColumnName(i);
					map.put(columnName, rs.getObject(i));
				}
				list.add(map);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			disposeDBSource(rs, ps, conn);
		}
		return list;
	}

	/**
	 * 判断查询结果集中是否存在某列
	 * 
	 * @param rs
	 *            查询结果集
	 * @param columnName
	 *            列名
	 * @return true 存在; false 不存咋
	 */
	public static boolean isColumnExist(ResultSet rs, String columnName) {
		try {
			if (rs.findColumn(columnName) > 0)
				return true;
		} catch (Exception e) {

		}
		return false;
	}

}