package fit.xiaozhang.blog.service;

import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.dto.UserOnlineDTO;
import fit.xiaozhang.blog.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.EmailVO;
import fit.xiaozhang.blog.vo.UserInfoVO;
import fit.xiaozhang.blog.vo.UserRoleVO;

/**
 * @author zhangzhi
 * @since 2020-05-18
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 修改用户资料
     *
     * @param userInfoVO 用户资料
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 修改用户头像
     *
     * @param file 头像图片
     * @return 头像OSS地址
     */
    String updateUserAvatar(MultipartFile file);

    /**
     * 绑定用户邮箱
     * @param emailVO 邮箱
     */
    void saveUserEmail(EmailVO emailVO);

    /**
     * 修改用户权限
     *
     * @param userRoleVO 用户权限
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 修改用户禁用状态
     *
     * @param userInfoId 用户信息id
     * @param isDisable  禁用状态
     */
    void updateUserDisable(Integer userInfoId, Integer isDisable);

    /**
     * 查看在线用户列表
     * @param conditionVO 条件
     * @return 在线用户列表
     */
    PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO);

    /**
     * 下线用户
     * @param userInfoId 用户信息id
     */
    void removeOnlineUser(Integer userInfoId);

}
