package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-1-30
 */

import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.junit.Before;
import org.junit.Test;

public class DefaultWebSessionManagerTest {
    private DefaultWebSessionManager webSessionManager;

    @Before
    public void setup() {
        webSessionManager = new DefaultWebSessionManager();
    }

    @Test
    public void testDefaultValue() {
        //cookie
        Cookie cookie = webSessionManager.getSessionIdCookie();
        System.out.println(cookie.getMaxAge());
        //globalsessiontime
        System.out.println(webSessionManager.getGlobalSessionTimeout());
        //session validation scheduler
//        webSessionManager.setSessionValidationSchedulerEnabled(true);
//        websession
        System.out.println(webSessionManager.getSessionValidationInterval());
        System.out.println(webSessionManager.isSessionValidationSchedulerEnabled());

    }
}
