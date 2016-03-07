package cn.lewkinglove.api.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonSyntaxException;

import cn.lewkinglove.common.util.CoderUtil;
import cn.lewkinglove.common.util.JSONUtil;

/**
 * Api核心认证类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class Authenticator {

	/**
	 * 对Api请求进行认证
	 * 
	 * @return
	 * @throws CallException
	 */
	public static CallRequest authRequest(HttpServletRequest request) throws CallException {
		// -1 请求方式不支持，服务被拒绝。
		if ("POST".equals(request.getMethod().toUpperCase()) == false)
			throw new CallException(Consts.BIZ_IDENTIFIER, "-1");

		// -2 请求参数不完整，服务被拒绝。
		String call;
		String args;
		String sign;
		String clientUA;
		try {
			call = request.getParameter("call");
			args = request.getParameter("args");
			sign = request.getParameter("sign");
			clientUA = request.getParameter("ua");
			if (call == null || call.trim().length() < 5 || args == null || args.trim().length() == 0 || sign == null || sign.trim().length() != 32 || clientUA == null || clientUA.trim().length() == 0) {
				throw new CallException(Consts.BIZ_IDENTIFIER, "-2");
			}
			call = URLDecoder.decode(call, "UTF-8");
			args = URLDecoder.decode(args, "UTF-8");
			clientUA = URLDecoder.decode(clientUA, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new CallException(Consts.BIZ_IDENTIFIER, "-2");
		}

		// -3 调用者身份标识验证失败，服务被拒绝。
		CallClient client = CallClientUtil.getCallClientByUA(clientUA);
		if (client == null) {
			throw new CallException(Consts.BIZ_IDENTIFIER, "-3");
		}

		// -4 请求来源IP不在允许范围内，服务被拒绝。
		String remoteIP = request.getRemoteAddr();
		if (false == CallClientUtil.isIPAllowed(remoteIP, client)) {
			throw new CallException(Consts.BIZ_IDENTIFIER, "-4");
		}

		// -5 请求签名认证失败，服务被拒绝。
		if (false == sign.equalsIgnoreCase(Authenticator.creatRequestSign(call, args, client))) {
			throw new CallException(Consts.BIZ_IDENTIFIER, "-5");
		}

		// -6 请求的API不在授权范围内，服务被拒绝。
		if (false == CallClientUtil.isApiAllowed(call, client)) {
			throw new CallException(Consts.BIZ_IDENTIFIER, "-6");
		}
		
		// 使用Gson格式化用户的ServiceApi调用参数
		HashMap<String, Object> mapArgs = null;
		try{
			mapArgs = JSONUtil.instance.fromJson(args, HashMap.class);
		} catch (JsonSyntaxException e){
			//参数args无法解析为JSON，服务被拒绝。
			throw new CallException("-2", "参数args无法解析为JSON，服务被拒绝。", e.getCause());
		}

		return new CallRequest(call, mapArgs, sign, client);
	}

	/**
	 * 获取请求签名字符串
	 * 
	 * @param call
	 *            请求的Api名称
	 * @param args
	 *            Api的请求参数
	 * @param client
	 *            当前请求的调用者对象
	 * @return 生成的签名字符串
	 */
	private static String creatRequestSign(String call, String args, CallClient client) {
		String API_SECKEY = client.getUA() + client.getSignKey() + client.getUA();

		return CoderUtil.getMD5(API_SECKEY + call + API_SECKEY + args + API_SECKEY, true);
	}

}
