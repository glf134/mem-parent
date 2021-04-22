package com.wbr.model.mem.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wbr.model.mem.dao.TagPointDao;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.TagPointService;
import com.wbr.model.mem.utils.*;
import com.wbr.model.mem.vo.TagPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author pxs
 * equipment_code'设备编码'+tag_name'参数名' 唯一索引
 */
@Service
public class TagPointServiceImpl implements TagPointService {
    /**
     * 缓存key
     */
    public static String tagPointAllData="tagPointAllData";

    @Autowired
    private TagPointDao tagPointDao;

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 参数权重配置新增
     * @param tagPoint
     * @return
     */
    @Override
    public Result insert(TagPoint tagPoint) throws BaseException {
        Result result = new Result();
        tagPoint.setId(UUIDUtils.getGUID32());
        tagPoint.setCreateTime(new Date());
        redisUtil.del(tagPointAllData);
        int num = tagPointDao.insert(tagPoint);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    /**
     * 修改参数权重配置
     *
     * @param tagPoint
     * @return
     */
    @Override
    public Result updateByPrimaryKey(TagPoint tagPoint) throws BaseException{
        Result result = new Result();
        redisUtil.del(tagPointAllData);
        int num = tagPointDao.updateByPrimaryKey(tagPoint);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    /**
     * 删除权重配置
     *
     * @param id
     * @return
     */
    @Override
    public Result deleteByPrimaryKey(String id) throws BaseException{
        Result result = new Result();
        redisUtil.del(tagPointAllData);
        int num = tagPointDao.deleteByPrimaryKey(id);
        if(num > 0) {
            result.setCode("200");
        }
        return result;
    }

    /**
     * 获取所有权重配置
     * @return
     */
    @Override
    public Result selectAll() throws BaseException{
        Result result = new Result();
        //从缓存中获取
        List<TagPoint> list= (List<TagPoint>) redisUtil.get(tagPointAllData);
        if(list == null){
            list = tagPointDao.selectAll();
            redisUtil.set(tagPointAllData,list);
        }
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
        List<TagPoint> list = tagPointDao.selectAll();
        if(list != null) {
            PageInfo pageInfo= new PageInfo<TagPoint>(list);
            result.setData(pageInfo);
        }
        return result;
    }
    /**
     * 导出excel
     * @param response
     * @return
     */
    @Override
    public Result exportExcel(HttpServletResponse response) throws BaseException{
        String[] titleName=new String[]{"equipmentNumber","equipmentCode","tagName","tagComment"
                ,"orgTagName","tagUnit","scaleFactor","minValue","maxValue","intercept","createTime","creator"};
        List<TagPoint> list = tagPointDao.selectAll();
        ExportExcel.exportExcel("参数映射表","参数映射表",titleName,titleName,list,"yyyy-MM-dd HH:mm:ss",response);
        Result result = new Result();
        result.setCode("200");
        result.setMessage("导出成功");
        return result;
    }
    /**
     * 导入excel
     * @return
     */
    @Override
    public Result deleteExcel(MultipartFile file) throws BaseException{
        Result result = new Result();
        try {
            redisUtil.del(tagPointAllData);
            tagPointDao.deleteAll(); //清空数据表
            List<TagPoint> list= (List<TagPoint>) ImportExcel.importExcel(file,TagPoint.class);
            if(list != null && list.size()>0){
                list.forEach(tagPoint ->{
                        if(!StringUtils.isBlank(tagPoint.getEquipmentCode())){
                            tagPoint.setId(UUIDUtils.getGUID32());
                            tagPointDao.insert(tagPoint);
                        }
                });
            }
            result.setCode("200");
            result.setMessage("导入成功");
        } catch (IOException e) {
            e.printStackTrace();
            result.setCode("500");
            result.setMessage("导入失败");
        }
        return result;
    }

    @Override
    public List<TagPoint> selectByEquipment(String equipmentCode) throws BaseException{
        List<TagPoint> list = tagPointDao.selectByEquipment(equipmentCode);
        return list;
    }
}
