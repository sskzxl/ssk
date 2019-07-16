package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.SimpleTriggerContext;
import redis.clients.jedis.Jedis;
@Slf4j
public class RedisPoolUtil {

    //设置key的过期时间
    public static Long expire(String key, int exTime){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getRedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("setEx key:{}  error",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        //释放连接资源
        RedisPool.returnResource(jedis);
        return result;
    }
    /*
        设置key 的生存时间，Time单位是秒
        和expire的操作
        SetEx是一个原子操作
        设置值，设置过期时间两个动作，会在同一时间完成
        在Redis缓存中，非常实用
     */
    public static String setEx(String key, String value, int exTime){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getRedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setEx key:{} value:{}  error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    //设置键值
    public static String set(String key, String value){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getRedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{}  error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    //根据键获取值
    public static String get(String key){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getRedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} value:{}  error",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getRedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} value:{}  error",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        RedisPoolUtil.setEx("key2","value",10);
        String value = RedisPoolUtil.get("key");
        System.out.println(value);
    }
}
