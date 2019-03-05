package com.my.blog.website.config.redis;

import org.apache.shiro.util.CollectionUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * create by shuanglin on 19-2-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisManager.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisManager redisManager;

    private String[] testStr;

    private String[] strWithPrefix;

    @Before
    public void init() {
        // data for delete();
        testStr = new String[]{"1", "2", "3", "4", "5"};
        String val;
        for (String str : testStr) {
            val = System.currentTimeMillis() + "s";
            redisTemplate.opsForValue().set(str, val);
        }

        // data for scan
        strWithPrefix = new String[]{"a1", "a2", "a3", "a4", "a5"};
        for (String str : strWithPrefix) {
            val = String.valueOf(System.currentTimeMillis()) + " ms";
            redisTemplate.opsForValue().set(str, val);
        }
    }

    @After
    public void destroy() {
        redisManager.delete(testStr);
        redisManager.delete(strWithPrefix);
    }

    @Test
    public void hasKeyTest() {
        for (int i = 1; i <= 5; i++) {
            Assert.assertEquals(Boolean.TRUE, redisManager.hasKey(String.valueOf(i)));
        }
        Assert.assertEquals(Boolean.FALSE, redisManager.hasKey(String.valueOf(6)));
    }

    @Test
    public void deleteTest() {
        for (int i = 1; i <= 5; i++) {
            logger.info(
                    "redis key:{} with value:{}", i, redisTemplate.opsForValue().get(String.valueOf(i)));
        }
        redisManager.delete("1", "2", "3", "4", "5");
        for (int i = 1; i <= 5; i++) {
            Assert.assertEquals(Boolean.FALSE, redisManager.hasKey(String.valueOf(i)));
        }
    }

    @Test
    public void deleteCollectionTest() {
        List<String> testList = CollectionUtils.asList(testStr);
        for (int i = 1; i <= 5; i++) {
            logger.info(
                    "redis key:{} with value:{}", i, redisTemplate.opsForValue().get(String.valueOf(i)));
        }
        redisManager.delete(testList);
        for (int i = 1; i <= 5; i++) {
            Assert.assertEquals(Boolean.FALSE, redisManager.hasKey(String.valueOf(i)));
        }
    }

    @Test
    public void scanTest() {
        String key = "a*";
        Set<String> res = redisManager.scan(key);
        logger.info("size is {}", res.size());
        for (String str : res) {
            logger.info("result of scan key{} is:{}", key, str);
            Assert.assertTrue(str.startsWith("a"));
        }

        Assert.assertEquals(5, res.size());
    }

    @Test
    public void scanSizeTest() {
        String prefix = "a*";
        Long num = redisManager.scanSize(prefix);
        Assert.assertEquals(Long.valueOf(5L), num);
        redisManager.delete(strWithPrefix);
        num = redisManager.scanSize(prefix);
        Assert.assertEquals(Long.valueOf(0L), num);
//        logger.info("prefix length is {}", prefix.length());
//        Assert.assertTrue(prefix != null);
//        Assert.assertTrue(prefix.length() == 0);
    }

    @Test
    public void setExpireTest() throws InterruptedException {
        String[] expireStr = {"7", "8", "9", "10", "6"};
        long expire = 5L;
        String val;
        for (String str : expireStr) {
            val = System.currentTimeMillis() + "ms";
            redisManager.set(str, val, expire);
        }
        for (String str : expireStr) {
            Assert.assertTrue(redisManager.hasKey(str));
            logger.info("current key {} with its value {}", str, redisManager.get(str));
        }
        TimeUnit.SECONDS.sleep(6L);
        for (String str : expireStr) {
            Assert.assertFalse(redisManager.hasKey(str));
            logger.info("current key {} with its value {}", str, redisManager.get(str));
        }
    }

    @Test
    public void NegativeExpireTest() {
        String[] negativeExpireKeys = new String[8];
        String val = null;
        long expire = -1L;
        for (String key : negativeExpireKeys) {
            key = "negative Expire:" + UUID.randomUUID().toString();
            val = System.currentTimeMillis() + " ms";
            redisManager.set(key, val, expire);
        }

        Set<String> set = redisManager.scan("negative*");
        redisManager.delete(set);


    }
}
