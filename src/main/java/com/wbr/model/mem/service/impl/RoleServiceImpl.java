package com.wbr.model.mem.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wbr.model.mem.dao.SysMenuPermissionDao;
import com.wbr.model.mem.dao.SysRoleDao;
import com.wbr.model.mem.dao.SysRolePermissionDao;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.RoleService;
import com.wbr.model.mem.utils.CollectionUtils;
import com.wbr.model.mem.utils.StringUtils;
import com.wbr.model.mem.utils.UUIDUtils;
import com.wbr.model.mem.vo.SysMenuPermission;
import com.wbr.model.mem.vo.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author glf
 */
@Service
public class RoleServiceImpl implements RoleService {
 
    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysMenuPermissionDao sysMenuPermissionDao;

    @Autowired
    private SysRolePermissionDao sysRolePermissionDao;

    /**
     * 新增
     * @param sysRole
     * @return
     */
    @Override
    public Result insert(SysRole sysRole) throws BaseException{
        Result result = new Result();
        sysRole.setId(UUIDUtils.getGUID32());
        sysRole.setCreateTime(new Date());
        sysRole.setUpdateTime(new Date());
        int num = sysRoleDao.insert(sysRole);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    /**
     * 修改
     * @param sysRole
     * @return
     */
    @Override
    public Result updateByPrimaryKey(SysRole sysRole) throws BaseException{
        Result result = new Result();
        int num = sysRoleDao.updateByPrimaryKey(sysRole);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public Result deleteByPrimaryKey(String id) throws BaseException{
        Result result = new Result();
        //删除角色对应的权限
        sysRolePermissionDao.deletePermissionByRole(id);
        //删除角色
        int num = sysRoleDao.deleteByPrimaryKey(id);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    /**
     * 查询
     * @return
     */
    @Override
    public Result selectByPrimaryKey(String id) throws BaseException{
        Result result = new Result();
        SysRole sysRole = sysRoleDao.selectByPrimaryKey(id);
        if(sysRole != null) {
            result.setData(sysRole);
        }
        result.setCode("200");
        return result;
    }

    /**
     * 查询所有
     * @return
     */
    @Override
    public Result selectAll() throws BaseException{
        Result result = new Result();
        List<SysRole> list = sysRoleDao.selectAll();
        if(list != null) {
            result.setData(list);
        }
        return result;
    }

    /**
     * 分页查询数据
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Result selectAllByPage(Integer pageNum, Integer pageSize) throws BaseException{
        Result result = new Result();
        PageHelper.startPage(pageNum, pageSize);
        List<SysRole> list = sysRoleDao.selectAll();
        if(list != null) {
            PageInfo pageInfo= new PageInfo<SysRole>(list);
            result.setData(pageInfo);
        }
        return result;
    }
    /**
     * 根据角色设置权限
     * @param jsonObject
     * @return
     */
    @Override
    public Result savePermissionByRole(JSONObject jsonObject) throws BaseException{

        return null;
    }
    /**
     * 根据角色查询权限
     * @param roleId
     * @return
     */
    @Override
    public Result selectPermissionByRole(String roleId) throws BaseException{
        Result result=new Result();
        //根据角色获取权限
        List<SysMenuPermission> sysMenuPermissions=sysMenuPermissionDao.selectPermissionByRole(roleId);
        result.setData(sysMenuPermissions);
        return result;
    }
    /**
     * 根据用户获取角色
     * @param userId
     * @return
     */
    @Override
    public Result selectRoleByUser(String userId) throws BaseException {
        Result result=new Result();
        //根据用户获取角色
        List<SysRole> sysRoles=sysRoleDao.selectRoleByUser(userId);
        result.setData(sysRoles);
        return result;
    }

    /**
     * 获取权限树形集合
     * @return
     */
    @Override
    public Result selectPermissionTree() throws BaseException{
        Result result=new Result();
        result.setData(getPermissionTree());
        result.setCode("200");
        return result;
    }
    /**
     * 获取一级菜单
     * @return
     */
    @Override
    public Result selectOneMenu() throws BaseException {
        Result result=new Result();
        List<SysMenuPermission> oneMenus=sysMenuPermissionDao.selectOneMenu();
        result.setData(oneMenus);
        return result;
    }

    @Override
    public Result selectMenuByStruId(String struid) throws BaseException {
        Result result=new Result();
        List<SysMenuPermission> oneMenus=sysMenuPermissionDao.selectMenuByStruId(struid);
        result.setData(oneMenus);
        return result;
    }

    /**
     * 获取权限树形集合
     * @return
     */
    public List getPermissionTree(){
        //获取所有权限
        List<SysMenuPermission> list=sysMenuPermissionDao.selectAll();
        if(list != null){
            //获取一级节点
            List<SysMenuPermission> ones= list.stream().filter(sysMenuPermission -> sysMenuPermission.getStruId().length()==4).collect(Collectors.toList());
            List rsJons=new ArrayList();
            ones.forEach(sysMenuPermission -> {
                String parnetStruId=sysMenuPermission.getStruId();
                Map map=new HashMap<>();
                map.put("name",sysMenuPermission.getName());
                map.put("code",sysMenuPermission.getCode());
                map.put("id",sysMenuPermission.getId());
                map.put("leaf",sysMenuPermission.getLeaf());
                map.put("struId",sysMenuPermission.getStruId());
                map.put("parentId",sysMenuPermission.getParentId());
                map.put("permissionType",sysMenuPermission.getPermissionType());
                structurePermissionTree(map,parnetStruId,list,new ArrayList());
                rsJons.add(map);
            });
            return rsJons;
        }
        return null;
    }
    /**
     * 递归树形
     * @param map
     * @param struId
     * @param list
     * @param jumps
     */
    public void structurePermissionTree(Map map,String struId, List<SysMenuPermission> list,List jumps){
        //跳出机制，防止递归死循环
        if(jumps.contains(struId)){
            return;
        }
        if(!StringUtils.isBlank(struId) && CollectionUtils.isNotEmpty(list)){
            jumps.add(struId);
            List<SysMenuPermission> leafs=list.stream().filter(sysMenuPermission -> {
                return sysMenuPermission.getStruId().startsWith(struId)
                       && !struId.equals(sysMenuPermission.getStruId())
                       && sysMenuPermission.getStruId().length()==struId.length()+4;
            }).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(leafs)){
                List childrens=new ArrayList();
                leafs.forEach(sysMenuPermission -> {
                    String parnetStruId=sysMenuPermission.getStruId();
                    Map leafMap=new HashMap<>();
                    leafMap.put("name",sysMenuPermission.getName());
                    leafMap.put("code",sysMenuPermission.getCode());
                    leafMap.put("id",sysMenuPermission.getId());
                    leafMap.put("leaf",sysMenuPermission.getLeaf());
                    leafMap.put("struId",sysMenuPermission.getStruId());
                    leafMap.put("parentId",sysMenuPermission.getParentId());
                    leafMap.put("permissionType",sysMenuPermission.getPermissionType());
                    if(sysMenuPermission.getLeaf() == 0){//父级节点标识
                        structurePermissionTree(leafMap,sysMenuPermission.getStruId(),list,jumps);
                    }
                    childrens.add(leafMap);
                });
                map.put("children",childrens);
            }
        }
    }
}
