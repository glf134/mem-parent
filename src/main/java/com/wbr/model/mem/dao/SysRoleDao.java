package com.wbr.model.mem.dao;


import com.wbr.model.mem.vo.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * 角色
 *
 * @author glf
 */
@Mapper
public interface SysRoleDao extends RowMapper<SysRole> {
    /**
     * 新增
     * @param record
     * @return
     */
    int insert(SysRole record);

    /**
     * 根据主键修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(SysRole record);

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
    SysRole selectByPrimaryKey(String id);

    /**
     * 根据用户获取角色
     * @param id
     * @return
     */
    List<SysRole> selectRoleByUser(String id);

    /**
     * 查询所有
     * @return
     */
    List<SysRole> selectAll();

}
