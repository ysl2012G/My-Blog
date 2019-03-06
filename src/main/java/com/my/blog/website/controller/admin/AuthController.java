package com.my.blog.website.controller.admin;

import com.my.blog.website.config.redis.RedisCacheManager;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.catalina.servlet4preview.http.HttpServletRequest;

/**
 * 用户后台登录/登出
 * Created by BlueT on 2017/3/11.
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private IUserService usersService;

    @Resource
    private ILogService logService;

    @Resource
    private RedisCacheManager cacheManager;


    @GetMapping(value = "/login")
    public String login() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.isRemembered()) {
            LOGGER.debug("subject has been authenticated or remembered");
            return "redirect:index";
        }

        return "admin/login";
    }

    @PostMapping(value = "/login")
    public String login2(@RequestParam String username, @RequestParam String password,
                         @RequestParam(required = false) boolean rememberMe, Model model,
                         HttpServletRequest request, HttpServletResponse response) {
//        boolean rememberFlag = null != rememberMe;
//        LOGGER.debug("rememberMe value is {}", rememberMe);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password, rememberMe);
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(usernamePasswordToken);
//            LOGGER.warn("subject is authenticated or remembered. {}", subject.isAuthenticated());
//            return "redirect:index";
        } catch (Exception e) {
            if (e instanceof UnknownAccountException) {
                model.addAttribute("msg", "用户名或者密码错误");
            } else if (e instanceof LockedAccountException) {
                model.addAttribute("msg", "失败登录超过3次，请10分钟后再试");
            } else {
                model.addAttribute("msg", "登陆失败");
            }
        }

        return "/admin/login";

    }


//    @PostMapping(value = "/login")
//    @ResponseBody
//    public RestResponseBo doLogin(@RequestParam String username, @RequestParam String password,
//                                  @RequestParam(required = false) boolean remeber_me,
//                                  HttpServletRequest request,
//                                  HttpServletResponse response) {
//
//
////        Cache<String, AtomicInteger> cache = cacheManager.getCache(WebConst.ERROR_LOGIN_CACHE_NAME, WebConst.ERROR_LOGIN_LOCK_TIME);
////        LOGGER.debug("get error login cache name:{}", WebConst.ERROR_LOGIN_CACHE_NAME);
////        String val = IPKit.getIpAddrByRequest(request);
////        AtomicInteger error_login_count = cache.get(val);
////        UserVo userVo = usersService.queryuserByUsername(username);
////        if (userVo != null) {
////            AtomicInteger error_count = cache.get(userVo.getUsername());
////        }
//        //设置 rememberme 布尔值
//        LOGGER.debug("checkbox value is {}", remeber_me);
////        boolean rememberMe = null != remeber_me;
//        Subject subject = SecurityUtils.getSubject();
//        UsernamePasswordToken loginToken = new UsernamePasswordToken(username, password, remeber_me);
//        try {
////            LOGGER.debug("current error login count is {}", error_login_count.get());
////            if (error_login_count.get() > 3) {
////                return RestResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试！");
////            }
//            subject.login(loginToken);
////            UserVo user = usersService.login(username, password);
//
//
//        } catch (Exception e) {
////            error_count = null == error_count ? 1 : error_count + 1;
////            if (error_login_count == null) {
////                error_login_count = new AtomicInteger(1);
////            } else {
////                error_login_count.incrementAndGet();
////            }
//
////            if (error_count > 3) {
////                return RestResponseBo.fail("您输入密码已经错误超过3次，请10分钟后尝试");
////            }
////            cache.set("login_error_count", error_count, 10 * 60);
//            String msg = "登录失败";
//            if (e instanceof TipException) {
//                msg = e.getMessage();
//            } else {
//                LOGGER.error(msg, e);
//            }
//            return RestResponseBo.fail(msg);
//        } finally {
//            if (subject.isAuthenticated()) {
//                UserVo userVo = (UserVo) subject.getPrincipal();
////            request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, userVo);
//                LOGGER.warn("subject getSession() is {}", subject.getSession().getId());
//                LOGGER.warn("request.getsession is {}", request.getSession().getId());
//                logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), userVo.getUid());
//            }
//        }
//
////        request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
//
////        if (StringUtils.isNotBlank(remeber_me)) {
////            TaleUtils.setCookie(response, user.getUid());
////        }
////        logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());
//
//        return RestResponseBo.ok();
//    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) {

//        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
//        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, "");
//        cookie.setValue(null);
//        cookie.setMaxAge(0);// 立即销毁cookie
//        cookie.setPath("/");
//        response.addCookie(cookie);
        Subject subject = SecurityUtils.getSubject();
        try {


            subject.logout();

//            response.sendRedirect("/admin/login");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }

    }

}
