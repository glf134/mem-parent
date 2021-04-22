package com.wbr.model.mem.controller;

import com.wbr.model.mem.annotation.LogAnnotation;
import com.wbr.model.mem.constant.DemoCon;
import com.wbr.model.mem.dao.SysLogDao;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.SysLogService;
import com.wbr.model.mem.utils.CollectionUtils;
import com.wbr.model.mem.vo.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author glf
 */
@Slf4j
@RestController
@Api(tags = "SysLog API")
@RequestMapping("/sysLog")
public class SysLogController {
    @Autowired
    private SysLogService sysLogService;

    @PostMapping("/insert")
    @ApiOperation(value = "新增日志")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result insert(@RequestBody SysLog sysLog) throws BaseException{
        return sysLogService.insert(sysLog);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改日志")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result update(@RequestBody SysLog sysLog)  throws BaseException{
        return sysLogService.updateByPrimaryKey(sysLog);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除日志")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result delete(@PathVariable(value = "id")String id)  throws BaseException{
        return sysLogService.deleteByPrimaryKey(id);
    }

    @GetMapping("/selectAll")
    @ApiOperation(value = "获取所有日志")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectAll() throws BaseException {
        return sysLogService.selectAll();
    }

    @GetMapping("/selectPage")
    @ApiOperation(value = "分页获取映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectPage(@RequestParam(value = "pageNum") int pageNum,@RequestParam(value = "pageSize") int pageSize)  throws BaseException{
        return sysLogService.selectAllByPage(pageNum,pageSize);
    }
    @Autowired
    private SysLogDao sysLogDao;
    @GetMapping("/dd")
    @ApiOperation(value = "dd")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public void dd()  throws BaseException{
        Map dataTypes = new HashMap();
        dataTypes.put("LEI全球法人机构识别编码","Varchar(255)");
        dataTypes.put("全局唯一标识符","Varchar(32)");
        dataTypes.put("十进制字符串","int(16)");
        dataTypes.put("十进制字符串类型","Varchar(255)");
        dataTypes.put("十进制浮点字符串","double(16,2)");
        dataTypes.put("多值数据类型","Varchar(255)");
        dataTypes.put("布尔型","Varchar(16)");
        dataTypes.put("日期字符串","datetime");
        dataTypes.put("时间字符串","datetime");
        dataTypes.put("枚举类型","Varchar(10)");
        dataTypes.put("设施分类编码","Varchar(255)");
        dataTypes.put("设施标识符","Varchar(255)");
        dataTypes.put("软件分类编码","Varchar(255)");
        dataTypes.put("通用字符串","Varchar(255)");
        dataTypes.put("金融机构编码","Varchar(255)");
        String d = "select data_type from data_template_detail_list  group by data_type ";
        List<Map> dtdl = sysLogDao.selectBySqlMap(d);
        for(int i=0;i<dtdl.size();i++){
            Map map = dtdl.get(i);
            String data_type = (String) map.get("data_type");
            System.out.println("DROP TABLE IF EXISTS "+DemoCon.tableName(data_type)+";");
        }
        System.out.println("=================生成sql===================");
        String dataSql = " select * from data_template_detail_list   ";
        List<Map> datas = sysLogDao.selectBySqlMap(dataSql);
        for(int k=0;k<dtdl.size();k++){
            Map map = dtdl.get(k);
            String dataType = (String) map.get("data_type");
            List<Map> nList = CollectionUtils.fetch(datas,"'"+dataType+"'.equals(data_type)");
            //去除重复列
            nList = nList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(
                    Comparator.comparing(map1 -> map1.get("identification").toString()))), ArrayList::new));
            String table = "";
            for (int i = 0; i < dataType.length(); i++) {
                if (Character.isUpperCase(dataType.charAt(i))) {
                    table += "_" + Character.toLowerCase(dataType.charAt(i));
                }else{
                    table += dataType.charAt(i);
                }
            }

            System.out.println("DROP TABLE IF EXISTS "+ DemoCon.tableName(dataType)+";");
            String sql="CREATE TABLE "+DemoCon.tableName(dataType)+" ( id BIGINT UNSIGNED AUTO_INCREMENT,"+
                    "  branch_id varchar(255)  DEFAULT '' COMMENT '上报批次号'," +
                    "  sync_status char(1)  DEFAULT '0' COMMENT '同步状态',";
            if(nList!=null){
                String interface_name = "";
                for(int j=0;j<nList.size();j++){
                    Map nMap = (Map) nList.get(j);
                    String identification = (String) nMap.get("identification");
                    String metadata_type =   nMap.get("metadata_type").toString().trim();
                    String metadata = (String) nMap.get("metadata");
                    interface_name = (String) nMap.get("interface_name");
                    String tableColumn = "";
                    for (int i = 0; i < identification.length(); i++) {
                        if (Character.isUpperCase(identification.charAt(i))) {
                            tableColumn += "_" + Character.toLowerCase(identification.charAt(i));
                        }else{
                            tableColumn += identification.charAt(i);
                        }
                    }
                    sql +=  tableColumn + " "+dataTypes.get(metadata_type)+" DEFAULT NULL COMMENT '" + metadata + "',";
                }
//              sql +="  status char(1)  DEFAULT '0' COMMENT '状态（0正常 1停用）'," +
                sql +=  "  del_flag char(1)  DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'," +
                        "  sync_date datetime  DEFAULT NULL COMMENT '同步时间'," +
                        "  create_by varchar(64)  DEFAULT '' COMMENT '创建者'," +
                        "  create_time datetime DEFAULT NULL COMMENT '创建时间'," +
                        "  update_by varchar(64)  DEFAULT '' COMMENT '更新者'," +
                        "  update_time datetime DEFAULT NULL COMMENT '更新时间',";
                sql += "PRIMARY KEY ( id )) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '" + interface_name +"';";
                System.out.println(sql);
            }
        }
    }

}
