package com.kelvem.crawler.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kelvem.common.CrawlerUtil;
import com.kelvem.common.DateUtils;
import com.kelvem.common.RegxUtil;
import com.kelvem.crawler.model.OffersModel;
import com.kelvem.crawler.tools.S2_Coupon_MeliuzDetail;

/**
 * TestHttpGet
 *
 * @ClassName TestHttpGet
 * @author kelvem
 * @version 1.0
 */
public class TestStoreLink {

	/**
	 * main
	 *
	 * @param args	
	 * @return void	
	 * @throws
	 */
	public static void main(String[] args) {
		
//		String content = CrawlerUtil.get("https://www.meliuz.com.br/redirecionar/ticket/25163532?nsp=1");
//		String content = CrawlerUtil.get("https://www.meliuz.com.br/redirecionar/ticket/25163532");
//		System.out.println(content);
//		
//		String link = RegxUtil.match(content, "&ULP=\\[\\[([\\s\\S]*?)\\]\\]", 1).get(0);
//		System.out.println();
//		System.out.println(link);
		
//		String url_company_sepha = "https://www.meliuz.com.br/desconto/sepha";
//		String url_company_sepha = "https://www.meliuz.com.br/desconto/a-casa-do-artista";
		String url_company_sepha = "https://www.meliuz.com.br/desconto/a-dama-e-o-cachorrinho";
		String content1 = CrawlerUtil.get(url_company_sepha);
		addCoupons(content1);
	}


	
	
	/**
	 * coupon
	 */
	public static void addCoupons(String content) {
		
		try {
			List<String> groupList = RegxUtil.match(content, 
					"box--shadow mb- coupon-container([\\s\\S]*?)bg--white box--round", 0);

			for (int j = 0; j < groupList.size(); j++) {
				String group = groupList.get(j);
				
				// </span> <p>
				List<String> coupons = RegxUtil.match(group, "data-code=\"([\\s\\S]*?)\"", 1);
				List<String> titles = RegxUtil.match(group, "<a class=\"txt--black txt--bold-face\"[^>]*>([\\s\\S]*?)</a>", 1);
				List<String> infos = RegxUtil.match(group, "</span> <p>([\\s\\S]*?)</p>", 1);
				List<String> links = RegxUtil.match(group, "href=\"(https[\\s\\S]*?)\"", 1);
				
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
				
				String link = getStoreLink(links.get(0));
				System.out.println(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static String getStoreLink(String url) {
		String content = CrawlerUtil.get(url);
		List<String> links = RegxUtil.match(content, "&ULP=\\[\\[([\\s\\S]*?)\\]\\]", 1);
		
		if (links.size() <= 0) {
			links = RegxUtil.match(content, "data-ticket-url=\"([\\s\\S]*?)\"", 1);
		}
		
		if (links.size() <= 0) {
			links = RegxUtil.match(content, "<a href=\"([\\s\\S]*?)\">", 1);
		}
		
		if (links.size() <= 0 || !links.get(0).startsWith("http")) {
			System.out.println(content);
		}
		
		
		return links.get(0);
	}
	
}
