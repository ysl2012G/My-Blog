package com.my.blog.website.config.shiro; /*
 * create by shuanglin on 19-1-23
 */

import com.my.blog.website.config.redis.RedisCacheManager;
import com.my.blog.website.config.redis.RedisManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {
    private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 登录网址
        shiroFilterFactoryBean.setLoginUrl("/admin/login");
        // 登录成功后跳转网址
        shiroFilterFactoryBean.setSuccessUrl("/admin/index");
        // 未授权网址
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //自定义拦截器
        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
        //同一账号在线人数
//        filtersMap.put("kickout", kickoutSessionControlFilter());
        //CSRF校验器
        filtersMap.put("request_attr", requestAttributeFilter());
        //注册
        shiroFilterFactoryBean.setFilters(filtersMap);

        // 拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();


        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/admin/css/**", "anon");
        filterChainDefinitionMap.put("/admin/images/**", "anon");
        filterChainDefinitionMap.put("/admin/js/**", "anon");
        filterChainDefinitionMap.put("/admin/plugins/**", "anon");
        filterChainDefinitionMap.put("/admin/logout", "logout");
        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问;user:记住我或者认证通过都可以访问-->
        filterChainDefinitionMap.put("/admin/**", "user");
        filterChainDefinitionMap.put("/**", "request_attr");
//        filterChainDefinitionMap.put("/admin/index", "anon");
//        filterChainDefinitionMap.put("/admin/index", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        logger.info("Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;
    }

    /**
     * 设置 Shiro 核心 security manager
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(webSessionManager());
        securityManager.setRememberMeManager(rememberMeManager());

        //        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * cachemanager will be setttle in securityManager methods, {@linkplain org.apache.shiro.mgt.RealmSecurityManager#setRealms(Collection)}
     * will call a afterrealms() method to set every realms with the cache manager settled in security manager class.
     *
     * @return
     */
    @Bean(name = "userRealm")
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        //开启缓存验证
        userRealm.setCachingEnabled(true);
        //缓存AuthenticationInfo的redis key name
        userRealm.setAuthenticationCachingEnabled(true);
        userRealm.setAuthenticationCacheName("authenticationCache");

        //缓存AuthorizationInfo的 redis key name
        userRealm.setAuthorizationCachingEnabled(true);
        userRealm.setAuthorizationCacheName("authorizationCache");
        //设置密码验证器
        userRealm.setCredentialsMatcher(passwordMatcher());
        //设置cachemanager
//        userRealm.setCacheManager(redisCacheManager());

        return userRealm;
    }

    /**
     * 配置密码验证器及PasswordService（用于修改密码时验证）
     *
     * @return
     */
    @Bean
    public ShiroPasswordMatcher passwordMatcher() {
        ShiroPasswordMatcher passwordMatcher = new ShiroPasswordMatcher();
        passwordMatcher.setPasswordService(passwordService());

        return passwordMatcher;
    }

    @Bean
    public UserPasswordService passwordService() {
        return new UserPasswordService();
    }


//    @Bean
//    public SessionDAO userSessionDAO() {
//        return new EnterpriseCacheSessionDAO();
//    }

    /**
     * 配置基于redis的缓存CacheManager
     */
    @Bean
    public RedisManager redisManager() {
        return new RedisManager();
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager();
        cacheManager.setRedisManager(redisManager());
        //Redis针对不同用户缓存
        cacheManager.setPrincipalIdFieldName("username");
        //set expire time
        cacheManager.setExpire(200000L);
        return cacheManager;

    }

    /**
     * SessionID 生成器
     *
     * @return
     */
    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    /**
     * Session DAO
     *
     * @return
     */
    @Bean
    public SessionDAO enterpriseCacheSessionDAO() {
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        sessionDAO.setCacheManager(redisCacheManager());
        sessionDAO.setActiveSessionsCacheName("session");
        sessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return sessionDAO;
    }

    /**
     * RememberMe Cokkie，CacheManager设置
     */
    //保存rememberMe的cookie对象，jsessionid，避免于servlet容器名冲突
    @Bean
    public SimpleCookie remembermeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(259200);
//        cookie.setPath("/admin");
        return cookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(remembermeCookie());
        //编码从Test文件中生成
        cookieRememberMeManager.setCipherKey(Base64.decode("YmJjNzQyYjktZjU0ZS00Yzg2LWE5Nzct"));
        return cookieRememberMeManager;
    }

    /**
     * 过滤器 过滤RememberME
     *
     * @return
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter filter = new FormAuthenticationFilter();
        filter.setRememberMeParam("rememberMe");
        return filter;
    }


    /**
     * 配置保存sessionId的cookie，与保存remmemberMe的cookie不同
     */
    @Bean("sessionIdCookie")
    public SimpleCookie sessionIdCookie() {
        SimpleCookie cookie = new SimpleCookie("sid");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        return cookie;
    }

    @Bean
    public DefaultWebSessionManager webSessionManager() {
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        //添加配置监听器
        Collection<SessionListener> listeners = new ArrayList<>();
        listeners.add(shiroSessionListener());
        webSessionManager.setSessionListeners(listeners);

        //设置session Cookie
        webSessionManager.setSessionIdCookie(sessionIdCookie());
        webSessionManager.setSessionIdCookieEnabled(true);

        //设置 session DAO
        webSessionManager.setSessionDAO(enterpriseCacheSessionDAO());

        //设置Cache Manager
//        webSessionManager.setCacheManager(redisCacheManager());

        //全局会话实践
        webSessionManager.setGlobalSessionTimeout(1800000);
        //删除无效的session对象
        webSessionManager.setDeleteInvalidSessions(true);

        //开启定时调度器检测过期session;n
        webSessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间
        webSessionManager.setSessionValidationInterval(3600000);
        //取消后面的jsessionid
        webSessionManager.setSessionIdUrlRewritingEnabled(false);


        return webSessionManager;
    }

    /*
     * 监控同时在线的session数量
     */

    /**
     * session listener to count login session number
     *
     * @return
     */
    @Bean
    public ShiroSessionListener shiroSessionListener() {
        return new ShiroSessionListener();
    }

//
//    /**
//     * 同时登录设置
//     *
//     * @return
//     */
//    @Bean
//    public KickoutSessionControlFilter kickoutSessionControlFilter() {
//        KickoutSessionControlFilter controlFilter = new KickoutSessionControlFilter();
//        controlFilter.setKickoutAfter(false);
//        controlFilter.setMaxSession(1);
//        controlFilter.setSessionManager(webSessionManager());
//        controlFilter.setCacheManager(redisCacheManager());
//        controlFilter.setKickoutUrl("/logout");
//        return controlFilter;
//    }

    @Bean
    public AdviceFilter requestAttributeFilter() {
        return new RequestAttributeFilter();
    }

    /**
     * shiro 生命周期管理器
     */
    @Bean

    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 解决： 无权限页面不跳转 shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized") 无效
     * shiro的源代码ShiroFilterFactoryBean.Java定义的filter必须满足filter instanceof AuthorizationFilter，
     * 只有perms，roles，ssl，rest，port才是属于AuthorizationFilter，而anon，authcBasic，auchc，user是AuthenticationFilter，
     * 所以unauthorizedUrl设置后页面不跳转 Shiro注解模式下，登录失败与没有权限都是通过抛出异常。
     * 并且默认并没有去处理或者捕获这些异常。在SpringMVC下需要配置捕获相应异常来通知用户信息
     *
     * @return
     */
    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty("org.apache.shiro.auth.UnauthorizedException", "/comm/error_403");
        properties.setProperty("org.apache.shiro.auth.UnauthenticatedException", "/comm/error_403");
        resolver.setExceptionMappings(properties);
        return resolver;
    }

    /**
     * 解决spring-boot Whitelabel Error Page
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
        return factory -> {
            ErrorPage error403Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/comm/error_403.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/comm/error_404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/comm/error_500.html");
            factory.addErrorPages(error403Page, error404Page, error500Page);
        };
    }
}

