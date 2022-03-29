package fit.xiaozhang.blog.service;

import fit.xiaozhang.blog.dto.MessageBackDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.MessageVO;
import fit.xiaozhang.blog.dto.MessageDTO;
import fit.xiaozhang.blog.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhangzhi
 * @since 2020-05-18
 */
public interface MessageService extends IService<Message> {

    /**
     * 添加留言弹幕
     *
     * @param messageVO 留言对象
     */
    void saveMessage(MessageVO messageVO);

    /**
     * 查看留言弹幕
     *
     * @return 留言列表
     */
    List<MessageDTO> listMessages();

    /**
     * 查看后台留言
     *
     * @param condition 条件
     * @return 留言列表
     */
    PageDTO<MessageBackDTO> listMessageBackDTO(ConditionVO condition);

}
