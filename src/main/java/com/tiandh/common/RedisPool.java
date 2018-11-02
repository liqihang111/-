package com.tiandh.common;

import com.tiandh.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: lenovo
 * @Date: 2018/10/28 22:11
 * @Description: Redis连接池
 */
public class RedisPool {

    //jedis连接池
    private static JedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    //在JedisPool中最大的Idle状态的（空闲的）Jedis实例的个数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    //在JedisPool中最小的Idle状态的（空闲的）Jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));
    //在borrow一个Jedis实例的时候，是否进行验证操作。
    //如果赋值为true，则得到的Jedis实例肯定是可用的
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    //在return一个Jedis实例的时候，是否进行验证操作
    //如果赋值为true，则放回JedisPool的Jedis实例肯定是可用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "false"));
    //redis.host
    private static String redisHost = PropertiesUtil.getProperty("redis.host");
    //redis.port
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    //初始化Redis连接池，该方法只会被调用一次（通过静态代码块调用）
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        //当连接耗尽时，是否阻塞，true阻塞直到超时，false会抛出异常。默认为true
        config.setBlockWhenExhausted(true);

        //初始化JedisPool, 超时时间：1000*2 单位为毫秒
        pool = new JedisPool(config, redisHost, redisPort, 1000*2);
    }

    //当类被加载时，调用Redis连接池初始化方法
    static {
        initPool();
    }

    //从连接池中返回一个Jedis实例
    public static Jedis getJedis(){
        return pool.getResource();
    }

    //将Jedis放回连接池中
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    //将损坏的连接放入BrokenResource
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }
}
