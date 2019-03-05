package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-3-3
 */

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参考代码
 * https://github.com/Clever-Wang/spring-boot-examples/blob/master/spring-boot-shiro-10/src/main/java/com/springboot/test/shiro/config/shiro/ShiroSessionListener.java
 */
public class ShiroSessionListener implements SessionListener {
    private static final Logger logger = LoggerFactory.getLogger(ShiroSessionListener.class);

    public static final AtomicInteger sessionCount = new AtomicInteger(0);

    @Override
    public void onStart(Session session) {
        logger.trace("a session has been created, id:{}", session.getId());
        sessionCount.incrementAndGet();
    }

    @Override
    public void onStop(Session session) {
        logger.trace("a session has been stopeed, id:{}", session.getId());
        sessionCount.decrementAndGet();

    }

    @Override
    public void onExpiration(Session session) {
        logger.trace("a session has been expired, id:{}", session.getId());
        sessionCount.decrementAndGet();
    }

    //获取在线人数
    public AtomicInteger getSessionCount() {
        return sessionCount;
    }
}
