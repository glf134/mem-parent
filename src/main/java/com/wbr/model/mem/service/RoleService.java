package com.wbr.model.mem.service;

import com.alibaba.fastjson.JSONObject;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.vo.SysRole;

/**
 * @author glf
 */
public interface RoleService {

    /**
     * 新增
     * @param sysRole
     * @return
     */
    Result insert(SysRole sysRole) throws BaseException;

    /**
     * 修改
     * @param sysRole
     * @return
     */
    Result updateByPrimaryKey(SysRole sysRole) throws BaseException;

    /**
     * 删除
     * @param id
     * @return
     */
    Result deleteByPrimaryKey(String id) throws BaseException;

    /**
     * 查询
     * @param id
     * @return
     */
    Result selectByPrimaryKey(String id) throws BaseException;

    /**
     * 查询所有
     * @return
     */
    Result selectAll();

    /**
     * 分页查询所有
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result selectAllByPage(Integer pageNum,Integer pageSize) throws BaseException;

    /**
     * 新增权限
     * @param jsonObject
     * @return
     */
    Result savePermissionByRole(JSONObject jsonObject) throws BaseException;

    /**
     *根据角色获取权限列表
     * @param roleId
     * @return
     */
    Result selectPermissionByRole(String roleId) throws BaseException;

    /**
     *根据用户获取角色
     * @param userId
     * @return
     */
    Result selectRoleByUser(String userId) throws BaseException;

    /**
     * 获取权限树
     * @return
     */
    Result selectPermissionTree() throws BaseException;

    /**
     * 获取一级菜单
     * @return
     */
    Result selectOneMenu() throws BaseException;

    /**
     * 获取子级菜单
     * @return
     */
    Result selectMenuByStruId(String struid) throws BaseException;

}
