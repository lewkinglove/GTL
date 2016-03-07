package cn.lewkinglove.common.util;

import java.util.Random;

/**
 * 随机数工具类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class RandomUtil {
	private static Random random = null;
	static {
		RandomUtil.random = new Random(System.currentTimeMillis());
	}

	public static long getLong() {
		return RandomUtil.random.nextLong();
	}

	public static double getDouble() {
		return RandomUtil.random.nextDouble();
	}

	public static float getFloat() {
		return RandomUtil.random.nextFloat();
	}

	public static boolean getBoolean() {
		return RandomUtil.random.nextBoolean();
	}

	public static int getInt() {
		return RandomUtil.random.nextInt();
	}

	/**
	 * 返回0到指定数值之间的随机数(包含0, 但是不包含max)
	 * 
	 * @param max
	 *            最大值, 必须为正数
	 * @return
	 */
	public static int getInt(int max) {
		return RandomUtil.random.nextInt(max);
	}

	/**
	 * 返回一个介于min和max之间的随机数(包含min和max)
	 * 
	 * @param min
	 *            随机范围最小值, 必须为正数
	 * @param max
	 *            随机范围最大值, 必须为负数
	 * @return
	 */
	public static int getInt(int min, int max) {
		return RandomUtil.random.nextInt(max) % (max - min + 1) + min;
	}

}
