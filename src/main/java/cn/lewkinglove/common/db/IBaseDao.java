package cn.lewkinglove.common.db;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.lewkinglove.common.vo.PageResult;

/**
 * Dao层通用基础模块接口,封装了模型操作的通用方法
 * 
 * @param <T>
 *            具体数据库映射模型
 * @author liujing(lewkinglove@gmail.com)
 */
public interface IBaseDao<T> {

	/**
	 * sql查询的SELECT
	 */
	String SELECT = "SELECT ";
	/**
	 * sql查询的FROM
	 */
	String FROM = " FROM ";
	/**
	 * sql删除的DELETE FORM
	 */
	String DELETE_FORM = "DELETE FROM ";
	/**
	 * sql条件WHERE
	 */
	String WHERE = " WHERE ";
	/**
	 * sql更新UPDATE
	 */
	String UPDATE = "UPDATE ";
	/**
	 * sql设置SET
	 */
	String SET = " SET ";
	/**
	 * sql限制LIMIT
	 */
	String LIMIT = " LIMIT ? OFFSET ? ";
	/**
	 * sql排序ORDER_BY
	 */
	String ORDER_BY = " ORDER BY ";
	/**
	 * 用于数据插入的SQL语句模板
	 */
	String INSERT_SQL_TMPL = "INSERT INTO `{#TABLE_NAME}` ({#COLUMNS}) VALUES({#VALUES})";

	/**
	 * 获取当前模型对应的数据表主键列名
	 * 
	 * @return 主键名
	 */
	String getPkName();

	/**
	 * 获取当前模型对应的数据表名
	 * 
	 * @return 实体对应的表名
	 */
	String getTableName();

	/**
	 * 保存模型到数据库
	 * 
	 * @param model
	 *            需要存储的模型
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(T model) throws Exception;

	/**
	 * 保存指定键值对数据为当前模型的一条新记录
	 * 
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为当前模型的一条新记录
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(Connection conn, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为指定表名的数据表的一条新记录
	 * 
	 * @param tableName
	 *            要保存到的数据表名
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(String tableName, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为指定表名的数据表的一条新记录
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param tableName
	 *            要保存到的数据表名
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(Connection conn, String tableName, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为当前模型的一条新记录
	 * 
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(GeneratedKey refAutoId, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为当前模型的一条新记录
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(Connection conn, GeneratedKey refAutoId, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为指定表名的数据表的一条新记录
	 * 
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param tableName
	 *            要保存到的数据表名
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(GeneratedKey refAutoId, String tableName, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 保存指定键值对数据为指定表名的数据表的一条新记录
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param refAutoId
	 *            用来接收自增ID的对象
	 * @param tableName
	 *            要保存到的数据表名
	 * @param data
	 *            键值对数据, key=列名, value=列值
	 * @return 保存是否成功
	 * @throws Exception
	 */
	boolean save(Connection conn, GeneratedKey refAutoId, String tableName, LinkedHashMap<String, Object> data) throws Exception;

	/**
	 * 通过主键删除数据,主键为long类型
	 * 
	 * @param pk
	 *            主键值
	 * @return 成功删除的个数
	 */
	int deleteByPk(long pk) throws Exception;

	/**
	 * 通过主键删除数据,主键为String类型
	 * 
	 * @param pk
	 *            主键值
	 * @return 成功删除的个数
	 */
	int deleteByPk(String pk) throws Exception;

	/**
	 * 通过主键删除数据,主键为long类型
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @return 成功删除的个数
	 */
	int deleteByPk(Connection conn, long pk) throws Exception;

	/**
	 * 通过主键删除数据,主键为String类型
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @return 成功删除的个数
	 */
	int deleteByPk(Connection conn, String pk) throws Exception;

	/**
	 * 通过单个属性删除数据
	 * 
	 * @param propertyName
	 *            属性名
	 * @param propertyValue
	 *            属性值
	 * @return 成功删除的个数
	 */
	int deleteByProperty(String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过单个属性删除数据
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param propertyName
	 *            属性名
	 * @param propertyValue
	 *            属性值
	 * @return 成功删除的个数
	 */
	int deleteByProperty(Connection conn, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过多个属性删除数据
	 * 
	 * @param propertys
	 *            属性集合,key属性名,value属性值
	 * @return 成功删除的个数
	 */
	int deleteByPropertys(Map<String, Object> propertys) throws Exception;

	/**
	 * 通过多个属性删除数据
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param propertys
	 *            属性集合,key属性名,value属性值
	 * @return 成功删除的个数
	 */
	int deleteByPropertys(Connection conn, Map<String, Object> propertys) throws Exception;

	/**
	 * 通过主键更新模型的单一属性
	 * 
	 * @param pk
	 *            主键值
	 * @param propertyName
	 *            要更新的属性(列)名
	 * @param propertyValue
	 *            要更新的属性(列)值
	 * @return 成功更新的个数
	 */
	int updateByPk(long pk, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过主键更新模型的单一属性
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @param propertyName
	 *            要更新的属性(列)名
	 * @param propertyValue
	 *            要更新的属性(列)值
	 * @return 成功更新的个数
	 */
	int updateByPk(Connection conn, long pk, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过主键更新模型的单一属性
	 * 
	 * @param pk
	 *            主键值
	 * @param propertyName
	 *            要更新的属性(列)名
	 * @param propertyValue
	 *            要更新的属性(列)值
	 * @return 成功更新的个数
	 */
	int updateByPk(String pk, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过主键更新模型的单一属性
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @param propertyName
	 *            要更新的属性(列)名
	 * @param propertyValue
	 *            要更新的属性(列)值
	 * @return 成功更新的个数
	 */
	int updateByPk(Connection conn, String pk, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过主键更新模型的多个属性
	 * 
	 * @param pk
	 *            主键值
	 * @param args
	 *            要更新的字段键值对组合; key=列名, value=列值
	 * @return 成功更新的个数
	 * @throws Exception
	 */
	int updateByPk(Long pk, Map<String, Object> args) throws Exception;

	/**
	 * 通过主键更新模型的多个属性
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @param args
	 *            要更新的字段键值对组合; key=列名, value=列值
	 * @return 成功更新的个数
	 * @throws Exception
	 */
	int updateByPk(Connection conn, Long pk, Map<String, Object> args) throws Exception;

	/**
	 * 通过主键更新模型的多个属性
	 * 
	 * @param pk
	 *            主键值
	 * @param args
	 *            要更新的字段键值对组合; key=列名, value=列值
	 * @return 成功更新的个数
	 * @throws Exception
	 */
	int updateByPk(String pk, Map<String, Object> args) throws Exception;

	/**
	 * 通过主键更新模型的多个属性
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @param args
	 *            要更新的字段键值对组合; key=列名, value=列值
	 * @return 成功更新的个数
	 * @throws Exception
	 */
	int updateByPk(Connection conn, String pk, Map<String, Object> args) throws Exception;

	/**
	 * 获取当前查询条件中的总记录数量
	 * 
	 * @param queryConditons
	 *            查询所需参数和参数值
	 * @return 记录总量
	 * @throws Exception
	 */
	long getResultCount(Map<String, Object> queryConditons) throws Exception;

	/**
	 * 获取当前查询条件中的总记录数量
	 *
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param queryConditons
	 *            查询所需参数和参数值
	 * @return 记录总量
	 * @throws Exception
	 */
	long getResultCount(Connection conn, Map<String, Object> queryConditons) throws Exception;

	/**
	 * 根据主键获取模型
	 * 
	 * @param pk
	 *            主键值
	 * @return 模型
	 * @throws Exception
	 */
	T findByPk(long pk) throws Exception;

	/**
	 * 根据主键获取模型
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @return 模型
	 * @throws Exception
	 */
	T findByPk(Connection conn, long pk) throws Exception;

	/**
	 * 根据主键获取模型
	 * 
	 * @param pk
	 *            主键值
	 * @return 模型
	 * @throws Exception
	 */
	T findByPk(String pk) throws Exception;

	/**
	 * 根据主键获取模型
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param pk
	 *            主键值
	 * @return 模型
	 * @throws Exception
	 */
	T findByPk(Connection conn, String pk) throws Exception;

	/**
	 * 查找当前模型对应数据表内所有的数据
	 * 
	 * @return 集合
	 */
	List<T> findAll() throws Exception;

	/**
	 * 分页查找所有的数据
	 * 
	 * @param pageResult
	 *            包含分页信息的PageResult
	 * @return 集合
	 */
	void findAll(PageResult<T> pageResult) throws Exception;

	/**
	 * 通过单个属性来查找指定的对象集合
	 * 
	 * @param propertyName
	 *            属性名称
	 * @param propertyValue
	 *            属性的值
	 * @return 集合
	 */
	List<T> findByProperty(String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过单个属性来查找指定的对象集合
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param propertyName
	 *            属性名称
	 * @param propertyValue
	 *            属性的值
	 * @return 集合
	 */
	List<T> findByProperty(Connection conn, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过单个属性来查找指定的对象集合并排序
	 * 
	 * @param propertyName
	 *            属性名称
	 * @param propertyValue
	 *            属性的值
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 * @return
	 */
	List<T> findByProperty(String propertyName, Object propertyValue, String fields, String orderBy) throws Exception;

	/**
	 * 通过单个属性来查找指定的对象集合并排序
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param propertyName
	 *            属性名称
	 * @param propertyValue
	 *            属性的值
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 * @return
	 */
	List<T> findByProperty(Connection conn, String propertyName, Object propertyValue, String fields, String orderBy) throws Exception;

	/**
	 * 通过多个属性来查找数据集
	 * 
	 * @param queryConditions
	 *            属性集合,key属性名,value属性值
	 * @return
	 */
	List<T> findByPropertys(Map<String, Object> queryConditions) throws Exception;

	/**
	 * 通过多个属性来查找数据集
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param queryConditions
	 *            属性集合,key属性名,value属性值
	 * @return
	 */
	List<T> findByPropertys(Connection conn, Map<String, Object> queryConditions) throws Exception;

	/**
	 * 通过多个属性来查找数据集并排序
	 * 
	 * @param queryConditions
	 *            属性集合,key属性名,value属性值
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 * @return
	 * @throws Exception
	 */
	List<T> findByPropertys(Map<String, Object> queryConditions, String fields, String orderBy) throws Exception;

	/**
	 * 通过多个属性来查找数据集并排序
	 * 
	 * @param conn
	 *            指定本次操作的数据库链接
	 * @param queryConditions
	 *            属性集合,key属性名,value属性值
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 * @return
	 */
	List<T> findByPropertys(Connection conn, Map<String, Object> queryConditions, String fields, String orderBy) throws Exception;

	/**
	 * 通过单个属性来分页查找指定的对象集合
	 * 
	 * @param pageResult
	 *            包含分页信息的PageResult
	 * @param propertyName
	 *            属性(列)名称
	 * @param propertyValue
	 *            属性的值
	 * @throws Exception
	 */
	void findByProperty(PageResult<T> pageResult, String propertyName, Object propertyValue) throws Exception;

	/**
	 * 通过单个属性来分页查找指定的对象集合并排序
	 * 
	 * @param pageResult
	 *            包含分页信息的PageResult
	 * @param propertyName
	 *            属性(列)名称
	 * @param propertyValue
	 *            属性的值
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 */
	void findByProperty(PageResult<T> pageResult, String propertyName, Object propertyValue, String fields, String orderBy) throws Exception;

	/**
	 * 通过多个属性来分页查找数据集
	 * 
	 * @param pageResult
	 *            包含分页信息的PageResult
	 * @param args
	 *            属性集合,key属性(列)名,value属性值
	 */
	void findByPropertys(PageResult<T> pageResult, Map<String, Object> args) throws Exception;

	/**
	 * 通过多个属性来分页查找数据集并排序
	 * 
	 * @param pageResult
	 *            包含分页信息的PageResult
	 * @param queryConditions
	 *            查询参数集合,key属性名,value属性值
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 */
	void findByPropertys(PageResult<T> pageResult, Map<String, Object> queryConditions, String fields, String orderBy) throws Exception;

	/**
	 * 基础查询方法.<br>
	 * 针对当前模型对应的表, 做自由查询, 结果返回一个集合
	 * 
	 * @param conn
	 *            指定的数据库链接, 如果传NULL,则开启新连接
	 * @param queryConditons
	 *            查询条件
	 * @param fields
	 *            需要查询返回的的列名列表, 如: "id, name, age"
	 * @param orderBy
	 *            SQL排序参数, 如: "age DESC, name ASC"
	 * @param offset
	 *            开始位置
	 * @param limit
	 *            限制个数
	 * @return 结果集
	 * @throws Exception
	 */
	List<T> find(Connection conn, Map<String, Object> queryConditons, String fields, String orderBy, long offset, long limit) throws Exception;

}