package com.snail.appstore.openapi.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.snail.appstore.openapi.base.IPojo;
import com.snail.appstore.openapi.base.vo.PageListVO;
import com.snail.appstore.openapi.base.vo.TableVO;
import com.snail.appstore.openapi.json.JSONArray;
import com.snail.appstore.openapi.json.JSONException;
import com.snail.appstore.openapi.json.JSONListObj;
import com.snail.appstore.openapi.json.JSONObject;

/**
 * JSON格式的转换工具类(注意此工具转换的JSON格式中不能出现注释部分,否则会出现死循环)
 * 
 * @author zhangqf
 * @date 2009-2-4
 * 
 */
public class JSONObjUtil {

	private static Logger logger = Logger.getLogger(JSONObjUtil.class.getName());

	/**
	 * 从JSON列表转换为对象列表
	 * 
	 * @param <T>
	 * @param clazz
	 * @param listObj
	 * @return 对象列表
	 */
	public static <T> List<T> getObjectList(Class<T> clazz, String jsonList) {
		JSONListObj listObj = null;
		try {
			listObj = new JSONListObj(new JSONObject(jsonList));
		} catch (JSONException e) {
			logger.log(Level.SEVERE, e.getMessage() + "   getObjectList jsonList:" + jsonList);
		}
		if (listObj != null) {
			return new TableVO<T>(clazz, listObj).getObjectList();
		} else {
			return null;
		}
	}

	/**
	 * 从JSON列表转换为对象列表
	 * 
	 * @param <T>
	 * @param clazz
	 * @param listObj
	 * @return 对象列表
	 */
	public static <T> List<T> getObjectList(Class<T> clazz, JSONObject jsonList) {
		JSONListObj listObj = new JSONListObj(jsonList);
		return new TableVO<T>(clazz, listObj).getObjectList();
	}

	/**
	 * 从JSON列表转换为分页对象
	 * 
	 * @param clazz
	 * @param jsonList
	 * @return 分页对象
	 */
	public static <T> PageListVO getPageListVO(Class<T> clazz, JSONObject jsonList) {
		JSONListObj listObj = new JSONListObj(jsonList);
		TableVO<T> tableVo = new TableVO<T>(clazz, listObj);
		return new PageListVO(tableVo.getObjectList(), tableVo.getPage());
	}

	/**
	 * 将JSON格式转换为指定对象
	 * 
	 * @param <T>
	 * @param jsonObj
	 * @param clazz
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws JSONException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToObj(JSONObject jsonObj, Class<T> clazz) throws JSONException, Exception {
		if (jsonObj == null) {
			return null;
		}
		Object obj = clazz.newInstance();
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			String name = m.getName();
			if (name.startsWith("set")) {
				name = name.substring(3, 4).toLowerCase() + name.substring(4);
				String paramTypeName = m.getParameterTypes()[0].getSimpleName();

				if ("String".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, jsonObj.optString(name));
					}
				} else if ("Integer".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, jsonObj.optInt(name));
					}
				} else if ("Long".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name)) && !"".equals(jsonObj.opt(name).toString())) {
						m.invoke(obj, jsonObj.optLong(name));
					}
				} else if ("Double".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, jsonObj.optDouble(name));
					}
				} else if ("Float".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, Float.valueOf(jsonObj.optString(name)));
					}
				} else if ("Boolean".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, jsonObj.optBoolean(name));
					}
				} else if ("Short".equalsIgnoreCase(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, (short) jsonObj.optInt(name));
					}
				} else if ("Date".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						Date date;
						Object dateObj = jsonObj.opt(name);
						if (dateObj instanceof Date) {
							date = (Date) dateObj;
						} else {
							String strDate = dateObj.toString();
							if (strDate.length() == 10) {
								date = DateUtil.parseDate(strDate);
							} else {
								date = DateUtil.parseTime(strDate);
							}
						}
						if (m.getParameterTypes()[0].equals(java.sql.Date.class)) {
							m.invoke(obj, new java.sql.Date(date.getTime()));
						} else {
							m.invoke(obj, date);
						}
					}
				} else if ("Timestamp".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						Object time = jsonObj.opt(name);
						if (time instanceof Timestamp) {
							m.invoke(obj, time);
						} else if (time instanceof Date) {
							m.invoke(obj, new Timestamp(((Date) time).getTime()));
						} else {
							m.invoke(obj, DateUtil.getTimestamp(jsonObj.optString(name)));
						}
					}
				} else if ("JSONObject".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, jsonObj.optJSONObject(name));
					}
				} else if ("JSONArray".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						m.invoke(obj, jsonObj.optJSONArray(name));
					}
				} else if ("Object[]".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						Object mongoArray = jsonObj.opt(name);
						if (mongoArray instanceof JSONArray) {
							JSONArray array = (JSONArray) mongoArray;
							Object[] o = new Object[array.length()];
							for (int i = 0; i < array.length(); i++) {
								o[i] = array.get(i);
							}
							m.invoke(obj, (Object) o);// 需要强制转换为Object对象
						}
					}
				} else if ("HashMap".equals(paramTypeName)) {
					if (!JSONObject.NULL.equals(jsonObj.opt(name))) {
						Object map = jsonObj.opt(name);
						JSONObject jo = null;
						if (map instanceof JSONObject) {
							jo = (JSONObject) map;
						} else {
							jo = new JSONObject(map.toString());
						}
						m.invoke(obj, jo.getMap());
					}
				} else {
					Object inObj = Class.forName(m.getParameterTypes()[0].getName()).newInstance();
					if (inObj instanceof IPojo) {
						inObj = jsonToObj(jsonObj, m.getParameterTypes()[0]);
						m.invoke(obj, inObj);
					}
				}
			}
		}
		return (T) obj;
	}

}