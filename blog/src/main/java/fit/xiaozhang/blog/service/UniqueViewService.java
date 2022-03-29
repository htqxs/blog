package fit.xiaozhang.blog.service;

import fit.xiaozhang.blog.dto.UniqueViewDTO;
import fit.xiaozhang.blog.entity.UniqueView;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhangzhi
 * @since 2020-05-18
 */
public interface UniqueViewService extends IService<UniqueView> {

    /**
     * 统计每日用户量
     */
    void saveUniqueView();

    /**
     * 获取7天用户量统计
     * @return 用户量
     */
    List<UniqueViewDTO> listUniqueViews();
}
