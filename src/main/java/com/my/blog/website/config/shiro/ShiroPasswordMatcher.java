package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-3-6
 */

import com.my.blog.website.dto.LogActions;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.ILogService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.springframework.beans.factory.annotation.Autowired;

public class ShiroPasswordMatcher extends PasswordMatcher {
    @Autowired
    private ILogService logService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        boolean matcher = super.doCredentialsMatch(token, info);
        if (matcher) {
            UserVo userVo = (UserVo) info.getPrincipals().getPrimaryPrincipal();
            logService.insertLog(LogActions.LOGIN.getAction(), null, null, userVo.getUid());
        }
        return matcher;
    }
}
