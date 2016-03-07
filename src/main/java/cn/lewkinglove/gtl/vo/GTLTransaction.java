package cn.lewkinglove.gtl.vo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import cn.lewkinglove.common.db.DBHelper;
import cn.lewkinglove.common.util.DateUtil;

/**
 * GTL事务对象
 * 包含单个事务所有关联的信息
 * @author liujing(lewkinglove@gmail.com)
 */
public class GTLTransaction {
	/**
	 * 常量, 标识事务已回滚
	 */
	public static final byte TX_ROLLBACKED = 0b01;
	
	/**
	 * 常量, 标识事务已提交
	 */
	public static final byte TX_COMMITTED = 0b10;
	
	/**
	 * 事务ID序列
	 */
	private static int TX_ID_SEQ = 0;
	
	/**
	 * 重置事务ID序列
	 * 由定时器定时执行, 如:每分钟一次
	 */
	public static synchronized void resetTXIDSequence(){
		synchronized (GTLTransaction.class) {
			GTLTransaction.TX_ID_SEQ=0;
        }
	}
	
	/**
	 * 获取下一个事务ID
	 * @return
	 */
	private static synchronized String getNextTXID(){
		//return "TX"+(++GTLTransaction.TX_ID_SEQ);
		return String.format("TX"+DateUtil.formatDate("yyyyMMddHHmm")+"%06d", ++GTLTransaction.TX_ID_SEQ);
	}
	
	/**
	 * 事务ID
	 */
	private String txId;
	
	/**
	 * 数据源ID
	 */
	private String dsId;
	
	/**
	 * 事务超时时间; 单位: 毫秒
	 * 默认值: 3000ms
	 */
	private int timeout=3000;
	
	/**
	 * 事务关联数据库链接
	 */
	private Connection txConnection; 
	
	
	/**
	 * 虚拟事务嵌套层数
	 */
	private int txNestingLevel = 0;
	
	/**
	 * 事务是否还在进行中
	 */
	private boolean isInProgress = true;
	
	/**
	 * 事务完成结果
	 * 用来在事务技术后, 检查事务是回滚还是提交
	 */
	private byte txFinishResult = 0b00; 
	
	/**
	 * 事务开始时间
	 */
	private long startTime;
	
	/**
	 * 事务结束时间
	 */
	private long finishTime;
	
	
	public String getTxId() {
		return txId;
	}

	public String getDsId() {
		return dsId;
	}

	public int getTimeout() {
		return timeout;
	}

	public Connection getTxConnection() {
		return txConnection;
	}

	public int getTxNestingLevel() {
		return txNestingLevel;
	}

	public int increaseTxNestingLevel() {
		return ++this.txNestingLevel;
	}
	
	public int decreaseTxNestingLevel() {
		return --this.txNestingLevel;
	}

	public boolean isInProgress() {
		return isInProgress;
	}

	public byte getTxFinishResult() {
		return txFinishResult;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	
	private GTLTransaction(){};
	public static GTLTransaction newTransaction(String dsId, int timeout)throws Exception{
		GTLTransaction tx = new GTLTransaction();
		tx.txId = GTLTransaction.getNextTXID();
		tx.dsId = dsId;
		tx.timeout = timeout;
		
//		DataSource ds = DataSourcePool.getMasterDataSource(dsId);
//		if(ds==null)
//			throw new DataSourceNotFoundException("事务指定的数据源["+dsId+"]不存在");
//		
//		tx.txConnection = ds.getConnection();
		tx.txConnection = DBHelper.getMasterConnection(dsId);
		
		
		tx.txConnection.setAutoCommit(false);
		
		tx.isInProgress = true;
		tx.increaseTxNestingLevel();
		tx.startTime = new Date().getTime();
		return tx;
	}
	
	public synchronized void rollback() throws SQLException{
		this.txConnection.rollback();
		this.txConnection.close();	//关闭数据库链接
		
		this.isInProgress = false;
		this.finishTime = new Date().getTime();
		this.txFinishResult = GTLTransaction.TX_ROLLBACKED;
	}
	
	public synchronized void commit() throws SQLException{
		//如果还有未提交的嵌套事务, 则返回
		if(this.decreaseTxNestingLevel()>0)
			return ;
		
		this.txConnection.commit();
		this.txConnection.close();	//关闭数据库链接
		
		this.isInProgress = false;
		this.finishTime = new Date().getTime();
		this.txFinishResult = GTLTransaction.TX_COMMITTED;
	}
}
