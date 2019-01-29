package com.my.blog.website.service.impl;/*
 * create by shuanglin on 19-1-29
 */

import com.my.blog.website.dao.RoleVoMapper;
import com.my.blog.website.model.Vo.RoleVo;
import com.my.blog.website.service.IRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RoleServiceImpl implements IRoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Resource
    private RoleVoMapper roleVoMapper;

    @Override
    public String getRoleName(Integer rid) {
        logger.debug("get RoleName by rid={}", rid);
        RoleVo role = roleVoMapper.selectByPrimaryKey(rid);
        if (role != null) {
            return role.getRolename();
        }
        return null;
    }
}
