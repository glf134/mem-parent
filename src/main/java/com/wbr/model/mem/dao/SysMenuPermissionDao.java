package com.wbr.model.mem.dao;


import com.wbr.model.mem.vo.SysMenuPermission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * 菜单
 *
 * @author glf
 */
@Mapper
public interface SysMenuPermissionDao extends RowMapper<SysMenuPermission> {
    /**
     * 新增
     * @param record
     * @return
     */
    int insert(SysMenuPermission record);

    /**
     * 根据主键修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(SysMenuPermission record);

    /**
     * 根据主键删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    SysMenuPermission selectByPrimaryKey(String id);

    /**
     * 查询所有
     * @return
     */
    List<SysMenuPermission> selectAll();

    /**
     * 根据角色查询权限
     * @param roleId
     * @return
     */
    List<SysMenuPermission> selectPermissionByRole(String roleId);

    /**
     * 根据用户查询权限
     * @param userId
     * @return
     */
    List<SysMenuPermission> selectPermissionByUser(String userId);

    /**
     * 获取一级菜单
     * @return
     */
    List<SysMenuPermission> selectOneMenu() ;

    /**
     * 获取子级菜单
     * @return
     */
    List<SysMenuPermission> selectMenuByStruId(String struid);
}
