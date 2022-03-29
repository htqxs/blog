package fit.xiaozhang.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.LabelOptionDTO;
import fit.xiaozhang.blog.dto.MenuDTO;
import fit.xiaozhang.blog.dto.UserMenuDTO;
import fit.xiaozhang.blog.service.MenuService;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.MenuVO;
import fit.xiaozhang.blog.vo.Result;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhangzhi
 **/
@Api(tags = "菜单模块")
@RestController
public class MenuController {
    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "查看菜单列表")
    @GetMapping("/admin/menus")
    public Result<List<MenuDTO>> listMenus(ConditionVO conditionVO) {
        return new Result<>(true, StatusConst.OK, "查询成功", menuService.listMenus(conditionVO));
    }

    @ApiOperation(value = "查看角色菜单选项")
    @GetMapping("/admin/role/menus")
    public Result<List<LabelOptionDTO>> listMenuOptions() {
        return new Result<>(true, StatusConst.OK, "查询成功", menuService.listMenuOptions());
    }

    @ApiOperation(value = "查看用户菜单")
    @GetMapping("/admin/user/menus")
    public Result<List<UserMenuDTO>> listUserMenus() {
        return new Result<>(true, StatusConst.OK, "查询成功", menuService.listUserMenus());
    }

    /**
     * 新增或修改菜单
     * @param menuVO 菜单
     * @return {@link Result<>}
     */
    @ApiOperation(value = "新增或修改菜单")
    @PostMapping("/admin/menus")
    public Result<?> saveOrUpdateMenu(@Valid @RequestBody MenuVO menuVO) {
        menuService.saveOrUpdateMenu(menuVO);
        return new Result<>();
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     * @return {@link Result<>}
     */
    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/admin/menus/{menuId}")
    public Result<?> deleteMenu(@PathVariable("menuId") Integer menuId){
        menuService.deleteMenu(menuId);
        return new Result<>();
    }
}
