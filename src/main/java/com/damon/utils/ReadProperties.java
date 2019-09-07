package com.damon.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @ClassName GetUrl
 * @Description  读取properties文件
 * @Author Damon
 * @Date 2018/11/28
 * @Version 1.0
 **/
public class ReadProperties {
    //获取测试url
//    public static String getUrl = bundle("config").getString("testUrl");

    //获取证书路径
    public static String getCerPath(){
        if (useSsl()){
            return configBundle().getString("cerPath");
        }else return null;
    }
    //是否使用ssl
    public static boolean useSsl(){
        try {
            String usessl = configBundle().getString("useSSLValidation");
            return usessl.equalsIgnoreCase("true");
        }catch (MissingResourceException e){
            return false;
        }
    }

    // 获取配置
    public static ResourceBundle configBundle(){
        return bundle("config");
    }

    private static ResourceBundle bundle(String fileName) {
        return ResourceBundle.getBundle(fileName,Locale.CHINA);
    }
}
