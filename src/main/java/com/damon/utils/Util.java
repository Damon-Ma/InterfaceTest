package com.damon.utils;


import java.util.Date;
import java.util.UUID;


public class Util {
    public static String TRA = "ffd43342-2102-4871-8a1c-b2761e4cceaf";

    public static long getTime(){
        return new Date().getTime();
    }

    public static String getHash(){
        return UUID.randomUUID().toString().trim().replaceAll("-", "").toUpperCase();
    }

    public static String getGuid(){
        return UUID.randomUUID().toString();
    }
}
