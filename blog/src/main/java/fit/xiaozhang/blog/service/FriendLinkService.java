package fit.xiaozhang.blog.service;

import fit.xiaozhang.blog.dto.FriendLinkBackDTO;
import fit.xiaozhang.blog.dto.FriendLinkDTO;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.entity.FriendLink;
import com.baomidou.mybatisplus.extension.service.IService;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.FriendLinkVO;

import java.util.List;

/**
 * @author zhangzhi
 * @since 2020-05-18
 */
public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 查看友链列表
     *
     * @return 友链列表
     */
    List<FriendLinkDTO> listFriendLinks();

    /**
     * 查看后台友链列表
     *
     * @param condition 条件
     * @return 友链列表
     */
    PageDTO<FriendLinkBackDTO> listFriendLinkDTO(ConditionVO condition);

    /**
     * 保存或更新友链
     * @param friendLinkVO 友链
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

}
