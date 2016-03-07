package cn.lewkinglove.api.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.lewkinglove.common.util.JSONUtil;

import com.google.gson.stream.JsonReader;

/**
 * 调用者客户端工具类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class CallClientUtil {

	private static List<CallClient> clientList;

	/**
	 * 初始化调用客户端的列表
	 * 
	 * @param in
	 *            包含Json格式配置文件的输入流
	 */
	public static void initClients(InputStream in) {
		List<String> list = JSONUtil.instance.fromJson(new JsonReader(new InputStreamReader(in)), ArrayList.class);

		CallClientUtil.clientList = new ArrayList<CallClient>();
		for (int i = 0; i < list.size(); i++) {
			Object clientStr = list.get(i);
			CallClient client = JSONUtil.instance.fromJson(clientStr.toString(), CallClient.class);
			CallClientUtil.clientList.add(client);
		}
	}

	/**
	 * 根据调用者身份标识获取调用者客户端对象
	 * 
	 * @param callClientUA
	 *            调用者身份标识
	 * @return 未找到, 则返回null
	 */
	public static CallClient getCallClientByUA(String callClientUA) {
		for (CallClient client : CallClientUtil.clientList) {
			if (client.getUA().equals(callClientUA))
				return client;
		}
		return null;
	}

	/**
	 * 检测指定IP是否在指定标识的客户端IP允许范围内
	 * 
	 * @param ipAddr
	 *            要检查的IP地址
	 * @param callClientUA
	 *            要检查的调用客户端身份标识
	 * @return 允许: true; 不允许: false;
	 */
	public static boolean isIPAllowed(String ipAddr, String callClientUA) {
		return CallClientUtil.isIPAllowed(ipAddr, CallClientUtil.getCallClientByUA(callClientUA));
	}

	/**
	 * 检测指定IP是否在指定的客户端IP允许范围内
	 * 
	 * @param ipAddr
	 *            要检查的IP地址
	 * @param client
	 *            要检查的调用客户端对象
	 * @return 允许: true; 不允许: false;
	 */
	public static boolean isIPAllowed(String ipAddr, CallClient client) {
		if (client == null)
			return false;

		String[] allowedIp = client.getAllowedIP();

		// 允许IP列表如果为空, 则默认为禁止所有请求
		if (allowedIp == null || allowedIp.length == 0)
			return false;

		// 如果该客户端允许所有IP发起请求进行调用
		if (allowedIp.length == 1 && allowedIp[0].equals("*"))
			return true;

		// 遍历Allowed IP List, 检查是否在IP列表内
		for (String ip : allowedIp) {
			if (ip.equals(ipAddr))
				return true;
		}

		return false;
	}

	/**
	 * 检测指定Api是否在指定标识的客户端Api调用允许范围内
	 * 
	 * @param api
	 *            要检查的Api标识
	 * @param callClientUA
	 *            要检查的调用客户端身份标识
	 * @return 允许: true; 不允许: false;
	 */
	public static boolean isApiAllowed(String api, String callClientUA) {
		return isApiAllowed(api, CallClientUtil.getCallClientByUA(callClientUA));
	}

	/**
	 * 检测指定Api是否在指定的客户端Api调用允许范围内
	 * 
	 * @param api
	 *            要检查的Api标识
	 * @param client
	 *            要检查的调用客户端对象
	 * @return 允许: true; 不允许: false;
	 */
	public static boolean isApiAllowed(String api, CallClient client) {
		if (client == null)
			return false;

		String[] allowedApi = client.getAllowedAPI();

		// 允许调用的API列表如果为空, 则默认为禁止所有Api调用
		if (allowedApi == null || allowedApi.length == 0)
			return false;

		// 如果该客户端允许调用所有Api.
		if (allowedApi.length == 1 && allowedApi[0].equals("*"))
			return true;

		// 遍历Allowed Api List, 检查是否在列表内
		for (String apiRule : allowedApi) {
			// 如果完全相等, 则直接返回true
			if (apiRule.equals(api))
				return true;
			else if (apiRule.indexOf("*") > -1) {
				// 如果不完全相等, 但是当前Api项目有通配符
				// 则根据通配符进行规则匹配
				apiRule = apiRule.substring(0, apiRule.length() - 1);
				if (api.startsWith(apiRule) == true)
					return true;
			}
		}
		return false;
	}

}
