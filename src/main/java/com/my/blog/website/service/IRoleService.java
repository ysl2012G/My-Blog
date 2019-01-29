package com.my.blog.website.service;/*
 * create by shuanglin on 19-1-29
 */

public interface IRoleService {

    /**
     * 根据role id 获取角色role名称;
     *
     * @return 角色名称
     */
    String getRoleName(Integer rid);
}
