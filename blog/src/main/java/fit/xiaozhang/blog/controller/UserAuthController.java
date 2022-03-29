package fit.xiaozhang.blog.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.dto.UserBackDTO;
import fit.xiaozhang.blog.dto.UserInfoDTO;
import fit.xiaozhang.blog.service.UserAuthService;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.PasswordVO;
import fit.xiaozhang.blog.vo.Result;
import fit.xiaozhang.blog.vo.UserVO;

import javax.validation.Valid;

/***
 * @author zhangzhi
 */
@Api(tags = "用户账号模块")
@RestController
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;

    @ApiOperation(value = "发送邮箱验证码")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @GetMapping("/users/code")
    public Result sendCode(String username) {
        userAuthService.sendCode(username);
        return new Result<>(true, StatusConst.OK, "发送成功！");
    }

    @ApiOperation(value = "查看后台用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<UserBackDTO>> listUsers(ConditionVO condition) {
        return new Result<>(true, StatusConst.OK, "查询成功！", userAuthService.listUserBackDTO(condition));
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/users")
    public Result saveUser(@Valid @RequestBody UserVO user) {
        userAuthService.saveUser(user);
        return new Result<>(true, StatusConst.OK, "注册成功！");
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("/users/password")
    public Result updatePassword(@Valid @RequestBody UserVO user) {
        userAuthService.updatePassword(user);
        return new Result<>(true, StatusConst.OK, "修改成功！");
    }

    @ApiOperation(value = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public Result updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return new Result<>(true, StatusConst.OK, "修改成功！");
    }

    @ApiOperation(value = "qq登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "openId", required = true, dataType = "String"),
            @ApiImplicitParam(name = "accessToken", value = "accessToken", required = true, dataType = "String")
    })
    @PostMapping("/users/oauth/qq")
    public Result<UserInfoDTO> qqLogin(String openId, String accessToken) {
        return new Result<>(true, StatusConst.OK, "登录成功！", userAuthService.qqLogin(openId, accessToken));
    }
}

