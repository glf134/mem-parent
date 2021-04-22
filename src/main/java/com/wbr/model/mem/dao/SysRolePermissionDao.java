package com.wbr.model.mem.dao;

import com.wbr.model.mem.vo.SysRolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author glf
 */
@Mapper
public interface SysRolePermissionDao extends RowMapper<SysRolePermission>{
    /**
     * 新增
     * @param record
     * @return
     */
    int insert(SysRolePermission record);
    /**
     * 根据角色删除权限
     * @param roleId
     * @return
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    int deletePermissionByRole(String roleId);

    /**
     * 根据权限标识删除权限
     * @param menuPermissionId
     * @return
     */
    @Delete("delete from sys_role_permission where menu_permission_id = #{menuPermissionId}")
    int deletePermissionByMenu(String menuPermissionId);

    /**
     * 根据主键删除权限
     * @param id
     * @return
     */
    @Delete("delete from sys_role_permission where id = #{id}")
    int deleteByPrimaryKey(String id);

}
