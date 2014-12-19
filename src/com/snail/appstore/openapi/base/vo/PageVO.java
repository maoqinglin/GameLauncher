package com.snail.appstore.openapi.base.vo;

import java.io.Serializable;

/**
 * 分页对象
 * 
 * @author zhangqf
 * @version 1.0 2010-5-5
 */

public class PageVO implements Serializable {

	private static final long serialVersionUID = -4684071708405300042L;
	private int iPageRowCount; // 每页行数
	private int iRequestPageNum;// 请求页数
	private int iTotalPageCount;// 总页数
	private int iTotalRowCount; // 总行数

	public PageVO () {
		this(10, 1);
	}
	
	public PageVO (int iPageRowCount, int iRequestPageNum) {
		this.setIPageRowCount(iPageRowCount);
		this.setIRequestPageNum(iRequestPageNum);
	}
	
	public int getIPageRowCount() {
		return iPageRowCount;
	}

	public void setIPageRowCount(int pageRowCount) {
		iPageRowCount = pageRowCount;
	}

	public int getIRequestPageNum() {
		if (iRequestPageNum > iTotalPageCount)
			iRequestPageNum = iTotalPageCount;
		else if (iRequestPageNum < 1) iRequestPageNum = 1;
		return iRequestPageNum;
	}

	public void setIRequestPageNum(int requestPageNum) {
		iRequestPageNum = requestPageNum;
	}

	public int getITotalPageCount() {
		return iTotalPageCount;
	}

	public void setITotalPageCount(int totalPageCount) {
		iTotalPageCount = totalPageCount;
	}

	public int getITotalRowCount() {
		return iTotalRowCount;
	}

	public void setITotalRowCount(int totalRowCount) {
		this.iTotalRowCount = totalRowCount;
		this.iTotalPageCount = (int) Math.ceil((this.iTotalRowCount + 0.0) / this.iPageRowCount);
	}
}
