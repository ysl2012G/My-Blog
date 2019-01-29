package com.my.blog.website.dao;

import com.my.blog.website.model.Vo.RoleVo;
import com.my.blog.website.model.Vo.RoleVoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleVoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    long countByExample(RoleVoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int deleteByExample(RoleVoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer rid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int insert(RoleVo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int insertSelective(RoleVo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    List<RoleVo> selectByExample(RoleVoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    RoleVo selectByPrimaryKey(Integer rid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") RoleVo record, @Param("example") RoleVoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") RoleVo record, @Param("example") RoleVoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RoleVo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_roles
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RoleVo record);
}
