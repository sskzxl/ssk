package com.mmall.util;

import com.mmall.service.impl.ProductServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    static{
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            //InputStreamReader:字符输入流，父类的Reader
            //InputStream是字节输入流，在InputStreamReader使用装饰模式，实现了向字符流的转换
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"utf-8"));
        } catch (IOException e) {
            logger.error("读取配置文件异常",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }
    public static String getProperty(String key,String defaltValue){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaltValue;
        }
        return value.trim();
    }

}
