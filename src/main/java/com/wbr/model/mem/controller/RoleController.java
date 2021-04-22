package com.wbr.model.mem.controller;

import com.alibaba.fastjson.JSONObject;
import com.wbr.model.mem.annotation.LogAnnotation;
import com.wbr.model.mem.constant.PermissionTypeCon;
import com.wbr.model.mem.dao.SysMenuPermissionDao;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.RoleService;
import com.wbr.model.mem.utils.StringUtils;
import com.wbr.model.mem.utils.UUIDUtils;
import com.wbr.model.mem.vo.SysMenuPermission;
import com.wbr.model.mem.vo.SysRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author glf
 */
@Slf4j
@RestController
@CrossOrigin(maxAge = 3600)
@Api(tags = "SysRole API")
@RequestMapping("/sysRole")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/insert")
    @ApiOperation(value = "新增")
    @LogAnnotation(module = "mem-parent", recordRequestParam = true)
    public Result insert(@RequestBody SysRole sysRole) throws BaseException {
        return roleService.insert(sysRole);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改")
    @LogAnnotation(module = "mem-parent", recordRequestParam = true)
    public Result update(@RequestBody SysRole sysRole) throws BaseException{
        return roleService.updateByPrimaryKey(sysRole);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除")
    @LogAnnotation(module = "mem-parent", recordRequestParam = true)
    public Result delete(@PathVariable(value = "id")String id) throws BaseException{
        return roleService.deleteByPrimaryKey(id);
    }

    @GetMapping("/select/{id}")
    @ApiOperation(value = "查询")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result select(@PathVariable(value = "id")String id) throws BaseException{
        return roleService.selectByPrimaryKey(id);
    }

    @GetMapping("/selectAll")
    @ApiOperation(value = "获取所有")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result selectAll() throws BaseException{ return roleService.selectAll();}

    @GetMapping("/savePermissionByRole")
    @ApiOperation(value = "设置角色对应的权限")
    @LogAnnotation(module = "mem-parent", recordRequestParam = true)
    public Result savePermissionByRole(@RequestBody JSONObject jsonObject) throws BaseException{
        return roleService.savePermissionByRole(jsonObject);
    }
    @GetMapping("/selectPermissionByRole/{roleId}")
    @ApiOperation(value = "设置角色对应的权限")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result selectPermissionByRole(@PathVariable(value = "roleId")String roleId) throws BaseException{
        return roleService.selectPermissionByRole(roleId);
    }
    @GetMapping("/selectPermission")
    @ApiOperation(value = "获取权限树形结构")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result selectPermissionTree() throws BaseException{
        return roleService.selectPermissionTree();
    }
    @GetMapping("/selectOneMenu")
    @ApiOperation(value = "获取一级菜单")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result selectOneMenu() throws BaseException{
        return roleService.selectOneMenu();
    }
    @GetMapping("/selectMenuByStruId/{struId}")
    @ApiOperation(value = "获取子集菜单")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result selectMenuByStruId(@PathVariable(value = "struId")String struId) throws BaseException{
        return roleService.selectMenuByStruId(struId);
    }
    @GetMapping("/selectRoleByUser/{userId}")
    @ApiOperation(value = "根据用户获取角色")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public Result selectRoleByUser(@PathVariable(value = "userId")String roleId) throws BaseException{
        return roleService.selectPermissionByRole(roleId);
    }
    @Autowired
    private SysMenuPermissionDao sysMenuPermissionDao;

    @GetMapping("/initMenu")
    @ApiOperation(value = "初始化菜单按钮")
    @LogAnnotation(module = "mem-parent", recordRequestParam = false)
    public void initMenu() {

//      management
//        首页，实时监测，报警管理 知识库 三维可视化 生产优化 模型管理 个人工作台 系统管理
//        实时监测：对比分析 导出
//        报警管理：
//          报警规则管理
//             规则配置   ：
//             设备测点均值标准
//          报警列表
//             实时报警：导出 详情 发布 忽略 批量发布 批量忽略
//             报警历史： 导出
//          事件列表
//             当前事件 新增 删除 处理 关闭
//             事件历史
//{"报警规则管理-rulesMan":{"规则设置-ruleSetting":"","设备测点均值标准-meanPoints":""}
// "报警列表-selectPage":{"实时报警-realPolice":"导出,详情,发布,忽略,批量发布,批量忽略","报警历史-alarmHistory":"导出"}
// "事件列表-eventPage":{"当前事件-currentEvent":"新增,删除,处理,关闭","事件历史-eventHistory":""}}

        JSONObject  menu=new JSONObject (new LinkedHashMap());
        menu.put("首页-index",null);
        menu.put("实时监测-realtime","对比分析,导出");
        menu.put("报警管理-alarm","{\"报警规则管理-rulesman\":{\"规则设置-rulesetting\":\"\",\"设备测点均值标准-meanPoints\":\"\"},\"报警列表-selectPage\":{\"实时报警-realPolice\":\"导出,详情,发布,忽略,批量发布,批量忽略\",\"报警历史-alarmHistory\":\"导出\"}, \"事件列表-eventPage\":{\"当前事件-currentEvent\":\"新增,删除,处理,关闭\",\"事件历史-eventHistory\":\"\"}}");
        menu.put("知识库-knowledge","");
        menu.put("三维可视化-3Dvisualiza","");
        menu.put("生产优化-productsuperior","");
        menu.put("模型管理-modelmanagement","");
        menu.put("个人工作台-personWork","");

        //基础数据 base
            Map system=new HashMap();
            system.put("基础数据-base","{\"组织架构-orgUnit\":\"新增,修改,删除\",\"设备管理-sysEqu\":\"新增,修改,删除,测点新增,测点修改,测点删除,测点导入,测点导出\",\"设备类型-unitType\":\"新增,修改,删除\",\"设备型号-unitType\":\"新增,修改,删除\",\"模型参数-modelParm\":\"新增,修改,删除\"}");
                //{"基础数据-base":{"组织架构-orgUnit":"新增,修改,删除","设备管理-sysEqu":"新增,修改,删除,测点新增,测点修改,测点删除,测点导入,测点导出","设备类型-unitType":"新增,修改,删除","设备型号-unitType":"新增,修改,删除","模型参数-modelParm":"新增,修改,删除"}}
//            system.put("用户管理-user","");
//            system.put("数据质量管理-dataqual","");
        menu.put("系统管理-system", "{\"基础数据-base\":{\"组织架构-orgUnit\":\"新增,修改,删除\",\"设备管理-sysEqu\":\"新增,修改,删除,测点新增,测点修改,测点删除,测点导入,测点导出\",\"设备类型-unitType\":\"新增,修改,删除\",\"设备型号-unitType\":\"新增,修改,删除\",\"模型参数-modelParm\":\"新增,修改,删除\"}}");
        String struId = "1000";

        for(Iterator it = menu.keySet().iterator(); it.hasNext();){
            struId = StringUtils.updateStruIdValue(struId,1);
            String key= (String) it.next();
            String value=(String)menu.get(key);
            insertMenus(key,value,struId,"/",null);
        }
    }
    public void insertMenus(String key,String value,String struId,String path,String parentId){
        String[] keys=key.split("\\-");
        SysMenuPermission sysMenuPermission = new SysMenuPermission();
        sysMenuPermission.setId(UUIDUtils.getGUID32());
        sysMenuPermission.setName(keys[0]);
        sysMenuPermission.setCode(keys[1]);
        sysMenuPermission.setCreateTime(new Date());
        sysMenuPermission.setHidden(0);
        sysMenuPermission.setLeaf(0);
        sysMenuPermission.setParentId(parentId);
        sysMenuPermission.setStruId(struId);
        sysMenuPermission.setPermissionType(PermissionTypeCon.MMENU);
        if(!"/".equals(path)){
            path=path+"/";
        }
        sysMenuPermission.setPath(path+sysMenuPermission.getCode());
        if(StringUtils.isBlank(value)){
            sysMenuPermission.setLeaf(1);
            sysMenuPermissionDao.insert(sysMenuPermission);
        }else{
            if(value.startsWith("{")){//菜单
                sysMenuPermissionDao.insert(sysMenuPermission);

                JSONObject njsonObject = JSONObject.parseObject(value);
                String nStr="1000";
                for(Iterator its =  njsonObject.keySet().iterator(); its.hasNext();){
                    String nkey = (String) its.next();
                    String nvalue = njsonObject.getString(nkey);
                    nStr = StringUtils.updateStruIdValue(nStr,1);
                    insertMenus(nkey,nvalue,sysMenuPermission.getStruId()+nStr,sysMenuPermission.getPath(),sysMenuPermission.getId());
                }
            }else{//按钮
                //sysMenuPermission.setLeaf(1);
                sysMenuPermissionDao.insert(sysMenuPermission);
                insertButton(value,sysMenuPermission);
            }
        }
    }
    public void insertButton(String value,SysMenuPermission sysMenuPermission){
         Map<String,String> buttons=new HashMap<String,String>();

         buttons.put("对比分析","comAnalysis");
         buttons.put("详情","viewInfo");
         buttons.put("发布","release");
         buttons.put("忽略","ignore");
         buttons.put("批量发布","batchRelease");
         buttons.put("批量忽略","batchIgnore");
         buttons.put("处理","deal");
         buttons.put("关闭","close");
        buttons.put("新增","insert");
        buttons.put("修改","update");
        buttons.put("删除","delete");
        buttons.put("导入","importExcel");
        buttons.put("导出","exportExcel");

        buttons.put("测点新增","insert");
        buttons.put("测点修改","update");
        buttons.put("测点删除","delete");
        buttons.put("测点导入","importExcel");
        buttons.put("测点导出","exportExcel");

         String[] button=value.split(",");
         String butStruid = sysMenuPermission.getStruId()+"1000";
         for(int j=0;j<button.length;j++){
             butStruid = StringUtils.updateStruIdValue(butStruid,1);
             String buttonName = button[j];
             SysMenuPermission sysMenuPermission2 = new SysMenuPermission();
             sysMenuPermission2.setId(UUIDUtils.getGUID32());
             sysMenuPermission2.setCode(buttons.get(buttonName));
             sysMenuPermission2.setName(buttonName);
             sysMenuPermission2.setCreateTime(new Date());
             sysMenuPermission2.setHidden(0);
             sysMenuPermission2.setLeaf(1);
             sysMenuPermission2.setPath(sysMenuPermission.getPath()+"/"+otherPath(buttonName)+buttons.get(buttonName));
             sysMenuPermission2.setPermissionType(PermissionTypeCon.BUTTON);
             sysMenuPermission2.setStruId(butStruid);
             sysMenuPermission2.setParentId(sysMenuPermission.getId());
             sysMenuPermissionDao.insert(sysMenuPermission2);
         }
    }
    public String  otherPath(String name){
        if(name.startsWith("测点")){
            return "tagPoint/";
        }
        return "";
    }
}
