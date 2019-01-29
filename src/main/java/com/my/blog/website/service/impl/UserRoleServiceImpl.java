package com.my.blog.website.service.impl;/*
 * create by shuanglin on 19-1-29
 */

import com.my.blog.website.dao.UserRoleVoMapper;
import com.my.blog.website.model.Vo.UserRoleVoExample;
import com.my.blog.website.model.Vo.UserRoleVoKey;
import com.my.blog.website.service.IUserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserRoleServiceImpl implements IUserRoleService {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleServiceImpl.class);
    private static final UserRoleVoExample example = new UserRoleVoExample();


    @Resource
    private UserRoleVoMapper userRoleVoMapper;

    @Override
    public void deleteByID(Integer uid, Integer rid) {
        logger.debug("delete user-role table: uid={},rid={}", uid, rid);
        setExampleCriteria(uid, rid);
        userRoleVoMapper.deleteByExample(example);
    }

    @Override
    public Long countByID(Integer uid, Integer rid) {
        logger.debug("count user-role table:uid={},rid={}", uid, rid);
        setExampleCriteria(uid, rid);
        long number = userRoleVoMapper.countByExample(example);
        logger.debug("methd return number={}", number);
        return number;
    }

    @Override
    public void insertUserRoleVo(UserRoleVoKey userRoleVoKey) {
        logger.debug("insert user-role values");
        userRoleVoMapper.insert(userRoleVoKey);
    }

    @Override
    public List<UserRoleVoKey> getUserRoleById(Integer uid, Integer rid) {
        logger.debug("get list result with:uid={},rid={}", uid, rid);
        setExampleCriteria(uid, rid);
        return userRoleVoMapper.selectByExample(example);
    }

    private void setExampleCriteria(Integer uid, Integer rid) {
        logger.debug("use private helper method to create criteria with: uid={},rid={}", uid, rid);
        example.clear();
        UserRoleVoExample.Criteria criteria = example.createCriteria();
        if (uid != null) {
            criteria.andUidEqualTo(uid);
        }

        if (rid != null) {
            criteria.andRidEqualTo(rid);
        }
    }
}
