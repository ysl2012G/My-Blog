package com.my.blog.website.config.redis; /*
 * create by shuanglin on 19-2-28
 */

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RedisCacheManager implements CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class);

    // fast lookup by name map
    private final ConcurrentMap<String, RedisCache> caches = new ConcurrentHashMap<>();

    private RedisManager redisManager;

    /**
     * expire time（timeunits:seconds)
     */
    private static final long DEFAULT_EXPIRE = 1800L;

    private long expire = DEFAULT_EXPIRE;

    /**
     * Redis Key Prefix
     */
    public static final String DEFAULT_CACHE_KEY_PREFIX = "shiro:cache:";

    private String keyPrefix = DEFAULT_CACHE_KEY_PREFIX;

    public static final String DEFAULT_PRINCIPAL_ID_FILED_NAME = "authCacheKey or id";
    private String principalIdFieldName = DEFAULT_PRINCIPAL_ID_FILED_NAME;

    @SuppressWarnings("uncheck assignment")
    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        logger.debug("get cache, name={}", name);
        RedisCache cache = caches.get(name);

        if (cache == null) {
            cache = new RedisCache<K, V>(redisManager, keyPrefix + name + ":", expire, principalIdFieldName);
            caches.put(name, cache);
        }
        return cache;
    }

    public <K, V> Cache<K, V> getCache(String name, long expireTime) throws CacheException {
        logger.debug("get cache, name={}, expire time={}", name, expireTime);
        RedisCache cache = caches.get(name);

        if (cache == null) {
            cache = new RedisCache<K, V>(redisManager, keyPrefix + name + ":", expireTime, principalIdFieldName);
            caches.put(name, cache);
        } else {
            cache.setExpire(expireTime);
        }
        return cache;
    }

    /**
     * getter Methods
     */
    public RedisManager getRedisManager() {
        return redisManager;
    }

    public long getExpire() {
        return expire;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public String getPrincipalIdFieldName() {
        return principalIdFieldName;
    }

    /**
     * setter Methods
     */

    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void setPrincipalIdFieldName(String principalIdFieldName) {
        this.principalIdFieldName = principalIdFieldName;
    }

    public void removeMapKey(String name) {
        caches.remove(name);
    }
}
