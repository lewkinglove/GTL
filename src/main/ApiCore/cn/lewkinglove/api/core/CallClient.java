package cn.lewkinglove.api.core;

/**
 * 调用者客户端类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class CallClient {

	/**
	 * 身份标识字符串
	 */
	private String ua;

	/**
	 * 签名密钥
	 */
	private String signKey;

	/**
	 * IP调用限制范围
	 */
	private String[] allowedIP;

	/**
	 * Api调用限制范围
	 */
	private String[] allowedAPI;

	// TODO 二期实现接口调用次数的限制
	// private int callTimesMinuteLimit;
	// private int callTimesDayLimit;
	// private int callTimesMonthLimit;

	public CallClient(String ua, String signKey, String[] allowedIP, String[] allowedAPI) {
		this.ua = ua;
		this.signKey = signKey;
		this.allowedIP = allowedIP;
		this.allowedAPI = allowedAPI;
	}

	/**
	 * 获取身份标识字符串
	 * 
	 * @return
	 */
	public String getUA() {
		return ua;
	}

	/**
	 * 获取签名密钥
	 * 
	 * @return
	 */
	public String getSignKey() {
		return signKey;
	}

	/**
	 * 获取IP调用限制范围
	 * 
	 * @return
	 */
	public String[] getAllowedIP() {
		return allowedIP;
	}

	/**
	 * 获取Api调用限制范围
	 * 
	 * @return
	 */
	public String[] getAllowedAPI() {
		return allowedAPI;
	}

}
