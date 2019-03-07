package com.my.blog.website.controller.admin;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.dto.Types;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.ILogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by 13 on 2017/2/21.
 */
@Controller()
@RequestMapping("admin/page")
public class PageController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @Resource
    private IContentService contentsService;

    @Resource
    private ILogService logService;

    private void initUID(Integer uid) {
        contentsService.SetCurrentUID(uid);
        logService.setCurrentUID(uid);
        this.setCurrentUID(uid);
    }

    @GetMapping(value = "")
    public String index(Model model) {
        this.initUID(this.getUid());
        ContentVoExample contentVoExample = new ContentVoExample();
        contentVoExample.setOrderByClause("created desc");
        contentVoExample.createCriteria().andTypeEqualTo(Types.PAGE.getType()).andAuthorIdEqualTo(getCurrentUID());
        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(contentVoExample, 1, WebConst.MAX_POSTS);
//        request.setAttribute("articles", contentsPaginator);
        model.addAttribute("articles", contentsPaginator);
        return "admin/page_list";
    }

    @GetMapping(value = "new")
    public String newPage(Model model) {
        return "admin/page_edit";
    }

    @GetMapping(value = "/{cid}")
    public String editPage(@PathVariable String cid, Model model) {
        ContentVo contents = contentsService.getContents(cid);
//        request.setAttribute("contents", contents);
        model.addAttribute("contents", contents);
        return "admin/page_edit";
    }

//    @PostMapping(value = "publish")
//    @ResponseBody
//    public RestResponseBo publishPage(@RequestParam String title, @RequestParam String content,
//                                      @RequestParam String status, @RequestParam String slug,
//                                      @RequestParam(required = false) Integer allowComment, @RequestParam(required = false) Integer allowPing, HttpServletRequest request) {
//
//        UserVo users = this.user(request);
//        ContentVo contents = new ContentVo();
//        contents.setTitle(title);
//        contents.setContent(content);
//        contents.setStatus(status);
//        contents.setSlug(slug);
//        contents.setType(Types.PAGE.getType());
//        if (null != allowComment) {
//            contents.setAllowComment(allowComment == 1);
//        }
//        if (null != allowPing) {
//            contents.setAllowPing(allowPing == 1);
//        }
//        contents.setAuthorId(users.getUid());
//        String result = contentsService.publish(contents);
//        if (!WebConst.SUCCESS_RESULT.equals(result)) {
//            return RestResponseBo.fail(result);
//        }
//        return RestResponseBo.ok();
//    }

    @PostMapping(value = "publish")
    @ResponseBody
    public RestResponseBo publishPage(HttpServletRequest request, @ModelAttribute(value = "contents") ContentVo contents) {

        UserVo users = this.user();
        this.initUID(this.getUid());
        contents.setType(Types.PAGE.getType());
        contents.setAuthorId(users.getUid());
        String result = contentsService.publish(contents);
        return isSuccessful(result);
    }

    @PostMapping(value = "modify")
    @ResponseBody
    public RestResponseBo modifyPage(@ModelAttribute(value = "contents") ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user();
        this.initUID(this.getUid());
        contents.setAuthorId(users.getUid());
        contents.setType(Types.PAGE.getType());
        String result = contentsService.updateArticle(contents);
        return isSuccessful(result);
    }

//    @PostMapping(value = "modify")
//    @ResponseBody
//    public RestResponseBo modifyArticle(@RequestParam Integer cid, @RequestParam String title,
//                                        @RequestParam String content,
//                                        @RequestParam String status, @RequestParam String slug,
//                                        @RequestParam(required = false) Integer allowComment, @RequestParam(required = false) Integer allowPing, HttpServletRequest request) {
//
//        UserVo users = this.user(request);
//        ContentVo contents = new ContentVo();
//        contents.setCid(cid);
//        contents.setTitle(title);
//        contents.setContent(content);
//        contents.setStatus(status);
//        contents.setSlug(slug);
//        contents.setType(Types.PAGE.getType());
//        if (null != allowComment) {
//            contents.setAllowComment(allowComment == 1);
//        }
//        if (null != allowPing) {
//            contents.setAllowPing(allowPing == 1);
//        }
//        contents.setAuthorId(users.getUid());
//        String result = contentsService.updateArticle(contents);
//        if (!WebConst.SUCCESS_RESULT.equals(result)) {
//            return RestResponseBo.fail(result);
//        }
//        return RestResponseBo.ok();
//    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam int cid, HttpServletRequest request) {
        this.initUID(this.getUid());
        String result = contentsService.deleteByCid(cid);
        logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "", request.getRemoteAddr(), this.getUid());
        return isSuccessful(result);
        }

}
