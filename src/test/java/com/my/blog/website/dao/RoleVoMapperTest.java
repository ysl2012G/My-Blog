package com.my.blog.website.dao;

import com.my.blog.website.model.Vo.RoleVo;
import com.my.blog.website.model.Vo.RoleVoExample;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * create by shuanglin on 19-1-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RoleVoMapperTest {
    @Autowired
    private RoleVoMapper roleVoMapper;

    @Before
    public void setup() {
        RoleVo roleVo = new RoleVo();
        roleVo.setRolename("test");
        roleVoMapper.insertSelective(roleVo);
    }

    @Test
    public void countByExampleTest() {
        int rid = 2;
        RoleVoExample roleVoExample = new RoleVoExample();
        RoleVoExample.Criteria criteria = roleVoExample.createCriteria();
        criteria.andRidGreaterThan(rid);
        long result = roleVoMapper.countByExample(roleVoExample);
        Assert.assertEquals(1L, result);

    }

    @Test
    public void selectByExampleTest() {
//        String roleName = "user";
        RoleVoExample roleVoExample = new RoleVoExample();
        RoleVoExample.Criteria criteria = roleVoExample.createCriteria();
        criteria.andRolenameIsNotNull();
        List<RoleVo> result = roleVoMapper.selectByExample(roleVoExample);
        Assert.assertEquals(3L, result.size());
//        System.out.println(result.get(2).getRid());
    }

    @Test
    public void deleteByExampleTest() {
        RoleVoExample example = new RoleVoExample();
        RoleVoExample.Criteria criteria = example.createCriteria();
        criteria.andRidGreaterThan(2);
        roleVoMapper.deleteByExample(example);
        example.clear();
        criteria = example.createCriteria();
        criteria.andRolenameIsNotNull();
        Assert.assertEquals(2L, roleVoMapper.countByExample(example));
    }

    @Test(expected = Exception.class)
    public void testUniqueKey() {
        RoleVo roleVo = new RoleVo();
        roleVo.setRolename("test");
        roleVoMapper.insertSelective(roleVo);
    }
}
