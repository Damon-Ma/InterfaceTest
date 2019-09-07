package com.damon.cases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aventstack.extentreports.utils.StringUtil;
import com.damon.base.BaseHttpClient;
import com.damon.base.BaseMethod;
import com.damon.base.BaseRequest;
import com.damon.config.HttpLogger;
import com.damon.config.TestCaseInfo;
import com.damon.config.TestConfig;
import com.damon.model.TestCaseModel;
import com.damon.model.UserDataModel;
import com.damon.model.InterfaceModel;
import com.damon.model.TestDataModel;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TestCase
 * @Description 测试逻辑
 * @Author Damon
 * @Date 2018/12/19
 * @Version 1.0
 **/
public class TestCase {

    public static Map<String,String> userDataMap = new HashMap();

    @DataProvider(name = "data")
    public Object[] testCases() {
        List<TestCaseModel> list = TestCaseInfo.getTestCase();
        List<TestDataModel> dataList = new ArrayList<>();
        for (TestCaseModel caseModel : list) {
            TestConfig.description = caseModel.getDescription();  //用例名称
            for (String data : caseModel.getBusiness().split(",")) {
                int dataId = Integer.valueOf(data);
                TestDataModel testData = TestCaseInfo.getTestData(dataId);
                testData.setCaseDescription(caseModel.getDescription());
                dataList.add(testData);
            }
        }
        return dataList.toArray();
    }

    @Test(dataProvider = "data")
    public void testCase(TestDataModel testData) {
        JSONObject json = new JSONObject();
        TestConfig.testDataModel = testData;
        int fid = testData.getFid();    //接口id
        String assertType = testData.getAssertType();   //断言方式
        int depend = testData.getDepend();  //接口依赖
        String dataDescription = testData.getDescription(); //测试项
        String expect = testData.getExpected(); //期望
        String param = testData.getParam(); //参数
        String saveDataKey = testData.getSaveDataKey(); //要保存的值

        InterfaceModel interfaceMsg = TestCaseInfo.getInterface(fid);
        String interfaceName = interfaceMsg.getName(); //接口名称;
        String header = interfaceMsg.getHeader();   //请求头
        String method = interfaceMsg.getMethod();   //接口方法
        String url = interfaceMsg.getUrl(); //接口地址
        String name = interfaceName + "-" + TestConfig.description + "-" + dataDescription; //接口详情

        json.put("depend", depend);
        json.put("method", method);
        json.put("url", url);
        json.put("param", param);
        json.put("header", header);
        json.put("assertType", assertType);
        json.put("expect", expect);
        json.put("saveDataKey", saveDataKey);
        json.put("name", name);
        this.doTest(json);
    }

    //执行测试
    private void doTest(JSONObject json) {
        Response response;
        String responseStr = null;

        int depend = (int) json.get("depend");
        String method = json.getString("method");
        String url = json.getString("url");
        String param = json.getString("param");
        String header = json.getString("header");
        String assertType = json.getString("assertType");
        String expect = json.getString("expect");
        String saveDataKey = json.getString("saveDataKey");
        String name = json.getString("name");
        Reporter.log("测试项：" + name
                + "</br>期望结果：" + expect);

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        if (depend == 0) {
            response = BaseMethod.request(method, url, param, header);
            Assert.assertNotNull(response);
            try {
                responseStr = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int result = TestCaseInfo.getTestResult(depend);
            if (result == 1) {
                response = BaseMethod.request(method, url, param, header);
                Assert.assertNotNull(response);
                try {
                    responseStr = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new SkipException("依赖执行失败，自动跳过！");
            }
        }
        //判断是否需要保存数据
        if (StringUtil.isNotNullOrEmpty(saveDataKey)) {
            String[] saveDatas = saveDataKey.split(",");
            this.saveData(responseStr, saveDatas);
        }
        this.myAssert(assertType, responseStr, expect);
    }

    //断言方法
    private void myAssert(String assertType, String responseStr, String expect) {

        //如果期望结果中有引用的参数，将这些参数替换成保存的数据
        Pattern r1 = Pattern.compile("(?<=\\$\\{).*?(?=})");
        Matcher m1 = r1.matcher(expect);
        while (m1.find()) {
            String userdata = m1.group();
            String value = userDataMap.get(userdata);
            if (value != null) {
                HttpLogger.logger.debug("更新用户数据：" + userdata + "=" + value);
                expect = expect.replace("${" + userdata + "}", value);
            } else {
                Reporter.log("用户数据获取失败：" + userdata);
                HttpLogger.logger.debug("用户数据获取失败：" + userdata);
//                expect = expect.replace("${" + userdata + "}", "0");
                Assert.fail("没有找到保存的数据，断言失败！");
            }
        }
        if (expect.equals("")) {
            return;
        }
        //响应体正则
        if (assertType.startsWith("#")) {
            assertType = assertType.split("#")[1];
            Pattern r = Pattern.compile(assertType);
            Matcher m = r.matcher(responseStr);
            Assert.assertTrue(m.find());
            String s = m.group();
            Reporter.log("正则匹配结果：" + s);
            HttpLogger.logger.info("响应结果：" + responseStr + "  匹配结果：" + s);
            Assert.assertEquals(s, expect);
        } else if (assertType.equalsIgnoreCase("") || assertType.equalsIgnoreCase("json")) {
            //json断言，AssertType = JSON
            //expect期望结果，格式为key=value|key=value，如果为嵌套json，则写成data.key=value
            //对expect字段进行处理
            JSONObject resJson = JSONObject.parseObject(responseStr);
            for (String expect1 : expect.split("\\|")) {
                Reporter.log("断言：" + expect1);
                jsonAssert(expect1, resJson);
            }
        } else if (assertType.equalsIgnoreCase("body")) {
            //响应体匹配
            HttpLogger.logger.info("响应结果：" + responseStr);
            Assert.assertEquals(responseStr, expect);
        }
    }

    /*保存响应结果中的值*/
    private void saveData(String responseStr, String[] saveDataKey) {
        JSONObject responseJson = JSON.parseObject(responseStr);

        for (String dataKey : saveDataKey) {
            String saveValue = responseJson.getString(dataKey);
            if (saveValue == null) {
                Pattern r = Pattern.compile("(?<=" + dataKey + "\\\":\\\").*?(?=\\\")");
                Pattern r1 = Pattern.compile("(?<=" + dataKey + "\\\":).*?(?=[,}])");

                Matcher m = r.matcher(responseStr);
                Matcher m1 = r1.matcher(responseStr);
                if (m.find()) {
                    saveValue = m.group();
                } else if (m1.find()) {
                    saveValue = m1.group();
                }
            }
            userDataMap.put(dataKey,saveValue);
        }
    }


    //expect: xxx.xxx=xxx
    private void jsonAssert(String expect, JSONObject resJson) {
        boolean flag = false;
        boolean isArray = false;
        String keyss = expect.split("=")[0];
        String value = expect.split("=")[1];
        String key = keyss.split("\\.")[0];
        String searchValue;
        try {
            searchValue = resJson.get(key).toString();
        } catch (NullPointerException e) {
            Assert.fail("未找到key--------->" + key);
            return;
        }
        if (searchValue.startsWith("[")) {
            isArray = true;
            JSONArray jsonArray = JSONArray.parseArray(searchValue);
            expect = expect.replace(key + ".", "");

            int count = 1;
            for (Object json : jsonArray) {

                JSONObject jsonObject = JSONObject.parseObject(json.toString());
                jsonAssert(expect, jsonObject);
                if (flag) {
                    Assert.assertTrue(flag);
                    isArray = false;
                    flag = false;
                    break;
                } else if (count == jsonArray.size()) {   //如果遍历到最后都找不到，那就断言失败
                    Reporter.log("断言失败：" + expect);
                    Assert.assertTrue(flag);
                    isArray = false;
                }
                count++;
            }

        } else if (searchValue.startsWith("{")) {
            JSONObject jsonObject = JSONObject.parseObject(searchValue);
            expect = expect.replace(key + ".", "");
            jsonAssert(expect, jsonObject);
        } else if (isArray) {
            if (searchValue.equalsIgnoreCase(value)) {
                flag = true;
            }
        } else {
            Assert.assertEquals(searchValue, value);
        }
    }


    @BeforeTest
    public void beforeTest() {
        HttpLogger.logger.debug("-------------------初始化测试-------------------");
        //初始化HttpClient
        TestConfig.httpClient = new BaseHttpClient().mOkHttpClient();
        //初始化request
        TestConfig.request = new BaseRequest();
        //初始化测试结果
        TestCaseInfo.initTestResult();
        //初始化数据库配置
//        TestConfig.dbConfig = TestCaseInfo.getDbConfig("testDB");
        //插入用户数据
        List<UserDataModel> userDataList = TestCaseInfo.getUserDataList();
        for (UserDataModel userData : userDataList){
            userDataMap.put(userData.getUserkey(),userData.getUserValue());
        }
        HttpLogger.logger.debug("-------------------初始化完成-------------------");
    }

    @AfterTest
    public void afterTest() {
        HttpLogger.logger.info("-------------------测试结束！-------------------");
    }
}
