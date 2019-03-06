package com.my.blog.website.controller;

import com.my.blog.website.config.redis.RedisCacheManager;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.Types;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by 13 on 2017/2/21.
 */
public abstract class BaseController {

    public static String THEME = "themes/default";

    //    protected MapCache cache = MapCache.single();
    @Autowired
    protected RedisCacheManager cacheManager;

    private static final String CONTROLLER_CACHE_PREFIX = "controller";


    /**
     * 主页的页面主题
     * @param viewName 页面
     * @return 返回thymeleaf名称
     */
    protected String render(String viewName) {
        return THEME + "/" + viewName;
    }

    /**
     * 获取当前sessionid的csrf_token
     *
     * @return Cache
     */

    protected String getCSRFToken() {
        Serializable sessionId = SecurityUtils.getSubject().getSession().getId();
        Cache<Serializable, String> cache = cacheManager.getCache(Types.CSRF_TOKEN.getType());
        return cache.get(sessionId);

    }

    protected <K, V> Cache<K, V> getCache(String types) {
        Subject subject = SecurityUtils.getSubject();
        Serializable sessionId = subject.getSession().getId();
        return cacheManager.getCache(CONTROLLER_CACHE_PREFIX + ":" + types);
    }

    protected <K, V> Cache<K, V> getCache(String types, long expireTime) {
        Serializable sessionId = SecurityUtils.getSubject().getSession().getId();
        return cacheManager.getCache(CONTROLLER_CACHE_PREFIX + ":" + types, expireTime);
    }

    public BaseController title(Model model, String title) {
        model.addAttribute("title", title);
        return this;
    }

    public BaseController keywords(Model model, String keywords) {
        model.addAttribute("keywords", keywords);
        return this;
    }

    /**
     * 获取请求绑定的登录对象
     * @param request Http Servlet Request
     * @return login user
     */
    public UserVo user(HttpServletRequest request) {
//        return TaleUtils.getLoginUser(request);
        return (UserVo) SecurityUtils.getSubject().getPrincipal();
    }

    public Integer getUid(HttpServletRequest request){
        return this.user(request).getUid();
    }

    protected String render_404() {
        return "comm/error_404";
    }

    protected RestResponseBo isSuccessful(String result) {
        return WebConst.SUCCESS_RESULT.equals(result) ? RestResponseBo.ok() : RestResponseBo.fail(result);
    }

}
