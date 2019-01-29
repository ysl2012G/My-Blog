package com.my.blog.website.config.shiro;

import com.my.blog.website.dao.UserVoMapper;
import com.my.blog.website.model.Vo.UserVo;
import org.apache.shiro.authc.credential.PasswordService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/*
 * create by shuanglin on 19-1-28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroConfigTest {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserVoMapper userVoMapper;

    private String passwords;
    //    private String sha256Hash;
    private int uid;

    @Before
    @Transactional
    public void setup() {
        passwords = "12345678";
//        sha256Hash = passwordService.encryptPassword(passwords);
//        String sha256Hash = "$shiro1$SHA-256$10$6iskYnntymLpFP7S8Ian0A==$KcE4QPIusNLCG7Tg+jRN3YuEJuvNTsN379zWO3cdH3I=";
        uid = 1;
//        UserVo user = userVoMapper.selectByPrimaryKey(uid);
//        user.setPassword(sha256Hash);
//        userVoMapper.updateByPrimaryKeySelective(user);
    }

    @Test
    @Transactional
    public void test() {
        UserVo userVo = userVoMapper.selectByPrimaryKey(uid);
        String mysqlHash = userVo.getPassword();
        System.out.println("encyrpted hash is :" + mysqlHash);
        System.out.println("hash length is :" + mysqlHash.length());
        Assert.assertTrue(passwordService.passwordsMatch(passwords, mysqlHash));
    }
}
