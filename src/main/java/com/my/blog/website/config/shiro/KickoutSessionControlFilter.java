package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-3-3
 */

import com.my.blog.website.config.redis.RedisCacheManager;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.UserVo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 参考代码:https://github.com/Clever-Wang/spring-boot-examples/blob/master/spring-boot-shiro-10/src/main/java/com/springboot/test/shiro/config/shiro/KickoutSessionControlFilter.java
 */
public class KickoutSessionControlFilter extends AccessControlFilter {
    private static final Logger logger = LoggerFactory.getLogger(KickoutSessionControlFilter.class);

    @Autowired
    private ResourceUrlProvider resourceUrlProvider;

    /**
     * 踢出后的地址url
     */
    private String kickoutUrl;

    /**
     * 踢出之前还是之后的用户，默认是之前
     */
    private boolean kickoutAfter = false;

    /**
     * 同一账号最大会话数，默认为1
     */
    private int maxSession = 1;

    private SessionManager sessionManager;
    private RedisCacheManager cacheManager;

    private static final String DEFAULT_KICKOUT_CACHE_KEY_PREFIX = "kickout:";
    private String keyPrefix = DEFAULT_KICKOUT_CACHE_KEY_PREFIX;


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("enter Kickout Session Control filter");
        Subject subject = getSubject(request, response);
        /**
         * 检测subject是否登录，如果登录则返回true（继续之后的流程）
         */
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            logger.trace("subject is not authenticated or rememebered.");
            return true;
        }

        /**
         * 判断是否为静态资源
         *
         */
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String path = httpServletRequest.getServletPath();
        if (isStaticFile(path)) {
            logger.trace("subject is static file");
            return true;
        }

        Session session = subject.getSession();
//        String username = (String) subject.getPrincipal();
        UserVo userVo = (UserVo) subject.getPrincipal();
        Serializable sessionId = session.getId();
        //get cache use cache manager
        String key = getRedisKickoutKey(userVo.getUsername());
        if (cacheManager == null) {
            logger.error("cache manager not set");
            throw new TipException("cache manager has not been settled!");
        }
        Cache<String, Deque<Serializable>> cache = cacheManager.getCache(key);
        Deque<Serializable> deque = cache.get(key);
        if (deque == null) {
            deque = new LinkedList<>();
            cache.put(key, deque);
        }

        if (!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
            deque.push(sessionId);
        }


        if (sessionManager == null) {
            logger.error("sessionManager not set!");
            throw new TipException("session manager has not been settled!");
        }
        while (deque.size() > maxSession) {
            Serializable kickoutSessionId = null;
            //如果踢出后者
            if (kickoutAfter) {
                kickoutSessionId = deque.removeFirst();
            } else {//提前者
                kickoutSessionId = deque.removeLast();
            }
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    kickoutSession.setAttribute("kickout", true);
                }
            } catch (Exception e) {
                logger.error("ops failed:setting kickout session attribute", e);
            }
        }
        //存储cache
        cache.put(key, deque);

        if (kickoutUrl == null) {
            throw new TipException("redirect kickout url is empty!");
        }
        if (session.getAttribute("kickout") != null) {
            try {
                subject.logout();
            } catch (Exception e) {
                logger.error("logout failed after session kickout", e);
            }
            saveRequest(request);
            WebUtils.issueRedirect(request, response, kickoutUrl);
            return false;
        }
        return true;


    }

    /**
     * helper function
     */
    private boolean isStaticFile(String path) {
        String staticUrl = resourceUrlProvider.getForLookupPath(path);
        return staticUrl != null;

    }

    /**
     * setter function
     */
    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setCacheManager(RedisCacheManager redisCacheManager) {
        this.cacheManager = redisCacheManager;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    /**
     * getter function
     */

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public String getRedisKickoutKey(String username) {
        return this.keyPrefix + username;
    }
}
