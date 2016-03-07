package cn.lewkinglove.common.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.lewkinglove.common.db.exception.DataSourceNotFoundException;
import cn.lewkinglove.common.vo.PageResult;

/**
 * DAO通用基础实现类,封装了模型操作的通用方法<br>
 * 所有DAO类均需要继承自本类, 并实现剩余的抽象方法。
 * 
 * @param <T>
 *            具体数据库映射模型
 * @author liujing(lewkinglove@gmail.com)
 */
public abstract class BaseDao<T> extends DBHelper<T> implements IBaseDao<T>, IRowMaper<T> {

	@Override
	public boolean save(LinkedHashMap<String, Object> data) throws SQLException, DataSourceNotFoundException {
		return save((Connection) null, getTableName(), data);
	}

	@Override
	public boolean save(Connection conn, LinkedHashMap<String, Object> data) throws SQLException, DataSourceNotFoundException {
		return save(conn, getTableName(), data);
	}

	@Override
	public boolean save(String tableName, LinkedHashMap<String, Object> data) throws SQLException, DataSourceNotFoundException {
		return save((Connection) null, getTableName(), data);
	}

	@Override
	public boolean save(Connection conn, String tableName, LinkedHashMap<String, Object> data) throws SQLException, DataSourceNotFoundException {
		if (data == null || data.size() < 1)
			throw new IllegalArgumentException("用于保存的SQL键值对参数不能为NULL, 或为空。");

		StringBuilder sbColumns = new StringBuilder(); // {#COLUMNS}
		StringBuilder sbValues = new StringBuilder(); // {#VALUES}
		List<Object> arguments = new ArrayList<Object>(); // JDBC 占位参数值

		Iterator<Entry<String, Object>> itor = data.entrySet().iterator();
		while (itor.hasNext()) {
			Entry<String, Object> entry = itor.next();
			sbColumns.append('`').append(entry.getKey()).append("`, "); // 列名限定部分
			sbValues.append("?, "); // insert 语句 values部分占位参数
			arguments.add(entry.getValue()); // 添加列值和占位参数
		}

		int sbColumnsLength = sbColumns.length();
		sbColumns.delete(sbColumnsLength - 2, sbColumnsLength);

		int sbValuesLength = sbValues.length();
		sbValues.delete(sbValuesLength - 2, sbValuesLength);

		// 格式化生成SQL Insert语句
		String sql = INSERT_SQL_TMPL.replace("{#TABLE_NAME}", tableName).replace("{#COLUMNS}", sbColumns.toString()).replace("{#VALUES}", sbValues.toString());

		int num = executeNonQuery(conn, sql, arguments.toArray());
		return num == -1 ? false : true;
	}

	@Override
	public boolean save(GeneratedKey refAutoId, LinkedHashMap<String, Object> data) throws Exception {
		return save(null, refAutoId, getTableName(), data);
	}

	@Override
	public boolean save(Connection conn, GeneratedKey refAutoId, LinkedHashMap<String, Object> data) throws Exception {
		return save(conn, refAutoId, getTableName(), data);
	}

	@Override
	public boolean save(GeneratedKey refAutoId, String tableName, LinkedHashMap<String, Object> data) throws Exception {
		return save(null, refAutoId, getTableName(), data);
	}

	@Override
	public boolean save(Connection conn, GeneratedKey refAutoId, String tableName, LinkedHashMap<String, Object> data) throws Exception {
		if (data == null || data.size() < 1)
			throw new IllegalArgumentException("用于保存的SQL键值对参数不能为NULL, 或为空。");

		StringBuilder sbColumns = new StringBuilder(); // {#COLUMNS}
		StringBuilder sbValues = new StringBuilder(); // {#VALUES}
		List<Object> arguments = new ArrayList<Object>(); // JDBC 占位参数值

		Iterator<Entry<String, Object>> itor = data.entrySet().iterator();
		while (itor.hasNext()) {
			Entry<String, Object> entry = itor.next();
			sbColumns.append('`').append(entry.getKey()).append("`, "); // 列名限定部分
			sbValues.append("?, "); // insert 语句 values部分占位参数
			arguments.add(entry.getValue()); // 添加列值和占位参数
		}

		int sbColumnsLength = sbColumns.length();
		sbColumns.delete(sbColumnsLength - 2, sbColumnsLength);

		int sbValuesLength = sbValues.length();
		sbValues.delete(sbValuesLength - 2, sbValuesLength);

		// 格式化生成SQL Insert语句
		String sql = INSERT_SQL_TMPL.replace("{#TABLE_NAME}", tableName).replace("{#COLUMNS}", sbColumns.toString()).replace("{#VALUES}", sbValues.toString());

		int num = executeNonQuery(conn, refAutoId, sql, arguments.toArray());
		return num == -1 ? false : true;
	}

	@Override
	public int deleteByPk(long pk) throws Exception {
		return deleteByPk(null, pk);
	}

	@Override
	public int deleteByPk(String pk) throws Exception {
		return deleteByPk(null, pk);
	}

	@Override
	public int deleteByPk(Connection conn, long pk) throws Exception {
		return deleteByPk(conn, String.valueOf(pk));
	}

	@Override
	public int deleteByPk(Connection conn, String pk) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(getPkName(), pk);
		return deleteByPropertys(conn, args);
	}

	@Override
	public int deleteByProperty(String propertyName, Object propertyValue) throws Exception {
		return deleteByProperty(null, propertyName, propertyValue);
	}

	@Override
	public int deleteByProperty(Connection conn, String propertyName, Object propertyValue) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(propertyName, propertyValue);
		return deleteByPropertys(conn, args);
	}

	@Override
	public int deleteByPropertys(Map<String, Object> args) throws Exception {
		return deleteByPropertys(null, args);
	}

	@Override
	public int deleteByPropertys(Connection conn, Map<String, Object> propertys) throws Exception {
		List<Object> argments = new ArrayList<Object>();
		String sql = DELETE_FORM + getTableName() + WHERE + createWhereArguments(propertys, argments);
		return executeNonQuery(conn, sql, argments.toArray());
	}

	@Override
	public int updateByPk(long pk, String propertyName, Object propertyValue) throws Exception {
		return updateByPk(null, pk, propertyName, propertyValue);
	}

	@Override
	public int updateByPk(Connection conn, long pk, String propertyName, Object propertyValue) throws Exception {
		return updateByPk(conn, String.valueOf(pk), propertyName, propertyValue);
	}

	@Override
	public int updateByPk(String pk, String propertyName, Object propertyValue) throws Exception {
		return updateByPk(null, pk, propertyName, propertyValue);
	}

	@Override
	public int updateByPk(Connection conn, String pk, String propertyName, Object propertyValue) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(propertyName, propertyValue);
		return updateByPk(conn, pk, args);
	}

	@Override
	public int updateByPk(Long pk, Map<String, Object> args) throws Exception {
		return updateByPk(null, pk, args);
	}

	@Override
	public int updateByPk(Connection conn, Long pk, Map<String, Object> args) throws Exception {
		return updateByPk(conn, String.valueOf(pk), args);
	}

	@Override
	public int updateByPk(String pk, Map<String, Object> args) throws Exception {
		return updateByPk((Connection) null, pk, args);
	}

	@Override
	public int updateByPk(Connection conn, String pk, Map<String, Object> args) throws Exception {
		if (args == null || args.size() < 1)
			throw new IllegalArgumentException("用于updateByPk的更新参数不能为NULL或为空。");

		List<Object> argments = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder(UPDATE + getTableName() + SET).append(createUpdateArguments(args, argments)).append(WHERE + getPkName() + "= ?");
		argments.add(pk);
		return executeNonQuery(conn, sb.toString(), argments.toArray());
	}

	@Override
	public long getResultCount(Map<String, Object> queryConditons) throws Exception {
		return getResultCount(null, queryConditons);
	}

	@Override
	public long getResultCount(Connection conn, Map<String, Object> queryConditons) throws Exception {
		List<Object> arguments = new ArrayList<Object>();
		String sql = "SELECT COUNT(*) count FROM " + getTableName() + WHERE + createWhereArguments(queryConditons, arguments);
		return Long.parseLong(executeScalarQuery(conn, sql, arguments.toArray()));
	}

	@Override
	public T findByPk(long pk) throws Exception {
		return findByPk(null, String.valueOf(pk));
	}

	@Override
	public T findByPk(Connection conn, long pk) throws Exception {
		return findByPk(conn, String.valueOf(pk));
	}

	@Override
	public T findByPk(String pk) throws Exception {
		return findByPk(null, pk);
	}

	@Override
	public T findByPk(Connection conn, String pk) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(getPkName(), pk);
		List<T> result = find(conn, args, null, null, -1, -1);
		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public List<T> findAll() throws Exception {
		return find(null, null, null, null, -1, -1);
	}

	@Override
	public void findAll(PageResult<T> pageResult) throws Exception {
		long offset = pageResult.getFirstResult();
		long limit = pageResult.getPageSize();
		long totalResult = getResultCount(null);
		List<T> resultList = find(null, null, null, null, offset, limit);
		pageResult.setTotalResult(totalResult);
		pageResult.setResultList(resultList);
	}

	@Override
	public List<T> findByProperty(String propertyName, Object propertyValue) throws Exception {
		return findByProperty((Connection) null, propertyName, propertyValue, null, null);
	}

	@Override
	public List<T> findByProperty(Connection conn, String propertyName, Object propertyValue) throws Exception {
		return findByProperty(conn, propertyName, propertyValue, null, null);
	}

	@Override
	public List<T> findByProperty(String propertyName, Object propertyValue, String fields, String orderBy) throws Exception {
		return findByProperty((Connection) null, propertyName, propertyValue, fields, orderBy);
	}

	@Override
	public List<T> findByProperty(Connection conn, String propertyName, Object propertyValue, String fields, String orderBy) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(propertyName, propertyValue);
		return findByPropertys(conn, args, fields, orderBy);
	}

	@Override
	public List<T> findByPropertys(Map<String, Object> queryConditions) throws Exception {
		return findByPropertys((Connection) null, queryConditions, null, null);
	}

	@Override
	public List<T> findByPropertys(Connection conn, Map<String, Object> queryConditions) throws Exception {
		return findByPropertys(conn, queryConditions, null, null);
	}

	@Override
	public List<T> findByPropertys(Map<String, Object> queryConditions, String fields, String orderBy) throws Exception {
		return findByPropertys((Connection) null, queryConditions, fields, orderBy);
	}

	@Override
	public List<T> findByPropertys(Connection conn, Map<String, Object> queryConditions, String fields, String orderBy) throws Exception {
		List<Object> argments = new ArrayList<Object>();
		String sql = SELECT + (fields == null || fields.length() == 0 ? '*' : fields) + FROM + getTableName() + WHERE + createWhereArguments(queryConditions, argments) + (orderBy == null || orderBy.length() == 0 ? "" : ORDER_BY + orderBy);

		return executeListQuery(conn, sql, this, argments.toArray());
	}

	@Override
	public void findByProperty(PageResult<T> pageResult, String propertyName, Object propertyValue) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(propertyName, propertyValue);
		findByPropertys(pageResult, args, null, null);
	}

	@Override
	public void findByProperty(PageResult<T> pageResult, String propertyName, Object propertyValue, String fields, String orderBy) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(propertyName, propertyValue);
		findByPropertys(pageResult, args, fields, orderBy);
	}

	@Override
	public void findByPropertys(PageResult<T> pageResult, Map<String, Object> queryConditions) throws Exception {
		findByPropertys(pageResult, queryConditions, null, null);
	}

	@Override
	public void findByPropertys(PageResult<T> pageResult, Map<String, Object> queryConditions, String fields, String orderBy) throws Exception {
		long offset = pageResult.getFirstResult();
		long limit = pageResult.getPageSize();
		long totalResult = getResultCount(queryConditions);
		List<T> resultList = find(null, queryConditions, fields, orderBy, offset, limit);
		pageResult.setTotalResult(totalResult);
		pageResult.setResultList(resultList);
	}

	@Override
	public List<T> find(Connection conn, Map<String, Object> queryConditions, String fields, String orderBy, long offset, long limit) throws Exception {
		List<Object> arguments = new ArrayList<Object>();

		String sql = SELECT + (fields == null || fields.length() == 0 ? '*' : fields) + FROM + getTableName() + WHERE + createWhereArguments(queryConditions, arguments) + (orderBy == null || orderBy.length() == 0 ? "" : ORDER_BY + orderBy);
		if (limit > 0 && offset > -1) {
			sql = sql + " LIMIT ? OFFSET ? ";
			arguments.add(limit);
			arguments.add(offset);
		}
		return executeListQuery(conn, sql, this, arguments.toArray());
	}

	/**
	 * 将一个Map集合形式的键值对组装为JDBC SQL Where条件语句<br>
	 * 并通过引用返回用于JDBC执行的参数数组.
	 * 
	 * @param args
	 *            sql where条件键值对, 形如: uname='admin', upwd='1233123'
	 * @param arguments
	 *            用于JDBC执行的参数值数组
	 * @return JDBC SQL Where条件语句
	 */
	private String createWhereArguments(Map<String, Object> args, List<Object> arguments) {
		if (args == null || args.size() < 1) {
			return " 1 = 1 ";
		}
		StringBuilder sb = new StringBuilder();

		Set<String> propertySet = args.keySet();
		for (String property : propertySet) {
			sb.append(property);
			sb.append("=? AND ");
			arguments.add(args.get(property));
		}
		int length = sb.length();
		if (length > 3)
			sb.delete(length - 4, length);
		return sb.toString();
	}

	/**
	 * 将一个Map集合形式的键值对组装为JDBC SQL Where条件语句<br>
	 * 并通过引用返回用于JDBC执行的参数数组.
	 * 
	 * @param args
	 *            sql update键值对, 形如: age=1,male="男"
	 * @param arguments
	 *            用于JDBC执行的参数值数组
	 * @return JDBC SQL Where条件语句
	 */
	private String createUpdateArguments(Map<String, Object> args, List<Object> arguments) {
		if (args == null || args.size() < 1)
			throw new IllegalArgumentException("用于生成JDBC Update 语句的参数不能为NULL, 或为空。");
		StringBuilder sb = new StringBuilder();

		Set<String> propertySet = args.keySet();
		for (String property : propertySet) {
			sb.append(property);
			sb.append("=?, ");
			arguments.add(args.get(property));
		}
		int length = sb.length();
		if (length > 0)
			sb.delete(length - 2, length);
		return sb.toString();
	}
}
