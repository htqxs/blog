package fit.xiaozhang.blog.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import fit.xiaozhang.blog.dao.RoleDao;
import fit.xiaozhang.blog.dto.UrlRoleDTO;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * 用来储存请求与权限的对应关系，即访问某个资源需要的角色集合,
 * (资源到角色集合的映射，这个角色集合的任一个角色都能访问该资源)
 * @author zhangzhi
 * @date 2021-01-06
 */
@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    /**
     * 接口角色列表
     */
    private static List<UrlRoleDTO> urlRoleList;

    @Autowired
    private RoleDao roleDao;

    /**
     * 加载接口角色信息，查询出所有的资源与角色的关系（一个资源对应角色集合，由UrlRoleDTO类体现）
     */
    @PostConstruct
    private void loadDataSource() {
        // 查询的是资源非匿名的集合
        urlRoleList = roleDao.listUrlRoles();
    }

    /**
     * 清空接口角色信息
     */
    public void clearDataSource() {
        urlRoleList = null;
    }

    /**
     * 返回请求的资源需要的角色集合
     * @param object FilterInvocation 类型，可以获取请求方式和请求路径
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 修改接口角色关系后重新加载
        if (CollectionUtils.isEmpty(urlRoleList)) {
            loadDataSource();
        }
        FilterInvocation fi = (FilterInvocation) object;
        // 获取用户请求方式
        String method = fi.getRequest().getMethod();
        // 获取用户请求Url
        String url = fi.getRequest().getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        // 获取接口角色信息，若为匿名接口则放行，若无对应角色则禁止
        // todo 每次都要遍历，角色和资源的关系封装成Map结构，匿名接口没看出来在哪里体现
        for (UrlRoleDTO urlRoleDTO : urlRoleList) {
            if (antPathMatcher.match(urlRoleDTO.getUrl(), url) && urlRoleDTO.getRequestMethod().equals(method)) {
                List<String> roleList = urlRoleDTO.getRoleList();
                // 为空，说明无对应角色则禁止
                if (CollectionUtils.isEmpty(roleList)) {
                    return SecurityConfig.createList("disable");
                }
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
