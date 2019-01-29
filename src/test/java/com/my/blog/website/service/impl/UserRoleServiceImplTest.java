package com.my.blog.website.service.impl;

import com.my.blog.website.model.Vo.UserRoleVoKey;
import com.my.blog.website.service.IUserRoleService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/*
 * create by shuanglin on 19-1-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRoleServiceImplTest {
    @Resource
    private IUserRoleService userRoleService;

    @Before
    public void setUp() throws Exception {
        int rid = 2;
        for (int uid = 2; uid <= 10; uid++) {

            UserRoleVoKey userRoleVoKey = new UserRoleVoKey();
            userRoleVoKey.setUid(uid);
            userRoleVoKey.setRid(rid);
            userRoleService.insertUserRoleVo(userRoleVoKey);

        }
    }

    @Test
    public void deleteByID() {
        userRoleService.deleteByID(null, 1);
        Assert.assertEquals(Long.valueOf(10L), userRoleService.countByID(null, null));

    }

    @Test
    public void countByID() {
        long num_user = userRoleService.countByID(null, 2);
        long num_admin = userRoleService.countByID(null, 1);
        Assert.assertEquals(10L, num_user);
        Assert.assertEquals(1L, num_admin);
    }

    @Test
    public void insertUserRoleVo() {
        Assert.assertEquals(Long.valueOf(11), userRoleService.countByID(null, null));
    }

    @Test
    public void getUserRoleById() {
        List<UserRoleVoKey> result = userRoleService.getUserRoleById(null, 2);
        result.sort(Comparator.comparingInt(UserRoleVoKey::getUid));
        for (int i = 1; i <= 10; i++) {
            Assert.assertEquals(Integer.valueOf(i), result.get(i - 1).getUid());
        }
        List<UserRoleVoKey> result_admin = userRoleService.getUserRoleById(null, 1);
        Assert.assertEquals(1, result_admin.size());
        Assert.assertEquals(Integer.valueOf(1), result.get(0).getUid());
    }
}
