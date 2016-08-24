package com.kelvem.crawler.test;

import java.util.Date;

import com.kelvem.common.DateUtils;

public class TestEndTime {

	public static void main(String[] args) {

		for (int i = 0; i < 50; i++) {
			long day = (long)(Math.random() * 23) + 7;
			long time = new Date().getTime() + 1000 * 60 * 60 * 24 * day;
			String s = DateUtils.getDateTimeString(new Date(time));
//			System.out.println(day);
			System.out.println(day + "\t" + s);
		}
	}

}
