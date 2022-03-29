package fit.xiaozhang.blog.handler;

import com.alibaba.fastjson.JSON;

import fit.xiaozhang.blog.vo.Result;
import fit.xiaozhang.blog.constant.StatusConst;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户没有权限处理，Security 鉴权失败，来解决认证过的用户访问无权限资源时的异常
 * @author zhangzhi
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result(false, StatusConst.AUTHORIZED,"没有操作权限")));
    }

}
