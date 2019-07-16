package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisPool {
    private static JedisPool pool;//jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));//在redisPool中最大的idle状态（空闲的）中jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","10"));//最小的
    private static boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","ture"));//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则得到的jedis实例肯定是可以用的。
    private static boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","ture"));//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true，则放回的jedisPool的jedis实例肯定是可以用的。
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    //静态方法不会在类加载时就加载
    private static void init(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//连接耗尽时，是否会阻塞，false不会阻塞，直接报错；true时会阻塞，直到超时，报超时异常

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }
    //静态代码块会在类加载时就记载
    static{
        init();
    }
    public static Jedis getRedis(){
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getRedis();
        jedis.set("zhengshuyi","sunshukun");
        returnResource(jedis);

        pool.destroy();


    }
}
