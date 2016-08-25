package com.kelvem.crawler.test;

import com.kelvem.crawler.model.HtmlSourceModel;
import com.kelvem.crawler.tools.S2_Coupon_MeliuzDetail;

public class TestStoreSepha {
	
	public static String site_name = "meliuz";
	
	public static String url_sitemap = "https://www.meliuz.com.br/desconto";
	public static String url_company_sepha = "https://www.meliuz.com.br/desconto/sepha";
	
	
	public static String[][] rules = {
			{"<li class=\"txt--truncate txt--\"><a href=\"", "\">"}
	};
	
	public static void main(String[] args) {

		try {
			
			HtmlSourceModel htmlSource = new HtmlSourceModel();
			htmlSource.setHtmlType("colpon");
			htmlSource.setMainDomain("meliuz.com.br");
			htmlSource.setSubDomain("www");
			htmlSource.setName("sepha");
			htmlSource.setUrl("https://www.meliuz.com.br/desconto/sepha");
			
				
			S2_Coupon_MeliuzDetail.crawler(htmlSource);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}






















