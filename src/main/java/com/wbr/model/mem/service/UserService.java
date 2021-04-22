package com.wbr.model.mem.service;

import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.vo.SysUser;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author glf
 */
public interface UserService {

    /**
     * 新增
     * @param sysUser
     * @return
     */
    Result insert(SysUser sysUser) throws BaseException;

    /**
     * 修改
     * @param sysUser
     * @return
     */
    Result updateByPrimaryKey(SysUser sysUser) throws BaseException;

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

    void login(Map<String,Object> map, Result result, HttpServletRequest request) throws BaseException, UnsupportedEncodingException, NoSuchAlgorithmException;

    void logout(Map<String,Object> map, Result result, HttpServletRequest request) throws BaseException, UnsupportedEncodingException, NoSuchAlgorithmException;

}
