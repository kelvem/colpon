package com.kelvem.crawler.test;

import java.util.Date;
import java.util.List;

import com.kelvem.common.CrawlerUtil;
import com.kelvem.common.DateUtils;
import com.kelvem.common.RegxUtil;
import com.kelvem.crawler.model.HtmlSourceModel;

public class TestMeliuzSiteMap {
	
	public static String site_name = "meliuz";
	
	public static String url_sitemap = "https://www.meliuz.com.br/desconto";
	public static String url_company_sepha = "https://www.meliuz.com.br/desconto/sepha";
	
	
	public static String[][] rules = {
			{"<li class=\"txt--truncate txt--\"><a href=\"", "\">"}
	};
	
	public static void main(String[] args) {

		try {
		
			String content = CrawlerUtil.get(url_sitemap);
//			String content = FileUtils.readFile("html/desconto_src.html");
			
			List<String> list = RegxUtil.match(content, "a href=\"(https://www.meliuz.com.br/desconto/[^\"]*)\">", 1);
			for (String url : list) {
				System.out.println(DateUtils.getDateTimeString(new Date()) + "\t" + url);
				HtmlSourceModel htmlSource = new HtmlSourceModel();
				htmlSource.setHtmlType("colpon");
				htmlSource.setMainDomain("meliuz.com.br");
				htmlSource.setSubDomain("www");
				htmlSource.setUrl(url);
				htmlSource.setName(url.substring("https://www.meliuz.com.br/desconto/".length()));
				htmlSource.setUrl(url);
				
				List<HtmlSourceModel> buf = HtmlSourceModel.queryModel(htmlSource);
				if (buf == null || buf.size() > 0) {
					continue;
				}
				
				htmlSource.setCreateTime(DateUtils.getDateTimeString(new Date()));
				HtmlSourceModel.addRecord(htmlSource);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}






















