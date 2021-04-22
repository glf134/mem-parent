package com.wbr.model.mem.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wbr.model.mem.dao.SysLogDao;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.SysLogService;
import com.wbr.model.mem.utils.UUIDUtils;
import com.wbr.model.mem.vo.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author glf
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogDao sysLogDao;

    @Override
    public Result insert(SysLog sysLog) throws BaseException{
        Result result = new Result();
        sysLog.setId(UUIDUtils.getGUID32());
        sysLog.setCreateTime(new Date());
        int num = sysLogDao.insert(sysLog);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    @Override
    public Result updateByPrimaryKey(SysLog sysLog) throws BaseException{
        Result result = new Result();
        int num = sysLogDao.updateByPrimaryKey(sysLog);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    @Override
    public Result deleteByPrimaryKey(String id) throws BaseException{
        Result result = new Result();
        int num = sysLogDao.deleteByPrimaryKey(id);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    @Override
    public Result selectAll() throws BaseException{
        Result result = new Result();
        List<SysLog> list = sysLogDao.selectAll();
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
        List<SysLog> list = sysLogDao.selectAll();
        if(list != null) {
            PageInfo pageInfo= new PageInfo<SysLog>(list);
            result.setData(pageInfo);
        }
        return result;
    }
}
