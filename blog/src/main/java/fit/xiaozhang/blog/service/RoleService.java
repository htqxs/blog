package fit.xiaozhang.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fit.xiaozhang.blog.dto.PageDTO;
import fit.xiaozhang.blog.dto.RoleDTO;
import fit.xiaozhang.blog.dto.UserRoleDTO;
import fit.xiaozhang.blog.entity.Role;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.RoleVO;

import java.util.List;

/**
 * @author: zhangzhi
 * @date: 2020-12-27
 **/
public interface RoleService extends IService<Role> {

    /**
     * 获取用户角色选项
     *
     * @return 角色
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 查询角色列表
     *
     * @param conditionVO 条件
     * @return 角色列表
     */
    PageDTO<RoleDTO> listRoles(ConditionVO conditionVO);

    /**
     * 保存或更新角色
     *
     * @param roleVO 角色
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 删除角色
     * @param roleIdList 角色id列表
     */
    void deleteRoles(List<Integer> roleIdList);

}
