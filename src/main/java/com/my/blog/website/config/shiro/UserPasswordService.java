package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-1-23
 */

import com.my.blog.website.constant.WebConst;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashService;
import org.apache.shiro.util.SimpleByteSource;

public class UserPasswordService extends DefaultPasswordService {

    public UserPasswordService() {
        super();
        HashService service = this.getHashService();
        if (service instanceof DefaultHashService) {
            ((DefaultHashService) service).setGeneratePublicSalt(true);
//            ((DefaultHashService) service).setHashAlgorithmName("MD5");
            ((DefaultHashService) service).setHashIterations(10);
            ((DefaultHashService) service).setPrivateSalt(new SimpleByteSource(WebConst.HASH_SALT));

        }

    }
}
