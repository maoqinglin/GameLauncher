package com.snail.appstore.openapi.base.vo;

/**
 * 错误值对象(用于返回操作结果)
 * 
 * @author songdawei
 * @version 1.0
 * @date 2008-07-22
 */
public class ResultVO {
	public final static int SUCCEED_CODE = 1; // 返回成功处理结果状态码为1

	private int code; // 错误码
	private String message; // 错误描述
	private String value; // 返回值
	private Object obj; // 返回对象

	public ResultVO() {}

	public ResultVO(int code) {
		this.code = code;
	}

	public ResultVO(String value) {
		this.code = SUCCEED_CODE;
		this.value = value;
	}

	public ResultVO(String value, Object obj) {
		this.code = SUCCEED_CODE;
		this.value = value;
		this.obj = obj;
	}

	public ResultVO(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResultVO(int code, Exception e) {
		this.code = code;
		this.obj = e;
	}

	public ResultVO(int code, String message, Object obj) {
		this.code = code;
		this.message = message;
		this.obj = obj;
	}

	public String getMessage() {
		return message;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	/**
	 * 是否为错误结果集对象
	 * 
	 * @return
	 */
	public boolean isErrorVO() {
		return this.code != SUCCEED_CODE;
	}

}
