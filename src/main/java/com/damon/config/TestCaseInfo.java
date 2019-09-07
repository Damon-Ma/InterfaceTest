package com.damon.config;


import com.damon.model.InterfaceModel;
import com.damon.model.TestCaseModel;
import com.damon.model.TestDataModel;
import com.damon.model.UserDataModel;
import com.damon.utils.DBProperties;
import com.damon.utils.DatabaseUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @ClassName TestCaseInfo
 * @Description 获取数据库查询结果
 * @Author Damon
 * @Date 2018/11/29
 * @Version 1.0
 **/
public class TestCaseInfo {
    //初始化sqlsession
    private static SqlSession sqlSession = DatabaseUtil.getSqlSession(DBProperties.DEFAULT);

    //初始化测试结果
    public static void initTestResult() {
        HttpLogger.logger.info("-------初始化测试结果--------");
        sqlSession.update("initTest");
    }

    //设置测试结果
    public static void setTestResult(TestDataModel model) {
        HttpLogger.logger.info("-------更新测试结果--------");
        sqlSession.update("setTestResult", model);
    }

    //获取测试结果
    public static int getTestResult(int id) {
        HttpLogger.logger.info("-------查询测试结果--------");
        //sqlSession.clearCache();
        return sqlSession.selectOne("getTestResult", id);

    }

    //获取配置
//    public static ConfigModel getDbConfig(String dbName) {
//        HttpLogger.logger.info("-------获取数据库配置-------");
//        return sqlSession.selectOne("getDbConfig", dbName);
//    }

    public static List<UserDataModel> getUserDataList() {
        HttpLogger.logger.info("------获取用户数据------");
        return sqlSession.selectList("getUserDataList");
    }
    //获取要执行的测试用例

    public static List<TestCaseModel> getTestCase(){
        return sqlSession.selectList("gettestcase");
    }
    //获取测试数据

    public static TestDataModel getTestData(int id){
        return sqlSession.selectOne("gettestdata",id);
    }
    //获取接口信息

    public static InterfaceModel getInterface(int id){
        return sqlSession.selectOne("getinterface",id);
    }

    public static void main(String[] args) {

    }

}
