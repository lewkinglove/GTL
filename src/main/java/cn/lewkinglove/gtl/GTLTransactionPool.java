package cn.lewkinglove.gtl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import cn.lewkinglove.common.util.LogUtil;
import cn.lewkinglove.gtl.vo.GTLTransaction;

public class GTLTransactionPool {
	
	private static LinkedHashMap<String, GTLTransaction> PoolMap = new LinkedHashMap<String, GTLTransaction>();

	public static GTLTransaction get(String txId){
		return GTLTransactionPool.PoolMap.get(txId);
	}
	
	/**
	 * 检查指定链接是否GTL事务链接
	 * @param conn 要检查的数据库链接
	 * @return
	 */
	public static boolean isTXConnection(Connection conn){
		Collection<GTLTransaction> values = PoolMap.values();
		Iterator<GTLTransaction> iter = values.iterator();
		while(iter.hasNext()){
			 if(conn==iter.next().getTxConnection())
				 return true;
		}
		return false;
	}
	
	public static void checkTXPoolAndClean(){
		Collection<GTLTransaction> values = PoolMap.values();
		Iterator<GTLTransaction> iter = values.iterator();
		while(iter.hasNext()){
			GTLTransaction tx  = iter.next();
			
			long curTimeStamp = System.currentTimeMillis();
			//如果事务还在进行中, 则检测超时
			if(tx.isInProgress()){
				//检测事务是否超时, 如果超时, 则进行回滚
				if( curTimeStamp - tx.getStartTime() > tx.getTimeout()){
					try {
		                tx.rollback();
		                LogUtil.info(GTLTransactionPool.class, "GTL事务["+tx.getTxId()+"]因为超时已经被回滚.");
	                } catch (SQLException e) {
		                e.printStackTrace();
	                }
				}
				continue ;		//处理下一个
			}
			
			//事务已完成, 并保留超过三分钟, 则删除
			if( tx.isInProgress()==false && curTimeStamp - tx.getFinishTime() > 3*60*1000){
				GTLTransactionPool.poolOut(tx);
			}
			
			
			
			
		}
	}
	
	
	public static void poolIn(GTLTransaction tx){
		GTLTransactionPool.PoolMap.put(tx.getTxId(), tx);
	}
	
	public static GTLTransaction poolOut(String txId){
		return GTLTransactionPool.PoolMap.remove(txId);
	}
	
	public static GTLTransaction poolOut(GTLTransaction tx){
		return GTLTransactionPool.PoolMap.remove(tx.getTxId());
	}
	
	
}
