package com.wbr.model.mem.controller;

import com.wbr.model.mem.annotation.LogAnnotation;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.model.Result;
import com.wbr.model.mem.service.UserService;
import com.wbr.model.mem.utils.StringUtils;
import com.wbr.model.mem.vo.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author glf
 */
@Slf4j
@RestController
@CrossOrigin(maxAge = 3600)
@Api(tags = "SysUser API")
@RequestMapping("/sysUser")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/insert")
    @ApiOperation(value = "新增用户")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result insert(@RequestBody SysUser sysUser) throws BaseException {
        return userService.insert(sysUser);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result update(@RequestBody SysUser sysUser)  throws BaseException{
        return userService.updateByPrimaryKey(sysUser);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除用户")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result delete(@PathVariable(value = "id")String id)  throws BaseException{
        return userService.deleteByPrimaryKey(id);
    }

    @GetMapping("/selectAll")
    @ApiOperation(value = "获取所有用户信息")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectAll() throws BaseException {
        return userService.selectAll();
    }
    @GetMapping("/select/{id}")
    @ApiOperation(value = "查询用户")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectByPrimaryKey(@PathVariable(value = "id")String id)  throws BaseException{
        return userService.selectByPrimaryKey(id);
    }
    @GetMapping("/selectPage")
    @ApiOperation(value = "分页获取映射")
    @LogAnnotation(module = "model-parent", recordRequestParam = false)
    public Result selectPage(@RequestParam(value = "pageNum") int pageNum,@RequestParam(value = "pageSize") int pageSize)  throws BaseException{
        return userService.selectAllByPage(pageNum,pageSize);
    }
    @PostMapping("/login")
    @ApiOperation(value = "登录")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result login(@RequestBody Map<String, Object> param,@ApiIgnore HttpServletRequest request) throws BaseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String username = (String)param.get("username");
        String password = (String)param.get("password");
        Result result=new Result();
        if(!StringUtils.isBlank(username) && !StringUtils.isBlank(password)){
            userService.login(param,result,request);
        }else{
            result.setMessage("用户名或密码错误，不能为空!");
            result.setCode("401");
        }
        return result;
    }
    @PostMapping("/logout")
    @ApiOperation(value = "注销")
    @LogAnnotation(module = "model-parent", recordRequestParam = true)
    public Result logout(@RequestParam(name = "username") String username,@ApiIgnore HttpServletRequest request) throws BaseException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Result result=new Result();
        if(!StringUtils.isBlank(username)){
            Map<String, Object> param = new HashMap();
            param.put("username",username);
            userService.logout(param,result,request);
        }
        return result;
    }
}
