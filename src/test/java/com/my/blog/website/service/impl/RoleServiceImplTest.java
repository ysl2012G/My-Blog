package com.my.blog.website.service.impl;

import com.my.blog.website.service.IRoleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/*
 * create by shuanglin on 19-1-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RoleServiceImplTest {


    @Resource
    private IRoleService roleService;


    @Test
    public void getRoleName() {
        Integer rid_admin = 1;
        Integer rid_user = 2;
        Assert.assertEquals("admin", roleService.getRoleName(rid_admin));
        Assert.assertEquals("user", roleService.getRoleName(rid_user));
    }
}
