package com.configscanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyConfigStore {
	
	private Properties properties;
	
	public void reloadProperties(String propertyfile) throws IOException{
		FileInputStream configStream = new FileInputStream(propertyfile);
		properties = new Properties();
		properties.clear();
		properties.load(configStream);
		configStream.close();
		configStream = null;
	}
	
	public String getPropertyValue(String key){
		return properties.getProperty(key);
	}
	
	public String getPropertyValue(String key, String defaultValue){
		return properties.getProperty(key, defaultValue);
	}
}
