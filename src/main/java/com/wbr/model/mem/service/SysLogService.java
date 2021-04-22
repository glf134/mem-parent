package com.wbr.model.mem.service;


import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.vo.SysLog;

/**
 * @author glf
 */
public interface SysLogService {

    /**
     * 新增
     * @param sysLog
     * @return
     */
    Result insert(SysLog sysLog) throws BaseException;
    /**
     * 修改
     * @param sysLog
     * @return
     */
    Result updateByPrimaryKey(SysLog sysLog) throws BaseException;
    /**
     * 删除
     * @param id
     * @return
     */
    Result deleteByPrimaryKey(String id) throws BaseException;
    /**
     * 查询所有
     * @return
     */
    Result selectAll() throws BaseException;
    /**
     * 分页查询所有
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result selectAllByPage(Integer pageNum,Integer pageSize) throws BaseException;

}
