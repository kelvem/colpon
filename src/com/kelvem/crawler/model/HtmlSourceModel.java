package com.kelvem.crawler.model;

import com.kelvem.common.database.sqlite.ModelHandler;

public class HtmlSourceModel extends MysqlBaseModel {

	public static void main(String[] args) {
		
		ModelHandler.dropTable(HtmlSourceModel.class);
		ModelHandler.createTable(HtmlSourceModel.class);
		
//		HtmlSourceModel h = new HtmlSourceModel();
//		h.setText("''");
//		h.setHtmlId(1);
//		addRecord(h);
	}

	private Integer htmlSourceId = null;
	private String htmlType = null;
	private String url = null;
	private String mainDomain = null;
	private String subDomain = null;
	private String name = null;
	private String createTime = null;
	private String updateTime = null;
	private String content = null;
	private Integer errCnt = null;
	
	
	
	public Integer getHtmlSourceId() {
		return htmlSourceId;
	}
	public void setHtmlSourceId(Integer htmlSourceId) {
		this.htmlSourceId = htmlSourceId;
	}
	public String getHtmlType() {
		return htmlType;
	}
	public void setHtmlType(String htmlType) {
		this.htmlType = htmlType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMainDomain() {
		return mainDomain;
	}
	public void setMainDomain(String mainDomain) {
		this.mainDomain = mainDomain;
	}
	public String getSubDomain() {
		return subDomain;
	}
	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getErrCnt() {
		return errCnt;
	}
	public void setErrCnt(Integer errCnt) {
		this.errCnt = errCnt;
	}
	
	
	
}
