package fit.xiaozhang.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fit.xiaozhang.blog.dto.OperationLogDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.entity.OperationLog;
import fit.xiaozhang.blog.vo.ConditionVO;

/**
 * @author: zhangzhi
 * @date: 2021-01-31
 **/
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 查询日志列表
     *
     * @param conditionVO 条件
     * @return 日志列表
     */
    PageDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}
