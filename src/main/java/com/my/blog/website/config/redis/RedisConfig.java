package com.my.blog.website.config.redis; /*
 * create by shuanglin on 19-2-27
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
// @AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {

    @Bean("shiroRedisTemplate")
    public RedisTemplate<String, Object> shiroredisTemplate(
            LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 替换默认序列化
        KryoRedisSerializer<Object> serializer = new KryoRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        // 加载客户端
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
