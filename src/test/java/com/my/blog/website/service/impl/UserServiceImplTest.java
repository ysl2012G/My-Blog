package com.my.blog.website.service.impl;

import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.UserRoleVoKey;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IUserRoleService;
import com.my.blog.website.service.IUserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/*
 * create by shuanglin on 19-1-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Resource
    IUserService userService;

    @Resource
    IUserRoleService userRoleService;
    @Test
    public void queryuserByUsername() {
        UserVo user = userService.queryuserByUsername("admin");
        Assert.assertEquals(Integer.valueOf(1), user.getUid());
    }

    @Test(expected = TipException.class)
    public void queryuserByUserNameException() {
        UserVo user = userService.queryuserByUsername("admin1");
        Assert.assertEquals(Integer.valueOf(1), user.getUid());
    }

    @Test
    public void testInsertUser() {
        UserVo userVo = new UserVo();
        userVo.setEmail("ysl2010un@163.com");
        userVo.setUsername("ysl");
        userVo.setPassword("0123473209");
        userService.insertUser(userVo);
        List<UserRoleVoKey> userRoleVoKeys = userRoleService.getUserRoleById(null, 2);
        Assert.assertEquals(2, userRoleVoKeys.size());
        userRoleVoKeys.sort(Comparator.comparingInt(UserRoleVoKey::getUid));
        Integer uid = userRoleVoKeys.get(1).getUid();
        UserVo user = userService.queryuserByUsername(userVo.getUsername());
        Assert.assertEquals(user.getUid(), uid);

    }

    @Test
    public void getRolesById() {
        Integer uid = 1;
        Set<String> roles = userService.getRolesByID(uid);
        Assert.assertTrue(roles.contains("admin"));
        Assert.assertTrue(roles.contains("user"));
    }

    @Test(expected = TipException.class)
    public void getRolesException() {
        Integer uid = 2;
        Set<String> roles = userService.getRolesByID(uid);
    }
}
