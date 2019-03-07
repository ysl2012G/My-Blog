package com.my.blog.website.controller.admin;


import com.github.pagehelper.PageInfo;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IMetaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by 13 on 2017/2/21.
 */
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Resource
    private IContentService contentsService;

    @Resource
    private IMetaService metasService;

    @Resource
    private ILogService logService;

    private void initUID(Integer uid) {
        contentsService.SetCurrentUID(uid);
        metasService.setCurrentUID(uid);
        this.setCurrentUID(uid);
    }

    @GetMapping(value = "")
    public String index(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit) {
        initUID(this.getUid());
        ContentVoExample contentVoExample = new ContentVoExample();
        contentVoExample.setOrderByClause("created desc");
        contentVoExample.createCriteria().andTypeEqualTo(Types.ARTICLE.getType()).andAuthorIdEqualTo(this.getUid());

        PageInfo<ContentVo> contentsPaginator = contentsService.getArticlesWithpage(contentVoExample, page, limit);
        model.addAttribute("articles", contentsPaginator);
        return "admin/article_list";
    }

    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        initUID(this.getUid());
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        return "admin/article_edit";
    }

    @GetMapping(value = "/{cid}")
    public String editArticle(Model model, @PathVariable(required = true) String cid) {
        initUID(this.getUid());
        ContentVo contents = contentsService.getContents(cid);
        model.addAttribute("contents", contents);
        List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
        model.addAttribute("categories", categories);
        model.addAttribute("active", "article");
        return "admin/article_edit";
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponseBo publishArticle(@ModelAttribute(value = "contents") ContentVo contents) {
        UserVo users = this.user();
        this.initUID(this.getUid());
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(contents.getCategories())) {
            contents.setCategories("默认分类");
        }
        String result = contentsService.publish(contents);
//        if (!WebConst.SUCCESS_RESULT.equals(result)) {
//            return RestResponseBo.fail(result);
//        }
//        return RestResponseBo.ok();
        return isSuccessful(result);
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponseBo modifyArticle(@ModelAttribute(value = "contents") ContentVo contents, HttpServletRequest request) {
        UserVo users = this.user();
        this.initUID(this.getUid());
        contents.setAuthorId(users.getUid());
        contents.setType(Types.ARTICLE.getType());
        String result = contentsService.updateArticle(contents);
//        if (!WebConst.SUCCESS_RESULT.equals(result)) {
//            return RestResponseBo.fail(result);
//        }
//        return RestResponseBo.ok();
        return isSuccessful(result);
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam(required = true) int cid, HttpServletRequest request) {
        this.initUID(this.getUid());
        String result = contentsService.deleteByCid(cid);
        logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "", request.getRemoteAddr(), this.getUid());
//        if (!WebConst.SUCCESS_RESULT.equals(result)) {
//            return RestResponseBo.fail(result);
//        }
//        return RestResponseBo.ok();
        return isSuccessful(result);
    }
//
//    private RestResponseBo articleHelper(String result) {
//        if (!WebConst.SUCCESS_RESULT.equals(result)) {
//            return RestResponseBo.fail(result);
//        } else return RestResponseBo.ok();
//    }
}
