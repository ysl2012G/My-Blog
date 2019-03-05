package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-3-4
 */

import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.service.IOptionService;
import com.my.blog.website.utils.AdminCommons;
import com.my.blog.website.utils.Commons;
import com.my.blog.website.utils.IPKit;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestAttributeFilter extends AccessControlFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAttributeFilter.class);
//    private static final String CSRF_TOKEN_PREFIX = "csrf_token";
//    private String cachePrefix = CSRF_TOKEN_PREFIX;


    //expire time 30 seconds;

    @Autowired
    private Commons commons;

    @Autowired
    private AdminCommons adminCommons;

    @Autowired
    private IOptionService optionService;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        LOGGER.info("用户访问地址{},来路地址", httpServletRequest.getRequestURI(), IPKit.getIpAddrByRequest(httpServletRequest));

//        Subject subject = SecurityUtils.getSubject();
//        long expire = CSRF_TOEKN_EXPIRE;
//        Cache<Serializable, String> CSRFCache = cacheManager.getCache(Types.CSRF_TOKEN.getType(), expire);
//        Session session = subject.getSession();
//        Serializable sessionId = session.getId();
//        String token = null;
//        token = (String) session.getAttribute("_csrf_token");
//        if (token == null) {
//            token = CSRFCache.get(Serializable)
//        }
//
//
//        if (CSRFCache.get(sessionId) == null) {
//            token = UUID.randomUUID().toString();
//            CSRFCache.put(sessionId, token);
//            //            session.setAttribute("_csrf_token", token);
//            request.setAttribute("__csrf_token", token);
//            LOGGER.debug("request has set attribute:{}", "_csrf_token");
//        }


        OptionVo ov = optionService.getOptionByName("site_record");
        request.setAttribute("commons", commons);
        LOGGER.debug("request has set attribute:{}", "commons");
        request.setAttribute("option", ov);
        LOGGER.debug("request has set attribute:{}", "option");
        request.setAttribute("adminCommons", adminCommons);
        LOGGER.debug("request has set attribute:{}", "adminCommons");
        return true;
    }
}
