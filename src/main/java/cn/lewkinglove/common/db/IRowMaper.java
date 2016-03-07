package cn.lewkinglove.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库表映射到模型的接口
 * @param <T>
 *            具体数据库映射模型
 * @author liujing(lewkinglove@gmail.com)
 */
public interface IRowMaper<T extends Object> {
	/**
	 * 将当前结果集中的数据映射为模型
	 * 
	 * @param rs
	 *            SQL结果集
	 * @return 映射成功的模型
	 * @throws Exception
	 *             模型属性不存在或者属性类型错误
	 */
	public abstract T mappingRow(ResultSet rs) throws SQLException;
}
