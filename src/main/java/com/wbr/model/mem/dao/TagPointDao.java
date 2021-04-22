package com.wbr.model.mem.dao;


import com.wbr.model.mem.vo.TagPoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * 参数权重配置 数据访问接口
 *
 * @author pxs
 */
@Mapper
public interface TagPointDao extends RowMapper<TagPoint> {

    /**
     * 新增配置
     * @param tagPoint
     * @return
     */
    int insert(TagPoint tagPoint);

    /**
     * 删除参数配置
     * @param id
     * @return
     */
    @Delete("delete from tag_point where id = #{id}")
    int deleteByPrimaryKey(String id);

    /**
     * 删除所有参数配置
     * @return
     */
    @Delete("delete from tag_point ")
    int deleteAll();

    /**
     * 获取所有配置
     * @return
     */
    List<TagPoint> selectAll();

    /**
     * 修改参数配置
     * @param tagPoint
     * @return
     */
    int updateByPrimaryKey(TagPoint tagPoint);

    /**
     * 根据设备号查询映射表
     * @return
     */
    List<TagPoint> selectByEquipment(String equipmentCode);
}
