package com.damon.config;

import com.damon.base.BaseRequest;
import com.damon.model.TestDataModel;
import okhttp3.OkHttpClient;

public class TestConfig {
    //httpclient
    public static OkHttpClient httpClient;
    //MyRequest
    public static BaseRequest request;
    //证书
    public static String cerPath;
    //TestCaseModel
    public static TestDataModel testDataModel;
    //ip:port
//    public static String testUrl;
    //日志收集
    public static String allMessage;
    public static String description;
}
