package com.my.blog.website.config.shiro;

import com.my.blog.website.dao.UserVoMapper;
import com.my.blog.website.model.Vo.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/*
 * create by shuanglin on 19-1-28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroConfigTest {
    public static final Logger logger = LoggerFactory.getLogger(ShiroConfigTest.class);
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
        passwords = "shuanglin";
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

    @Test(expected = UnavailableSecurityManagerException.class)
    @Transactional
    public void testSecurityUtils() {
//        SecurityManager manager = SecurityUtils.getSecurityManager();

//        Assert.assertTrue(manager instanceof DefaultWebSecurityManager);
        Subject subject = SecurityUtils.getSubject();
//        System.out.println(manager.getClass());
    }

    @Test
    public void testBase64() {
        String passwd = "My-Blog-by-shuanglin201903";
        String encoder = Base64.encodeToString(passwd.getBytes());
        logger.debug("current encoder is {} with size {}.", encoder, encoder.length());
        logger.debug("decoder is {}", Base64.decodeToString(encoder));
        String currentEncoder = "4AvVhmFLUs0KTA3Kprsdag==";
        logger.debug("previous encoder is {} with size {}.", currentEncoder, currentEncoder.length());
        logger.debug("decoder is {}", Base64.decodeToString(currentEncoder));


        String key = UUID.randomUUID().toString().substring(0, 24);
        logger.debug("current key is {}", key);
        byte[] encoderBytes = Base64.encode(key.getBytes());
        logger.debug("encoder is {} with length {}", new String(encoderBytes), new String(encoderBytes).length());
        logger.debug("re-decoder is{}", Base64.decodeToString(encoderBytes));


    }
}
