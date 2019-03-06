package com.my.blog.website.exception; /*
 * create by shuanglin on 19-2-28
 */

public class PrincipalInstanceException extends RuntimeException {

    /**
     * 参考
     * https://github.com/Clever-Wang/spring-boot-examples/blob/master/spring-boot-shiro-10/src/main/java/com/springboot/test/shiro/global/exceptions/PrincipalInstanceException.java
     */
    private static final String MESSAGE =
            "We need a field to identify this Cache Object in Redis. "
                    + "So you need to defined an id field which you can get unique id to identify this principal. "
                    + "For example, if you use UserInfo as Principal class, the id field maybe userId, userName, email, etc. "
                    + "For example, getUserId(), getUserName(), getEmail(), etc.\n"
                    + "Default value is authCacheKey or id, that means your principal object has a method called \"getAuthCacheKey()\" or \"getId()\"";

    public PrincipalInstanceException(Class tClass, String idMethodName) {
        super(tClass + "must has getter for field:" + idMethodName + "\n" + MESSAGE);
    }

    public PrincipalInstanceException(Class tClass, String idMethodName, Exception e) {
        super(tClass + "must has getter for field:" + idMethodName + "\n" + MESSAGE, e);
    }
}
