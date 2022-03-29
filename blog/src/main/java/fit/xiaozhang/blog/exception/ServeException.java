package fit.xiaozhang.blog.exception;

/**
 * 自定义异常类
 * @author zhangzhi
 */
public class ServeException extends RuntimeException {
    public ServeException(String message) {
        super(message);
    }
}
