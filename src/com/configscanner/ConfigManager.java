package com.configscanner;

import java.io.IOException;

public class ConfigManager {
	
	private static PropertyConfigStore propertyConfigStore = new PropertyConfigStore();
	
	public static String getString(String key){
		
		return propertyConfigStore.getPropertyValue(key);
	}
	
	public static String getString(String key, String defaultValue){
		
		return propertyConfigStore.getPropertyValue(key,defaultValue);
	}
	
	public static void reloadConfigurations(String propertyfile) throws IOException{
		propertyConfigStore.reloadProperties(propertyfile);
	}
}
