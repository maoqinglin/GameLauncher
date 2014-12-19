package com.snail.appstore.openapi.base.vo;

import java.util.List;


/**
 * 分页列表对象
 * @author zhangqf
 * @version 1.0 2010-9-26
 */
public class PageListVO {
	private PageVO page;
	private List<?> list;
	
	public PageListVO(){}
	
	public PageListVO(List<?> list, PageVO page){
		this.list = list;
		this.page = page;
	}
	
	public PageVO getPage() {
		return page;
	}
	public void setPage(PageVO page) {
		this.page = page;
	}
	public List<?> getList() {
		return list;
	}
	public void setList(List<?> list) {
		this.list = list;
	}
	
	
}
