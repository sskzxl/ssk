package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jdk.nashorn.internal.parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/***
 * token本地缓存，使用guava实现
 */
public class TokenCache {
    //创建logback的logger
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //LRU算法(最少使用算法 Least recently used)
    //声明一个静态内存块，guava里面的本地缓存
    private static LoadingCache<String,String> localCache =
            //构建本地缓存，采用链的方式，1000是设置缓存的初始容量，maxnumsize是设置缓存最大容量，当超过了最大容量，guava将使用LRU算法来移除缓存项
            //设置缓存有效期为12小时
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
                    //build里面要实现一个匿名抽象类
                    .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值时，如果key没有对应的值，就调用这个方法进行加载
                public String load(String s) throws Exception {
                    //到时可能会把"null".equals,如果使用null会抛出空指针异常
                    return "null";
                }
            });
    //添加本地缓存
    public static void setKey(String key, String value){
        localCache.put(key,value);
    }
    //设置本地缓存
    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
