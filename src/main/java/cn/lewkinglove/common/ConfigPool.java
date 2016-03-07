package cn.lewkinglove.common;

import java.util.Properties;

import cn.lewkinglove.api.core.CallClientUtil;
import cn.lewkinglove.common.db.DataSourcePool;

/**
 * 配置池
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class ConfigPool extends Properties {
	private static final long serialVersionUID = 1L;

	/**
	 * 当前项目的部署环境标识
	 */
	private static String environment;

	private static ConfigPool instance = new ConfigPool();

	private ConfigPool() {
	}

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static ConfigPool getInstance() {
		if (environment == null)
			init();
		return instance;
	}

	/**
	 * 获得当前项目的部署环境标识<br>
	 * 如: local/dev/test/release等
	 * 
	 * @return
	 */
	public static String getEnvironment() {
		return environment;
	}

	/**
	 * 初始化方法.<br>
	 * 如果自己进行依赖管理, 则使用前必须调用本方法进行初始化.<br>
	 * 如果使用Spring进行依赖管理,则需要配置当前方法为对应bean的初始化方法.
	 * 
	 * @return 当前类的唯一实例
	 */
	public static void init() {
		if (environment != null)
			return ;

		// 初始化环境配置文件
		String envCfgPath = "configs/environment.properties";
		instance.loadPropertiesFile(envCfgPath);
		environment = instance.getProperty("config.environment");

		// 加载基础配置文件
		String baseCfgPath = "configs/" + environment + "/config.properties";
		instance.loadPropertiesFile(baseCfgPath);

		// 加载公共消息配置文件
		String messageCfgPath = "configs/" + environment + "/message.properties";
		instance.loadPropertiesFile(messageCfgPath);

		// 初始化Api调用客户端相关配置
		String clientCfgPath = "configs/" + environment + "/config.clients.json";
		CallClientUtil.initClients(ConfigPool.class.getClassLoader().getResourceAsStream(clientCfgPath));

		// 加载Redis相关配置文件
		String redisCfgPath = "configs/" + environment + "/config.redis.properties";
		instance.loadPropertiesFile(redisCfgPath);
		RedisPool.init(); // 初始化Redis的连接池

		// 加载数据库配置文件
		String dbCfgPath = "configs/" + environment + "/config.db.properties";
		instance.loadPropertiesFile(dbCfgPath);
		DataSourcePool.init(); // 初始化数据库数据源
	}

	private void loadPropertiesFile(String filepath) {
		try {
			instance.load(ConfigPool.class.getClassLoader().getResourceAsStream(filepath));
		} catch (Exception e) {
			throw new Error("配置文件[" + filepath + "]未找到，系统无法启动！", e);
		}
	}

}
