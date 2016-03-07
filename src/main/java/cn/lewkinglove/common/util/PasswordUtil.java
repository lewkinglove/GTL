package cn.lewkinglove.common.util;

import cn.lewkinglove.common.ConfigPool;

/**
 * 密码工具类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class PasswordUtil {

	/**
	 * 公用密码摘要安全Key<br>
	 * 由配置文件中配置, 程序自动读取初始化.
	 */
	private static String PASSWORD_SEC_KEY;

	static {
		PASSWORD_SEC_KEY = ConfigPool.getInstance().getProperty("config.util.password_sec_key", "lewkinglove@gmail.com");
	}

	/**
	 * 生成密码的摘要文本
	 * 
	 * @param password
	 *            待摘要的密码字符串
	 * @param salt
	 *            摘要使用的自定义盐
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String getPasswordDigest(String password, String salt) throws IllegalArgumentException {
		if (password == null || password.trim().length() == 0)
			throw new IllegalArgumentException("password can't be empty!");

		if (salt == null || salt.trim().length() == 0)
			throw new IllegalArgumentException("salt can't be empty!");

		// 对密码进行明文的浅度预加密
		String res = CoderUtil.getMD5(password, true);

		// 重新生成公用的密码安全钥字符串
		String curPwdSecKey = CoderUtil.getMD5(salt + PASSWORD_SEC_KEY + salt, true);

		//@formatter:off
		//开始进行摘要, 摘要运算次数同curPwdSecKey的长度
		for (int i = curPwdSecKey.length()-1; i > -1; i--) {
			String tempSecKey = curPwdSecKey.substring(i);
			String resDigest = CoderUtil.getMD5(res, true);
			StringBuffer sb = new StringBuffer()
			  .append(resDigest).append(salt).append(tempSecKey)
			  .append(tempSecKey).append(resDigest).append(salt)
			  .append(salt).append(tempSecKey).append(resDigest).reverse();
			
			res = CoderUtil.getMD5(sb.toString(), true);
		}
		//@formatter:on
		return res;
	}

}
