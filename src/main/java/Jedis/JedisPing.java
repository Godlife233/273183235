package Jedis;

import redis.clients.jedis.Jedis;

public class JedisPing {
    public static void main(String[] args) {
        Jedis jedisM = new Jedis("120.78.76.160",6379);
        Jedis jedisS = new Jedis("120.78.76.160",6380);
        jedisS.slaveof("120.78.76.160",6379);
    }
}
