package com.snail.appstore.openapi.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.snail.appstore.openapi.base.vo.PageVO;



/**
 * JSON 列表对象
 * 
 * @author zhangqf
 * @version 1.0 2010-3-25
 */
public class JSONListObj {

	private Logger logger = Logger.getLogger(JSONListObj.class.getName());

	private JSONArray dataArr;
	private JSONArray headerArr;
	private JSONObject page;
	private Map<String, Integer> headMap = new HashMap<String, Integer>();

	public JSONListObj(JSONObject list) {
		dataArr = list.optJSONArray("data");
		headerArr = list.optJSONArray("header");
		page = list.optJSONObject("page");
	}

	/**
	 * 获取指定行数和指定列的值
	 * 
	 * @param row
	 *            行数
	 * @param colName
	 *            列名
	 * @return
	 */
	public String getString(int row, String colName) {
		int index = getColIndex(colName);
		if (index == -1) {
			return null;
		}
		if (dataArr.optJSONArray(row) == null) {
			return null;
		}
		return dataArr.optJSONArray(row).optString(index);
	}

	/**
	 * 获取指定行数和指定列的值
	 * 
	 * @param row
	 *            行数
	 * @param colName
	 *            列名
	 * @return
	 */
	public Date getDate(int row, String colName) {
		int index = getColIndex(colName);
		if (index == -1) {
			return null;
		}
		if (dataArr.optJSONArray(row) == null) {
			return null;
		}
		return dataArr.optJSONArray(row).optDate(index);
	}

	/**
	 * 获取指定行数和指定列的值
	 * 
	 * @param row
	 *            行数
	 * @param colName
	 *            列名
	 * @return
	 */
	public Long getLong(int row, String colName) {
		int index = getColIndex(colName);
		if (index == -1) {
			return null;
		}
		if (dataArr.optJSONArray(row) == null) {
			return null;
		}
		return dataArr.optJSONArray(row).optLong(index);
	}

	/**
	 * 获取指定行数和指定列的值
	 * 
	 * @param row
	 *            行数
	 * @param colName
	 *            列名
	 * @return
	 */
	public Double getDouble(int row, String colName) {
		int index = getColIndex(colName);
		if (index == -1) {
			return null;
		}
		if (dataArr.optJSONArray(row) == null) {
			return null;
		}
		return dataArr.optJSONArray(row).optDouble(index);
	}

	/**
	 * 获取指定行数和指定列的值
	 * 
	 * @param row
	 *            行数
	 * @param colName
	 *            列名
	 * @return
	 */
	public Integer getInteger(int row, String colName) {
		int index = getColIndex(colName);
		if (index == -1) {
			return null;
		}
		if (dataArr.optJSONArray(row) == null) {
			return null;
		}
		return dataArr.optJSONArray(row).optInt(index);
	}

	/**
	 * 获取行数
	 * 
	 * @return
	 */
	public int getRows() {
		if (dataArr == null) {
			return 0;
		}
		return dataArr.length();
	}

	/**
	 * 根据指定列的值,找到另一个列的值
	 * 
	 * @param colName
	 *            列名称
	 * @param val
	 *            列值
	 * @param findColName
	 *            查找列名称
	 * @return
	 */
	public String findColVal(String colName, String val, String findColName) {
		int index = this.getColIndex(colName);
		int findIndex = this.getColIndex(findColName);
		if (index == -1 || findIndex == -1 || val == null) {
			return null;
		}
		for (int i = 0; i < dataArr.length(); i++) {
			if (val.equals(dataArr.optJSONArray(i).optString(index))) {
				return dataArr.optJSONArray(i).optString(findIndex);
			}
		}
		return null;
	}

	/**
	 * 根据指定列的值，找到另外列值的数组
	 * 
	 * @param colName
	 *            列名称
	 * @param val
	 *            列值
	 * @param findColName
	 *            查找列名称
	 * @return
	 */
	public List<String> findColListVal(String colName, String val, String findColName) {
		List<String> list = new ArrayList<String>();
		int index = this.getColIndex(colName);
		int findIndex = this.getColIndex(findColName);
		if (index == -1 || findIndex == -1 || val == null) {
			return null;
		}
		for (int i = 0; i < dataArr.length(); i++) {
			if (val.equals(dataArr.optJSONArray(i).optString(index))) {
				list.add(dataArr.optJSONArray(i).optString(findIndex));
			}
		}
		return list;
	}

	/**
	 * 根据指定列的值，找到符合条件的JSONArray
	 * 
	 * @param colName
	 *            列名称
	 * @param val
	 *            列值
	 * @return
	 */
	public List<JSONArray> findJSONArrayVal(String colName, String val) {
		List<JSONArray> list = new ArrayList<JSONArray>();
		int index = this.getColIndex(colName);
		if (index == -1 || val == null) {
			return null;
		}
		for (int i = 0; i < dataArr.length(); i++) {
			if (val.equals(dataArr.optJSONArray(i).optString(index))) {
				list.add(dataArr.optJSONArray(i));
			}
		}
		return list;
	}

	/**
	 * 获取指定行对象
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, Object> getObjectByIndex(int index) {
		if (dataArr == null || headerArr == null || index > dataArr.length()) {
			return null;
		}

		JSONArray valArray = dataArr.optJSONArray(index);
		if (valArray == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < headerArr.length(); i++) {
			map.put(headerArr.optString(i), valArray.opt(i));
		}
		return map;
	}

	public JSONArray findHeaderArr() {
		return headerArr;
	}

	/**
	 * 根据指定KEY和VAL生成MAP
	 * 
	 * @param keyName
	 *            健名称
	 * @param colName
	 *            值名称
	 * @return
	 */
	public Map<String, String> toMap(String keyName, String valName) {
		int keyIndex = this.getColIndex(keyName);
		int valIndex = this.getColIndex(valName);
		if (keyIndex == -1 || valIndex == -1) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < dataArr.length(); i++) {
			JSONArray arr = dataArr.optJSONArray(i);
			if (arr != null) {
				map.put(arr.optString(keyIndex), arr.optString(valIndex));
			}
		}
		return map;
	}

	/**
	 * 根据列表返回一列值的集合
	 * 
	 * @param colName
	 * @return
	 */
	public List<Date> getDateListByColName(String colName) {
		int index = this.getColIndex(colName);
		if (index == -1) {
			return null;
		}
		List<Date> list = new ArrayList<Date>();
		for (int i = 0; i < dataArr.length(); i++) {
			list.add(dataArr.optJSONArray(i).optDate(index));
		}
		return list;
	}

	/**
	 * 根据列表返回一列值的集合
	 * 
	 * @param colName
	 * @return
	 */
	public List<String> getStringListByColName(String colName) {
		int index = this.getColIndex(colName);
		if (index == -1) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < dataArr.length(); i++) {
			list.add(dataArr.optJSONArray(i).optString(index));
		}
		return list;
	}

	/**
	 * 根据列表返回一列值的集合
	 * 
	 * @param colName
	 * @return
	 */
	public List<Long> getLongListByColName(String colName) {
		int index = this.getColIndex(colName);
		if (index == -1) {
			return null;
		}
		List<Long> list = new ArrayList<Long>();
		for (int i = 0; i < dataArr.length(); i++) {
			list.add(dataArr.optJSONArray(i).optLong(index));
		}
		return list;
	}

	/**
	 * 根据列表返回一列值的集合
	 * 
	 * @param colName
	 * @return
	 */
	public List<Double> getDoubleListByColName(String colName) {
		int index = this.getColIndex(colName);
		if (index == -1) {
			return null;
		}
		List<Double> list = new ArrayList<Double>();
		for (int i = 0; i < dataArr.length(); i++) {
			list.add(dataArr.optJSONArray(i).optDouble(index));
		}
		return list;
	}

	/**
	 * 根据列表返回一列值的集合
	 * 
	 * @param colName
	 * @return
	 */
	public List<Integer> getIntegerListByColName(String colName) {
		int index = this.getColIndex(colName);
		if (index == -1) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < dataArr.length(); i++) {
			list.add(dataArr.optJSONArray(i).optInt(index));
		}
		return list;
	}

	/**
	 * 获取分页对象
	 * 
	 * @return
	 */
	public PageVO getDisPage() {
		if (page != null) {
			try {
				PageVO p = new PageVO();
				p.setIPageRowCount(page.getInt("iPageRowCount"));
				p.setIRequestPageNum(page.getInt("iRequestPageNum"));
				p.setITotalPageCount(page.getInt("iTotalPageCount"));
				p.setITotalRowCount(page.getInt("iTotalRowCount"));
				return p;
			} catch (JSONException e) {
				logger.log(Level.SEVERE, "page:" + page.toString(), e.getClass().getSimpleName());
				return null;
			}

		}
		return null;
	}

	/**
	 * 获取头数组对象
	 * 
	 * @return
	 */
	public String[] getHeader() {
		if (this.headerArr == null) {
			return null;
		}
		Object[] objs = this.headerArr.toList().toArray();
		String[] strs = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			strs[i] = String.valueOf(objs[i]);
		}
		return strs;
	}

	/**
	 * 获取数据对象数组
	 * 
	 * @return
	 */
	public Object[][] getData() {
		int row = this.getRows();
		if (row > 0) {
			Object[][] data = new Object[row][this.headerArr.length()];
			for (int i = 0; i < row; i++) {
				JSONArray rowData = this.dataArr.optJSONArray(i);
				for (int j = 0; j < rowData.length(); j++) {
					data[i][j] = rowData.opt(j);
				}
			}
			return data;
		}
		return null;
	}

	/**
	 * 获取列所在的索引
	 * 
	 * @param headerName
	 *            名称
	 * @return -1: 没有找到
	 */
	public int getColIndex(String colName) {
		Integer index = headMap.get(colName);
		if (index != null) {
			return index;
		}
		if (dataArr == null || headerArr == null || colName == null) {
			return -1;
		}

		try {
			for (int i = 0; i < headerArr.length(); i++) {
				if (headerArr.getString(i).equals(colName)) {
					headMap.put(colName, i);
					return i;
				}
			}
		} catch (JSONException e) {
			logger
				.log(Level.SEVERE, "jsonStr:" + headerArr.toString() + " headerName:" + colName, e.getClass().getSimpleName());
			return -1;
		}
		return -1;
	}

}
