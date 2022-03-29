package fit.xiaozhang.blog.controller;

import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.OperationLogDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.service.OperationLogService;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangzhi
 **/
@Api(tags = "日志模块")
@RestController
public class LogController {
    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "查看操作日志")
    @GetMapping("/admin/operation/logs")
    public Result<PageDTO<OperationLogDTO>> listOperationLogs(ConditionVO conditionVO) {
        return new Result<>(true, StatusConst.OK, "查询成功", operationLogService.listOperationLogs(conditionVO));
    }

    @ApiOperation(value = "删除操作日志")
    @DeleteMapping("/admin/operation/logs")
    public Result deleteOperationLogs(@RequestBody List<Integer> logIdList) {
        operationLogService.removeByIds(logIdList);
        return new Result<>(true, StatusConst.OK, "删除成功");
    }

}
