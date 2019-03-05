package com.my.blog.website.config.redis; /*
 * create by shuanglin on 19-2-27
 */

import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisManager {
    private static final Logger logger = LoggerFactory.getLogger(RedisManager.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置过期时间
     *
     * @param key  redis键
     * @param time 以秒为单位
     */
    public void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 检查key是否存在
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除keys
     *
     * @param keys
     */
    public void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(CollectionUtils.asList(keys));
            }
        }
    }

    /**
     * 删除keys
     *
     * @param keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 获取value
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置key-value
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置key-value以及过期时间
     *
     * @param key
     * @param value
     * @param time
     */
    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * scan命令
     *
     * @param key
     * @return
     */
    public Set<String> scan(String key) {
        Set<String> execute =
                this.redisTemplate.execute(
                        (RedisCallback<Set<String>>)
                                connection -> {
                                    Set<String> binaryKeys = new HashSet<>();

                                    Cursor<byte[]> cursor =
                                            connection.scan(
                                                    new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                                    while (cursor.hasNext()) {
                                        binaryKeys.add(new String(cursor.next()));
                                    }
                                    return binaryKeys;
                                });
        return execute;
    }

    /**
     * 获取scan 结果数量
     *
     * @param key
     * @return
     */
    public Long scanSize(String key) {
        Long num =
                this.redisTemplate.execute(
                        (RedisCallback<Long>)
                                connection -> {
                                    long count = 0L;
                                    Cursor<byte[]> cursor =
                                            connection.scan(
                                                    new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                                    while (cursor.hasNext()) {
                                        cursor.next();
                                        count++;
                                    }
                                    return count;
                                });
        return num;
    }
}
