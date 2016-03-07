package api;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.management.openmbean.KeyAlreadyExistsException;

public class ServiceApiAlias {
	/**
	 * 存储所有接口的别名数据
	 * key		=	alias name
	 * value	=	original api path
	 */
	private static HashMap<String, String> AliasConfig = new LinkedHashMap<String, String>();
	
	/**
	 * 添加别名
	 * @param aliasPath		别名Path
	 * @param originalPath	原始的ApiPath
	 */
	public static void addAlias(String aliasPath, String originalPath){
		if(AliasConfig.containsKey(aliasPath))
			throw new KeyAlreadyExistsException("接口别名: " + aliasPath + "，已经被使用。");
		
		AliasConfig.put(aliasPath, originalPath);
	}
	
	/**
	 * 获取原始Path
	 * @param aliasPath 要获取原始Path的别名Path
	 * @return
	 */
	public static String getOriginal(String aliasPath){
		return AliasConfig.get(aliasPath);
	}
	
}
