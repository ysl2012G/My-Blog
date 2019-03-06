package com.my.blog.website.exception; /*
 * create by shuanglin on 19-2-28
 */

public class PrincipalIDNullException extends RuntimeException {

    /**
     * 参考https://github.com/Clever-Wang/spring-boot-examples/blob/master/spring-boot-shiro-10/src/main/java/com/springboot/test/shiro/global/exceptions/PrincipalIdNullException.java
     */
    private static final String MESSAGE = "Principal Id should not be null!";

    public PrincipalIDNullException(Class tClass, String idMethodName) {
        super(tClass + " id filed:" + idMethodName + " , value is null\n" + MESSAGE);
    }
}
