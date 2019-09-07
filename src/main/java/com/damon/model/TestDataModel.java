package com.damon.model;

import lombok.Data;

/**
 * @ClassName TestCase1
 * @Description 测试用例表
 * @Author Damon
 * @Date 2018/11/29
 * @Version 1.0
 **/
@Data
public class TestDataModel {
   private int id;
   private int fid;
   private String param;
   private int depend;
   private String expected;
   private String assertType;
   private String description;
   private String saveDataKey;
   private int result;
   private String caseDescription;
}
