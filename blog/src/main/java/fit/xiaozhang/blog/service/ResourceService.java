package fit.xiaozhang.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fit.xiaozhang.blog.dto.ResourceDTO;
import fit.xiaozhang.blog.dto.LabelOptionDTO;
import fit.xiaozhang.blog.entity.Resource;
import fit.xiaozhang.blog.vo.ResourceVO;

import java.util.List;


/**
 * @author: zhangzhi
 * @date: 2020-12-27
 **/
public interface ResourceService extends IService<Resource> {

    /**
     * 导入swagger权限
     */
    void importSwagger();

    /**
     * 添加或修改资源
     * @param resourceVO
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /**
     * 查看资源列表
     *
     * @return 资源列表
     */
    List<ResourceDTO> listResources();

    /**
     * 查看资源选项
     * @return 资源选项
     */
    List<LabelOptionDTO> listResourceOption();

}
