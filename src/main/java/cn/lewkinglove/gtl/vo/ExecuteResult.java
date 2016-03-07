package cn.lewkinglove.gtl.vo;

/**
 * 非查询类SQL执行结果
 * @author liujing(lewkinglove@gmail.com)
 */
public class ExecuteResult extends SQLResult {
	private int affectedRows;
	private Object autoIncrementId;

	public int getAffectedRows() {
		return affectedRows;
	}

	public void setAffectedRows(int affectedRows) {
		this.affectedRows = affectedRows;
	}

	public Object getAutoIncrementId() {
		return autoIncrementId;
	}

	public void setAutoIncrementId(Object autoIncrementId) {
		this.autoIncrementId = autoIncrementId;
	}

}
