package fit.xiaozhang.blog.dao;

import fit.xiaozhang.blog.dto.CategoryDTO;
import fit.xiaozhang.blog.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author zhangzhi
 */
@Repository
public interface CategoryDao extends BaseMapper<Category> {

    /**
     * 查询分类和对应文章数量
     * @return 分类集合
     */
    List<CategoryDTO> listCategoryDTO();

}
