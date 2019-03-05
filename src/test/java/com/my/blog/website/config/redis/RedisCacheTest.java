package com.my.blog.website.config.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/*
 * create by shuanglin on 19-3-1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisCacheTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheTest.class);
    @Autowired
    private RedisCacheManager cacheManager;

    private Cache<String, String> cache;


    private String[] testKeys;
    private int testNum;

    private String cacheName = "test";


//    String prefix;

    @Before
    public void init() {
        cache = cacheManager.getCache(cacheName);
        //init redis keys
        testNum = 8;
        testKeys = new String[testNum];
        for (int i = 0; i < testNum; i++) {
            String key = UUID.randomUUID().toString();
            testKeys[i] = key;
            String val = System.currentTimeMillis() + " ms";
            cache.put(key, val);

        }


//        prefix = cacheManager.getKeyPrefix() + cacheName + ":";
    }

    @After
    public void destory() {
        cache.clear();
    }


    @Test
    public void getTest() {
        for (String key : testKeys) {
//            String redisKey = prefix + key;
            String val = cache.get(key);
            logger.info("key [{}]; value [{}]", key, val);
            Assert.assertFalse(val.isEmpty());
        }

    }


    @Test
    public void sizeTest() {
        Assert.assertEquals(testNum, cache.size());
//        String extraKey = UUID.randomUUID().toString();
//        String extraVal = System.currentTimeMillis() + " ms";

    }

    @Test
    public void keysTest() {
        Set<String> scanedKeys = cache.keys();
        //assert size
        Assert.assertEquals(testNum, cache.keys().size());

        // check each keys in testKeys
        RedisCache a = (RedisCache) cacheManager.getCache(cacheName);
        String prefix = a.getKeyPrefix();
        for (String key : testKeys) {
            Assert.assertTrue(scanedKeys.contains(prefix + key));
        }
    }

    @Test
    public void putTest() {

        for (int i = 0; i < testNum; i++) {
            String key = UUID.randomUUID().toString();
            String val = System.currentTimeMillis() + " ms";
            cache.put(key, val);
            Assert.assertFalse(cache.get(key).isEmpty());
        }
        logger.debug("scan size is {}", cache.keys().size());
        Assert.assertEquals(testNum * 2, cache.size());
    }


    @Test
    public void removeTest() {
        for (int i = testKeys.length - 1; i >= 0; i--) {
            cache.remove(testKeys[i]);
            Assert.assertEquals(i, cache.size());
        }
    }

    @Test
    public void valuesTest() {
        cache.clear();
        String[] valArray = new String[testNum];
        for (int i = 0; i < testNum; i++) {
            valArray[i] = System.currentTimeMillis() + " ms";
            cache.put(testKeys[i], valArray[i]);
        }
        Assert.assertTrue(cache.values().containsAll(CollectionUtils.asList(valArray)));
    }

    @Test
    public void clearTest() {
        Assert.assertEquals(testNum, cache.size());
        cache.clear();
        Assert.assertEquals(0, cache.size());
    }

    /**
     * 测试serializable 作为key 能否成功
     */
    @Test
    public void serializableKeyTest() {
        Session session = new SimpleSession();
        Serializable sessionid = new JavaUuidSessionIdGenerator().generateId(session);
        String cacheName = "serializableKey";
        Cache<Serializable, Session> sessionCache = cacheManager.getCache(cacheName);
        Assert.assertNull(sessionCache.get(sessionid));
        ((SimpleSession) session).setId(sessionid);
        sessionCache.put(sessionid, session);
        Assert.assertEquals(sessionCache.get(sessionid).getId(), sessionid);
        sessionCache.clear();
    }


}
