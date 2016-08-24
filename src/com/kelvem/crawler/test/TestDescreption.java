package com.kelvem.crawler.test;

public class TestDescreption {

	public static void main(String[] args) {
		
		String str = " <p class=\"mb-\"> Atualizado em 24/08/2016.<br>  Encontramos 0 cupom de desconto para A Casa do Artista. &nbsp;&nbsp;<a href=\"#\" class=\"partner-desc-hide txt--gray-darker\"><span class=\"icon icon--chevron-down\" style=\"vertical-align:-3px;\"></span></a> </p>  <p class=\"partner-desc mb-\" style=\"display:none;\"> Não perca tempo e compre mais barato com cupom desconto A Casa do Artista. Te ajudamos a encontrar o melhor preço, frete grátis e promoções A Casa do Artista. </p> <p class=\"partner-desc mb-\" style=\"display:none;\"> Cupom de desconto A Casa do Artista 2016 é aqui! &nbsp;&nbsp;<a href=\"#\" class=\"txt--gray-darker\"><span class=\"icon icon--chevron-up\" style=\"vertical-align:-3px;\"></span></a> </p> ";
		System.out.println(str);
		
		if (str.indexOf("<br>") > 0) {
			str = str.substring(str.indexOf("<br>") + 4);
			System.out.println(str);
		}
		
		str = str.replace("&nbsp;", "");
		System.out.println(str);
		
		str = str.replaceAll("<[^>]*>", "");
		System.out.println(str);
		
	}

}
