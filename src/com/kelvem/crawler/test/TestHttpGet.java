package com.kelvem.crawler.test;

import com.kelvem.common.CrawlerUtil;

/**
 * TestHttpGet
 *
 * @ClassName TestHttpGet
 * @author kelvem
 * @version 1.0
 */
public class TestHttpGet {

	/**
	 * main
	 *
	 * @param args	
	 * @return void	
	 * @throws
	 */
	public static void main(String[] args) {
		
		String content = CrawlerUtil.get("https://www.cuponomia.com.br/cupom/alimentos-e-bebidas");
		System.out.println(content);
	}

}
