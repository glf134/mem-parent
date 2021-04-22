package com.wbr.model.mem.service;


import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.vo.TagPoint;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author pxs
 */
public interface TagPointService {

    /**
     * 参数权重配置新增
     * @param tagPoint
     * @return
     */
    Result insert(TagPoint tagPoint) throws BaseException;
    /**
     * 修改参数权重配置
     * @param tagPoint
     * @return
     */
    Result updateByPrimaryKey(TagPoint tagPoint) throws BaseException;
    /**
     * 删除权重配置
     * @param id
     * @return
     */
    Result deleteByPrimaryKey(String id) throws BaseException;
    /**
     * 获取所有权重配置
     *
     * @return
     */
    Result selectAll()  throws BaseException;
    /**
     * 分页查询所有
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result selectAllByPage(Integer pageNum,Integer pageSize) throws BaseException;
    /**
     * 导出
     * @param response
     */
    Result exportExcel(HttpServletResponse response) throws BaseException;
    /**
     * 导入
     * @param file
     */
    Result deleteExcel(MultipartFile file) throws BaseException;
    /**
     * 根据设备号查询映射表
     * @return
     */
    List<TagPoint> selectByEquipment(String equipmentCode) throws BaseException;
}
