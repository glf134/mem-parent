package com.wbr.model.mem.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wbr.model.mem.constant.SystemCon;
import com.wbr.model.mem.dao.SysMenuPermissionDao;
import com.wbr.model.mem.dao.SysRoleUserDao;
import com.wbr.model.mem.dao.SysUserDao;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.exception.ExceptionErrorCode;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.UserService;
import com.wbr.model.mem.utils.*;
import com.wbr.model.mem.vo.SysMenuPermission;
import com.wbr.model.mem.vo.SysRole;
import com.wbr.model.mem.vo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author glf
 */
@Service
public class UserServiceImpl implements UserService {
 
    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysRoleUserDao sysRoleUserDao;

    @Autowired
    private SysMenuPermissionDao sysMenuPermissionDao;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result insert(SysUser sysUser) throws BaseException {
        Result result = new Result();
        sysUser.setId(UUIDUtils.getGUID32());
        sysUser.setCreateTime(new Date());
        try {
            sysUser.setPassword(MD5Util.encrypt(sysUser.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int num = sysUserDao.insert(sysUser);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    @Override
    public Result updateByPrimaryKey(SysUser sysUser) throws BaseException {
        Result result = new Result();
        int num = sysUserDao.updateByPrimaryKey(sysUser);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    @Override
    public Result deleteByPrimaryKey(String id) throws BaseException {
        Result result = new Result();
        //删除用户角色关联
        sysRoleUserDao.deleteRoleUserByUser(id);
        //删除用户
        int num = sysUserDao.deleteByPrimaryKey(id);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    @Override
    public Result selectByPrimaryKey(String id) throws BaseException {
        Result result = new Result();
        SysUser sysUser = sysUserDao.selectByPrimaryKey(id);
        if(sysUser != null) {
            result.setData(sysUser);
        }
        return result;
    }

    @Override
    public Result selectAll() {
        Result result = new Result();
        List<SysUser> list = sysUserDao.selectAll();
        if(list != null) {
            result.setData(list);
        }
        return result;
    }

    @Override
    public Result selectAllByPage(Integer pageNum, Integer pageSize) throws BaseException {
        Result result = new Result();
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserDao.selectAll();
        if(list != null) {
            PageInfo pageInfo= new PageInfo<SysUser>(list);
            result.setData(pageInfo);
        }
        return result;
    }

    @Override
    public void login(Map<String, Object> map, Result result, HttpServletRequest request) throws BaseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String username = (String)map.get("username");
        String password = (String)map.get("password");
        if(!StringUtils.isBlank(username) && !StringUtils.isBlank(password)){
            //根据账号查询用户
            SysUser sysUser = sysUserDao.selectUserByName(username);
            if(sysUser != null && MD5Util.validPassword(password,sysUser.getPassword())){
                List<SysRole> roles=sysUser.getSysRoles();
                List role=new ArrayList();  //角色
                Set perms=new HashSet(); //权限
                if(!CollectionUtils.isEmpty(roles)){
                    roles.forEach(sysRole -> {
                        Map rol=new HashMap();
                        rol.put("id",sysRole.getId());
                        rol.put("name",sysRole.getName());
                        rol.put("code",sysRole.getCode());
                        role.add(rol);
                        List<SysMenuPermission> sysPermission = sysRole.getSysMenuPermissions();
                        if(!CollectionUtils.isEmpty(sysPermission)){
                            sysPermission.forEach(sysMenuPermission -> {
                                perms.add(sysMenuPermission.getPath());
                            });
                        }
                    });
                }
                Map<String,Object> claims=new HashMap();
                claims.put("username",username);
                claims.put("roles",role);
                claims.put("permission",perms);
                Map res=new HashMap();
                res.put("token",JwtTokenUtils.generateToken(claims));
                result.setData(res);
                redisUtil.set(SystemCon.USERTOKENKEY+username,res.get("token").toString(),JwtTokenUtils.EXPIRE_TIME/1000);
                setOnLine(request,username);
                result.setMessage("登录成功");
                result.setCode("200");
                return;
            }
        }
        result.setMessage(ExceptionErrorCode.ERROR_MES_00017);
    }

    @Override
    public void logout(Map<String, Object> map, Result result, HttpServletRequest request) throws BaseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String username= (String) map.get("username");
        if(!StringUtils.isBlank(username)){
            HttpSession session = request.getSession(false);//防止创建Session
            if(session != null){
                session.invalidate();
            }
            //删除token
            if(redisUtil.hasKey(SystemCon.USERTOKENKEY+username)){
                redisUtil.del(SystemCon.USERTOKENKEY+username);
            }
            result.setCode("200");
        }
    }

    /**
     * 设置在线信息
     * @param request
     * @param username
     */
    public void setOnLine(HttpServletRequest request,String username){
        SimpleDateFormat min=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        request.getSession().setAttribute(SystemCon.SESSIONUSERDATA,username);
        JSONObject jsonObject = null;
        synchronized (UserServiceImpl.class){
            if(redisUtil.get(SystemCon.SESSIONUSERDATA) != null){
                jsonObject = (JSONObject) redisUtil.get(SystemCon.SESSIONUSERDATA);
                JSONArray jsonArray= (JSONArray) jsonObject.get("onLines");
                if(jsonArray != null && !jsonArray.contains(username)){
                    int redisCount= (int) jsonObject.get("count");
                    jsonObject.put("count", ++redisCount);
                    jsonArray.add(username);//用户名
                }
            }else{
                jsonObject= new JSONObject();
                jsonObject.put("count",1);
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(username);//用户名
                jsonObject.put("onLines",jsonArray);
            }
            redisUtil.set(SystemCon.SESSIONUSERDATA,jsonObject);
        }
    }
}
