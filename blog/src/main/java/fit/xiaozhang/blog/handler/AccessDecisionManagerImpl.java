package fit.xiaozhang.blog.handler;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 决策器，由AbstractSecurityInterceptor调用，负责鉴定用户是否有访问对应资源（方法或URL）的权限（角色）。
 *
 * @author zhangzhi
 * @date 2021-01-06
 **/
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    /**
     * 通过传递的参数来决定用户是否有访问对应受保护对象的权限
     *
     * @param authentication 包含了当前的用户信息，包括拥有的权限。这里的权限来源就是前面登录时UserDetailsService中设置的authorities。
     * @param object  就是FilterInvocation对象，可以得到request等web资源
     * @param configAttributes configAttributes是本次访问需要的权限
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 获取用户权限列表
        List<String> permissionList = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        // 判断该用户是否用户本次访问需要的权限（角色）
        for (ConfigAttribute item : configAttributes) {
            if (permissionList.contains(item.getAttribute())) {
                return;
            }
            // for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            //     if (grantedAuthority.getAuthority().equals(item.getAttribute())) {
            //         return;
            //     }
            // }
        }
        throw new AccessDeniedException("没有操作权限");
    }

    /**
     * 表示此 AccessDecisionManager 是否能够处理传递的ConfigAttribute呈现的授权请求
     */
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    /**
     * 表示当前AccessDecisionManager实现是否能够为指定的安全对象（方法调用或Web请求）提供访问控制决策
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
