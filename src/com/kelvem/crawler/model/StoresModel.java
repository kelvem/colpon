package com.kelvem.crawler.model;

import com.kelvem.common.database.sqlite.ModelHandler;

public class StoresModel extends MysqlBaseModel {

	public static void main(String[] args) {
		
		ModelHandler.dropTable(StoresModel.class);
		ModelHandler.createTable(StoresModel.class);
		
//		HtmlSourceModel h = new HtmlSourceModel();
//		h.setText("''");
//		h.setHtmlId(1);
//		addRecord(h);
	}

	public Integer id = null;
	public String initials = null;
	public Integer categoryId = null;
	public String name = null;
	public String description = null;
	public String link = null;
	public String titleslug = null;
	public String createdAt = null;
	public String updatedAt = null;
	
	
	
	
	
	
}
