package fit.xiaozhang.blog.controller;

import fit.xiaozhang.blog.annotation.OptLog;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.UserRoleDTO;
import fit.xiaozhang.blog.service.RoleService;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.Result;
import fit.xiaozhang.blog.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static fit.xiaozhang.blog.constant.OptTypeConst.REMOVE;
import static fit.xiaozhang.blog.constant.OptTypeConst.SAVE_OR_UPDATE;

/**
 * @author zhangzhi
 **/
@Api(tags = "角色模块")
@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "查询用户角色选项")
    @GetMapping("/admin/users/role")
    public Result<List<UserRoleDTO>> listUserRole() {
        return new Result<>(true, StatusConst.OK, "查询成功", roleService.listUserRoles());
    }

    @ApiOperation(value = "查询角色列表")
    @GetMapping("/admin/roles")
    public Result<List<UserRoleDTO>> listRoles(ConditionVO conditionVO) {
        return new Result<>(true, StatusConst.OK, "查询成功", roleService.listRoles(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "保存或更新角色")
    @PostMapping("/admin/role")
    public Result listRoles(@RequestBody @Valid RoleVO roleVO) {
        roleService.saveOrUpdateRole(roleVO);
        return new Result<>(true, StatusConst.OK, "操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除角色")
    @DeleteMapping("/admin/roles")
    public Result deleteRoles(@RequestBody List<Integer> roleIdList) {
        roleService.deleteRoles(roleIdList);
        return new Result<>(true, StatusConst.OK, "操作成功");
    }

}
