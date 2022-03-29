package fit.xiaozhang.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 资源与角色的关系类，即访问一个资源需要用户哪些角色
 * 这里的资源由 url 和 requestMethod 确定
 * @author zhangzhi
 */
@Data
public class UrlRoleDTO {

    /**
     * 资源id
     */
    private Integer id;

    /**
     * 路径
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 角色名
     */
    private List<String> roleList;

    /**
     * 是否匿名
     */
    private Integer isAnonymous;
}
