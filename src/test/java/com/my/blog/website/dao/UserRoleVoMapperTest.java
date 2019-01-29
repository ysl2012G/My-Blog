package com.my.blog.website.dao;

import com.my.blog.website.model.Vo.UserRoleVoExample;
import com.my.blog.website.model.Vo.UserRoleVoKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/*
 * create by shuanglin on 19-1-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRoleVoMapperTest {

    @Autowired
    private UserRoleVoMapper userRoleVoMapper;

    @Before
    public void setup() throws Exception {
        UserRoleVoKey userRole = new UserRoleVoKey();
        userRole.setRid(2);
        userRole.setUid(2);
        userRoleVoMapper.insert(userRole);
    }

    @Test(expected = Exception.class)
    public void testInsertSelective() throws Exception {
        UserRoleVoKey userRoleVoKey = new UserRoleVoKey();
        userRoleVoKey.setRid(2);
        userRoleVoMapper.insertSelective(userRoleVoKey);
    }

    @Test
    public void countbyExampleTest() {
        UserRoleVoExample example = new UserRoleVoExample();
        UserRoleVoExample.Criteria criteria = example.createCriteria();
        criteria.andRidIsNotNull().andUidIsNotNull();
        Assert.assertEquals(3L, userRoleVoMapper.countByExample(example));
    }

    @Test
    public void selectByExampleTest() {
        UserRoleVoExample example = new UserRoleVoExample();
        UserRoleVoExample.Criteria criteria = example.createCriteria();
        criteria.andRidEqualTo(2);
        List<UserRoleVoKey> result = userRoleVoMapper.selectByExample(example);
        Assert.assertEquals(2, result.size());
        result.sort(Comparator.comparingInt(UserRoleVoKey::getUid));
        Assert.assertEquals(Integer.valueOf(1), result.get(0).getUid());
        Assert.assertEquals(Integer.valueOf(2), result.get(1).getUid());
    }


}
