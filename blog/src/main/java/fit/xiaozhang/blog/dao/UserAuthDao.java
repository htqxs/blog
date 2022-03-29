package fit.xiaozhang.blog.dao;

import fit.xiaozhang.blog.dto.UserBackDTO;
import fit.xiaozhang.blog.entity.UserAuth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fit.xiaozhang.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author zhangzhi
 */
@Repository
public interface UserAuthDao extends BaseMapper<UserAuth> {

    /**
     * 查询后台用户列表
     * @param condition 条件
     * @return 用户集合
     */
    List<UserBackDTO> listUsers(@Param("condition") ConditionVO condition);

    /**
     * 查询后台用户数量
     * @param condition 条件
     * @return 用户数量
     */
    Integer countUser(@Param("condition") ConditionVO condition);
}
