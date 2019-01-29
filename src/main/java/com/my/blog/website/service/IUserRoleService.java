package com.my.blog.website.service;/*
 * create by shuanglin on 19-1-29
 */

import com.my.blog.website.model.Vo.UserRoleVoKey;

import java.util.List;

public interface IUserRoleService {

    /**
     * 根据联合主键删除
     *
     * @param uid
     * @param rid
     */
    void deleteByID(Integer uid, Integer rid);

    /**
     * 根据联合主键统计条数
     *
     * @param uid
     * @param rid
     * @return
     */
    Long countByID(Integer uid, Integer rid);

    /**
     * 插入数据
     *
     * @param userRoleVoKey
     */
    void insertUserRoleVo(UserRoleVoKey userRoleVoKey);

    /**
     * 根据联合主键搜索
     *
     * @param uid
     * @param rid
     * @return
     */
    List<UserRoleVoKey> getUserRoleById(Integer uid, Integer rid);

}
