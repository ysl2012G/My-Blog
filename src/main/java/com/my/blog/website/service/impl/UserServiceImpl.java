package com.my.blog.website.service.impl;

import com.my.blog.website.dao.UserVoMapper;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.UserRoleVoKey;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.model.Vo.UserVoExample;
import com.my.blog.website.service.IRoleService;
import com.my.blog.website.service.IUserRoleService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by BlueT on 2017/3/3.
 */
@Service
public class UserServiceImpl implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final Integer rid = 2;


    @Resource
    private UserVoMapper userDao;

    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IRoleService roleService;

    @Override
    @Transactional
    public Integer insertUser(UserVo userVo) {
        Integer uid = null;
//        Integer rid = 2;
        if (StringUtils.isNotBlank(userVo.getUsername()) && TaleUtils.isEmail(userVo.getEmail())) {

            //            String encodePwd = TaleUtils.MD5encode(userVo.getUsername() +
            // userVo.getPassword());
            //            userVo.setPassword(encodePwd);
            userDao.insertSelective(userVo);
            uid = insertUserRole(userVo.getUsername());
        } else {
            LOGGER.debug("username({}) or email({}) are not qulified!", userVo.getUsername(), userVo.getEmail());
        }
        return uid;
    }

    @Transactional
    protected Integer insertUserRole(String username) {
        Integer uid = null;
        UserVo sqlUser = queryuserByUsername(username);
        uid = sqlUser.getUid();
        UserRoleVoKey userRoleVoKey = new UserRoleVoKey();
        userRoleVoKey.setUid(uid);
        userRoleVoKey.setRid(rid);
        LOGGER.debug("insert role-base table with:uid={}", uid);
        userRoleService.insertUserRoleVo(userRoleVoKey);
        return uid;
    }

    @Override
    public Set<String> getRolesByID(Integer uid) {
        if (uid == null) {
            throw new TipException("用户id不能为空");
        }
        List<UserRoleVoKey> userRoleVoKeys = userRoleService.getUserRoleById(uid, null);
        if (userRoleVoKeys.size() < 1) {
            throw new TipException("查询结果为空");
        }
        LOGGER.debug("get roles by uid={}", uid);
        Set<String> set = new HashSet<>();
        for (UserRoleVoKey userRoleVoKey : userRoleVoKeys) {
            set.add(roleService.getRoleNameById(userRoleVoKey.getRid()));
        }
        return set;
    }

    @Override
    public UserVo queryUserById(Integer uid) {
        UserVo userVo = null;
        if (uid != null) {
            userVo = userDao.selectByPrimaryKey(uid);
        }
        return userVo;
    }

    @Override
    public UserVo queryuserByUsername(String username) {
        if (username.isEmpty()) {
            throw new TipException("用户名不能为空");
        }
        UserVoExample example = new UserVoExample();
        example.setDistinct(true);
        UserVoExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        long number = userDao.countByExample(example);
        if (number < 1) {
            throw new TipException("该用户不存在");
        }
        List<UserVo> users = userDao.selectByExample(example);
        return users.get(0);

    }

    @Override
    public UserVo login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }
        UserVoExample example = new UserVoExample();
        UserVoExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        long count = userDao.countByExample(example);
        if (count < 1) {
            throw new TipException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username + password);
        criteria.andPasswordEqualTo(pwd);
        List<UserVo> userVos = userDao.selectByExample(example);
        if (userVos.size() != 1) {
            throw new TipException("用户名或密码错误");
        }
        return userVos.get(0);
    }

    @Override
    @Transactional
    public void updateByUid(UserVo userVo) {
        if (null == userVo || null == userVo.getUid()) {
            throw new TipException("userVo is null");
        }
        int i = userDao.updateByPrimaryKeySelective(userVo);
        if (i != 1) {
            throw new TipException("update user by uid and retrun is not one");
        }
    }
}
