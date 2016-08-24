package com.kelvem.crawler.test;

import java.util.List;

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
			List<HtmlSourceModel> list = HtmlSourceModel.queryModel(htmlSource);
			
			System.out.println(list.size());
			
			
			for (int i = 0; i < list.size(); i++) {
				
				try {
					HtmlSourceModel source = list.get(i);
					System.out.println(source.getUrl());
					
					S2_Coupon_MeliuzDetail.crawler(source);
					
				} catch (Exception e) {
					System.out.println("no : " + i);
					try {
						System.out.println(list.get(i).getUrl());
						System.out.println(e.getMessage());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}






















