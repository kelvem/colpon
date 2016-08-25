package com.kelvem.crawler.test;

import com.kelvem.common.CrawlerUtil;
import com.kelvem.common.RegxUtil;

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
		String content = CrawlerUtil.get("https://www.meliuz.com.br/redirecionar/ticket/25163532");
		System.out.println(content);
		
		String link = RegxUtil.match(content, "&ULP=\\[\\[([\\s\\S]*?)\\]\\]", 1).get(0);
		System.out.println();
		System.out.println(link);
	}

}
