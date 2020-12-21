package com.qdw.utils;

import java.io.*;

import java.util.Properties;

public final class PropertiesUtil {
    private Properties prop;

    public PropertiesUtil(){
        prop = new Properties();
        //ÀàÂ·¾¶
//        load("com/qdw/config/test.properties");
        load("src/main/java/com/qdw/config/test.properties");
    }
    public PropertiesUtil(String classPath){
        prop = new Properties();
        load(classPath);
    }
    public void load(String classPath){

        try {
//            prop.load(new FileInputStream(new File(classPath)));
            prop.load(new InputStreamReader(new FileInputStream(classPath), "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String getValuesString(String key){
        return prop.getProperty(key);
    }
    public int getValuesInt(String key){
        return Integer.parseInt(prop.getProperty(key));
    }
    public double getValuesDouble(String key){
        return Double.parseDouble(prop.getProperty(key));
    }
    public boolean getValuesBoolean(String key){
        return Boolean.parseBoolean(prop.getProperty(key));
    }
}

