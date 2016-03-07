package cn.lewkinglove.gtl.vo;

import java.util.HashMap;
import java.util.List;

/**
 * 查询类SQL执行结果
 * @author liujing(lewkinglove@gmail.com)
 */
public class QueryResult extends SQLResult {
	private List<HashMap<String, Object>> resultSet;

	public List<HashMap<String, Object>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<HashMap<String, Object>> resultSet) {
		this.resultSet = resultSet;
	}
	
	

}
