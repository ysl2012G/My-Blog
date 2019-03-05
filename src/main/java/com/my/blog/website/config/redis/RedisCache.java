package com.my.blog.website.config.redis; /*
 * create by shuanglin on 19-2-28
 */

import com.my.blog.website.exception.PrincipalIDNullException;
import com.my.blog.website.exception.PrincipalInstanceException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RedisCache<K, V> implements Cache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private RedisManager redisManager;
    private String keyPrefix = "";
    private long expire = 0L;
    private String principalIdFieldName = RedisCacheManager.DEFAULT_PRINCIPAL_ID_FILED_NAME;

    /**
     * constructor
     *
     * @param redisManager
     * @param keyPrefix
     * @param expire
     * @param principalIdFieldName
     */
    public RedisCache(
            RedisManager redisManager, String keyPrefix, long expire, String principalIdFieldName) {
        if (redisManager == null) {
            logger.error("redismanager is null");
            throw new IllegalArgumentException("RedisManager should not be null");
        }
        this.redisManager = redisManager;
        if (keyPrefix != null && keyPrefix.length() > 0) {
            this.keyPrefix = keyPrefix;
            logger.debug("keyPrefix reset to {}", this.keyPrefix);
        }


        this.expire = expire;
//        logger.debug("expire time reset to {}", this.expire);

        if (principalIdFieldName != null && principalIdFieldName.length() > 0) {
            this.principalIdFieldName = principalIdFieldName;
            logger.debug("principal Id Field Name reset to {}", this.principalIdFieldName);
        }
    }

    @Override
    public V get(K key) throws CacheException {
        logger.debug("get key [{}]", key);
        if (key == null) {
            return null;
        }
        try {

            String redisCacheKey = getRedisCacheKey(key);
            Object rawValue = redisManager.get(redisCacheKey);
            if (rawValue == null) {
                return null;
            }
            V value = (V) rawValue;
            return value;
        } catch (Exception e) {
            logger.error("Redis Get Operations occurs an exception", e);
            throw new CacheException(e);
        }
    }

    @Override
    public V put(K key, V val) throws CacheException {
        if (key == null) {
            return null;
        }

        try {
            String redisCacheKey = getRedisCacheKey(key);
            redisManager.set(redisCacheKey, val, expire);
            logger.debug("put Key {} -- value {}", redisCacheKey, val);
            return val;
        } catch (Exception e) {
            logger.error("redis put operation had an exception", e);
            throw new CacheException(e);
        }
    }


//    public V put(K key, V val, long tempExpire) {
//        long currentExpire = getExpire();
//        setExpire(tempExpire);
//        V resultValue = put(key, val);
//        setExpire(currentExpire);
//        return resultValue;
//
//    }


    @Override
    public V remove(K key) throws CacheException {
        logger.debug("Redis: remove key {}", key);

        if (key == null) {
            return null;
        }

        try {
            String redisCacheKey = getRedisCacheKey(key);
            Object rawValue = redisManager.get(redisCacheKey);
            V currentVal = (V) rawValue;
            redisManager.delete(redisCacheKey);
            return currentVal;
        } catch (Exception e) {
            logger.error("Redis Remove Operation failed", e);

            throw new CacheException(e);
        }

    }

    @Override
    public void clear() throws CacheException {
        logger.debug("Redis Clear Cache Operation");
        Set<String> keys = null;
        try {

            keys = redisManager.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            logger.error("Redis Scan Keys error", e);
        }

        if (keys == null || keys.size() == 0) {
            return;
        }
        for (String key : keys) {
            redisManager.delete(key);
        }

    }

    @Override
    public int size() {
        Long num = 0L;
        try {
            num = redisManager.scanSize(this.keyPrefix + "*");
        } catch (Exception e) {
            logger.error("Redis Ops: scan keys size error", e);
        }

        return num.intValue();
    }

    @SuppressWarnings("unchecked cast")
    @Override
    public Set<K> keys() {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            logger.error("Redis Ops:scan keys error", e);
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Set<K> RawKeys = new HashSet<>();
        for (String key : keys) {
            try {
                RawKeys.add((K) key);
            } catch (Exception e) {
                logger.error("Redis Ops:get Keys error", e);
            }
        }
        return RawKeys;

    }


    @Override
    public Collection<V> values() {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            logger.error("Redis Ops: get Values() failed", e);
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> valList = new ArrayList<>(keys.size());
        V val = null;
        for (String key : keys) {
            try {
                val = (V) redisManager.get(key);
            } catch (Exception e) {
                logger.error("Redis Ops: get Method failed", e);
            }
            if (val != null) {
                valList.add(val);
            }
        }
        return Collections.unmodifiableList(valList);
    }

    /**
     * helper function:get redis cache prefix
     */
    private String getRedisCacheKey(K key) {
        if (key == null) {
            return null;
        }
        return this.keyPrefix + getStringRedisKey(key);
    }

    private String getStringRedisKey(K key) {
        String redisKey;
        if (key instanceof PrincipalCollection) {
            redisKey = getRedisKeyFromPrincipalIdField((PrincipalCollection) key);
        } else {
            redisKey = key.toString();
        }
        return redisKey;
    }

    /**
     * 参考代码
     * <a href="https://github.com/Clever-Wang/spring-boot-examples/blob/master/spring-boot-shiro-10/src/main/java/com/springboot/test/shiro/config/shiro/RedisCache.java">RedisCache.java</a>
     *
     * @param key
     * @return
     */
    private String getRedisKeyFromPrincipalIdField(PrincipalCollection key) {
        String redisKey;
        Object principalObject = key.getPrimaryPrincipal();
        // getter methods name use reflect
        Method principalIdGetter = null;
        Method[] methods = principalObject.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (RedisCacheManager.DEFAULT_PRINCIPAL_ID_FILED_NAME.equals(this.principalIdFieldName)
                    && ("getAuthCacheKey".equals(m.getName()) || "getId".equals(m.getName()))) {
                principalIdGetter = m;
                break;
            }
            if (m.getName()
                    .equals(
                            "get"
                                    + this.principalIdFieldName.substring(0, 1).toUpperCase()
                                    + this.principalIdFieldName.substring(1))) {
                principalIdGetter = m;
                break;
            }
        }
        if (principalIdGetter == null) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName);
        }
        try {

            Object idObj = principalIdGetter.invoke(principalObject);
            if (idObj == null) {
                throw new PrincipalIDNullException(principalObject.getClass(), this.principalIdFieldName);
            }
            redisKey = idObj.toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PrincipalInstanceException(
                    principalObject.getClass(), this.principalIdFieldName, e);
        }
        return redisKey;
    }


    /**
     * getter setter function
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        logger.debug("key prefix changed to {}", keyPrefix);

    }

    public void setPrincipalIdFieldName(String principalIdFieldName) {
        this.principalIdFieldName = principalIdFieldName;
        logger.debug("principal Id Field Name changed to {}", principalIdFieldName);
    }

    public void setExpire(long expire) {
        this.expire = expire;
        logger.debug("expire time changed to {}", expire);
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
}
