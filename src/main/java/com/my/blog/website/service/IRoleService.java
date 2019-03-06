package com.my.blog.website.service;/*
 * create by shuanglin on 19-1-29
 */

import com.my.blog.website.model.Vo.RoleVo;

public interface IRoleService {

    /**
     * 根据role id 获取角色role名称;
     *
     * @return 角色名称
     */
    String getRoleNameById(Integer rid);

    /**
     * 根据角色名称获取rid
     */

    RoleVo getRoleVoByName(String roleName);

    /**
     * 根据名称添加角色
     *
     * @param roleName
     */
    void insertRoleName(String roleName);

    /**
     * 添加角色
     *
     * @param roleVo
     */
    void insertRoleVo(RoleVo roleVo);
}
