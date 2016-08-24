package com.kelvem.crawler.model;

import com.kelvem.common.database.sqlite.ModelHandler;

public class OffersModel extends MysqlBaseModel {

	public static void main(String[] args) {
		
		ModelHandler.dropTable(OffersModel.class);
		ModelHandler.createTable(OffersModel.class);
		
//		HtmlSourceModel h = new HtmlSourceModel();
//		h.setText("''");
//		h.setHtmlId(1);
//		addRecord(h);
	}

	public Integer id = null;
	public Integer storeId = null;
	public String type = null;
	public String name = null;
	public String description = null;
	public String link = null;
	public String code = null;
	public Integer status = 1;
	public String confirmDate = null;
	public String ends = "";
	public String createdAt = null;
	public String updatedAt = null;
	

	
	public String key() {
		return this.storeId + "|||" + this.name + "|||" + this.code;
	}
	
	
	
	
	
	
}
