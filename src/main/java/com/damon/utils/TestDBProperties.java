package com.damon.utils;

/**
 * 自定义数据库配置
 * 这个数据库可以是接口所用的数据库
 */
public class TestDBProperties {
//
//	public static final Properties testDB;
//
//	static{
//        String driver = TestConfig.dbConfig.getDbDriver();
//        String url = TestConfig.dbConfig.getDbUrl();
//        String user = TestConfig.dbConfig.getDbUser();
//        String password = TestConfig.dbConfig.getDbPwd();
//
//        testDB = new Properties();
//        testDB.setProperty("jdbc.driver", driver);
//        testDB.setProperty("jdbc.url", url);
//        testDB.setProperty("jdbc.user", user);
//        testDB.setProperty("jdbc.password", password);
//	}
//
//	private static SqlSession testSqlSession = DatabaseUtil.getSqlSession(testDB);
//
//	public static String selectResultOfString(String sql){
//		StringBuilder result = new StringBuilder();
//		List list = testSqlSession.selectList("selectSql",sql);
//		for (Object o : list){
//			result.append(o);
//		}
//		return String.valueOf(result);
//	}
}