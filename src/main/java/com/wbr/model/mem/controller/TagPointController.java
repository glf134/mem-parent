package com.wbr.model.mem.controller;

import com.wbr.model.mem.annotation.LogAnnotation;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.TagPointService;
import com.wbr.model.mem.vo.TagPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 参数权重配置控制器
 * @author glf
 */
@Slf4j
@RestController
@Api(tags = "TagPoint API")
@RequestMapping("/tagPoint")
public class TagPointController {

    @Autowired
    private TagPointService tagPointService;

    @PostMapping("/insert")
    @ApiOperation(value = "新增测点映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result insert(@RequestBody TagPoint tagPoint)  throws BaseException {
        return tagPointService.insert(tagPoint);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改测点映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result update(@RequestBody TagPoint tagPoint)  throws BaseException{
        return tagPointService.updateByPrimaryKey(tagPoint);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除测点映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result delete(@PathVariable(value = "id")String id) throws BaseException{
        return tagPointService.deleteByPrimaryKey(id);
    }

    @GetMapping("/selectAll")
    @ApiOperation(value = "获取所有映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectAll() throws BaseException {
        return tagPointService.selectAll();
    }

    @GetMapping("/selectPage")
    @ApiOperation(value = "分页获取映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectPage(@RequestParam(value = "pageNum") int pageNum,@RequestParam(value = "pageSize") int pageSize)  throws BaseException{
        return tagPointService.selectAllByPage(pageNum,pageSize);
    }
    @GetMapping("/exportExcel")
    @ApiOperation(value = "导出Excel")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result exportExcel(HttpServletResponse response) throws BaseException{
        return tagPointService.exportExcel(response);
    }

    @PostMapping("/importExcel")
    @ApiOperation(value = "导入Excel")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result importExcel(@RequestParam(value="file", required = false) MultipartFile file) throws BaseException{
        return tagPointService.deleteExcel(file);
    }


}
