package com.snail.appstore.openapi.base.vo;

import java.util.List;

import com.snail.appstore.openapi.base.CommonErrorCode;
import com.snail.appstore.openapi.json.JSONException;
import com.snail.appstore.openapi.json.JSONObject;
import com.snail.appstore.openapi.util.JSONObjUtil;

/**
 * JSON 格式的结果集对象
 * 
 * @author gewq
 * @version 1.0 2014-6-18
 */
public class JSONResultVO extends ResultVO {
	private JSONObject resultJson;
	private JSONObject objectJson;
	private Object object;

	/**
	 * 通过返回结果JSON构造结果对象
	 * 
	 * @param sResultJson
	 *            返回结果JSON
	 * @throws JSONException
	 */
	public JSONResultVO(String sResultJson) throws JSONException {
		this.resultJson = new JSONObject(sResultJson);
		int code = resultJson.optInt("code", CommonErrorCode.UNKNOWN_ERROR_1);
		this.setCode(code == 0 ? 1 : code);
		this.setMessage(resultJson.optString("msg"));
		this.setValue(resultJson.optString("val"));
		if (resultJson.has("item")) {
			objectJson = resultJson.optJSONObject("item");
			if (objectJson == null) {
				object = resultJson.opt("item");
				this.setObj(object);
			}
		} else if (resultJson.has("list")) {
			objectJson = resultJson.optJSONObject("list");
		}
	}

	/**
	 * 通过返回结果JSON构造结果对象
	 * 
	 * @param sResultJson
	 *            返回结果JSON
	 * @param clazz
	 *            对象类型
	 * @throws JSONException
	 * @throws Exception
	 */
	public JSONResultVO(String sResultJson, Class<?> clazz) throws JSONException, Exception {
		this.resultJson = new JSONObject(sResultJson);
		int code = resultJson.optInt("code", CommonErrorCode.UNKNOWN_ERROR_1);
		this.setCode(code == 0 ? 1 : code);
		this.setMessage(resultJson.optString("msg"));
		this.setValue(resultJson.optString("val"));
		if (resultJson.has("item")) {
			objectJson = resultJson.optJSONObject("item");
			if (objectJson == null) {
				object = resultJson.opt("item");
				this.setObj(object);
			} else {
				object = JSONObjUtil.jsonToObj(objectJson, clazz);
				this.setObj(object);
			}
		} else if (resultJson.has("list")) {
			objectJson = resultJson.optJSONObject("list");
			if (objectJson.has("page")) {
				object = JSONObjUtil.getPageListVO(clazz, objectJson);
			} else {
				object = JSONObjUtil.getObjectList(clazz, objectJson);
			}
			this.setObj(object);
		}
	}

	/**
	 * 获取当前对象
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws Exception
	 *             返回当前对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> clazz) throws Exception {
		if (object != null) {
			return (T) object;
		} else if (objectJson != null) {
			object = JSONObjUtil.jsonToObj(objectJson, clazz);
			return (T) object;
		}
		return null;
	}

	/**
	 * 获取当前对象列表
	 * 
	 * @param <T>
	 * @param clazz
	 * @return 返回对象列表
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getObjectList(Class<T> clazz) {
		if (object != null) {
			return (List<T>) object;
		} else if (objectJson != null) {
			object = JSONObjUtil.getObjectList(clazz, objectJson);
			return (List<T>) object;
		}
		return null;
	}
}
