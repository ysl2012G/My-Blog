package com.my.blog.website.config.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/*
 * create by shuanglin on 19-2-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisConfigTest {
    private Logger logger = LoggerFactory.getLogger(RedisConfigTest.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {
        String key = "1";
        String val = "shuanglin";
        redisTemplate.opsForValue().set(key, val);
        logger.info("val:" + redisTemplate.opsForValue().get(key));
    }
}
