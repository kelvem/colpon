package com.kelvem.crawler.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kelvem.common.CrawlerUtil;
import com.kelvem.common.DateUtils;
import com.kelvem.common.RegxUtil;
import com.kelvem.crawler.model.HtmlSourceModel;
import com.kelvem.crawler.model.OffersModel;
import com.kelvem.crawler.model.StoresModel;

public class S2_Coupon_MeliuzDetail {
	
	
	public static String url_sitemap = "https://www.meliuz.com.br/desconto";
	public static Integer max_count = 300000;
	
	public static Map<Integer, StoresModel> oldCompanyMap = new HashMap<Integer, StoresModel>();
	public static Map<String, OffersModel> oldCouponMap = new HashMap<String, OffersModel>();
	
	static {
		try {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {

		try {
			System.out.println("Version.0825");
			
			HtmlSourceModel htmlSource = new HtmlSourceModel();
			htmlSource.setHtmlType("colpon");
			htmlSource.setMainDomain("meliuz.com.br");
			htmlSource.setSubDomain("www");
			String where = "where html_type='colpon' and update_time < '" + DateUtils.getDateTimeString(new Date()) + "'";
			List<HtmlSourceModel> list = HtmlSourceModel.sql(where, htmlSource);
			
			System.out.println(list.size());
			
			
			for (int i = 0; i < list.size(); i++) {
				
				try {
					HtmlSourceModel source = list.get(i);
					
					System.out.println(source.getUrl());
					if (!source.getUrl().startsWith("https://www.meliuz.com.br/desconto/")) {
						continue;
					}
					
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
		addCoupons(htmlSourc, content);
	}
	
	/**
	 * company
	 * @return 
	 */
	public static Integer addCompany(HtmlSourceModel htmlSourc, String content) {
		
		if (oldCompanyMap.containsKey(htmlSourc.getHtmlSourceId())) {
			return htmlSourc.getHtmlSourceId();
		}
		
		List<String> companys = RegxUtil.match(content, "container xs/pv pt pb++([\\s\\S]*?)grid__col xs/w-1of1 sm/w-1of1 w-4of5", 1);
		
		String company = companys.get(0);
		
		List<String> companyImages = RegxUtil.match(company, "<img src=\"([\\s\\S]*?)\"", 1);
		List<String> companyTitles = RegxUtil.match(company, "title=\"([\\s\\S]*?)\"", 1);
		List<String> companyInfos = RegxUtil.match(company, "class=\"txt--small txt--gray xs/d-n\">([\\s\\S]*?)</div>", 1);

		if (companyImages.size() > 1) {
			System.out.println("Err companyImages : " + company);
		}
		if (companyTitles.size() != 1) {
			System.out.println("Err companyTitles : " + company);
		}
		if (companyInfos.size() != 1) {
			System.out.println("Err companyInfos : " + company);
		}
		
		String companyImage = "https:" + companyImages.get(0);
		String companyTitle = companyTitles.get(0);
		String companyInfo = companyInfos.get(0);
		
//		System.out.println("companyImage : " + companyImage);
//		System.out.println("companyTitle : " + companyTitle);
//		System.out.println("companyInfo : " + companyInfo);
		
		
		StoresModel store = new StoresModel();
		store.id = htmlSourc.getHtmlSourceId();
		store.categoryId = 0;
		store.name = companyTitle.replace("Cupom de Desconto ", "");
		store.description = filterDesc(companyInfo);
		store.link = companyImage;
		store.titleslug = htmlSourc.getName();
		store.initials = store.titleslug.substring(0,1).toUpperCase();
		store.createdAt = DateUtils.getDateTimeString(new Date());
		store.updatedAt = DateUtils.getDateTimeString(new Date());
		
		Integer storeId = StoresModel.addRecord(store);
//		StoresModel.updateRecord(companyModel, storeId, "id", htmlSourc.getHtmlSourceId().toString());
//		try {
//			CmdUtils.shell("wget -x  -np -O /images/Store-" + htmlSourc.getHtmlSourceId() + ".png " + companyImage);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return storeId;
	}
	
	
	/**
	 * coupon
	 */
	public static void addCoupons(HtmlSourceModel htmlSourc, String content) {
		
		List<String> groupList = RegxUtil.match(content, 
				"box--shadow mb- coupon-container([\\s\\S]*?)bg--white box--round", 0);

//		System.out.println("coupon cnt : " + groupList.size());
		List<OffersModel> couponList = new ArrayList<OffersModel>();
		for (int j = 0; j < groupList.size(); j++) {
			String group = groupList.get(j);
			
			// </span> <p>
			List<String> coupons = RegxUtil.match(group, "data-code=\"([\\s\\S]*?)\"", 1);
			List<String> titles = RegxUtil.match(group, "<a class=\"txt--black txt--bold-face\"[^>]*>([\\s\\S]*?)</a>", 1);
			List<String> infos = RegxUtil.match(group, "</span> <p>([\\s\\S]*?)</p>", 1);
			List<String> links = RegxUtil.match(group, "href=\"(https:[\\s\\S]*?)\"", 1);
			
			if (coupons.size() > 1) {
				System.out.println("Err coupons NO" + (j+1) + " : " + group);
			}
			if (titles.size() != 1) {
				System.out.println("Err titles NO" + (j+1) + " : " + group);
			}
			if (infos.size() > 1) {
				System.out.println("Err infos NO" + (j+1) + " : " + group);
			}
			if (links.size() <= 0) {
				System.out.println("Err links NO" + (j+1) + " : " + group);
			}
			
			String coupon = (coupons.size() == 1) ? coupons.get(0).trim() : "";
			String type = (coupon == null || coupon.length() <= 0) ? "D" : "C";
			String title = titles.get(0);
			String info = (infos.size() == 1) ? infos.get(0) : "";
			String url = links.get(0);
			Integer enableFlag = group.indexOf(">EXPIRADO</span>") >= 0 ? 0 : 1;
			
			OffersModel model = new OffersModel();
			model.storeId = htmlSourc.getHtmlSourceId();
			model.type = type;
			model.name = title.trim();
			model.description = filterDesc(info);
			model.link = getStoreLink(url);
			model.code = coupon;
			model.status = enableFlag;
			model.confirmDate = DateUtils.getDateTimeString(new Date());
			model.createdAt = DateUtils.getDateTimeString(new Date());
			model.updatedAt = DateUtils.getDateTimeString(new Date());
			
			long day = (long)(Math.random() * 23) + 7;
			long time = new Date().getTime() + 1000 * 60 * 60 * 24 * day;
			model.ends = DateUtils.getDateString(new Date(time));
			
			if (oldCouponMap.containsKey(model.key())) {
				// 已存在
				if (enableFlag == 0) {
					// 过期， 最后统一更新
					OffersModel delete = oldCouponMap.get(model.key());
					OffersModel.updateRecord(delete, delete.id, "status", "0");
					OffersModel.updateRecord(delete, delete.id, "ends", (DateUtils.getDateString(new Date())));
					oldCouponMap.remove(model.key());
				} else {
					// 未过期
					OffersModel update = oldCouponMap.get(model.key());
					OffersModel.updateRecord(update, update.id, "updatedAt", model.updatedAt);
					OffersModel.updateRecord(update, update.id, "ends", model.ends);
					OffersModel.updateRecord(update, update.id, "link", model.link);
					oldCouponMap.remove(model.key());
				}
			} else {
				// 不存在则添加(过期 与 未过期)
				couponList.add(model);
			}
			// 最后未爬取到的都置为过期
		}
		if (couponList.size() > 0) {
			OffersModel.addRecord(couponList);
		}
	}
	
	private static String filterDesc(String str) {
		//String str = " <p class=\"mb-\"> Atualizado em 24/08/2016.<br>  Encontramos 0 cupom de desconto para A Casa do Artista. &nbsp;&nbsp;<a href=\"#\" class=\"partner-desc-hide txt--gray-darker\"><span class=\"icon icon--chevron-down\" style=\"vertical-align:-3px;\"></span></a> </p>  <p class=\"partner-desc mb-\" style=\"display:none;\"> Não perca tempo e compre mais barato com cupom desconto A Casa do Artista. Te ajudamos a encontrar o melhor preço, frete grátis e promoções A Casa do Artista. </p> <p class=\"partner-desc mb-\" style=\"display:none;\"> Cupom de desconto A Casa do Artista 2016 é aqui! &nbsp;&nbsp;<a href=\"#\" class=\"txt--gray-darker\"><span class=\"icon icon--chevron-up\" style=\"vertical-align:-3px;\"></span></a> </p> ";
		if (str.indexOf("<br>") > 0) {
			str = str.substring(str.indexOf("<br>") + 4);
		}
		str = str.replace("&nbsp;", "");
		str = str.replaceAll("<[^>]*>", "");
		str = str.trim();
		return str;
	}
	
	private static String getStoreLink(String url) {
		String content = CrawlerUtil.get(url);
		String link = RegxUtil.match(content, "&ULP=\\[\\[([\\s\\S]*?)\\]\\]", 1).get(0);
		return link;
	}
	
}






















