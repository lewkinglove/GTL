package api.GTL;

import java.util.HashMap;
import java.util.List;

import api.ServiceApiAlias;
import api.ServiceApiProvider;
import cn.lewkinglove.api.core.CallException;
import cn.lewkinglove.gtl.Consts;
import cn.lewkinglove.gtl.GTLTransactionProxy;
import cn.lewkinglove.gtl.vo.ExecuteResult;
import cn.lewkinglove.gtl.vo.QueryResult;
import cn.lewkinglove.gtl.vo.SQLResult;

@ServiceApiProvider
public class Gate {
	static{
		/**
		 * 为Api添加别名
		 */
		ServiceApiAlias.addAlias("getVersion", "GTL.Gate.getVersion");
		ServiceApiAlias.addAlias("beginTransaction", "GTL.Gate.beginTransaction");
		ServiceApiAlias.addAlias("rollbackTransaction", "GTL.Gate.rollbackTransaction");
		ServiceApiAlias.addAlias("commitTransaction", "GTL.Gate.commitTransaction");
		ServiceApiAlias.addAlias("executeQuery", "GTL.Gate.executeQuery");
		ServiceApiAlias.addAlias("executePreparedQuery", "GTL.Gate.executePreparedQuery");
		ServiceApiAlias.addAlias("executeNonQuery", "GTL.Gate.executeNonQuery");
		ServiceApiAlias.addAlias("executePreparedNonQuery", "GTL.Gate.executePreparedNonQuery");
	}
	
	@ServiceApiProvider
	public String getVersion(HashMap<String, Object> args) throws CallException {
		return "V0.0.1a";
	}
	
	/**
	 * 开始事务
	 * @param args
	 * <pre>
	 *    dsId    [可选参数]数据源编号, 当有txId的参数时, 此项可不传 
	 *    timeout [可选参数]指定事务超时时间, 如果未指定, 则使用全局设定; 单位: 毫秒 
	 *    txId    [可选参数]事务编号; 如果已经有了一个已开启的事务, 则直接把编号发过来, 这边会选中这个已开启的事务;
	 * </pre>
	 * @return	
	 * 	  开始的事务编号, 可用于其他操作复用
	 * @throws Exception 
	 */
	@ServiceApiProvider
	public String beginTransaction(HashMap<String, Object> args) throws Exception {
		String dsId = (String) args.get("dsId");
		String txId = (String) args.get("txId");
		int timeout = GTLTransactionProxy.TX_DEFAULT_TIME_OUT; 
		if(args.containsKey("timeout")){
			try {
	            timeout = (int)Double.parseDouble((args.get("timeout").toString()));
            } catch (NumberFormatException e){
            	throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "timeout" });
            }
		}
		
		if (dsId == null && txId == null) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2", new String[] { "dsId和txId参数必须不能全部为空" });
		
		String result = GTLTransactionProxy.beginTransaction(dsId, txId, timeout);
		return result;
	}
	
	/**
	 * 回滚事务
	 * @param args
	 * <pre>
	 *    txId    事务编号
	 * </pre>
	 * @return
	 *    布尔型; 成功或者失败
	 * @throws Exception
	 */
	@ServiceApiProvider
	public boolean rollbackTransaction(HashMap<String, Object> args) throws Exception {
		String txId = (String) args.get("txId");
		if (txId == null) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		boolean result = GTLTransactionProxy.rollbackTransaction(txId);
		return result;
	}
	
	/**
	 * 提交事务
	 * @param args
	 * <pre>
	 *    txId    事务编号
	 * </pre>
	 * @return
	 *    布尔型; 成功或者失败
	 * @throws Exception
	 */
	@ServiceApiProvider
	public boolean commitTransaction(HashMap<String, Object> args) throws Exception {
		String txId = (String) args.get("txId");
		if (txId == null) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		boolean result = GTLTransactionProxy.commitTransaction(txId);
		return result;
	}

	/**
	 * 执行SQL查询(事务环境内)
	 * @param args
	 * <pre>
	 *    txId    事务编号
	 *    sql     要执行的SQL语句; 形如: SELECT * FROM User WHERE uname='liujng' AND upwd='123123'
	 * </pre>
	 * @return	
	 * 	  Table List Or Row Array
	 * @throws Exception
	 */
	@ServiceApiProvider
	public List<HashMap<String, Object>> executeQuery(HashMap<String, Object> args) throws Exception {
		String txId = (String) args.get("txId");
		String sql = (String) args.get("sql");
		if ( txId == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		if ( sql == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		
		QueryResult result = GTLTransactionProxy.executeQuery(txId, sql);
		if(result.getErrorCode()!= SQLResult.ERROR_CODE_OK)
			throw new CallException("0", result.getErrorMessage(), (Throwable)null);
		
		return result.getResultSet();
	}
	
	/**
	 * 执行预编译的SQL查询(事务环境内)
	 * @param args
	 * <pre>
	 *    txId    事务编号
	 *    sql     要执行的SQL语句; 形如: SELECT * FROM User WHERE uname='liujng' AND upwd='123123'
	 *    sqlArgs value array, 预编译SQL的参数; 形如: ['liujing','123123']
	 * </pre>
	 * @return	
	 * 	  Table List Or Row Array
	 * @throws Exception
	 */
	@ServiceApiProvider
	public List<HashMap<String, Object>> executePreparedQuery(HashMap<String, Object> args) throws Exception {
		String txId = (String) args.get("txId");
		if ( txId == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		String sql = (String) args.get("sql");
		if ( sql == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		
		List<Object> sqlArgs = (List<Object>) args.get("sqlArgs");
		if ( sqlArgs==null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sqlArgs" });

		QueryResult result = GTLTransactionProxy.executePreparedQuery(txId, sql, sqlArgs.toArray());
		if(result.getErrorCode()!= SQLResult.ERROR_CODE_OK)
			throw new CallException("0", result.getErrorMessage(), (Throwable)null);
		
		return result.getResultSet();
	}
	
	/**
	 * 执行非查询类SQL(事务环境内)
	 * @param args
	 * <pre>
	 *    txId    事务编号
	 *    sql     要执行的SQL语句; 形如: UPDATE User SET upwd='456456' WHERE uname='liujing'
	 * </pre>
	 * @return	
	 * 	  {affectedRows: 影响的行数, autoIncrementId: 自增编号}
	 * @throws Exception
	 */
	@ServiceApiProvider
	public ExecuteResult executeNonQuery(HashMap<String, Object> args) throws Exception {
		String txId = (String) args.get("txId");
		String sql = (String) args.get("sql");
		if ( txId == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		if ( sql == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		ExecuteResult result = GTLTransactionProxy.executeNonQuery(txId, sql);
		if(result.getErrorCode()!= SQLResult.ERROR_CODE_OK)
			throw new CallException("0", result.getErrorMessage(), (Throwable)null);
		
		return result;
	}
	
	/**
	 * 执行预编译的非查询类SQL(事务环境内)
	 * @param args
	 * <pre>
	 *    txId    事务编号
	 *    sql     要执行的预编译SQL语句; 形如: UPDATE User SET upwd=? WHERE uname=?
	 *    sqlArgs value array, 预编译SQL的参数; 形如: ['456456', 'liujing']
	 * </pre>
	 * @return	
	 * 	  {affectedRows: 影响的行数, autoIncrementId: 自增编号}
	 * @throws Exception
	 */
	@ServiceApiProvider
	public ExecuteResult executePreparedNonQuery(HashMap<String, Object> args) throws Exception {
		String txId = (String) args.get("txId");
		String sql = (String) args.get("sql");
		if ( txId == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "txId" });
		
		if ( sql == null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sql" });
		
		List<Object> sqlArgs = (List<Object>) args.get("sqlArgs");
		if ( sqlArgs==null ) 
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1", new String[] { "sqlArgs" });

		ExecuteResult result = GTLTransactionProxy.executePreparedNonQuery(txId, sql, sqlArgs.toArray());
		if(result.getErrorCode()!= SQLResult.ERROR_CODE_OK)
			throw new CallException("0", result.getErrorMessage(), (Throwable)null);
		
		return result;
	}
	
}