package com.my.blog.website.service.impl;/*
 * create by shuanglin on 19-1-29
 */

import com.my.blog.website.dao.RoleVoMapper;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.RoleVo;
import com.my.blog.website.model.Vo.RoleVoExample;
import com.my.blog.website.service.IRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Resource
    private RoleVoMapper roleVoMapper;

    @Override
    public String getRoleNameById(Integer rid) {
        logger.debug("get RoleName by rid={}", rid);
        RoleVo role = roleVoMapper.selectByPrimaryKey(rid);
        if (role != null) {
            return role.getRolename();
        }
        return null;
    }

    @Override
    public RoleVo getRoleVoByName(String roleName) {
        if (roleName == null) {
            throw new TipException("角色名不能为空");
        }
        RoleVo role = null;
        RoleVoExample example = new RoleVoExample();
        RoleVoExample.Criteria criteria = example.createCriteria();
        criteria.andRolenameEqualTo(roleName);
        long count = roleVoMapper.countByExample(example);
        if (count < 1) {
            logger.error("no such roleVo");
            throw new TipException("没用这个角色");
        }
        logger.debug("get roleVo from role Name");
        List<RoleVo> roleVos = roleVoMapper.selectByExample(example);
        return roleVos.get(0);
    }

    @Override
    @Transactional
    public void insertRoleName(String roleName) {
        logger.debug("insert RoleName");
        RoleVo role = new RoleVo();
        role.setRolename(roleName);
        roleVoMapper.insert(role);
    }


    @Override
    @Transactional
    public void insertRoleVo(RoleVo roleVo) {
        logger.debug("insert RoleVo");
        roleVoMapper.insert(roleVo);
    }
}
