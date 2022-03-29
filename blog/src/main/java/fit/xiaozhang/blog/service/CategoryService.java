package fit.xiaozhang.blog.service;

import fit.xiaozhang.blog.dto.CategoryDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import fit.xiaozhang.blog.vo.CategoryVO;
import fit.xiaozhang.blog.vo.ConditionVO;

import java.util.List;


/**
 * @author zhangzhi
 * @date 2020-05-16
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询分类列表
     *
     * @return 分类列表
     */
    PageDTO<CategoryDTO> listCategories();

    /**
     * 查询后台分类
     *
     * @param conditionVO 条件
     * @return 分类列表
     */
    PageDTO<Category> listCategoryBackDTO(ConditionVO conditionVO);

    /**
     * 删除分类
     *
     * @param categoryIdList 分类id集合
     */
    void deleteCategory(List<Integer> categoryIdList);

    /**
     * 添加或修改分类
     * @param categoryVO 分类
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

}