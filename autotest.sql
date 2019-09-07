/*
Navicat MySQL Data Transfer

Source Server         : wwww
Source Server Version : 50643
Source Host           : 47.104.201.53:3306
Source Database       : autotest

Target Server Type    : MYSQL
Target Server Version : 50643
File Encoding         : 65001

Date: 2019-09-07 09:11:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for interface
-- ----------------------------
DROP TABLE IF EXISTS `interface`;
CREATE TABLE `interface` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '接口名称',
  `method` varchar(256) NOT NULL DEFAULT '',
  `header` varchar(256) NOT NULL DEFAULT '',
  `url` varchar(256) NOT NULL DEFAULT '',
  `param` varchar(256) DEFAULT '' COMMENT '参数名：是否必须',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for test_case
-- ----------------------------
DROP TABLE IF EXISTS `test_case`;
CREATE TABLE `test_case` (
  `id` int(11) NOT NULL,
  `business` varchar(256) NOT NULL,
  `description` varchar(256) DEFAULT '' COMMENT '描述',
  `isrun` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for test_data
-- ----------------------------
DROP TABLE IF EXISTS `test_data`;
CREATE TABLE `test_data` (
  `id` int(11) NOT NULL,
  `fid` int(11) NOT NULL,
  `param` varchar(512) NOT NULL DEFAULT '',
  `depend` int(11) unsigned DEFAULT '0' COMMENT '测试点',
  `expected` varchar(256) DEFAULT '',
  `assert_type` varchar(256) DEFAULT '' COMMENT '断言方式，默认json',
  `save_data_key` varchar(256) DEFAULT '',
  `result` tinyint(2) NOT NULL DEFAULT '0',
  `description` varchar(256) DEFAULT NULL COMMENT '描述啊',
  `env` tinyint(11) NOT NULL DEFAULT '0' COMMENT '环境：0测试 1生产',
  PRIMARY KEY (`id`,`fid`,`env`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_data
-- ----------------------------
DROP TABLE IF EXISTS `user_data`;
CREATE TABLE `user_data` (
  `user_key` varchar(256) NOT NULL,
  `user_value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`user_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
