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

public class C1_HtmlSource_Cuponomia1 {
	
	public static String url_sitemap = "https://www.cuponomia.com.br/cupom";
	
	public static void main(String[] args) {

		try {
			HtmlSourceModel query = new HtmlSourceModel();
			query.setHtmlType("categories");
			query.setMainDomain("cuponomia.com.br");
			List<HtmlSourceModel> oldList = HtmlSourceModel.queryModel(query);
			if (oldList == null) {
				throw new RuntimeException("查询历史记录HtmlSourceModel, null");
			}
			Map<String, HtmlSourceModel> oldMap = new HashMap<String, HtmlSourceModel>();
			for (HtmlSourceModel old : oldList) {
				oldMap.put(old.getUrl(), old);
			}
			
			String content = CrawlerUtil.get(url_sitemap);
//			List<String> list = RegxUtil.match(content, "a href=\"(https://www.meliuz.com.br/desconto/[^\"]*)\">", 1);
			List<String> list = RegxUtil.match(content, "<a href=\"(/cupom/[^\"]*)\">", 1);
			
			List<HtmlSourceModel> needAdd = new ArrayList<HtmlSourceModel>();
			for (String url : list) {
				if (oldMap.containsKey(url)) {
					oldMap.remove(url);
					continue;
				}
				
				// 增加
				System.out.println(DateUtils.getDateTimeString(new Date()) + "\t" + url);
				HtmlSourceModel htmlSource = new HtmlSourceModel();
				htmlSource.setHtmlType("categories");
				htmlSource.setMainDomain("cuponomia.com.br");
				htmlSource.setSubDomain("www");
				htmlSource.setUrl("https://www.cuponomia.com.br" + url);
				htmlSource.setName(url.substring("/cupom/".length()));
				htmlSource.setCreateTime(DateUtils.getDateTimeString(new Date()));
				htmlSource.setUpdateTime(DateUtils.getDateTimeString(new Date()));
				htmlSource.setContent("");
				htmlSource.setErrCnt(0);
				needAdd.add(htmlSource);
			}
			System.out.println("to add, count=" + needAdd.size());
			HtmlSourceModel.addRecord(needAdd);
			
			System.out.println("to delete, count=" + oldMap.size());
			for (HtmlSourceModel delete : oldMap.values()) {
				HtmlSourceModel.updateRecord(delete, delete.getHtmlSourceId(), "errCnt", String.valueOf(delete.getErrCnt() + 1));
				HtmlSourceModel.updateRecord(delete, delete.getHtmlSourceId(), "updateTime", DateUtils.getDateTimeString(new Date()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}






















