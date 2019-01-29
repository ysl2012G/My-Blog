package com.my.blog.website.service.impl;

import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IUserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/*
 * create by shuanglin on 19-1-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    IUserService userService;

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
}
