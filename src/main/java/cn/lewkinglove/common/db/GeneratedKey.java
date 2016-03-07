package cn.lewkinglove.common.db;

/**
 * 数据表自增列值容器对象
 * @author liujing(lewkinglove@gmail.com)
 */
public final class GeneratedKey {
	private Long value;

	public GeneratedKey() {
	}

	public void setValue(Object value) {
		this.setValue((Long) value);
	}

	public void setValue(Long value) {
		this.value = (Long) value;
	}

	public Long getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
	    return this.value==null ? "GeneratedKey: NULL" : "GeneratedKey: " + this.value;
	}
}
