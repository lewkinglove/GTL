package cn.lewkinglove.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * 用户数据编解码和加解密相关的公共类
 * @author liujing(lewkinglove@gmail.com)
 */
public class CoderUtil {

	/**
	 * 获取指定字符串的MD5哈希值(全大写)
	 * 
	 * @param input
	 *            要计算hash的字符串
	 * @return 大写的哈希值
	 */
	public static String getMD5(String input) {
		return getMD5(input, false);
	}

	/**
	 * 获取指定字符串的MD5哈希值
	 * 
	 * @param input
	 *            要计算hash的字符串
	 * @param useLowerLetter
	 *            使用小写的字母. true: 使用小写; false: 使用大写
	 * @return 哈希值
	 */
	public static String getMD5(String input, boolean useLowerLetter) {
		try {
			// 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			// 输入的字符串转换成字节数组
			byte[] inputByteArray = input.getBytes("utf-8");
			// inputByteArray是输入字符串转换得到的字节数组
			messageDigest.update(inputByteArray);
			// 转换并返回结果，也是字节数组，包含16个元素
			byte[] resultByteArray = messageDigest.digest();
			// 字节数组转换成字符串返回
			return byteArrayToHex(resultByteArray, useLowerLetter);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 字节数组转换成字符串(字母全大写)
	 * 
	 * @param byteArray
	 *            要转换的字节数组
	 * @return 转换后的字符串, 全大写结果
	 */
	public static String byteArrayToHex(byte[] byteArray) {
		return byteArrayToHex(byteArray, false);
	}

	/**
	 * 字节数组转换成字符串
	 * 
	 * @param byteArray
	 *            要转换的字节数组
	 * @param useLowerLetter
	 *            使用小写的字母. true: 使用小写; false: 使用大写
	 * @return 转换后的字符串
	 */
	public static String byteArrayToHex(byte[] byteArray, boolean useLowerLetter) {
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = null;
		if (useLowerLetter)
			hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		else
			hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray = new char[byteArray.length * 2];

		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}

		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}

	private final static Base64.Encoder encoder = Base64.getEncoder();
	private final static Base64.Decoder decoder = Base64.getDecoder();

	/**
	 * 使用Base64进行编码
	 * 
	 * @param text
	 *            要编码的字符串
	 * @return
	 */
	public static String encodeWithBase64(String text) {
		try {
			byte[] textByte = text.getBytes("UTF-8");
			return encoder.encodeToString(textByte);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用Base64进行解码
	 * 
	 * @param encodedText
	 *            已编码的base64字符
	 * @return
	 */
	public static String decodeWithBase64(String encodedText) {
		try {
			return new String(decoder.decode(encodedText), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
