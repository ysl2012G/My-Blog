package com.my.blog.website.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BlueT on 2017/3/3.
 */
@Component
public class WebConst {
    public static Map<String, String> initConfig = new HashMap<>();


    public static String LOGIN_SESSION_KEY = "login_user";

    public static final String USER_IN_COOKIE = "S_L_ID";

    /**
     * aes加密加盐
     */
    public static String AES_SALT = "0123456789abcdef";


    /**
     * hash加密盐值
     */
    public static String HASH_SALT = "my-blog201901-03";

    /**
     * 最大获取文章条数
     */
    public static final int MAX_POSTS = 9999;

    /**
     * 最大页码
     */
    public static final int MAX_PAGE = 100;

    /**
     * 文章最多可以输入的文字数
     */
    public static final int MAX_TEXT_COUNT = 200000;

    /**
     * 文章标题最多可以输入的文字个数
     */
    public static final int MAX_TITLE_COUNT = 200;

    /**
     * 点击次数超过多少更新到数据库
     */
    public static final int HIT_EXCEED = 10;

    /**
     * 上传文件最大1M
     */
    public static Integer MAX_FILE_SIZE = 1048576;

    /**
     * 成功返回
     */
    public static String SUCCESS_RESULT = "SUCCESS";

    /**
     * 同一篇文章在2个小时内无论点击多少次只算一次阅读
     */
    public static Integer HITS_LIMIT_TIME = 7200;

    /**
     * 点击数量保存实践(30天)
     */

    public static Long HITS_EXPIRE_TIME = 2592000L;

    /**
     * 评论间隔时间
     */
    public static Long COMMENT_FREQUENCY_EXPIRE_TIME = 60L;

    /**
     * 保存登陆失败的cache name
     */
    public static String ERROR_LOGIN_CACHE_NAME = "error_login_user";
    /**
     * 登录超过次数的锁定时间
     */

    public static long ERROR_LOGIN_LOCK_TIME = 600L;
}
