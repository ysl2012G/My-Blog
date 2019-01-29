package com.my.blog.website.dao;

import com.my.blog.website.model.Vo.LogVo;
import com.my.blog.website.model.Vo.LogVoExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogVoMapper {
    long countByExample(LogVoExample example);

    int deleteByExample(LogVoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(LogVo record);

    int insertSelective(LogVo record);

    List<LogVo> selectByExample(LogVoExample example);

    LogVo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") LogVo record, @Param("example") LogVoExample example);

    int updateByExample(@Param("record") LogVo record, @Param("example") LogVoExample example);

    int updateByPrimaryKeySelective(LogVo record);

    int updateByPrimaryKey(LogVo record);
}
