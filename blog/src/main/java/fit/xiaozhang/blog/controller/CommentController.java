package fit.xiaozhang.blog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import fit.xiaozhang.blog.annotation.OptLog;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.dto.CommentBackDTO;
import fit.xiaozhang.blog.dto.CommentDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.dto.ReplyDTO;
import fit.xiaozhang.blog.service.CommentService;
import fit.xiaozhang.blog.vo.CommentVO;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.DeleteVO;
import fit.xiaozhang.blog.vo.Result;

import javax.validation.Valid;
import java.util.List;

import static fit.xiaozhang.blog.constant.OptTypeConst.REMOVE;
import static fit.xiaozhang.blog.constant.OptTypeConst.UPDATE;

/**
 * @author zhangzhi
 */
@RestController
@Api(tags = "评论模块")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "查询评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "current", value = "当前页码", required = true, dataType = "Long")
    })
    @GetMapping("/comments")
    public Result<PageDTO<CommentDTO>> listComments(Integer articleId, Long current) {
        return new Result<>(true, StatusConst.OK, "查询成功！", commentService.listComments(articleId, current));
    }

    @ApiOperation(value = "添加评论或回复")
    @PostMapping("/comments")
    public Result saveComment(@Valid @RequestBody CommentVO commentVO) {
        commentService.saveComment(commentVO);
        return new Result<>(true, StatusConst.OK, "评论成功！");
    }

    @ApiOperation(value = "查询评论下的回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "文章id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "current", value = "当前页码", required = true, dataType = "Long")
    })
    @GetMapping("/comments/replies/{commentId}")
    public Result<List<ReplyDTO>> listRepliesByCommentId(@PathVariable("commentId") Integer commentId, Long current) {
        return new Result<>(true, StatusConst.OK, "查询成功！", commentService.listRepliesByCommentId(commentId, current));
    }

    @ApiOperation(value = "评论点赞")
    @PostMapping("/comments/like")
    public Result saveCommentList(Integer commentId) {
        commentService.saveCommentLike(commentId);
        return new Result<>(true, StatusConst.OK, "点赞成功！");
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "删除或恢复评论")
    @PutMapping("/admin/comments")
    public Result deleteComment(DeleteVO deleteVO) {
        commentService.updateCommentDelete(deleteVO);
        return new Result<>(true, StatusConst.OK, "操作成功！");
    }

    @OptLog(optType = REMOVE)
    @ApiOperation(value = "物理删除评论")
    @DeleteMapping("/admin/comments")
    public Result deleteComments(@RequestBody List<Integer> commentIdList) {
        commentService.removeByIds(commentIdList);
        return new Result<>(true, StatusConst.OK, "操作成功！");
    }

    @ApiOperation(value = "查询后台评论")
    @GetMapping("/admin/comments")
    public Result<PageDTO<CommentBackDTO>> listCommentBackDTO(ConditionVO condition) {
        return new Result<>(true, StatusConst.OK, "查询成功", commentService.listCommentBackDTO(condition));
    }

}

