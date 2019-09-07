package com.damon.config;

import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.log4j.Logger;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {

        if (message.startsWith("json")){
            try {
                message = URLDecoder.decode(message,"UTF-8")+"</br>";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (message.startsWith("--> POST")||message.startsWith("--> GET")){
            logger.debug("==========================================================================================");
            TestConfig.allMessage = null;
            Reporter.log(" ");
        }
        logger.debug("| "+message);
//        Reporter.log(message);
        if (TestConfig.allMessage == null){
            TestConfig.allMessage = message;
        }else {
            TestConfig.allMessage = TestConfig.allMessage + "</br>" + message;
        }

        if (message.startsWith("<-- END HTTP")){
            logger.debug("==========================================================================================");
            Reporter.log(TestConfig.allMessage);
            Reporter.log(" ");
        }

    }

    public static Logger logger = Logger.getLogger(Test.class);

}



