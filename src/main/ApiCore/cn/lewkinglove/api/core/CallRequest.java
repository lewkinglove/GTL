package cn.lewkinglove.api.core;

import java.util.HashMap;


/**
 * 单次Api请求对象
 * @author liujing(lewkinglove@gmail.com)
 */
public class CallRequest {

	private String call;
	private HashMap<String, Object> args;
	private String sign;
	private CallClient client;
	
	public CallRequest(String call, HashMap<String, Object> args, String sign, CallClient client){
		this.call = call;
		this.args = args;
		this.sign = sign;
		this.client = client;
	}

	/**
	 * 获取请求的Api名称
	 * @return
	 */
	public String getCall() {
		return call;
	}


	/**
	 * 获取Api的请求参数
	 * @return
	 */
	public HashMap<String, Object> getArgs() {
		return args;
	}

	/**
	 * 获取请求签名
	 * @return
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * 获取调用者客户端对象
	 * @return
	 */
	public CallClient getClient() {
		return client;
	}
}
