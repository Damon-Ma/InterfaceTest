package com.damon.base;

import com.damon.config.HttpLogger;
import com.damon.config.TestConfig;
import okhttp3.Request;
import okhttp3.Response;
import org.testng.Reporter;

/**
 * @ClassName MyMethod
 * @Description 封装http请求方法
 * @Author Damon
 * @Date 2018/11/27
 * @Version 1.0
 **/
public class BaseMethod {


    public static Response request(String method, String url, String param, String header){
        HttpLogger.logger.info("请求参数：method="+method+", url="+url+", param="+param+", header="+header);
        if (header.equalsIgnoreCase("")){
            header = null;
        }
        Request request = TestConfig.request.mRequest(method,url,param,header);
        try {
            Response response = TestConfig.httpClient.newCall(request).execute();
            if (response.isSuccessful()){
                return response;
            }else {
                HttpLogger.logger.error("请求失败："+response.code()+" "+request);
            }
        } catch (Exception e) {
            Reporter.log("ERR:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
