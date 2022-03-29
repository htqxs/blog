package fit.xiaozhang.blog.dao;

import fit.xiaozhang.blog.dto.CommentBackDTO;
import fit.xiaozhang.blog.dto.CommentDTO;
import fit.xiaozhang.blog.dto.ReplyCountDTO;
import fit.xiaozhang.blog.dto.ReplyDTO;
import fit.xiaozhang.blog.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fit.xiaozhang.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangzhi
 */
@Repository
public interface CommentDao extends BaseMapper<Comment> {

    /**
     * 查看评论
     *
     * @param articleId 文章id
     * @param current   当前页码
     * @return 评论集合
     */
    List<CommentDTO> listComments(@Param("articleId") Integer articleId, @Param("current") Long current);

    /**
     * 查看评论id集合下的回复
     *
     * @param commentIdList 评论id集合
     * @return 回复集合
     */
    List<ReplyDTO> listReplies(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 查看当条评论下的回复
     *
     * @param commentId 评论id
     * @param current   当前页码
     * @return 回复集合
     */
    List<ReplyDTO> listRepliesByCommentId(@Param("commentId") Integer commentId, @Param("current") Long current);

    /**
     * 根据评论id查询回复总量
     *
     * @param commentIdList 评论id集合
     * @return 回复数量
     */
    List<ReplyCountDTO> listReplyCountByCommentId(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 查询后台评论
     *
     * @param condition 条件
     * @return 评论集合
     */
    List<CommentBackDTO> listCommentBackDTO(@Param("condition") ConditionVO condition);

    /**
     * 统计后台评论数量
     *
     * @param condition 条件
     * @return 评论数量
     */
    Integer countCommentDTO(@Param("condition") ConditionVO condition);

}
