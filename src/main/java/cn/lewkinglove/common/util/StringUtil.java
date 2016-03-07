package cn.lewkinglove.common.util;

/**
 * 字符串相关工具类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class StringUtil {

	public static String mysqlEscape(String s) {
		return mysqlEscape(s, false);
	}

	/**
	 * 转义参数
	 * 
	 * @param s
	 *            需要转义的字符串
	 * @param searchMode
	 *            模糊查询
	 * @return 转以后的结果
	 */
	public static String mysqlEscape(String s, boolean searchMode) {
		//@formatter:off
	    if (s == null) {
	        return s;
	    }
	    String[][] chars;
	    if(!searchMode) {
	        chars = new String[][ ]{
	                {"\\",  "\\\\"},
	                {"\0", "\\0"},
	                {"'", "\\'"}, 
	                {"\"",  "\\\""},
	                {"\b",  "\\b"},
	                {"\n",  "\\n"},
	                {"\r",  "\\r"},
	                {"\t",  "\\t"},
	                {"\\Z", "\\\\Z"}, // not sure about this one
	                {"%", "\\%"},     // used in searching
	                {"_", "\\_"}
	        };
	    } else {
	        chars = new String[][ ]{
	                {"\\",  "\\\\"},
	                {"\0", "\\0"},
	                {"'", "\\'"}, 
	                {"\"",  "\\\""},
	                {"\b",  "\\b"},
	                {"\n",  "\\n"},
	                {"\r",  "\\r"},
	                {"\t",  "\\t"},
	                {"\\Z", "\\\\Z"}, // not sure about this one
	        };
	    }
	    for (String[] c : chars) {
	        s = s.replace(c[0], c[1]);
	    }
	    return s;
	  //@formatter:on
	}

	/**
	 * 对StringBuilder中的字符进行替换
	 * 
	 * @param sb
	 *            要操作的StringBuilder对象
	 * @param oldStr
	 *            要替换的字符串
	 * @param newStr
	 *            要替换为的字符串
	 */
	public static void replace(StringBuilder sb, String oldStr, String newStr) {
		int start = -1;
		while ((start = sb.indexOf(oldStr)) > -1) {
			int end = start + oldStr.length();
			sb.replace(start, end, newStr);
		}
	}

	/**
	 * 对StringBuffer中的字符进行替换
	 * 
	 * @param sb
	 *            要操作的StringBuffer对象
	 * @param oldStr
	 *            要替换的字符串
	 * @param newStr
	 *            要替换为的字符串
	 */
	public static void replace(StringBuffer sb, String oldStr, String newStr) {
		int start = -1;
		while ((start = sb.indexOf(oldStr)) > -1) {
			int end = start + oldStr.length();
			sb.replace(start, end, newStr);
		}
	}

}
