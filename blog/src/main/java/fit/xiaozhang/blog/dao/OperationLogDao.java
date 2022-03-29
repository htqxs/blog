package fit.xiaozhang.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fit.xiaozhang.blog.entity.OperationLog;
import org.springframework.stereotype.Repository;

/**
 * @author zhangzhi
 **/
@Repository
public interface OperationLogDao extends BaseMapper<OperationLog> {
}
