package cn.lewkinglove.common.vo;

import java.util.List;

/**
 * 数据分页对象
 * @author liujing(lewkinglove@gmail.com)
 */
public class PageResult<T> {
	private int pageSize = 15;
	private int pageNum = 1;
	private long totalResult;
	private List<T> resultList = null;

	/**
	 * 获取页容量
	 * 
	 * @return 容量
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置页容量
	 * 
	 * @param pageSize
	 *            容量
	 */
	public void setPageSize(int pageSize) {
		pageSize = pageSize < 1 ? 15 : pageSize;
		this.pageSize = pageSize;
	}

	/**
	 * 获取当前页号
	 * 
	 * @return 页号
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * 设置当前页号
	 * 
	 * @param pageNum
	 *            页号
	 */
	public void setPageNum(int pageNum) {
		pageNum = pageNum < 1 ? 1 : pageNum;
		this.pageNum = pageNum;
	}

	/**
	 * 获取总记录数量
	 * 
	 * @return 总记录数量
	 */
	public long getTotalResult() {
		return totalResult;
	}

	/**
	 * 设置总记录数量
	 * 
	 * @param totalResult
	 *            记录数量
	 */
	public void setTotalResult(long totalResult) {
		this.totalResult = totalResult;
	}

	/**
	 * 获取对应结果集
	 * 
	 * @return 结果集
	 */
	public List<T> getResultList() {
		return resultList;
	}

	/**
	 * 设置结果集
	 * 
	 * @param resultList
	 *            结果集
	 */
	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPage() {
		int totalPage = (int) this.getTotalResult() / this.getPageSize();
		if (this.getTotalResult() % this.getPageSize() != 0)
			totalPage++;
		return totalPage < 1 ? 1 : totalPage;
	}

	/**
	 * 获取第一条记录的索引
	 * 
	 * @return 索引
	 */
	public int getFirstResult() {
		int firstRes = (this.getPageNum() - 1) * this.getPageSize();
		return firstRes < 0 ? 0 : firstRes;
	}

}
