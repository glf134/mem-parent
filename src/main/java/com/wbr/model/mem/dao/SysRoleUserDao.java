package com.wbr.model.mem.dao;

import com.wbr.model.mem.vo.SysRoleUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author glf
 */
@Mapper
public interface SysRoleUserDao extends RowMapper<SysRoleUser>{
    /**
     * 新增
     * @param record
     * @return
     */
    int insert(SysRoleUser record);
    /**
     * 根据角色删除用户
     * @param roleId
     * @return
     */
    @Delete("delete from sys_role_user where role_id = #{roleId}")
    int deleteRoleUserByRole(String roleId);

    /**
     * 根据用户标识删除用户
     * @param userId
     * @return
     */
    @Delete("delete from sys_role_user where user_id = #{userId}")
    int deleteRoleUserByUser(String userId);

    /**
     * 根据主键删除权限
     * @param id
     * @return
     */
    @Delete("delete from sys_role_user where id = #{id}")
    int deleteByPrimaryKey(String id);

}
