package cn.lewkinglove.common;

import java.util.ArrayList;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import cn.lewkinglove.common.util.LogUtil;
import cn.lewkinglove.common.util.RandomUtil;

/**
 * Redis连接池工具类
 * @author liujing(lewkinglove@gmail.com)
 */
public class RedisPool {
	/**
	 * 是否启用RedisPool
	 */
	private static boolean enabled = false;
	
	/**
	 * 存储Master服务器的链接信息
	 */
	private static RedisServer master;
	
	/**
	 * Master的连接池
	 */
	private static JedisPool masterPool;
	
	/**
	 * Master是否承载读请求
	 */
	private static boolean masterBearRead = false;
	
	/**
	 * 存储所有Slave服务器的链接信息
	 */
	private static ArrayList<RedisServer> slaveList = new ArrayList<RedisServer>();
	
	/**
	 * 存储所有Slave的连接池
	 */
	private static ArrayList<JedisPool> slavePoolList = new ArrayList<JedisPool>();
	
	private RedisPool(){}
	
	
	/**
	 * 初始化方法. 
	 * 如果自己进行依赖管理, 则创建当前类之后必须调用本方法进行初始化.
	 * 如果使用Spring进行依赖管理,则需要配置当前方法为对应bean的初始化方法.
	 */
	public static void init(){
		ConfigPool configger = ConfigPool.getInstance();
		
		//开启Redis连接池, 默认值: true
		RedisPool.enabled = Boolean.parseBoolean(configger.getProperty("config.redis.enabled", "true")); 
		if( RedisPool.enabled==false ){
			LogUtil.info(RedisPool.class,"RedisPool is disabled. RedisPool.init() method will quit now.");
			return;
		}
		
		JedisPoolConfig masterPoolConfig = initMasterPoolConfig();
		JedisPoolConfig slavePoolConfig = initSlavePoolConfig();
		
		//初始化 是否master承受纯读操作, 参与读负载均衡, 默认值: true
		RedisPool.masterBearRead = Boolean.parseBoolean(configger.getProperty("config.redis.master.bearRead", "true"));
		
		//初始化 master 连接池
		String masterConfig = configger.getProperty("config.redis.master");	//{$ip}:{$port}:{$auth_password}
		String[] masterDSN = masterConfig.split(":");
		
		//初始化Master的服务器链接信息
		RedisPool.master = new RedisServer(masterDSN[0], Integer.parseInt(masterDSN[1]), masterDSN.length>2 ? masterDSN[2] : null);
		//根据链接信息, 初始化master的连接池
		RedisPool.masterPool = RedisPool.initJedisPool(masterPoolConfig, RedisPool.master.ip, RedisPool.master.port, (int)masterPoolConfig.getMaxWaitMillis(), RedisPool.master.password);
	
		//初始化 slave 连接池
		String slaveConfig = configger.getProperty("config.redis.slaves");	//{$ip}:{$port}:{$auth_password},{$ip}:{$port}:{$auth_password}...
		if(slaveConfig!=null && slaveConfig.trim().length()>0){
			String[] slaveDSNs = slaveConfig.split(",");
			for(String slaveDSNString : slaveDSNs){
				String[] slaveDSN = slaveDSNString.split(":");
				if(slaveDSN.length<3)
					continue;
				
				RedisServer slave = new RedisServer(slaveDSN[0], Integer.parseInt(slaveDSN[1]), slaveDSN.length>2 ? slaveDSN[2] : null);
				RedisPool.slaveList.add(slave);	//存储本slave的连接信息
				//初始化当前slave的连接池
				RedisPool.slavePoolList.add(RedisPool.initJedisPool(slavePoolConfig, slave.ip, slave.port, (int)slavePoolConfig.getMaxWaitMillis(), slave.password ));
			}
		}
		
		// Master/Slave 主从复制设置, 强制进行服务器主从设置
		RedisPool.masterPool.getResource().slaveofNoOne();
		for(JedisPool pool : RedisPool.slavePoolList){
			pool.getResource().slaveof( RedisPool.master.ip, RedisPool.master.port);
		}
	}

	private static JedisPoolConfig initMasterPoolConfig(){
		ConfigPool configger = ConfigPool.getInstance();
		String minIdle = configger.getProperty("config.redis.cp.master.MinIdle");
		String maxIdle = configger.getProperty("config.redis.cp.master.MaxIdle");
		String MaxTotal = configger.getProperty("config.redis.cp.master.MaxTotal");
		String MaxWaitMillis = configger.getProperty("config.redis.cp.master.MaxWaitMillis");
		
		String TestOnCreate = configger.getProperty("config.redis.cp.master.TestOnCreate");
		String TestOnReturn = configger.getProperty("config.redis.cp.master.TestOnReturn");
		String TestOnBorrow = configger.getProperty("config.redis.cp.master.TestOnBorrow");
		String TestWhileIdle = configger.getProperty("config.redis.cp.master.TestWhileIdle");
		
		
		String Lifo = configger.getProperty("config.redis.cp.master.Lifo");
		String BlockWhenExhausted = configger.getProperty("config.redis.cp.master.BlockWhenExhausted");
		String NumTestsPerEvictionRun = configger.getProperty("config.redis.cp.master.NumTestsPerEvictionRun");
		String TimeBetweenEvictionRunsMillis = configger.getProperty("config.redis.cp.master.TimeBetweenEvictionRunsMillis");
		String MinEvictableIdleTimeMillis = configger.getProperty("config.redis.cp.master.MinEvictableIdleTimeMillis");
		String SoftMinEvictableIdleTimeMillis = configger.getProperty("config.redis.cp.master.SoftMinEvictableIdleTimeMillis");
		
		
		JedisPoolConfig config = new JedisPoolConfig(); 
		config.setMinIdle( Integer.parseInt(minIdle) );	//最小空闲连接数, 默认0
		config.setMaxIdle( Integer.parseInt(maxIdle) );	//最大空闲连接数, 默认8个
		config.setMaxTotal( Integer.parseInt(MaxTotal) );		//最大连接数, 默认8个
		config.setMaxWaitMillis( Long.parseLong(MaxWaitMillis) );	//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1

		config.setTestOnCreate( Boolean.parseBoolean(TestOnCreate) );
		config.setTestOnReturn( Boolean.parseBoolean(TestOnReturn) );
		config.setTestOnBorrow( Boolean.parseBoolean(TestOnBorrow) );	//在获取连接的时候检查有效性, 默认false
		config.setTestWhileIdle( Boolean.parseBoolean(TestWhileIdle) );	//在空闲时检查有效性, 默认false
		
		config.setLifo( Boolean.parseBoolean(Lifo) );	//是否启用后进先出, 默认true
		config.setBlockWhenExhausted( Boolean.parseBoolean(BlockWhenExhausted) );	//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		
		//每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		config.setNumTestsPerEvictionRun( Integer.parseInt(NumTestsPerEvictionRun) ); 
		//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		config.setTimeBetweenEvictionRunsMillis( Long.parseLong(TimeBetweenEvictionRunsMillis) );
		//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		config.setMinEvictableIdleTimeMillis( Long.parseLong(MinEvictableIdleTimeMillis) );
		//对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
		config.setSoftMinEvictableIdleTimeMillis( Long.parseLong(SoftMinEvictableIdleTimeMillis) );
		
		return config;
	}
	
	private static JedisPoolConfig initSlavePoolConfig(){
		ConfigPool configger = ConfigPool.getInstance();
		String minIdle = configger.getProperty("config.redis.cp.slave.MinIdle");
		String maxIdle = configger.getProperty("config.redis.cp.slave.MaxIdle");
		String MaxTotal = configger.getProperty("config.redis.cp.slave.MaxTotal");
		String MaxWaitMillis = configger.getProperty("config.redis.cp.slave.MaxWaitMillis");
		
		String TestOnCreate = configger.getProperty("config.redis.cp.slave.TestOnCreate");
		String TestOnReturn = configger.getProperty("config.redis.cp.slave.TestOnReturn");
		String TestOnBorrow = configger.getProperty("config.redis.cp.slave.TestOnBorrow");
		String TestWhileIdle = configger.getProperty("config.redis.cp.slave.TestWhileIdle");
		
		
		String Lifo = configger.getProperty("config.redis.cp.slave.Lifo");
		String BlockWhenExhausted = configger.getProperty("config.redis.cp.slave.BlockWhenExhausted");
		String NumTestsPerEvictionRun = configger.getProperty("config.redis.cp.slave.NumTestsPerEvictionRun");
		String TimeBetweenEvictionRunsMillis = configger.getProperty("config.redis.cp.slave.TimeBetweenEvictionRunsMillis");
		String MinEvictableIdleTimeMillis = configger.getProperty("config.redis.cp.slave.MinEvictableIdleTimeMillis");
		String SoftMinEvictableIdleTimeMillis = configger.getProperty("config.redis.cp.slave.SoftMinEvictableIdleTimeMillis");
		
		
		JedisPoolConfig config = new JedisPoolConfig(); 
		config.setMinIdle( Integer.parseInt(minIdle) );	//最小空闲连接数, 默认0
		config.setMaxIdle( Integer.parseInt(maxIdle) );	//最大空闲连接数, 默认8个
		config.setMaxTotal( Integer.parseInt(MaxTotal) );		//最大连接数, 默认8个
		config.setMaxWaitMillis( Long.parseLong(MaxWaitMillis) );	//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1

		config.setTestOnCreate( Boolean.parseBoolean(TestOnCreate) );
		config.setTestOnReturn( Boolean.parseBoolean(TestOnReturn) );
		config.setTestOnBorrow( Boolean.parseBoolean(TestOnBorrow) );	//在获取连接的时候检查有效性, 默认false
		config.setTestWhileIdle( Boolean.parseBoolean(TestWhileIdle) );	//在空闲时检查有效性, 默认false
		
		config.setLifo( Boolean.parseBoolean(Lifo) );	//是否启用后进先出, 默认true
		config.setBlockWhenExhausted( Boolean.parseBoolean(BlockWhenExhausted) );	//连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		
		//每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		config.setNumTestsPerEvictionRun( Integer.parseInt(NumTestsPerEvictionRun) ); 
		//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
		config.setTimeBetweenEvictionRunsMillis( Long.parseLong(TimeBetweenEvictionRunsMillis) );
		//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		config.setMinEvictableIdleTimeMillis( Long.parseLong(MinEvictableIdleTimeMillis) );
		//对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)   
		config.setSoftMinEvictableIdleTimeMillis( Long.parseLong(SoftMinEvictableIdleTimeMillis) );
		
		return config;
	}
	
	private static JedisPool initJedisPool(JedisPoolConfig config, String ip, int port, int timeOut, String password){
		//如果有设置密码字段, 且密码不为空
		if(password!=null && password.trim().length()>0){
			return new JedisPool(config, ip, port, timeOut, password);
		}else
			return new JedisPool(config, ip, port, timeOut);
	}
	
	public static Jedis getWriter(){
		return RedisPool.getMasterJedis();
	}
	
	public static Jedis getReader(){
		return RedisPool.getSlaveJedis();
	}
	
	private static Jedis getMasterJedis(){
		return RedisPool.masterPool.getResource();
	}
	
	private static Jedis getSlaveJedis(){
		int slaveCnt = RedisPool.slavePoolList.size();
		if(slaveCnt==0)		//如果没有配置从服, 则
			return RedisPool.getMasterJedis();	//直接返回Master
		
		if(RedisPool.masterBearRead==false && slaveCnt==1)		//如果主不负读, 且只有一个从服, 则
			return RedisPool.slavePoolList.get(0).getResource();	//直接返回从

		int index = RandomUtil.getInt( RedisPool.masterBearRead ? slaveCnt+1 : slaveCnt );
		if(index==slaveCnt)
			return RedisPool.getMasterJedis();
		
		//读请求负载均衡,采用随机平均法
		return RedisPool.slavePoolList.get( index ).getResource();
	}
	
}

class RedisServer{
	public String ip; 
	public int port; 
	public String password; 
	
	public RedisServer(String ip, int port, String password){
		this.ip = ip;
		this.port = port;
		this.password = password;
	}
}

