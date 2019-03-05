package com.my.blog.website.service.impl;

import com.my.blog.website.dao.CommentVoMapper;
import com.my.blog.website.model.Vo.CommentVoExample;
import com.my.blog.website.service.ICommentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/*
 * create by shuanglin on 19-1-30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommentServiceImplTest {

    @Resource
    private ICommentService commentService;

    @Resource
    private CommentVoMapper commentVoMapper;

    @Test
    public void deleteByCid() {
        Integer cid = 12;
        commentService.deleteByCid(cid);
        CommentVoExample example = new CommentVoExample();
        CommentVoExample.Criteria criteria = example.createCriteria();
        criteria.andCoidIsNotNull();
        long num = commentVoMapper.countByExample(example);
        Assert.assertEquals(6L, num);
        cid = 15;
        commentService.deleteByCid(15);
        num = commentVoMapper.countByExample(example);
        Assert.assertEquals(4L, num);


    }
}
