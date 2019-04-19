package com.dao;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-02-24 18:13
 */
@Service("jedisFactory")
public class JedisFactory {

    private JedisPool jedisPool;

    public JedisFactory() {
        JedisShardInfo shardInfo = new JedisShardInfo("redis://47.106.107.142:6379");//这里是连接的本地地址和端口
        shardInfo.setPassword("123456");//这里是密码

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(30);
        jedisPoolConfig.setMaxIdle(10);

        this.jedisPool = new JedisPool(jedisPoolConfig, "47.106.107.142", 6379, 100 ,"123456");
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

}
