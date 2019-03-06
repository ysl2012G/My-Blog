package com.my.blog.website.config.shiro;/*
 * create by shuanglin on 19-1-23
 */

import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class UserRealm extends AuthorizingRealm {
    private final static Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    public IUserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.info("shiro授权：UserRealm.doGetAuthoriztionInfo");
        UserVo userVo = (UserVo) principals.getPrimaryPrincipal();
//        String username = (String) principals.getPrimaryPrincipal();
//        UserVo userVo = userService.queryuserByUsername(username);
        Set<String> roleSet = userService.getRolesByID(userVo.getUid());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(roleSet);
        return authorizationInfo;

    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        logger.info("身份认证：UserRealm.doGetAuthenticationInfo()");
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
//        String password = new String(usernamePasswordToken.getPassword());
        UserVo sqlUser = null;
        try {
            sqlUser = userService.queryuserByUsername(username);
        } catch (TipException e) {
            logger.error("数据库获取信息失败" + e.getMessage());
            throw new UnknownAccountException(e);
//            throw new AuthenticationException("身份认证出错！");
        }
        if (sqlUser == null) {
            throw new UnknownAccountException("数据库不存在当前用户");
        }
//        String sqlPassword = sqlUser.getPassword();
        return new SimpleAuthenticationInfo(sqlUser, sqlUser.getPassword(), getName());

    }

    /**
     * 清除当前用户授权缓存
     */
    @Override
    protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清除当前用户认证缓存
     */
    @Override
    protected void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    protected void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    /**
     * 清除所有认证缓存
     */
    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    /**
     * 清除所有授权缓存
     */
    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    /**
     * 清除所有token缓存
     */
    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
