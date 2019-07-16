package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import com.sun.xml.internal.ws.developer.Serialization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static{
        /*
           序列化配置
         */
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //同意日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        /*
            反序列化配置
         */
        //忽略在json字符串中存在，但是在java中对象对应的属性不存在的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        }

        public static <T> String Obj2String(T obj){
            if(obj == null){
                return null;
            }
            try {
                return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
            } catch (Exception e) {
                log.warn("Parse Object to String error",e);
                return null;
            }
        }
    //格式化好的
    public static <T> String Obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> T String2Obj (String str, Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T String2Obj (String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? str : objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }
    public static <T> T String2Obj (String str, Class<?> collectionType,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionType,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail(" s");

        //String obj2String = JsonUtil.Obj2String(u1);

        String obj2String2 = JsonUtil.Obj2StringPretty(u1);
        log.info("obj2String2{}",obj2String2);

//        log.info("obj2String{}",obj2String);

        System.out.println("==================");

//        User user = JsonUtil.String2Obj(obj2String, User.class);
//        log.info("user{}",user);
//        List<User> users = Lists.newArrayList();
//        users.add(u1);
//        users.add(user);
//        String obj2StringPretty = JsonUtil.Obj2StringPretty(users);
//        log.info("obj2StringPretty{}",obj2StringPretty);
//        //当反序列化为List时，集合的对象会自动转换为LinkedListHashMap类型，属性变为键值对，没有了对象，不能调用对象的方法
//        List userList = JsonUtil.String2Obj(obj2StringPretty, List.class);
//        //解决1：
//        List userList1 = JsonUtil.String2Obj(obj2StringPretty, new TypeReference<List<User>>() {
//        });
//        //解决2：
//        List userList2 = JsonUtil.String2Obj(obj2StringPretty,List.class,User.class);
//
//
//
//        log.info("obj2StringPretty{}",obj2StringPretty);
    }

}
