package cn.lewkinglove.gtl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import cn.lewkinglove.api.core.CallException;
import cn.lewkinglove.common.db.DBHelper;
import cn.lewkinglove.common.db.GeneratedKey;
import cn.lewkinglove.common.db.exception.DataSourceNotFoundException;
import cn.lewkinglove.common.util.LogUtil;
import cn.lewkinglove.gtl.vo.ExecuteResult;
import cn.lewkinglove.gtl.vo.GTLTransaction;
import cn.lewkinglove.gtl.vo.QueryResult;

public class GTLTransactionProxy {
	
	/**
	 * 事务默认超时时间; 单位: 毫秒
	 */
	public static final int TX_DEFAULT_TIME_OUT = 3000;
	

	/**
	 * 开始事务
	 * @param dsId	[可选参数]数据源编号, 当有txId的参数时, 此项可不传
	 * @param txId	[可选参数]事务编号;如果已经有了一个已开启的事务, 则直接把编号发过来, 这边会选中这个已开启的事务;
	 * @param timeout	[可选参数]指定事务超时时间, 如果未指定, 则使用全局设定; 单位: 毫秒
	 * @return 开始的事务编号, 可用于其他操作复用
	 * @throws Exception
	 */
	public static String beginTransaction(String dsId, String txId, Integer timeout) throws Exception {
		if (dsId == null && txId == null)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "dsId和txId参数必须不能全部为空" });
		
		//如果传递了txId, 则
		//检查是否存在对应事务,且状态为未完成
		//如果存在, 则对事务嵌套层数进行递增
		if( txId!=null &&txId.trim().length()>0 ){
			GTLTransaction tx =GTLTransactionPool.get(txId);
			
			if(tx==null)
				throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "txId指向的事务不存在" });
			
			if(tx.isInProgress()==false)
				throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "txId指向的事务已经结束" });
			
			tx.increaseTxNestingLevel();
			
			return txId;
		}
		
		GTLTransaction tx;
		try {
			//如果传递了dsId, 则创建并开启事务
			tx = GTLTransaction.newTransaction(dsId, timeout);
        } catch (DataSourceNotFoundException ex) {
        	throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "dsId指向的数据源不存在" });
        }
		
		GTLTransactionPool.poolIn(tx);	//缓存事务到事务池中
		
		LogUtil.info(GTLTransactionProxy.class, "GTL事务["+tx.getTxId()+"]已创建成功. 超时时间: "+timeout+"ms");
		
		return tx.getTxId();
	}

	/**
	 * 回滚事务
	 * @param txId	事务编号
	 * @return	成功或者失败
	 * @throws Exception
	 */
	public static boolean rollbackTransaction(String txId) throws CallException {
		if (txId == null) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		GTLTransaction tx =GTLTransactionPool.get(txId);
		
		if(tx==null)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "txId指向的事务["+txId+"]不存在" });
		
		if(tx.isInProgress()==false)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-3", new String[] { "txId指向的事务["+txId+"]已经结束" });
		
		try {
	        tx.rollback();
        } catch (SQLException e) {
	        e.printStackTrace();
	        throw new CallException(Consts.BIZ_IDENTIFIER, "-3", new String[] { "事务回滚失败["+e.getErrorCode()+"]["+e.getMessage()+"]" });
        }
		return true;
	}
	
	/**
	 * 提交事务
	 * @param txId	事务编号
	 * @return	成功或者失败
	 * @throws Exception
	 */
	public static boolean commitTransaction(String txId) throws Exception {
		if (txId == null) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		GTLTransaction tx =GTLTransactionPool.get(txId);
		
		if(tx==null)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "txId指向的事务["+txId+"]不存在" });
		
		if(tx.isInProgress()==false)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-3", new String[] { "txId指向的事务["+txId+"]已经结束" });
		
		try {
	        tx.commit();
        } catch (SQLException e) {
	        e.printStackTrace();
	        throw new CallException(Consts.BIZ_IDENTIFIER, "-3", new String[] { "事务提交失败["+e.getErrorCode()+"]["+e.getMessage()+"]" });
        }
		return true;
	}
	
	/**
	 * 执行SQL查询(事务环境内)
	 * @param txId	事务编号
	 * @param sql	要执行的SQL语句; 形如: SELECT * FROM User WHERE uname='liujng' AND upwd='123123'
	 * @return	查询结果
	 * @throws Exception
	 */
	public static QueryResult executeQuery(String txId, String sql) throws Exception {
		return executePreparedQuery(txId, sql, new String[]{});
	}
	
	/**
	 * 执行预编译的SQL查询(事务环境内)
	 * @param txId	事务编号
	 * @param sql	要执行的SQL语句; 形如: SELECT * FROM User WHERE uname='liujng' AND upwd='123123'
	 * @param sqlArgs	value array, 预编译SQL的参数; 形如: ['liujing','123123']
	 * @return	查询结果
	 * @throws Exception
	 */
	public static QueryResult executePreparedQuery(String txId, String sql, Object[] sqlArgs) throws Exception {
		if ( txId == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		if ( sql == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		if ( sqlArgs == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		GTLTransaction tx =GTLTransactionPool.get(txId);
		
		if(tx==null)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "txId指向的事务["+txId+"]不存在" });
		
		if(tx.isInProgress()==false)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-3", new String[] { "txId指向的事务["+txId+"]已经结束" });
		
		
		QueryResult result = new QueryResult();
        try {
	        List<HashMap<String, Object>> resultRows = DBHelper.executeFreeQuery(tx.getTxConnection(), sql, sqlArgs);
	        
	        result.setResultSet(resultRows);
        } catch (Exception e) {
        	e.printStackTrace();
        	if(e instanceof SQLException){
        		SQLException ex = (SQLException)e;
        		result.setErrorCode(ex.getSQLState());	
        		result.setErrorMessage(ex.getMessage());
        	}
        	result.setErrorCode("-9999");
        	result.setErrorMessage("未知SQL错误, 消息: " + e.getMessage());
        }

		return result;
	}
	
	
	/**
	 * 执行非查询类SQL(事务环境内)
	 * @param txId	事务编号
	 * @param sql	要执行的SQL语句; 形如: UPDATE User SET upwd='456456' WHERE uname='liujing'
	 * @return	{affectedRows: 影响的行数, autoIncrementId: 自增编号}
	 * @throws Exception
	 */
	public static ExecuteResult executeNonQuery(String txId, String sql) throws Exception {
		return executePreparedNonQuery(txId, sql, new String[]{});
	}
	
	/**
	 * 执行预编译的非查询类SQL(事务环境内)
	 * @param txId	事务编号
	 * @param sql	要执行的预编译SQL语句; 形如: UPDATE User SET upwd=? WHERE uname=?
	 * @param sqlArgs	value array, 预编译SQL的参数; 形如: ['456456', 'liujing']
	 * @return	{affectedRows: 影响的行数, autoIncrementId: 自增编号}
	 * @throws Exception
	 */
	public static ExecuteResult executePreparedNonQuery(String txId, String sql, Object[] sqlArgs) throws Exception {
		if ( txId == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		if ( sql == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		if ( sqlArgs == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		GTLTransaction tx =GTLTransactionPool.get(txId);
		
		if(tx==null)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "txId指向的事务["+txId+"]不存在" });
		
		if(tx.isInProgress()==false)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-3", new String[] { "txId指向的事务["+txId+"]已经结束" });
		
		ExecuteResult result = new ExecuteResult();
        try {
        	GeneratedKey autoId = new GeneratedKey();
    		int affectedRows = DBHelper.executeNonQuery(tx.getTxConnection(), autoId, sql, sqlArgs);
    		
    		result.setAffectedRows(affectedRows);
    		result.setAutoIncrementId(autoId.getValue());
        } catch (Exception e) {
        	e.printStackTrace();
        	if(e instanceof SQLException){
        		SQLException ex = (SQLException)e;
        		result.setErrorCode(ex.getSQLState());	
        		result.setErrorMessage(ex.getMessage());
        	}
        	result.setErrorCode("-9999");
        	result.setErrorMessage("未知SQL错误, 消息: " + e.getMessage());
        }

		return result;
	}
}
