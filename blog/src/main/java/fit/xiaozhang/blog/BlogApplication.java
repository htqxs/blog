package fit.xiaozhang.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 博客启动类
 *
 * @author zhangzhi
 */
@MapperScan("fit.xiaozhang.blog.dao")
@SpringBootApplication
@EnableScheduling
public class BlogApplication    {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // /**
    //  * http 转 https
    //  */
    // @Bean
    // public Connector connector() {
    //     Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    //     connector.setScheme("http");
    //     // 监听的http端口
    //     connector.setPort(8080);
    //     connector.setSecure(false);
    //     // 监听到http端口后跳转的https端口
    //     connector.setRedirectPort(443);
    //     return connector;
    // }
    //
    // /**
    //  * 拦截所有的请求
    //  */
    // @Bean
    // public TomcatServletWebServerFactory tomcatServletWebServerFactory(Connector connector) {
    //     TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
    //         @Override
    //         protected void postProcessContext(Context context) {
    //             SecurityConstraint securityConstraint = new SecurityConstraint();
    //             securityConstraint.setUserConstraint("CONFIDENTIAL");
    //             SecurityCollection collection = new SecurityCollection();
    //             collection.addPattern("/*");
    //             securityConstraint.addCollection(collection);
    //             context.addConstraint(securityConstraint);
    //         }
    //     };
    //     tomcat.addAdditionalTomcatConnectors(connector);
    //     return tomcat;
    // }
}