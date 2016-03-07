package cn.lewkinglove.common.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import cn.lewkinglove.common.ConfigPool;
import cn.lewkinglove.common.util.LogUtil;
import cn.lewkinglove.common.util.RandomUtil;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * 数据库链接源
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class DataSourcePool {
	/**
	 * 默认数据源标识
	 * 只要启用DataSourcePool engine, 则强制要求必须包含名为default的数据源
	 */
	public static final String DEFAULT_DS_ID = "default";
	

	/**
	 * 是否启用DataSourcePool engine
	 */
	private static boolean enabled = false;

	/**
	 * 数据库链接驱动
	 */
	private static String dbDriver = "com.mysql.jdbc.Driver";

	/**
	 * 数据库JDBCURL模式串
	 */
	private static String dbJdbcUrl = "jdbc:mysql://{#ip}:{#port}/{#dbName}";
	/**
	 * JDBCUrl自定义参数
	 */
	private static String dbJdbcUrlArgs = "?useUnicode=true&characterEncoding=UTF-8&useCompression=true&enableQueryTimeouts=false&cacheCallableStmts=true&callableStmtCacheSize=256&useServerPrepStmts=true&cachePrepStmts=true&prepStmtCacheSize=512&prepStmtCacheSqlLimit=4096";
	
	/**
	 * 所有命名数据源的池子
	 */
	private static HashMap<String, NamedDataSource> PoolMap = new HashMap<String, NamedDataSource>();
	
	

	/**
	 * 初始化方法. 如果自己进行依赖管理, 则创建当前类之后必须调用本方法进行初始化.
	 * 如果使用Spring进行依赖管理,则需要配置当前方法为对应bean的初始化方法.
	 */
	public static void init() {
		ConfigPool configger = ConfigPool.getInstance();

		// 开启数据库连接池, 默认值: true
		DataSourcePool.enabled = Boolean.parseBoolean(configger.getProperty("config.db.enabled", "true"));
		if (DataSourcePool.enabled == false) {
			LogUtil.info(DataSourcePool.class,"DataSourcePool engine is disabled in db.properties. DataSourcePool.init() method will quit now.");
			return;
		}

		// 获取配置的数据库链接驱动类, 默认值: com.mysql.jdbc.Driver
		DataSourcePool.dbDriver = configger.getProperty("config.db.driver", DataSourcePool.dbDriver);
		// 获取配置的JDBC-URL模式串 默认值: jdbc:mysql://{#ip}:{#port}/{#dbName}
		DataSourcePool.dbJdbcUrl = configger.getProperty("config.db.jdbcUrl", DataSourcePool.dbJdbcUrl);
		// JDBCUrl的后缀参数, 默认值: DataSourcePool.dbJdbcUrlArgs
		DataSourcePool.dbJdbcUrlArgs = configger.getProperty("config.db.jdbcUrl.args", DataSourcePool.dbJdbcUrlArgs);

		//获取配置启用的数据源配置列表
		String dsListCfg = configger.getProperty("config.db.enabled.dsList");
		dsListCfg = dsListCfg==null || dsListCfg.trim().length()==0 ? DataSourcePool.DEFAULT_DS_ID : DataSourcePool.DEFAULT_DS_ID+","+dsListCfg;
		
		//遍历所有需要初始化的dataSource进行初始化
		String[] dsList = dsListCfg.split(",");
		for(String dsId : dsList){
			NamedDataSource ds = new NamedDataSource();
			ds.dsId = dsId;
			ds.aliasName = configger.getProperty("config.db."+dsId+".aliasName");
			
			ds.dbName = configger.getProperty("config.db."+dsId+".dbname");
			if (ds.dbName == null || ds.dbName.trim().length() == 0) 
				throw new Error("config.db."+dsId+".dbname not find or not correct in db.properties.");
			
			// 获取datasource主库是否承担读请求的配置, 默认值: true
			ds.masterBearRead = Boolean.parseBoolean(configger.getProperty("config.db."+dsId+".master.bearRead", "true"));
			
			// 初始化主库数据源
			String masterDSN = configger.getProperty("config.db."+dsId+".master");
			ds.master = DataSourcePool.createDataSource(masterDSN, ds.dbName, "config.db."+dsId+".cp.master.");

			// 初始化从库数据源
			String slaves = configger.getProperty("config.db."+dsId+".slaves", "");
			String[] slaveDSNs = slaves.split(",");
			for (String slaveDSN : slaveDSNs) {
				if (slaveDSN == null || slaveDSN.equals("")) continue;
				ds.slaves.add(DataSourcePool.createDataSource(slaveDSN, ds.dbName, "config.db."+dsId+".cp.slave."));
			}
			
			//添加当前datasource到数据源索引中
			if(DataSourcePool.PoolMap.containsKey(dsId))
				throw new Error("DataSourcePool enging found two datasource have same name["+dsId+"] in db.properties. DataSourcePool.init() method will quit now.");
			
			DataSourcePool.PoolMap.put(dsId, ds);
			if(ds.aliasName!=null && ds.aliasName.trim().length()>0){
				if(DataSourcePool.PoolMap.containsKey(ds.aliasName))
					throw new Error("DataSourcePool enging found a alias name conflict["+ds.aliasName+"] in db.properties. DataSourcePool.init() method will quit now.");
				DataSourcePool.PoolMap.put(ds.aliasName, ds);
			}
		}
		//至此, 所有配置的datasource都已完成初始化.
	}

	/**
	 * 创建数据源对象, 并初始化数据源配置
	 * 
	 * @param serverDSN
	 *            服务器的链接信息, 格式:　{$ip}:{$port}:{$username}:{$password}
	 * @param databaseName
	 * 			  Datasource的数据库名称
	 * @param cpCfgPrefix
	 *            连接池配置的前缀
	 * @return 创建成功的数据源
	 */
	private static DataSource createDataSource(String serverDSN,String databaseName, String cpCfgPrefix) {
		if (serverDSN == null || serverDSN.trim().length() < 13) 
			throw new Error("创建数据源时传递的DSN参数[" + serverDSN + "]格式不正确!");
		
		String[] dsn = serverDSN.split(":");
		if (dsn == null || dsn.length != 4) 
			throw new Error("创建数据源时传递的DSN参数[" + serverDSN + "]格式不正确!");

		ConfigPool configPool = ConfigPool.getInstance();

		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setDriverClass(DataSourcePool.dbDriver);

		dataSource.setJdbcUrl(getJDBCUrl(dsn[0], Integer.parseInt(dsn[1]), databaseName));
		dataSource.setUsername(dsn[2]);
		dataSource.setPassword(dsn[3]);

		dataSource.setIdleMaxAgeInMinutes(Long.parseLong(configPool.getProperty(cpCfgPrefix + "idleMaxAgeInMinutes")));
		dataSource.setIdleConnectionTestPeriodInMinutes(Long.parseLong(configPool.getProperty(cpCfgPrefix + "idleConnectionTestPeriodInMinutes")));

		dataSource.setPartitionCount(Integer.parseInt(configPool.getProperty(cpCfgPrefix + "partitionCount")));
		dataSource.setMinConnectionsPerPartition(Integer.parseInt(configPool.getProperty(cpCfgPrefix + "minConnectionsPerPartition")));
		dataSource.setMaxConnectionsPerPartition(Integer.parseInt(configPool.getProperty(cpCfgPrefix + "maxConnectionsPerPartition")));
		dataSource.setAcquireIncrement(Integer.parseInt(configPool.getProperty(cpCfgPrefix + "acquireIncrement")));
		dataSource.setStatementsCacheSize(Integer.parseInt(configPool.getProperty(cpCfgPrefix + "statementsCacheSize")));
		return dataSource;
	}

	/**
	 * 依据jdbcURL格式,生成具体的jdbcURL
	 * 
	 * @param ip
	 *            数据库ip
	 * @param port
	 *            数据库端口
	 * @param dbName
	 *            数据库名
	 * @return 完整的jdbcURL
	 */
	private static String getJDBCUrl(String ip, int port, String dbName) {
		return DataSourcePool.dbJdbcUrl.replace("{#ip}", ip).replace("{#port}", port + "").replace("{#dbName}", dbName) + DataSourcePool.dbJdbcUrlArgs;
	}

	/**
	 * 获得主库数据源
	 * @param datasourceName
	 * 			 数据源标识或者数据源别名
	 * @return 主库数据源
	 */
	public static DataSource getMasterDataSource(String datasourceName) {
		NamedDataSource ds = DataSourcePool.PoolMap.get(datasourceName);
		if(ds==null)
			return null;
		return ds.master;
	}
	
	/**
	 * 获取默认主库数据源
	 * @return
	 */
	public static DataSource getMasterDataSource() {
		return getMasterDataSource(DEFAULT_DS_ID);
	}
	
	/**
	 * 获得默认从库数据源
	 * @return
	 */
	public static DataSource getSlaveDataSource() {
		return getSlaveDataSource(DEFAULT_DS_ID);
	}
	
	
	/**
	 * 获得从库数据源
	 * 如果主库也负担读,主库也可能被随机选中
	 * @param datasourceName
	 * 			 数据源标识或者数据源别名
	 * @return 随机的从库数据源
	 */
	public static DataSource getSlaveDataSource(String datasourceName) {
		NamedDataSource ds = DataSourcePool.PoolMap.get(datasourceName);
		if(ds==null)
			return null;
		
		int slaveCnt = ds.slaves.size();
		
		if (slaveCnt == 0) 
			return ds.master;
		
		if (ds.masterBearRead == false && slaveCnt == 1) 
			return ds.slaves.get(0);
		
		int index = RandomUtil.getInt(ds.masterBearRead ? slaveCnt + 1 : slaveCnt);
		if (index == slaveCnt) 
			return ds.master;
	
		return ds.slaves.get(index);
	}

}


class NamedDataSource{
	/**
	 * 数据源标识
	 */
	public String dsId;
	
	/**
	 * 数据源名称
	 */
	public String aliasName;
	
	/**
	 * 数据库名
	 */
	public String dbName;
	
	/**
	 * Master是否承载读请求
	 */
	public boolean masterBearRead= false;
	
	/**
	 * 主库
	 */
	public DataSource master;

	/**
	 * 从库集合
	 */
	public List<DataSource> slaves = new ArrayList<DataSource>();
}
