package com.damon.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class DatabaseUtil {
    public static SqlSession getSqlSession(Properties properties){
        //获取配置文件

        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("databaseConfig.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader,properties);
        //能够执行配置文件中的sql语句
        SqlSession sqlSession = factory.openSession(true);
        return sqlSession;
    }
}
