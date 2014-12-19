package com.snail.appstore.openapi.base.vo;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.snail.appstore.openapi.base.IPojo;
import com.snail.appstore.openapi.json.JSONArray;
import com.snail.appstore.openapi.json.JSONListObj;
import com.snail.appstore.openapi.json.JSONObject;
import com.snail.appstore.openapi.util.DateUtil;



/**
 * D1XN 表格对象VO
 * 
 * @author zhangqf
 * @version 1.0 2010-7-6
 */
public class TableVO<T> {

	private Logger logger = Logger.getLogger(TableVO.class.getName());

	private PageVO page;
	private String[] header;
	private Map<String, Integer> headerMap;
	private Object data[][];
	private List<T> objectList;
	private Class<T> clazz;

	public TableVO(Class<T> clazz) {
		this.clazz = clazz;
	}

	public TableVO(Class<T> clazz, JSONListObj listObj) {
		this.clazz = clazz;
		this.setPage(listObj.getDisPage());
		this.setHeader(listObj.getHeader());
		this.setData(listObj.getData());
	}

	public PageVO getPage() {
		return page;
	}

	public void setPage(PageVO page) {
		this.page = page;
	}

	public String[] getHeader() {
		return header;
	}

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}

	public void setHeader(String[] header) {
		this.header = header;
		if (this.header != null) {
			headerMap = new HashMap<String, Integer>();
			for (int i = 0; i < header.length; i++) {
				headerMap.put(header[i].toLowerCase(), i);
			}
		}
	}

	public Integer getHeaderColIndex(String name) {
		return headerMap.get(name.toLowerCase());
	}

	/**
	 * 获取数据列表对象
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> getObjectList() {
		if (this.objectList == null && header != null && data != null) {
			objectList = new ArrayList<T>();
			String name = clazz.getSimpleName();
			if ("Map".equals(name)) {
				for (int i = 0; i < data.length; i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (int j = 0; j < header.length; j++) {
						map.put(header[j], data[i][j]);
					}
					this.objectList.add((T) map);
				}
			} else if ("List".equals(name)) {
				for (int i = 0; i < data.length; i++) {
					this.objectList.add((T) Arrays.asList(data[i]));
				}
			} else if (name.endsWith("[]")) {
				for (int i = 0; i < data.length; i++) {
					this.objectList.add((T) data[i]);
				}
			} else {
				Map<Method, Class> methodMap = getClassSetMethod(clazz);
				for (int i = 0; i < data.length; i++) {
					Object obj = getObject(i, clazz, methodMap);
					if (obj != null) {
						this.objectList.add((T) obj);
					}
				}
			}
		}
		return this.objectList;
	}

	/**
	 * 获取列名所在数据列的索引
	 * 
	 * @param headerName
	 *            列名
	 * @return 找到的列索引
	 */
	public int getHeaderIndex(String headerName) {
		Integer index = getHeaderColIndex(headerName);
		if (index == null) {
			return 0;
		} else {
			return index;
		}
	}

	/**
	 * 从指定的列值，返回符合结果的列值
	 * 
	 * @param keyHeader
	 *            查找列名
	 * @param keyVal
	 *            查找列值
	 * @param resultHeader
	 *            结果列名
	 * @return 查找到符合条件的第一个值
	 */
	public Object findData(String keyHeader, String keyVal, String resultHeader) {
		Integer keyIndex = getHeaderColIndex(keyHeader);
		Integer resultIndex = getHeaderColIndex(resultHeader);
		if (keyIndex == null || resultIndex == null || keyVal == null) {
			return null;
		}
		for (int i = 0; i < data.length; i++) {
			if (keyVal.equals(String.valueOf(data[i][keyIndex]))) {
				return data[i][resultIndex];
			}
		}
		return null;
	}

	/**
	 * 从指定的列值，返回符合结果的列值的集合
	 * 
	 * @param keyHeader
	 *            查找列名
	 * @param keyVal
	 *            查找列值
	 * @param resultHeader
	 *            结果列名
	 * @return 查找到符合条件的结果列值集合
	 */
	public List<Object> findDatas(String keyHeader, String keyVal, String resultHeader) {
		Integer keyIndex = getHeaderColIndex(keyHeader);
		Integer resultIndex = getHeaderColIndex(resultHeader);
		if (keyIndex == null || resultIndex == null || keyVal == null) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < data.length; i++) {
			if (keyVal.equals(String.valueOf(data[i][keyIndex]))) {
				list.add(data[i][resultIndex]);
			}
		}
		return list;
	}

	/**
	 * 查找指定符合指定列值的对象
	 * 
	 * @param keyHeader
	 *            查找列名
	 * @param keyVal
	 *            查找列值
	 * @param clazz
	 *            返回对象
	 * @return 符合条件的行
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T findRowData(String keyHeader, String keyVal, Class<T> clazz) {
		Integer keyIndex = getHeaderColIndex(keyHeader);
		if (keyIndex == null || keyVal == null) {
			return null;
		}
		String name = clazz.getSimpleName();
		Map<Method, Class> methodMap = getClassSetMethod(clazz);
		for (int i = 0; i < data.length; i++) {
			if (keyVal.equals(String.valueOf(data[i][keyIndex]))) {
				if ("Map".equals(name)) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (int j = 0; j < header.length; j++) {
						map.put(header[j], data[i][j]);
					}
					return (T) map;
				} else if ("List".equals(name)) {
					return (T) Arrays.asList(data[i]);
				} else if (name.endsWith("[]")) {
					return (T) data[i];
				} else {
					return this.getObject(i, clazz, methodMap);
				}
			}
		}
		return null;
	}

	/**
	 * 查找指定符合指定列值的对象集合
	 * 
	 * @param keyHeader
	 *            查找列名
	 * @param keyVal
	 *            查找列值
	 * @param clazz
	 *            返回对象
	 * @return 符合条件的行集合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findRowDatas(String keyHeader, String keyVal, Class<T> clazz) {
		Integer keyIndex = getHeaderColIndex(keyHeader);
		if (keyIndex == null || keyVal == null) {
			return null;
		}
		String name = clazz.getSimpleName();

		List<T> list = new ArrayList<T>();
		Map<Method, Class> methodMap = getClassSetMethod(clazz);
		for (int i = 0; i < data.length; i++) {
			if (keyVal.equals(String.valueOf(data[i][keyIndex]))) {
				if ("Map".equals(name)) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (int j = 0; j < header.length; j++) {
						map.put(header[j], data[i][j]);
					}
					list.add((T) map);
				} else if ("List".equals(name)) {
					list.add((T) Arrays.asList(data[i]));
				} else if (name.endsWith("[]")) {
					list.add((T) data[i]);
				} else {
					T obj = getObject(i, clazz, methodMap);
					if (obj != null) {
						list.add(obj);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 获取对象SET方法
	 * 
	 * @param clazz
	 * @return 方法对象及方法参数类
	 */
	@SuppressWarnings("rawtypes")
	private Map<Method, Class> getClassSetMethod(Class<T> clazz) {
		Method[] methods = clazz.getMethods();

		Map<Method, Class> map = new HashMap<Method, Class>();
		for (Method m : methods) {
			if (m.getName().startsWith("set")) {
				map.put(m, m.getParameterTypes()[0]);
			}
		}

		return map;
	}

	/**
	 * 转换为对象
	 * 
	 * @param i
	 *            数据行索引
	 * @param clazz
	 *            对象类型
	 * @return 转换后的对象
	 */
	@SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
	private <T> T getObject(int i, Class<T> clazz, Map<Method, Class> methodMap) {
		try {
			Object obj = clazz.newInstance();
			Set<Entry<Method, Class>> set = methodMap.entrySet();
			for (Entry<Method, Class> e : set) {
				Method m = e.getKey();
				Class paramClazz = e.getValue();
				Integer colIndex = getHeaderColIndex(e.getKey().getName().substring(3));
				if (colIndex == null) {
					if (paramClazz.getPackage() != null && !paramClazz.getPackage().getName().startsWith("java")) {
						Object inObj = paramClazz.newInstance();
						if (inObj instanceof IPojo) {
							inObj = getObject(i, paramClazz, this.getClassSetMethod(paramClazz));
							m.invoke(obj, inObj);
						}
					}
					continue;
				}

				Object value = data[i][colIndex];
				if (JSONObject.NULL.equals(value)) {
					continue;
				}

				String paramTypeName = paramClazz.getSimpleName();
				try {
					if (value instanceof String) {
						String val = (String) value;
						if (val.isEmpty()) {
							continue;
						}
						if ("String".equals(paramTypeName)) {
							m.invoke(obj, val);
						} else if ("Integer".equals(paramTypeName)) {
							m.invoke(obj, Integer.valueOf(val));
						} else if ("Long".equals(paramTypeName)) {
							m.invoke(obj, Long.valueOf(val));
						} else if ("Timestamp".equals(paramTypeName)) {
							m.invoke(obj, DateUtil.getTimestamp(val));
						} else if ("Double".equals(paramTypeName)) {
							m.invoke(obj, Double.valueOf(val));
						} else if ("Float".equals(paramTypeName)) {
							m.invoke(obj, Float.valueOf(val));
						} else if ("Boolean".equals(paramTypeName)) {
							m.invoke(obj, Boolean.valueOf(val));
						} else if ("Short".equalsIgnoreCase(paramTypeName)) {
							m.invoke(obj, Short.valueOf(val));
						} else if ("Date".equals(paramTypeName)) {
							m.invoke(obj, DateUtil.parseTime(val));
						} else if ("JSONObject".equals(paramTypeName)) {
							m.invoke(obj, new JSONObject(val));
						} else if ("JSONArray".equals(paramTypeName)) {
							m.invoke(obj, new JSONArray(val));
						} else if ("HashMap".equals(paramTypeName)) {
							Object map = value;
							JSONObject jo = new JSONObject(map.toString());
							m.invoke(obj, jo.getMap());
						} else {
							m.invoke(obj, data[i][colIndex]);
						}
					} else {
						if ("String".equals(paramTypeName)) {
							m.invoke(obj, value.toString());
						} else if ("Integer".equals(paramTypeName)) {
							if (value instanceof Integer) {
								m.invoke(obj, value);
							} else {
								m.invoke(obj, ((Number) value).intValue());
							}
						} else if ("Long".equals(paramTypeName)) {
							if (value instanceof Long) {
								m.invoke(obj, value);
							} else {
								m.invoke(obj, ((Number) value).longValue());
							}
						} else if ("Short".equals(paramTypeName)) {
							if (value instanceof Short) {
								m.invoke(obj, value);
							} else {
								m.invoke(obj, ((Number) value).shortValue());
							}
						} else if ("Double".equals(paramTypeName)) {
							if (value instanceof Double) {
								m.invoke(obj, value);
							} else {
								m.invoke(obj, ((Number) value).doubleValue());
							}
						} else if ("Timestamp".equals(paramTypeName)) {
							if (value instanceof Date) {
								m.invoke(obj, new Timestamp(((Date) value).getTime()));
							} else {
								m.invoke(obj, value);
							}
						} else if ("Date".equals(paramTypeName)) {
							Date date = (Date) value;
							if (m.getParameterTypes()[0].equals(java.sql.Date.class)) {
								m.invoke(obj, new java.sql.Date(date.getTime()));
							} else {
								m.invoke(obj, date);
							}
						} else if ("Object[]".equals(paramTypeName)) {
							Object mongoArray = value;
							if (mongoArray instanceof JSONArray) {
								JSONArray array = (JSONArray) mongoArray;
								Object[] o = new Object[array.length()];
								for (int j = 0; j < array.length(); j++) {
									o[j] = array.get(j);
								}
								m.invoke(obj, (Object) o);// 需要强制转换为Object对象
							}
						} else if ("HashMap".equals(paramTypeName)) {
							Object map = value;
							JSONObject jo = null;
							if (map instanceof JSONObject) {
								jo = (JSONObject) map;
							} else {
								jo = new JSONObject(map.toString());
							}
							m.invoke(obj, jo.getMap());
						} else {
							m.invoke(obj, value);
						}
					}
				} catch (Exception ex) {
					logger.log(Level.SEVERE, "clazz:" + clazz + " paramTypeName:" + paramTypeName + " value:" + value
							+ " objClass:" + value.getClass() + " msg:" + ex.getMessage());
				}
			}
			return (T) obj;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}
}
