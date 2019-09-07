package com.damon.utils;

import java.util.Properties;

public class DBProperties {

 	public static final Properties DEFAULT;

	static{
		DEFAULT = new Properties();
		DEFAULT.setProperty("jdbc.driver",ReadProperties.configBundle().getString("jdbc.driver"));
		DEFAULT.setProperty("jdbc.url", ReadProperties.configBundle().getString("jdbc.url"));
		DEFAULT.setProperty("jdbc.username", ReadProperties.configBundle().getString("jdbc.username"));
		DEFAULT.setProperty("jdbc.password", ReadProperties.configBundle().getString("jdbc.password"));
	}
}