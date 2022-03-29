package fit.xiaozhang.blog.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import fit.xiaozhang.blog.annotation.OptLog;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.MessageBackDTO;
import fit.xiaozhang.blog.dto.MessageDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.service.MessageService;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.MessageVO;
import fit.xiaozhang.blog.vo.Result;

import javax.validation.Valid;
import java.util.List;

import static fit.xiaozhang.blog.constant.OptTypeConst.REMOVE;

/**
 * @author zhangzhi
 */
@Api(tags = "留言模块")
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    @ApiOperation(value = "添加留言")
    @PostMapping("/messages")
    public Result saveMessage(@Valid @RequestBody MessageVO messageVO) {
        messageService.saveMessage(messageVO);
        return new Result<>(true, StatusConst.OK, "添加成功");
    }

    @ApiOperation(value = "查看留言列表")
    @GetMapping("/messages")
    public Result<List<MessageDTO>> listMessages() {
        return new Result<>(true, StatusConst.OK, "添加成功", messageService.listMessages());
    }

    @ApiOperation(value = "查看后台留言列表")
    @GetMapping("/admin/messages")
    public Result<PageDTO<MessageBackDTO>> listMessageBackDTO(ConditionVO condition) {
        return new Result<>(true, StatusConst.OK, "添加成功", messageService.listMessageBackDTO(condition));
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result deleteMessages(@RequestBody List<Integer> messageIdList) {
        messageService.removeByIds(messageIdList);
        return new Result<>(true, StatusConst.OK, "操作成功");
    }

}

