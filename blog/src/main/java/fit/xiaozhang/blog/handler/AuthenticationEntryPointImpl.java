package fit.xiaozhang.blog.handler;

import com.alibaba.fastjson.JSON;
import fit.xiaozhang.blog.constant.StatusConst;
import fit.xiaozhang.blog.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户未登录处理，用来解决匿名用户访问无权限资源时的异常
 *
 * @author zhangzhi
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<>(false, StatusConst.NOT_LOGIN, "请登录")));
    }
}
