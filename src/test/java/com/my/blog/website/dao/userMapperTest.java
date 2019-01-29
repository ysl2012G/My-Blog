package com.my.blog.website.dao;/*
 * create by shuanglin on 19-1-28
 */

import com.my.blog.website.model.Vo.UserVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class userMapperTest {
    @Autowired
    private UserVoMapper userVoMapper;

    private String sha256Hash;

    @Before
    @Transactional
    public void setup() {
        sha256Hash = "$shiro1$SHA-256$10$6iskYnntymLpFP7S8Ian0A==$KcE4QPIusNLCG7Tg+jRN3YuEJuvNTsN379zWO3cdH3I=";
        int uid = 1;
        UserVo user = userVoMapper.selectByPrimaryKey(uid);
        user.setPassword(sha256Hash);
        userVoMapper.updateByPrimaryKeySelective(user);
    }

    @Test
    @Transactional
    public void updatePasswords() {
        int uid = 1;
        UserVo user = userVoMapper.selectByPrimaryKey(uid);
        Assert.assertTrue(sha256Hash.equals(user.getPassword()));

    }
}
