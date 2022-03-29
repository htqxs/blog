package fit.xiaozhang.blog.controller;


import fit.xiaozhang.blog.annotation.OptLog;
import fit.xiaozhang.blog.dto.ArticlePreviewListDTO;
import fit.xiaozhang.blog.dto.CategoryDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.entity.Category;
import fit.xiaozhang.blog.service.ArticleService;
import fit.xiaozhang.blog.service.CategoryService;
import fit.xiaozhang.blog.vo.CategoryVO;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.Result;
import fit.xiaozhang.blog.constant.StatusConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static fit.xiaozhang.blog.constant.OptTypeConst.SAVE_OR_UPDATE;
import static fit.xiaozhang.blog.constant.OptTypeConst.REMOVE;


/**
 * @author zhangzhi
 */
@Api(tags = "分类模块")
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看分类列表")
    @GetMapping("/categories")
    public Result<PageDTO<CategoryDTO>> listCategories() {
        return new Result<>(true, StatusConst.OK, "查询成功", categoryService.listCategories());
    }

    @ApiOperation(value = "查看后台分类列表")
    @GetMapping("/admin/categories")
    public Result<PageDTO<Category>> listCategoryBackDTO(ConditionVO condition) {
        return new Result<>(true, StatusConst.OK, "查询成功", categoryService.listCategoryBackDTO(condition));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "添加或修改分类")
    @PostMapping("/admin/categories")
    public Result saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO) {
        categoryService.saveOrUpdateCategory(categoryVO);
        return new Result<>(true, StatusConst.OK, "操作成功");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除分类")
    @DeleteMapping("/admin/categories")
    public Result deleteCategories(@RequestBody List<Integer> categoryIdList) {
        categoryService.deleteCategory(categoryIdList);
        return new Result<>(true, StatusConst.OK, "删除成功");
    }

    @ApiOperation(value = "查看分类下对应的文章")
    @GetMapping("/categories/{categoryId}")
    public Result<ArticlePreviewListDTO> listArticlesByCategoryId(@PathVariable("categoryId") Integer categoryId, Integer current) {
        ConditionVO conditionVO = ConditionVO.builder()
                .categoryId(categoryId)
                .current(current)
                .build();
        return new Result<>(true, StatusConst.OK, "查询成功", articleService.listArticlesByCondition(conditionVO));
    }
}

