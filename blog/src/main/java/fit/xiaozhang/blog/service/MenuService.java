package fit.xiaozhang.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fit.xiaozhang.blog.dto.LabelOptionDTO;
import fit.xiaozhang.blog.dto.MenuDTO;
import fit.xiaozhang.blog.dto.UserMenuDTO;
import fit.xiaozhang.blog.entity.Menu;
import fit.xiaozhang.blog.vo.ConditionVO;
import fit.xiaozhang.blog.vo.MenuVO;

import java.util.List;

/**
 * @author: zhangzhi
 **/
public interface MenuService extends IService<Menu> {

    /**
     * 查看菜单列表
     * @param conditionVO 条件
     * @return 菜单列表
     */
    List<MenuDTO> listMenus(ConditionVO conditionVO);

    /**
     * 查看角色菜单选项
     * @return 角色菜单选项
     */
    List<LabelOptionDTO> listMenuOptions();

    /**
     * 查看用户菜单
     * @return 菜单列表
     */
    List<UserMenuDTO> listUserMenus();

    /**
     * 新增或修改菜单
     *
     * @param menuVO 菜单信息
     */
    void saveOrUpdateMenu(MenuVO menuVO);

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     */
    void deleteMenu(Integer menuId);
}
