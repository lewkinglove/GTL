package cn.lewkinglove.api.core;

import cn.lewkinglove.common.util.JSONUtil;
import cn.lewkinglove.common.util.MessageUtil;

/**
 * 单次Api请求的响应对象
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class CallResponse {
	
	/**
	 * 响应状态码
	 */
	public String status = "0";

	/**
	 * 状态消息文本
	 */
	public String message = null;

	/**
	 * 响应数据
	 */
	public Object data = null;

	public CallResponse() {
	}

	public CallResponse(String statusCode) {
		this.status = statusCode;
		this.message = MessageUtil.getMessage(statusCode);
	}

	public CallResponse(String statusCode, Object dataObj) {
		this.status = statusCode;
		this.message = MessageUtil.getMessage(statusCode);
		this.data = dataObj;
	}

	public CallResponse(String statusCode, String message) {
		this.status = statusCode;
		this.message = message;
	}

	public CallResponse(String statusCode, String message, Object dataObj) {
		this.status = statusCode;
		this.message = message;
		this.data = dataObj;
	}

	public String toJson() {
		return JSONUtil.instance.toJson(this);
	}

	@Override
	public String toString() {
		return this.toJson();
	}

}
