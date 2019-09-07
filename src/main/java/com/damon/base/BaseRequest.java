package com.damon.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.damon.cases.TestCase;
import com.damon.config.HttpLogger;
import com.damon.config.TestCaseInfo;
import com.damon.config.TestListener;
import com.damon.utils.Util;
import okhttp3.*;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.TestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.damon.utils.ReadProperties.*;

/**
 * @ClassName MyRequest
 * @Description 封装request
 * @Author Damon
 * @Date 2018/11/27
 * @Version 1.0
 **/
public class BaseRequest {

    //带请求头的
    public Request mRequest(String method, String url, String param, String header) {

        Request request;
        RequestBody body = null;
        param = getUserValue(param);     //拿到param之后直接替换引用参数

        //拼接get请求的url
        if (method.equalsIgnoreCase("get") && !param.equals("")) {
            url = url + "?" + param;
        }

        //处理post请求参数
        if (method.equalsIgnoreCase("post") && !param.equals("")) {
            if (param.startsWith("{")) {
                MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
                body = RequestBody.create(mediaType, param);
            } else {
                //否则默认为表单格式
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                //分割字符串
                for (String form : param.split("&")) {
                    try {
                        String key = form.split("=")[0];
                        String value = form.split("=")[1];
                        HttpLogger.logger.info("添加参数：" + key + "=" + value);
                        formBodyBuilder.add(key, value);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        HttpLogger.logger.info("添加参数：" + form);
                        formBodyBuilder.add(form, "");
                    }
                }
                body = formBodyBuilder.build();
            }
        }
        Request.Builder builder = new Request.Builder().url(url);
        //如果请求头不为空
        if (header != null && !header.equals("")) {
            //处理请求头
            //多个请求头以&&分割
            String[] stringHeaders = header.split("&&");
            for (String h : stringHeaders) {
                try {
                    String name = h.split(":")[0];
                    String value = h.split(":")[1];
                    HttpLogger.logger.info("添加头信息：" + name + ":" + value);
                    builder = builder.addHeader(name, value);
                } catch (ArrayIndexOutOfBoundsException e) {
                    HttpLogger.logger.error("请求头格式错误，请参照name:value格式！\nERROR: " + h);
                }
            }
        }

        if (method.equalsIgnoreCase("get")) {
            request = builder
                    .get()
                    .build();
            return request;
        } else if (method.equalsIgnoreCase("post")) {
            request = builder
                    .post(body)
                    .build();
            return request;
        } else {
            return new Request.Builder().build();
        }
    }

    private String getUserValue(String param) {
        Pattern r = Pattern.compile("(?<=\\$\\{).*?(?=})");
        Matcher m = r.matcher(param);
        while (m.find()) {
            String key = m.group();
            String value = TestCase.userDataMap.get(key);
            if (value != null) {
                HttpLogger.logger.info("更新参数：" + key + "=" + value);
                param = param.replace("${" + key + "}", value);
            } else {
                throw new SkipException("参数" + key + "获取失败，自动跳过！");
            }

        }
        return param;

//            if (key.equalsIgnoreCase("hash")){
//                value = Util.getHash();
//                HttpLogger.logger.info("更新参数："+key+"="+value);
//                param = param.replace("${hash}",value);
//            }else if (key.equalsIgnoreCase("t")){
//                value = String.valueOf(Util.getTime());
//                HttpLogger.logger.info("更新参数："+key+"="+value);
//                param = param.replace("${t}",value);
//            }else if (key.equalsIgnoreCase("passCode")){
//                        HttpLogger.logger.info("-->获取短信验证码");
//                        value = Util.getPassCode(TestCaseInfo.getUserData("codePhoneNumber"));
//                        param = param.replace("${passCode}",value);
//            }else if (key.equalsIgnoreCase("nguid")){
//                value = Util.getGuid();
//                HttpLogger.logger.info("更新参数："+key+"="+value);
//                param = param.replace("${nguid}",value);
//            }else{
//
//            }

    }


    private JSONObject getJsonData(String s) {
        JSONObject json = new JSONObject();
//        JSONObject json = JSON.parseObject(s);
//        添加用户参数
        JSONObject userJson = JSON.parseObject(s);
        for (String key : userJson.keySet()) {
            json.put(key, userJson.get(key).toString());
        }
        //添加配置参数
        String appid = configBundle().getString("appid");
        int versionCode = Integer.valueOf(configBundle().getString("versionCode"));
        String os_version = configBundle().getString("os_version");
        String os_name = configBundle().getString("os_name");
        String tra = configBundle().getString("tra");
        String uchannel = configBundle().getString("uchannel");
        String nt = configBundle().getString("nt");
        String mac = configBundle().getString("mac");
        String mType = configBundle().getString("mType");
        double mLatitude = Double.valueOf(configBundle().getString("mLatitude"));
        double mLongitude = Double.valueOf(configBundle().getString("mLongitude"));
        String adcode = configBundle().getString("adcode");
        String address = configBundle().getString("address");
        int apiversion = Integer.valueOf(configBundle().getString("apiversion"));

        json.putIfAbsent("appid", appid);
        json.putIfAbsent("versionCode", versionCode);
        json.putIfAbsent("os_version", os_version);
        json.putIfAbsent("os_name", os_name);
        json.putIfAbsent("tra", tra);
        json.putIfAbsent("uchannel", uchannel);
        json.putIfAbsent("nt", nt);
        json.putIfAbsent("mac", mac);
        json.putIfAbsent("mType", mType);
        json.putIfAbsent("mLatitude", mLatitude);
        json.putIfAbsent("mLongitude", mLongitude);
        json.putIfAbsent("adcode", adcode);
        json.putIfAbsent("address", address);
        json.putIfAbsent("apiversion", apiversion);
        json.putIfAbsent("t", Util.getTime());


        return json;
    }
}
