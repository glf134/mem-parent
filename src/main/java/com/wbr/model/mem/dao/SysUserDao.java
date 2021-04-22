package com.wbr.model.mem.dao;

import com.wbr.model.mem.vo.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author glf
 */
@Mapper
public interface SysUserDao {
    /**
     * 新增
     * @param record
     * @return
     */
    int insert(SysUser record);

    /**
     * 根据主键修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(SysUser record);

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
    SysUser selectByPrimaryKey(String id);

    /**
     * 根据账号查询
     * @param username
     * @return
     */
    SysUser selectUserByName(String username);

    /**
     * 查询所有
     * @return
     */
    List<SysUser> selectAll();
}
