package fit.xiaozhang.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fit.xiaozhang.blog.dao.OperationLogDao;
import fit.xiaozhang.blog.dto.OperationLogDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.entity.OperationLog;
import fit.xiaozhang.blog.service.OperationLogService;
import fit.xiaozhang.blog.util.BeanCopyUtil;
import fit.xiaozhang.blog.vo.ConditionVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author: zhangzhi
 * @date: 2021-01-31
 **/
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogDao, OperationLog> implements OperationLogService {

    @Override
    public PageDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO) {
        Page<OperationLog> page = new Page<>(conditionVO.getCurrent(), conditionVO.getSize());
        // 查询日志列表
        Page<OperationLog> operationLogPage = this.page(page, new LambdaQueryWrapper<OperationLog>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()), OperationLog::getOptModule, conditionVO.getKeywords())
                .or()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()), OperationLog::getOptDesc, conditionVO.getKeywords())
                // todo 时间范围没有用到
                .gt(Objects.nonNull(conditionVO.getStartTime()), OperationLog::getCreateTime, conditionVO.getStartTime())
                .lt(Objects.nonNull(conditionVO.getEndTime()), OperationLog::getCreateTime, conditionVO.getEndTime())
                .orderByDesc(OperationLog::getId));
        List<OperationLogDTO> operationLogDTOList = BeanCopyUtil.copyList(operationLogPage.getRecords(), OperationLogDTO.class);
        return new PageDTO<>(operationLogDTOList, (int) operationLogPage.getTotal());
    }

}
