package com.my.blog.website.service.impl;

import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.RoleVo;
import com.my.blog.website.service.IRoleService;
import org.junit.Assert;
import org.junit.Before;
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

    @Before
    public void setup() {
        String roleName = "test";
        roleService.insertRoleName(roleName);
    }

    @Test
    public void getRoleNameByID() {
        Integer rid_admin = 1;
        Integer rid_user = 2;
        Assert.assertEquals("admin", roleService.getRoleNameById(rid_admin));
        Assert.assertEquals("user", roleService.getRoleNameById(rid_user));
    }

    @Test
    public void getRoleVoTest() {
        Integer rid_admin = 1;
        Integer rid_user = 2;
        Assert.assertEquals(rid_admin, roleService.getRoleVoByName("admin").getRid());
        Assert.assertEquals(rid_user, roleService.getRoleVoByName("user").getRid());
        Assert.assertEquals("test", roleService.getRoleVoByName("test").getRolename());
    }

    @Test(expected = TipException.class)
    public void getRoleVoException() {
        roleService.getRoleVoByName("test1");
    }

    @Test
    public void insertRoleVoTest() {
        RoleVo role = new RoleVo();
        role.setRid(40);
        role.setRolename("scala");
        roleService.insertRoleVo(role);
        Assert.assertEquals("scala", roleService.getRoleNameById(40));
        Assert.assertEquals(Integer.valueOf(40), roleService.getRoleVoByName("scala").getRid());
    }

    @Test
    public void insertRoleNameTest() {
        roleService.insertRoleName("scala");
        Integer rid = roleService.getRoleVoByName("scala").getRid();
        Assert.assertEquals("scala", roleService.getRoleNameById(rid));
        Assert.assertEquals(rid, roleService.getRoleVoByName("scala").getRid());
    }

}
