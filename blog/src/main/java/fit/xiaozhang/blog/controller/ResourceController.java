package fit.xiaozhang.blog.controller;

import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.ResourceDTO;
import fit.xiaozhang.blog.dto.LabelOptionDTO;
import fit.xiaozhang.blog.service.ResourceService;
import fit.xiaozhang.blog.vo.ResourceVO;
import fit.xiaozhang.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: zhangzhi
 **/
@Api(tags = "资源模块")
@RestController
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @ApiOperation(value = "导入swagger接口")
    @GetMapping("/admin/resources/import/swagger")
    public Result importSwagger() {
        resourceService.importSwagger();
        return new Result<>(true, StatusConst.OK, "导入成功");
    }

    @ApiOperation(value = "查看资源列表")
    @GetMapping("/admin/resources")
    public Result<List<ResourceDTO>> listResources() {
        return new Result<>(true, StatusConst.OK, "查询成功", resourceService.listResources());
    }

    @ApiOperation(value = "删除资源")
    @DeleteMapping("/admin/resources")
    public Result deleteResources(@RequestBody List<Integer> resourceIdList) {
        resourceService.removeByIds(resourceIdList);
        return new Result<>(true, StatusConst.OK, "删除成功");
    }

    @ApiOperation(value = "新增或修改资源")
    @PostMapping("/admin/resources")
    public Result saveOrUpdateResource(@RequestBody @Valid ResourceVO resourceVO) {
        resourceService.saveOrUpdateResource(resourceVO);
        return new Result<>(true, StatusConst.OK, "操作成功");
    }

    @ApiOperation(value = "查看角色资源选项")
    @GetMapping("/admin/role/resources")
    public Result<List<LabelOptionDTO>> listResourceOption() {
        return new Result<>(true, StatusConst.OK, "查询成功", resourceService.listResourceOption());
    }
}
