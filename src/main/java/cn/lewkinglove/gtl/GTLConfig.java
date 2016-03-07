package cn.lewkinglove.gtl;

import java.util.Properties;

import cn.lewkinglove.common.ConfigPool;

/**
 * 初始化配置文件
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class GTLConfig extends Properties {
	private static final long serialVersionUID = 1L;

	/**
	 * 单例
	 */
	private static GTLConfig instance;

	static {
		GTLConfig.instance = new GTLConfig();
		GTLConfig.instance.init();
	}

	private GTLConfig() {
	}

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static GTLConfig getInstance() {
		return GTLConfig.instance;
	}

	/**
	 * 当前项目的部署环境
	 */
	private String environment;

	public void init() {
		// 加载环境
		String envPropertiesPath = "configs/environment.properties";
		this.loadPropertiesFile(envPropertiesPath);
		
		environment = getProperty("config.environment");
		
		String schedulePath = "configs/" + environment + "/schedule.properties";
		this.loadPropertiesFile(schedulePath);
		
		String tradePath = "configs/" + environment + "/trade.properties";
		this.loadPropertiesFile(tradePath);
	}

	private void loadPropertiesFile(String filepath) {
		try {
			this.load(ConfigPool.class.getClassLoader().getResourceAsStream(filepath));
		} catch (Exception e) {
			throw new Error("配置文件[" + filepath + "]未找到，系统无法启动！", e);
		}
	}

	/**
	 * 当前环境
	 * 
	 * @return 环境值
	 */
	public String getCurrentEnvironment() {
		return this.environment;
	}
}
