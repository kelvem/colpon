package com.kelvem.crawler.model;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kelvem.common.StringUtil;
import com.kelvem.common.database.base.DataBaseSession;
import com.kelvem.common.database.sqlite.SqliteSession;

public abstract class SqliteBaseModel {


	private static DataBaseSession session = new SqliteSession();
	static {
		session.open();
	}
    
	
	public static <T> int count(T t) {

		try {
//			System.out.println("根据对象查询个数： " + t);
			String sql = " select count(1) ";
			sql += getFromPartSql(t);
			sql += getWherePartSql(t);
			
			int count = session.count(sql);
			
			return count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static <T> int maxId(T t) {

		try {
//			System.out.println("根据对象查询个数： " + t);
			String primaryKey = getPrimaryKeyName(t);
			String sql = " select max(" + primaryKey + ") ";
			sql += getFromPartSql(t);
			sql += getWherePartSql(t);
			
			int count = session.count(sql);
			
			return count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> List<T> queryModel(T t) {

		try {
			System.out.println("根据对象查询列表： " + t);
			String sql = " select * ";
			sql += getFromPartSql(t);
			sql += getWherePartSql(t);
			
			ResultSet rs = session.query(sql);

			// ResultSet -> List<T>
			List<T> result = new ArrayList<T>();
			while(rs.next()) {
				Class<?> clazz = t.getClass();
				T m = (T)clazz.newInstance(); 
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					String fieldName = field.getName();
					String columnName = StringUtil.aaaAaaToaaa_aaa(fieldName);
					Object value = rs.getObject(columnName);
					if (value == null) {
						continue;
					}
					field.setAccessible(true);
					field.set(m, value);
				}
				result.add(m);
			}
			
			return result;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (e.getMessage().indexOf("no such table:") >= 0) {
				createTable(t);
				return queryModel(t);
			} else {
				throw new RuntimeException(e);
			}
		}
	}
	

	public static <T> void updateRecord(T t, Integer id, String fieldName, String fieldValue) {

//		System.out.println("修改表记录： " + t);
		String sql = updateRecordSql(t, id, fieldName, fieldValue);

		try {
			session.execute(sql);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (e.getMessage().indexOf("no such table:") >= 0) {
				createTable(t);
				updateRecord(t, id, fieldName, fieldValue);
			} else {
				throw new RuntimeException(e);
			}
		}
	}
		
		
	public static <T> Integer addRecord(T t) {

//		System.out.println("增加表记录： " + t);
		String sql = addRecordSql(t);

		try {
			session.execute(sql);
			
			Integer maxId = maxId(t);
			
			return maxId;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (e.getMessage().indexOf("no such table:") >= 0) {
				createTable(t);
				return addRecord(t);
			} else {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static <T> Integer addRecord(List<T> list) {

//		System.out.println("增加表记录： " + t);
		if (list == null || list.size() <= 0) {
			return 0;
		}
		
		String sql = "";
		for (T t : list) {
			sql += addRecordSql(t);
		}

		try {
			session.execute(sql);
			
			Integer maxId = maxId(list.get(0));
			
			return maxId;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			if (e.getMessage().indexOf("no such table:") >= 0) {
				createTable(list.get(0));
				return addRecord(list);
			} else {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static <T> void createTable(T t) {

//		System.out.println("增加表结构： " + t);
		String sql = createTableSql(t);

		try {
			session.execute(sql);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	


	
	public static String sqliteEscape(String keyWord){
//	    keyWord = keyWord.replace("/", "//");
	    keyWord = keyWord.replace("'", "''");
//	    keyWord = keyWord.replace("[", "/[");
//	    keyWord = keyWord.replace("]", "/]");
//	    keyWord = keyWord.replace("%", "/%");
//	    keyWord = keyWord.replace("&","/&");
//	    keyWord = keyWord.replace("_", "/_");
//	    keyWord = keyWord.replace("(", "/(");
//	    keyWord = keyWord.replace(")", "/)");
	    return keyWord;
	}
	
	protected static <T> String getPrimaryKeyName(T t) {
		String className = t.getClass().getSimpleName();
		String tableName = StringUtil.aaaAaaToaaa_aaa(className);
		if (tableName.endsWith("_model")) {
			tableName = tableName.substring(0, tableName.length() - "_model".length());
		}
		
		String primaryKey = tableName + "_id";
		return primaryKey;
	}
	
	protected static <T> String getFromPartSql(T t) {
		
		String className = t.getClass().getSimpleName();
		String tableName = StringUtil.aaaAaaToaaa_aaa(className);
		if (tableName.endsWith("_model")) {
			tableName = tableName.substring(0, tableName.length() - "_model".length());
		}
		
		String from = " from " + tableName + " \r\n";
		return from;
	}
	
	protected static <T> String getWherePartSql(T t) {
		
		try {
			Map<String, String> columnMap = new LinkedHashMap<String, String>();
			
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String columnName = StringUtil.aaaAaaToaaa_aaa(fieldName);
				
				field.setAccessible(true);
				Object fieldValue = field.get(t);
				String columnValue = "";
				if (fieldValue == null) {
					continue;
				}
				
				columnValue = "'" + sqliteEscape(fieldValue.toString()) + "'";
				
				columnMap.put(columnName, columnValue);
			}
			
			StringBuilder sb = new StringBuilder();
			
			boolean isFirst = true;
			for (String key : columnMap.keySet()) {
				if (isFirst == true) {
					sb.append(" where ");
					isFirst = false;
				} else {
					sb.append(" and ");
				}
				sb.append(key).append("=").append(columnMap.get(key));
			}
			
			String where = sb.toString();

			return where;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static <T> String updateRecordSql(T t, Integer id, String fieldName, String fieldValue) {
		
		try {
			String className = t.getClass().getSimpleName();
			String tableName = StringUtil.aaaAaaToaaa_aaa(className);
			if (tableName.endsWith("_model")) {
				tableName = tableName.substring(0, tableName.length() - "_model".length());
			}
			String primaryKey = tableName + "_id";
			
			String columnName = StringUtil.aaaAaaToaaa_aaa(fieldName);
			
			
			
			String sql = String.format("UPDATE %s SET %s='%s' where %s=%s;", tableName, columnName, fieldValue, primaryKey, id);
//			System.out.println(sql);

			return sql;
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return "";
	}

	protected static <T> String addRecordSql(T t) {
		
		try {
			Map<String, String> columnMap = new LinkedHashMap<String, String>();
			
			String className = t.getClass().getSimpleName();
			String tableName = StringUtil.aaaAaaToaaa_aaa(className);
			if (tableName.endsWith("_model")) {
				tableName = tableName.substring(0, tableName.length() - "_model".length());
			}
			String primaryKey = tableName + "_id";
			
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String columnName = StringUtil.aaaAaaToaaa_aaa(fieldName);
				if (columnName.equalsIgnoreCase(primaryKey)) {
					continue;
				}
				
				field.setAccessible(true);
				Object fieldValue = field.get(t);
				String columnValue = "";
				if (fieldValue == null) {
					columnValue = "null";
				} else {
					columnValue = "'" + sqliteEscape(fieldValue.toString()) + "'";
				}
				
				columnMap.put(columnName, columnValue);
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("insert into ").append(tableName).append("\r\n");
			sb.append("(");
			
			boolean isFirst = true;
			for (String key : columnMap.keySet()) {
				if (isFirst == true) {
					isFirst = false;
				} else {
					sb.append(",");
				}
				sb.append(key);
			}
			sb.append(")\r\n");
			
			sb.append("values (");
			isFirst = true;
			for (String key : columnMap.keySet()) {
				if (isFirst == true) {
					isFirst = false;
				} else {
					sb.append(",");
				}
				sb.append(columnMap.get(key));
			}
			sb.append(");");
			
			String sql = sb.toString();
//			System.out.println(sql);

			return sql;
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

//	CREATE TABLE html_source(
//			html_source_id integer primary key autoincrement,
//			tag_rule_group_id integer,
//			html_type text,
//			url text,
//			main_domain text,
//			sub_domain text,
//			content text,
//			text text);
	protected static <T> String createTableSql(T t) {
		
		try {
			Map<String, String> columnMap = new LinkedHashMap<String, String>();
			
			String className = t.getClass().getSimpleName();
			String tableName = StringUtil.aaaAaaToaaa_aaa(className);
			if (tableName.endsWith("_model")) {
				tableName = tableName.substring(0, tableName.length() - "_model".length());
			}
			String primaryKey = tableName + "_id";
			
			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String columnName = StringUtil.aaaAaaToaaa_aaa(fieldName);
				if (columnName.equalsIgnoreCase(primaryKey)) {
					continue;
				}
				
				String columnType = getColumnType(field);
				columnMap.put(columnName, columnType);
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("create table ").append(tableName).append("\r\n");
			sb.append("(").append("\r\n");;
			sb.append("\t").append(primaryKey).append(" integer  primary key autoincrement");
			
			for (String key : columnMap.keySet()) {
				sb.append(",\r\n");
				sb.append("\t").append(key).append(" ").append(columnMap.get(key));
			}
			
			sb.append("\r\n)\r\n");
			
			String sql = sb.toString();
			System.out.println(sql);

			return sql;
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getColumnType(Field field) {
		String fieldType = field.getType().getSimpleName();
		if ("Integer".equalsIgnoreCase(fieldType)) {
			return "integer";
		} else if ("Long".equalsIgnoreCase(fieldType)) {
			return "long";
		} else if ("String".equalsIgnoreCase(fieldType)) {
			return "text";
		} else {
			return "text";
		}
	}
	
//	public static void main(String[] args) {
//		HtmlSourceModel model = new HtmlSourceModel();
//		Field[] fields = model.getClass().getDeclaredFields();
//		
//		
//		for (Field field : fields) {
//			System.out.println(field.getType().getSimpleName());
//		}
//		
//		Field field = fields[0];
//		System.out.println(field.getType().toGenericString());
//		System.out.println(field.getGenericType());
//		
//	}
}
