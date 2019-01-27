package com.my.blog.website.controller;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.ErrorCode;
import com.my.blog.website.dto.MetaDto;
import com.my.blog.website.dto.Types;
import com.my.blog.website.model.Bo.ArchiveBo;
import com.my.blog.website.model.Bo.CommentBo;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.CommentVo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.service.ICommentService;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.utils.IPKit;
import com.my.blog.website.utils.PatternKit;
import com.my.blog.website.utils.TaleUtils;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.List;

/**
 * 首页
 * Created by Administrator on 2017/3/8 008.
 */
@Controller
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private IContentService contentService;

    @Resource
    private ICommentService commentService;

    @Resource
    private IMetaService metaService;

    @Resource
    private ISiteService siteService;

    /**
     * 首页
     *
     * @return thymeleaf
     */
    @GetMapping(value = "/")
    public String index(Model model, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.index(model, 1, limit);
    }

    /**
     * 首页分页
     *
     * @param model   spring model
     * @param p       第几页
     * @param limit   每页大小
     * @return 主页
     */
    @GetMapping(value = "page/{p}")
    public String index(Model model, @PathVariable int p, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        p = p < 0 || p > WebConst.MAX_PAGE ? 1 : p;
        PageInfo<ContentVo> articles = contentService.getContents(p, limit);
//        request.setAttribute("articles", articles);
        model.addAttribute("articles", articles);
        if (p > 1) {
            this.title(model, "第" + p + "页");
        }
        return this.render("index");
    }

    /**
     * 文章页
     *
     * @param request 请求
     * @param cid     文章主键
     * @return thymeleaf html
     */
    @GetMapping(value = {"article/{cid}", "article/{cid}.html"})
    public String getArticle(Model model, HttpServletRequest request, @PathVariable String cid, @RequestParam(value = "cp", defaultValue = "1") String cp) {
        ContentVo contents = contentService.getContents(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }
//        model.addAttribute("article", contents);
//        model.addAttribute("is_post", true);
        completeArticle(model, contents, cp);
        if (!checkHitsFrequency(request, cid)) {
            updateArticleHit(contents.getCid(), contents.getHits());
        }
        return this.render("post");


    }

    /**
     * 文章页(预览)
     *
     * @param model Model
     * @param cid     文章主键
     * @return thymeleaf
     */
    @GetMapping(value = {"article/{cid}/preview", "article/{cid}.html"})
    public String articlePreview(Model model, HttpServletRequest request, @PathVariable String cid, @RequestParam(value = "cp", defaultValue = "1") String cp) {
        ContentVo contents = contentService.getContents(cid);
        if (null == contents) {
            return this.render_404();
        }
//        request.setAttribute("article", contents);
//        request.setAttribute("is_post", true);
        completeArticle(model, contents, cp);
        if (!checkHitsFrequency(request, cid)) {
            updateArticleHit(contents.getCid(), contents.getHits());
        }
        return this.render("post");


    }


    /**
     * 抽取公共方法
     *
     * @param model Model
     * @param contents com.my.blog.website.model.vo.ContentVO
     */
    private void completeArticle(Model model, ContentVo contents, String cp) {
        model.addAttribute("article", contents);
        model.addAttribute("is_post", true);
        if (contents.getAllowComment()) {
//            String cp = request.getParameter("cp");
//            if (StringUtils.isBlank(cp)) {
//                cp = "1";
//            }
//            request.setAttribute("cp", cp);
            //default cp value is "1";
            model.addAttribute("cp", cp);
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
//            request.setAttribute("comments", commentsPaginator);
            model.addAttribute("comments", commentsPaginator);
        }
    }

    /**
     * 注销
     *
     * @param session Session
     * @param response Http Response
     */
    @RequestMapping("logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session, response);
    }

    /**
     * 评论操作
     */
    @PostMapping(value = "comment")
    @ResponseBody
    public RestResponseBo comment(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam Integer cid, @RequestParam Integer coid,
                                  @RequestParam String author, @RequestParam String mail,
                                  @RequestParam String url, @RequestParam String text, @RequestParam String _csrf_token) {

        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
        }

        String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);
        if (StringUtils.isBlank(token)) {
            return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
        }

        if (null == cid || StringUtils.isBlank(text)) {
            return RestResponseBo.fail("请输入完整后评论");
        }

        if (StringUtils.isNotBlank(author) && author.length() > 50) {
            return RestResponseBo.fail("姓名过长");
        }

        if (StringUtils.isNotBlank(mail) && !TaleUtils.isEmail(mail)) {
            return RestResponseBo.fail("请输入正确的邮箱格式");
        }

        if (StringUtils.isNotBlank(url) && !PatternKit.isURL(url)) {
            return RestResponseBo.fail("请输入正确的URL格式");
        }

        if (text.length() > 200) {
            return RestResponseBo.fail("请输入200个字符以内的评论");
        }

        String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return RestResponseBo.fail("您发表评论太快了，请过会再试");
        }

        author = TaleUtils.cleanXSS(author);
        text = TaleUtils.cleanXSS(text);

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        CommentVo comments = new CommentVo();
        comments.setAuthor(author);
        comments.setCid(cid);
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(url);
        comments.setContent(text);
        comments.setMail(mail);
        //TODO
        comments.setParent(coid);
        try {
            String result = commentService.insertComment(comments);
            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLEncoder.encode(mail, "UTF-8"), 7 * 24 * 60 * 60, response);
            if (StringUtils.isNotBlank(url)) {
                cookie("tale_remember_url", URLEncoder.encode(url, "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            // 设置对每个文章1分钟可以评论一次
            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);
            if (!WebConst.SUCCESS_RESULT.equals(result)) {
                return RestResponseBo.fail(result);
            }
            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "评论发布失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
    }


    /**
     * 分类页
     *
     * @return thymeleaf html
     */
    @GetMapping(value = "category/{keyword}")
    public String categories(Model model, @PathVariable String keyword, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
//        return this.categories(model, keyword, 1, limit);
//    }
//
//    @GetMapping(value = "category/{keyword}")
//    public String categories(Model model, @PathVariable String keyword,
//                             @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
        MetaDto metaDto = metaService.getMeta(Types.CATEGORY.getType(), keyword);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);

        model.addAttribute("articles", contentsPaginator);
        model.addAttribute("meta", metaDto);
        model.addAttribute("type", "分类");
        model.addAttribute("keyword", keyword);
        return this.render("page-category");
    }


    /**
     * 归档页
     *
     * @return thymeleaf html
     */
    @GetMapping(value = "archives")
    public String archives(Model model) {
        List<ArchiveBo> archives = siteService.getArchives();
        model.addAttribute("archives", archives);
        return this.render("archives");
    }

    /**
     * 友链页
     *
     * @return thymeleaf html
     */
    @GetMapping(value = "links")
    public String links(Model model) {
        List<MetaVo> links = metaService.getMetas(Types.LINK.getType());
        model.addAttribute("links", links);
        return this.render("links");
    }

    /**
     * 自定义页面,如关于的页面
     */
    @GetMapping(value = "/{pagename}")
    public String page(Model model, @PathVariable String pagename, @RequestParam(value = "cp", defaultValue = "1") String cp) {
        ContentVo contents = contentService.getContents(pagename);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }
//        if (contents.getAllowComment()) {
//            String cp = request.getParameter("cp");
//            if (StringUtils.isBlank(cp)) {
//                cp = "1";
//            }
//            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
//            model.addAttribute("comments", commentsPaginator);
//        }
//        model.addAttribute("article", contents);
//        if (!checkHitsFrequency(request, String.valueOf(contents.getCid()))) {
//            updateArticleHit(contents.getCid(), contents.getHits());
//        }
        completeArticle(model, contents, cp);
        return this.render("page");
    }

    @GetMapping("/{pagename}/preview")
    public String pagePrewview(Model model, @PathVariable(value = "pagename") String
            pagename, @RequestParam(value = "cp", defaultValue = "1") String cp) {
        ContentVo contents = contentService.getContents(pagename);
        if (null == contents) {
            return this.render_404();
        }
        completeArticle(model, contents, cp);
        return this.render("page");


    }


    /**
     * 搜索页
     *
     * @param keyword 关键字
     * @return thymeleaf html
     */
    @GetMapping(value = "search/{keyword}")
    public String search(Model model, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.search(model, keyword, 1, limit);
    }

    @GetMapping(value = "search/{keyword}/{page}")
    public String search(Model model, @PathVariable String keyword, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
        PageInfo<ContentVo> articles = contentService.getArticles(keyword, page, limit);
        model.addAttribute("articles", articles);
        model.addAttribute("type", "搜索");
        model.addAttribute("keyword", keyword);
        return this.render("page-category");
    }

    /**
     * 更新文章的点击率
     *
     * @param cid contentVo id
     * @param chits current hits
     */
    private void updateArticleHit(Integer cid, Integer chits) {
        Integer hits = cache.hget("article" + cid, "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXCEED) {
            ContentVo temp = new ContentVo();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateContentByCid(temp);
            cache.hset("article" + cid, "hits", 1);
        } else {
            cache.hset("article" + cid, "hits", hits);
        }
    }

//    /**
//     * p标签页
//     *
//     * @param name
//     * @return
//     */
//    @GetMapping(value = "tag/{name}")
//    public String tags(Model model, @PathVariable String name, @RequestParam(value = "limit", defaultValue = "12") int limit) {
//        return this.tags(model, name, 1, limit);
//    }

    /**
     * 标签分页
     *
     * @param model Model
     * @param name Name
     * @param page Page
     * @param limit Limit
     * @return thymeleaf
     */
    @GetMapping(value = "tag/{name}")
    public String tags(Model model, @PathVariable String name, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {

        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
//        对于空格的特殊处理
        name = name.replaceAll("\\+", " ");
        MetaDto metaDto = metaService.getMeta(Types.TAG.getType(), name);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);
        model.addAttribute("articles", contentsPaginator);
        model.addAttribute("meta", metaDto);
        model.addAttribute("type", "标签");
        model.addAttribute("keyword", name);

        return this.render("page-category");
    }

    /**
     * 设置cookie
     *
     * @param name     Name
     * @param value Value
     * @param maxAge max age
     * @param response http response
     */
    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    /**
     * 检查同一个ip地址是否在2小时内访问同一文章
     *
     * @param request http servlet request
     * @param cid content id
     * @return boolean value
     */
    private boolean checkHitsFrequency(HttpServletRequest request, String cid) {
        String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
        Integer count = cache.hget(Types.HITS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return true;
        }
        cache.hset(Types.HITS_FREQUENCY.getType(), val, 1, WebConst.HITS_LIMIT_TIME);
        return false;
    }

}
