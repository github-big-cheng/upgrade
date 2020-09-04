package com.dounion.server.core.helper;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class ConfigurationHelper extends PropertyPlaceholderConfigurer {

    private static Map<String, String> propertiesMap;

    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {

        super.processProperties(beanFactoryToProcess, props);

        propertiesMap = new HashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            propertiesMap.put(keyStr, value);
        }
    }

    public static String getProperty(String key){
        return propertiesMap.get(key);
    }
    public static String getProperty(String key, String defVal){
        return getProperty(key)==null ? defVal : getProperty(key);
    }



    public static String getString(String key){
        return getProperty(key);
    }
    public static String getString(String key, String defVal){
        return getProperty(key, defVal);
    }


    public static int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }
    public static int getInt(String key, int defVal) {
        String val = getProperty(key);
        if(!StringUtils.isNumeric(val)){
            return defVal;
        }
        return getInt(val);
    }


    public static Long getLong(String key) {
        return Long.parseLong(getProperty(key));
    }
    public static Long getLong(String key, long defVal) {
        String val = getProperty(key);
        if(!StringUtils.isNumeric(val)){
            return defVal;
        }
        return getLong(val);
    }
}
