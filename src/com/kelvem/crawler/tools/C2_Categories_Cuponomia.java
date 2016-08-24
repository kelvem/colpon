package com.kelvem.crawler.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kelvem.common.CmdUtils;
import com.kelvem.common.CrawlerUtil;
import com.kelvem.common.DateUtils;
import com.kelvem.common.RegxUtil;
import com.kelvem.crawler.model.OffersModel;
import com.kelvem.crawler.model.HtmlSourceModel;
import com.kelvem.crawler.model.StoresModel;

public class C2_Categories_Cuponomia {
	
	
	public static Integer max_count = 300000;
	
	public static Map<Integer, StoresModel> oldCompanyMap = new HashMap<Integer, StoresModel>();
	public static Map<String, OffersModel> oldCouponMap = new HashMap<String, OffersModel>();
	
	static {
		StoresModel queryCompany = new StoresModel();
		List<StoresModel> oldCompanyList = StoresModel.queryModel(queryCompany);
		for (StoresModel old : oldCompanyList) {
			oldCompanyMap.put(old.id, old);
		}
		
		OffersModel queryCoupon = new OffersModel();
		List<OffersModel> oldCouponList = OffersModel.queryModel(queryCoupon);
		for (OffersModel old : oldCouponList) {
			oldCouponMap.put(old.key(), old);
		}
	}
	
	public static void main(String[] args) {

		try {
			
			HtmlSourceModel htmlSource = new HtmlSourceModel();
			htmlSource.setHtmlType("categories");
			htmlSource.setMainDomain("cuponomia.com.br");
			htmlSource.setSubDomain("www");
			String where = ""; //"where html_type='colpon' and update_time < '" + DateUtils.getDateTimeString(new Date()) + "'";
			List<HtmlSourceModel> list = HtmlSourceModel.sql(where, htmlSource);
			
			System.out.println(list.size());
			
			
			for (int i = 0; i < list.size(); i++) {
				
				try {
					HtmlSourceModel source = list.get(i);
					
					if (!source.getUrl().startsWith("https://www.cuponomia.com.br/cupom/")) {
						continue;
					}
					
					System.out.println(source.getUrl());
					crawler(source);
					
					long date = new Date().getTime() + 2 * 24 * 60 * 60 * 1000 + (long)(Math.random() * 24 * 60 * 60 * 1000);
					HtmlSourceModel.updateRecord(source, source.getHtmlSourceId(), "updateTime", DateUtils.getDateTimeString(new Date(date)));
					
					if (i > max_count) {
						break;
					}
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

			// 最后未爬取到的都置为过期
			System.out.println("to delete, count=" + oldCouponMap.size());
			for (OffersModel delete : oldCouponMap.values()) {
				OffersModel.updateRecord(delete, delete.id, "enableFlag", "0");
				OffersModel.updateRecord(delete, delete.id, "expireTime", (DateUtils.getDateTimeString(new Date())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void crawler(HtmlSourceModel htmlSourc) {

		
		String content = CrawlerUtil.get(htmlSourc.getUrl());
		
		/**
		 * company
		 */
		addCompany(htmlSourc, content);
		
		/**
		 * coupon
		 */
//		addCoupons(htmlSourc, content);
	}
	
	/**
	 * company
	 * @return 
	 */
	public static void addCompany(HtmlSourceModel htmlSourc, String content) {
		
		List<String> companys = RegxUtil.match(content, "item-code   item-badge([\\s\\S]*?)item v1 scroll-item", 1);
		
		System.out.println(companys.size());
		for (String company : companys) {

			List<String> companyImages = RegxUtil.match(company, "src=\"(https://cuponomia-a.akamaihd.net/img/stores/[\\s\\S]*?)\"", 1);
			List<String> companyTitles = RegxUtil.match(company, "title=\"([\\s\\S]*?)\">", 1);
			List<String> companyInfos = RegxUtil.match(company, "alt=\"([\\s\\S]*?)\" />", 1);
			List<String> companyNames = RegxUtil.match(company, "<h3 class=\"([\\s\\S]*?)\">", 1);

			if (companyImages.size() > 1) {
				System.out.println("Err companyImages : " + company);
			}
			if (companyTitles.size() != 1) {
				System.out.println("Err companyTitles : " + company);
			}
			if (companyInfos.size() != 1) {
				System.out.println("Err companyInfos : " + company);
			}
			if (companyNames.size() != 1) {
				System.out.println("Err companyNames : " + company);
			}
			
			String companyImage = companyImages.get(0);
			String companyTitle = companyTitles.get(0);
			String companyInfo = companyInfos.get(0);
			String companyName = companyNames.get(0);
			
//			System.out.println("companyImage : " + companyImage);
//			System.out.println("companyTitle : " + companyTitle);
//			System.out.println("companyInfo : " + companyInfo);
//			System.out.println("companyName : " + companyName);
			
			
			StoresModel stores = new StoresModel();
			stores.id = htmlSourc.getHtmlSourceId();
			stores.initials = companyImage.substring(0,1);
			stores.categoryId = 0;
			stores.name = companyTitle;
			stores.description = companyInfo;
			stores.link = companyImage;
			stores.titleslug = htmlSourc.getName();
			stores.createdAt = DateUtils.getDateTimeString(new Date());
			stores.updatedAt = DateUtils.getDateTimeString(new Date());

			System.out.println("#\t" + htmlSourc.getName() + "\t" + companyName + "\t" + companyImage);
		}
	}
	
	/**
	 * company
	 * @return 
	 */
	public static Integer addCompany1(HtmlSourceModel htmlSourc, String content) {
		
//		if (oldCompanyMap.containsKey(htmlSourc.getHtmlSourceId())) {
//			return htmlSourc.getHtmlSourceId();
//		}
		
		List<String> companys = RegxUtil.match(content, "staff-item  item-code   item-badge([\\s\\S]*?)item v1 scroll-item", 1);
		
		String company = companys.get(0);
		
		List<String> companyImages = RegxUtil.match(company, "src=\"(https://cuponomia-a.akamaihd.net/img/stores/[\\s\\S]*?)\"", 1);
		List<String> companyTitles = RegxUtil.match(company, "title=\"([\\s\\S]*?)\">", 1);
		List<String> companyInfos = RegxUtil.match(company, "alt=\"([\\s\\S]*?)\" />", 1);
		List<String> companyNames = RegxUtil.match(company, "<h3 class=\"([\\s\\S]*?)\">", 1);

		if (companyImages.size() > 1) {
			System.out.println("Err companyImages : " + company);
		}
		if (companyTitles.size() != 1) {
			System.out.println("Err companyTitles : " + company);
		}
		if (companyInfos.size() != 1) {
			System.out.println("Err companyInfos : " + company);
		}
		
		String companyImage = companyImages.get(0);
		String companyTitle = companyTitles.get(0);
		String companyInfo = companyInfos.get(0);
		
//		System.out.println("companyImage : " + companyImage);
//		System.out.println("companyTitle : " + companyTitle);
//		System.out.println("companyInfo : " + companyInfo);
		
		
		StoresModel stores = new StoresModel();
		stores.id = htmlSourc.getHtmlSourceId();
		stores.initials = companyImage.substring(0,1);
		stores.categoryId = 0;
		stores.name = companyTitle;
		stores.description = companyInfo;
		stores.link = companyImage;
		stores.titleslug = htmlSourc.getName();
		stores.createdAt = DateUtils.getDateTimeString(new Date());
		stores.updatedAt = DateUtils.getDateTimeString(new Date());

		Integer storeId = StoresModel.addRecord(stores);
		return storeId;
	}
	
}






















