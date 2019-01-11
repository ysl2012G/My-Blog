package com.my.blog.website.controller;

import com.my.blog.website.constant.WebConst;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.utils.MapCache;
import com.my.blog.website.utils.TaleUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 13 on 2017/2/21.
 */
public abstract class BaseController {

    public static String THEME = "themes/default";

    protected MapCache cache = MapCache.single();

    /**
     * 主页的页面主题
     * @param viewName 页面
     * @return 返回thymeleaf名称
     */
    protected String render(String viewName) {
        return THEME + "/" + viewName;
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
        return TaleUtils.getLoginUser(request);
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
