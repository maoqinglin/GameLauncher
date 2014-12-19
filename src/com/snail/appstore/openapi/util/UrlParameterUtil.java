package com.snail.appstore.openapi.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static com.snail.appstore.openapi.AppPlatFormConfig.API_KEY;
import static com.snail.appstore.openapi.AppPlatFormConfig.APP_SECRET;
import static com.snail.appstore.openapi.AppPlatFormConfig.HTTP_HOST;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_API_KEY;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_REQ_TIME;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_SIGN;

/**
 * url参数封装Map
 * 
 * @author gewq
 * @version 1.0 2014-6-4
 */
public class UrlParameterUtil {

	private static final SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 封装url请求参数
	 * 
	 * @param parameterMap
	 *            参数Map
	 * 
	 * @return
	 */
	public static void packUrlParameterMap(Map<String, String> parameterMap) {
		parameterMap.put(PARAMETER_REQ_TIME, dateTimeSdf.format(new Date()));
		parameterMap.put(PARAMETER_API_KEY, API_KEY);
		parameterMap.put(PARAMETER_SIGN, SecretCodeUtil.sign(new TreeMap<String, Object>(parameterMap), APP_SECRET));
	}

	/**
	 * 生成Get请求地址
	 * 
	 * @param parameterMap
	 *            参数
	 * @return
	 * @throws Exception
	 */
	public static String generateGetParam(Map<String, String> parameterMap) throws Exception {
//		UrlParameterUtil.packUrlParameterMap(parameterMap);
		String paramerterUrl = HttpUtil.generatorParamString(parameterMap);
		return paramerterUrl;
	}

	/**
	 * 生成请求方式地址
	 * 
	 * @param reqUri
	 *            接口uri
	 * @param parameterMap
	 *            参数键值
	 * @param isGet
	 *            是否是Get请求
	 * @return
	 * @throws Exception
	 */
	private static String generateReqUrl(String reqUri, Map<String, String> parameterMap, boolean isGet) throws Exception {
		String reqUrl = generateHttpUrl(reqUri);
//		UrlParameterUtil.packUrlParameterMap(parameterMap);
		if (!isGet) {
			return reqUrl;
		}
		return reqUrl + "?" + HttpUtil.generatorParamString(parameterMap);

	}

	/**
	 * 生成GET请求方式地址
	 * 
	 * @param reqUri
	 *            接口uri
	 * @param parameterMap
	 *            参数键值
	 * @return
	 * @throws Exception
	 */
	public static String generateGetUrl(String reqUri, Map<String, String> parameterMap) throws Exception {
		return generateReqUrl(reqUri, parameterMap, true);
	}

	/**
	 * 生成POST PUT DELETE请求地址
	 * 
	 * @param reqUri
	 *            接口uri
	 * @param parameterMap
	 *            参数键值
	 * @return
	 * @throws Exception
	 */
	public static String generateUrl(String reqUri, Map<String, String> parameterMap) throws Exception {
		return generateReqUrl(reqUri, parameterMap, false);
	}

	/**
	 * 生成完整请求地址
	 * 
	 * @param reqUri
	 *            接口uri
	 * @return
	 */
	private static String generateHttpUrl(String reqUri) {
		return HTTP_HOST + reqUri;
	}
}
