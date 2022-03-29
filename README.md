# blog
基于SpringBoot、MyBatis-Plus、Vue的前后端分离博客系统

## 数据库

### 数据表

| 表名             | 中文含义         | 说明                                                         |
| ---------------- | ---------------- | ------------------------------------------------------------ |
| tb_article       | 文章表           | 存放文章标题、内容、分类id、发布时间、置顶状态等             |
| tb_category      | 分类表           | 存放分类名称、创建时间                                       |
| tb_tag           | 标签表           | 存放标签名称、创建时间                                       |
| tb_article_tag   | 文章标签关系表   | 文章与标签之类是多对多的关系，存放article_id、tag_id         |
| tb_friend_link   | 友链表           | 存放友链信息，名称、地址、介绍、头像、创建时间等             |
| tb_message       | 留言表           | 存放留言用户ip、地址、昵称、头像url、内容、时间等            |
| tb_comment       | 评论表           | 存放评论用户id、评论文章id、内容、回复用户id、父评论id等     |
| tb_operation_log | 操作日志表       | 记录管理也操作的日志，操作模块、类型、url、方法、描述、参数、请求方式、返回数据、用户id、昵称、操作ip、地址等 |
| tb_menu          | 菜单表           | 存放菜单名、菜单路径、组件、菜单icon、父菜单id、时间、状态信息等 |
| tb_resource      | 权限表           | 存放权限名、权限路径、请求方式、父权限id、时间、状态信息等   |
| tb_role          | 角色表           | 存放角色名、角色描述、时间、状态信息等                       |
| tb_role_menu     | 角色菜单关系表   | 菜单和角色是多对多的关系，存放role_id、menu_id               |
| tb_role_resource | 角色权限关系表   | 角色和权限是多对多的关系，存放role_id、resource_id           |
| tb_user_auth     | 用户登录信息表   | 存放用户个人信息id、用户名、密码、登录类型、ip、ip来源、时间等 |
| tb_user_info     | 用户个人信息表   | 存放邮箱、昵称、头像url、用户简介、个人网站、时间、是否禁用等 |
| tb_user_role     | 用户角色关系表   | 用户和角色是多对多的关系，存放user_id、role_id               |
| tb_unique_view   | 网站单日访问量表 | 存放网站每天的访问量信息，如时间、访问量                     |

### 表关系

+ 文章和标签是多对多的关系，即一个文章可以含有多个标签，一个标签也可以对应多个文章。所以使用tb_article_tag 来维护这个关系。
+ 菜单和角色是多对多的关系，一个菜单可以被多个角色访问，一个角色也可以访问多个菜单。
+ 角色和权限是多对多的关系，一个角色可以有多个访问权限，一个权限也可以被多个角色访问。
+ 用户和角色是多对多的关系，一个用户可以有多个角色，一个角色可以被多个用户拥有。

> 注意
>
> + 用户的访问权限该博客是使用 SpringSecurity 来控制的，具体的见后序分析。
> + 用户对菜单的访问是在查询是用代码控制的。
>   + 用户id可以确定角色集合
>   + 角色可以确定访问的菜单集合

## 博客整体概览

### 包结构

| 包名       | 说明                                                         |
| ---------- | ------------------------------------------------------------ |
| annotation | 自定义注解                                                   |
| config     | 自定义配置类，MybatisPlus、Redis、Swagger的配置类等          |
| constant   | 自定义的常量类，Redis的key常量、操作类型、状态码等           |
| controller | 控制层代码                                                   |
| dao        | 持久层接口                                                   |
| dto        | 把后端返回前端的数据封装成DTO                                |
| entity     | 实体类                                                       |
| enums      | 自定义枚举类，登录类型、角色枚举、操作类型等                 |
| exception  | 自定义异常类                                                 |
| handler    | 自定义处理器，SpringSecurity的自定义handler、日志切面类、监听器等 |
| service    | 业务层代码                                                   |
| utils      | 工具类，日期、OSS、ip等工具类                                |
| vo         | 用于接收前端传来的数据，把这些数据封装成VO便于接收参数       |

### 核心功能

#### 操作日志记录

对所有加了 `@OptLog  ` 注解（通过 `optType` 指明操作类型）的方法进行日志记录。

**操作日志注解 annotation.OptLog**

```java
/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptLog {
    /**
     * @return 操作类型
     */
    String optType() default "";
}
```

**操作类型常量类 constant.OptTypeConst**

```java
/**
 * 操作日志类型常量
 */
public class OptTypeConst {

    /**
     * 新增操作
     */
    public static final String SAVE_OR_UPDATE = "新增或修改";

    /**
     * 新增
     */
    public static final String SAVE = "新增";

    /**
     * 修改操作
     */
    public static final String UPDATE = "修改";

    /**
     * 删除操作
     */
    public static final String REMOVE = "删除";

    /**
     * 上传操作
     */
    public static final String UPLOAD = "上传";
}
```

**日志切面类 handler.OptLogAspect**

```java
/**
 * 操作日志切面处理
 */
@Component
public class OptLogAspect {

    @Autowired
    private OperationLogDao operationLogDao;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(top.bravecoder.blog.annotation.OptLog)")
    public void optLogPointCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     * @param joinPoint 切入点
     * @param keys      返回结果
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    @AfterReturning(value = "optLogPointCut()", returning = "keys")
    public void saveOptLog(JoinPoint joinPoint, Object keys) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);
        OperationLog operationLog = new OperationLog();
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        // 获取操作
        Api api = (Api) signature.getDeclaringType().getAnnotation(Api.class);
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        OptLog optLog = method.getAnnotation(OptLog.class);
        // 操作模块
        operationLog.setOptModule(api.tags()[0]);
        // 操作类型
        operationLog.setOptType(optLog.optType());
        // 操作描述
        operationLog.setOptDesc(apiOperation.value());
        // 获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        // 获取请求的方法名
        String methodName = method.getName();
        methodName = className + "." + methodName;
        // 请求方式
        operationLog.setRequestMethod(Objects.requireNonNull(request).getMethod());
        // 请求方法
        operationLog.setOptMethod(methodName);
        // 请求参数
        operationLog.setRequestParam(JSON.toJSONString(joinPoint.getArgs()));
        // 返回结果
        operationLog.setResponseData(JSON.toJSONString(keys));
        // 请求用户ID
        operationLog.setUserId(UserUtil.getLoginUser().getId());
        // 请求用户
        operationLog.setNickname(UserUtil.getLoginUser().getNickname());
        // 请求IP
        String ipAddr = IpUtil.getIpAddr(request);
        operationLog.setIpAddr(ipAddr);
        operationLog.setIpSource(IpUtil.getIpSource(ipAddr));
        // 请求URL
        operationLog.setOptUrl(request.getRequestURI());
        // 创建时间
        operationLog.setCreateTime(new Date());
        operationLogDao.insert(operationLog);
    }
}
```

对需要的进行日志记录的方法上加上 `@OptLog  ` 注解，并通过 `OptTypeConst` 常量类指明对应的操作类型，日志切面类 `OptLogAspect` 会进行日志记录，读取方法上的注解，把数据封装成 `OperationLog` 对象，插入数据库。

#### 异常处理

**自定义异常类 exception.ServeException**

```java
/**
 * 自定义异常类
 */
public class ServeException extends RuntimeException {
    public ServeException(String message) {
        super(message);
    }
}

```

**返回状态码常量 constant.StatusConst** 

```java
package top.bravecoder.blog.constant;

/**
 * 返回码常量
 */
public class StatusConst {

    /**
     * 成功
     */
    public static final int OK = 20000;

    /**
     * 失败
     */
    public static final int ERROR = 20001;

    /**
     * 系统异常
     */
    public static final int SYSTEM_ERROR = 50000;

    /**
     * 未登录
     */
    public static final int NOT_LOGIN = 40001;

    /**
     * 没有操作权限
     */
    public static final int AUTHORIZED = 40003;
}
```

**全局异常处理类 controller.ControllerAdvice**

```java
/**
 * 全局异常处理
 */
@RestControllerAdvice
public class ControllerAdvice {

    /**
     * 处理服务异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = ServeException.class)
    public Result errorHandler(ServeException e) {
        return new Result(false, StatusConst.ERROR, e.getMessage());
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result errorHandler(MethodArgumentNotValidException e) {
        return new Result(false, StatusConst.ERROR, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception e) {
        return new Result(false, StatusConst.SYSTEM_ERROR, "系统异常");
    }
}
```

#### SpringScurity 权限控制

**SpringSecurity 配置类 config.WebSecurityConfig**

```java
/**
 * Security配置类
 */
@Configuration
// 开启security自定义配置
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailHandlerImpl authenticationFailHandler;
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    // 自定义登录校验接口
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public FilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new FilterInvocationSecurityMetadataSourceImpl();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new AccessDecisionManagerImpl();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 防用户重复登录
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 指定 userDetailsService
        auth.userDetailsService(userDetailsService)
                // 密码编码器
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 配置权限
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置登录注销路径
        http.formLogin()
                .loginProcessingUrl("/login")
                // 登录成功处理
                .successHandler(authenticationSuccessHandler)
                // 登陆失败处理
                .failureHandler(authenticationFailHandler).and()
                .logout().logoutUrl("/logout")
                // 退出登录处理
                .logoutSuccessHandler(logoutSuccessHandler);

        // 配置路由权限信息
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        // 设置自定义 FilterInvocationSecurityMetadataSource
                        fsi.setSecurityMetadataSource(securityMetadataSource());
                        // 设置自定义的 AccessDecisionManager
                        fsi.setAccessDecisionManager(accessDecisionManager());
                        return fsi;
                    }
                })
                .anyRequest().permitAll()
                .and()
                // 关闭跨站请求防护
                .csrf().disable().exceptionHandling()
                // 未登录处理
                .authenticationEntryPoint(authenticationEntryPoint)
                // 权限不足处理
                .accessDeniedHandler(accessDeniedHandler).and()
                // SpringSecurity使用X-Frame-Options防止网页被Frame，把x-frame-options disable
                .headers().frameOptions().disable()
                .and()
                // 开启session管理，session并发最多20个超出后，旧的session被注销，新的会注册，这种操作称为缺省实现。
                .sessionManagement()
                .maximumSessions(20)
                // sessionRegistry 用来统计在线用户
                .sessionRegistry(sessionRegistry());
    }
}
```

**用户登录信息，UserDetails 的实现类 UserInfoDTO**

```java
/*
 * 用户登录信息
 */
@Data
@Builder
public class UserInfoDTO implements UserDetails {
    /**
     * 用户账号id
     */
    private Integer id;

    /**
     * 用户信息id
     */
    private Integer userInfoId;

    /**
     * 邮箱号
     */
    private String email;

    /**
     * 登录方式
     */
    private Integer loginType;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户角色
     */
    private List<String> roleList;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户简介
     */
    private String intro;

    /**
     * 个人网站
     */
    private String webSite;

    /**
     * 点赞文章集合
     */
    private Set<Integer> articleLikeSet;

    /**
     * 点赞评论集合
     */
    private Set<Integer> commentLikeSet;

    /**
     * 用户登录ip
     */
    private String ipAddr;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;

    /**
     * 获取角色集合
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 账户是非过期的
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户账号是非被锁定的
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户密码是非过期的
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否可用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}

```

**自定义登录校验 UserDetailsServiceImpl**

```java
/**
 * 自定义 UserDetailsService，将用户信息和权限注入进来
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new ServeException("用户名不能为空！");
        }
        // 查询账号是否存在
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getId, UserAuth::getUserInfoId, UserAuth::getUsername, UserAuth::getPassword, UserAuth::getLoginType)
                .eq(UserAuth::getUsername, username));
        if (Objects.isNull(user)) {
            throw new ServeException("用户名不存在!");
        }
        // 查询账号信息
        UserInfo userInfo = userInfoDao.selectOne(new LambdaQueryWrapper<UserInfo>()
                .select(UserInfo::getId, UserInfo::getEmail, UserInfo::getNickname, UserInfo::getAvatar, UserInfo::getIntro, UserInfo::getWebSite, UserInfo::getIsDisable)
                .eq(UserInfo::getId, user.getUserInfoId()));
        // 查询账号对应的角色集合
        List<String> roleList = roleDao.listRolesByUserInfoId(userInfo.getId());
        // 查询账号点赞信息
        // 点赞的文章集合
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(ARTICLE_USER_LIKE).get(userInfo.getId().toString());
        // 点赞的评论集合
        Set<Integer> commentLikeSet = (Set<Integer>) redisTemplate.boundHashOps(COMMENT_USER_LIKE).get(userInfo.getId().toString());
        // 封装登录信息为 UserDetailsService，即 UserInfoDTO 对象
        return convertLoginUser(user, userInfo, roleList, articleLikeSet, commentLikeSet, request);
    }
}

------------------------------------------------------------------------
/**
 * 用户工具类
 */
public class UserUtil {

    /**
     * 获取当前登录用户
     * @return 用户登录信息
     */
    public static UserInfoDTO getLoginUser() {
        return (UserInfoDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 封装用户登录信息
     *
     * @param user           用户账号
     * @param userInfo       用户信息
     * @param articleLikeSet 点赞文章id集合
     * @param commentLikeSet 点赞评论id集合
     * @param request        请求
     * @return 用户登录信息
     */
    public static UserInfoDTO convertLoginUser(UserAuth user, UserInfo userInfo, List<String> roleList, Set<Integer> articleLikeSet, Set<Integer> commentLikeSet, HttpServletRequest request) {
        // 获取登录信息
        String ipAddr = IpUtil.getIpAddr(request);
        String ipSource = IpUtil.getIpSource(ipAddr);
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        // 封装权限集合
        return UserInfoDTO.builder()
                .id(user.getId())
                .loginType(user.getLoginType())
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(userInfo.getEmail())
                .roleList(roleList)
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .webSite(userInfo.getWebSite())
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .ipAddr(ipAddr)
                .ipSource(ipSource)
                .browser(userAgent.getBrowser().getName())
                .os(userAgent.getOperatingSystem().getName())
                .lastLoginTime(new Date())
                .build();
    }
}
```

**登录成功处理器 handler.AuthenticationSuccessHandlerImpl**

```java
/**
 * 登录成功处理，更新用户信息，返回登录用户信息
 */
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Autowired
    private UserAuthDao userAuthDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        // 更新用户ip，最近登录时间
        updateUserInfo();
        UserLoginDTO userLoginDTO = BeanCopyUtil.copyObject(UserUtil.getLoginUser(), UserLoginDTO.class);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<UserInfoDTO>(true, StatusConst.OK, "登录成功！", userLoginDTO)));
    }

    /**
     * 更新用户信息
     */
    @Async
    public void updateUserInfo() {
        UserAuth userAuth = UserAuth.builder()
                .id(UserUtil.getLoginUser().getId())
                .ipAddr(UserUtil.getLoginUser().getIpAddr())
                .ipSource(UserUtil.getLoginUser().getIpSource())
                .lastLoginTime(UserUtil.getLoginUser().getLastLoginTime())
                .build();
        userAuthDao.updateById(userAuth);
    }
}
```

**登录失败处理器 handler.AuthenticationFailHandlerImpl**

```java
/**
 * 登录失败处理，返回错误信息
 */
@Component
public class AuthenticationFailHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<>(false, StatusConst.ERROR, e.getMessage())));
    }
}
```

**退出成功处理器 handler.LogoutSuccessHandlerImpl**

```java
/**
 * 注销处理，返回成功信息
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result(true, StatusConst.OK,"注销成功")));
    }
}
```

**权限不足处理器（访问被拒绝）handler.AccessDeniedHandlerImpl**

```java
/**
 * 用户权限不足，返回没有操作权限的错误信息
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result(false, StatusConst.AUTHORIZED, "没有操作权限")));
    }
}
```

**用户未登录处理 hanlder.AuthenticationEntryPointImpl**

```java
/**
 * 用户未登录处理，返回未登录的提示信息
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<>(false, StatusConst.NOT_LOGIN, "请登录")));
    }
}
```

**自定义的 FilterInvocationSecurityMetadataSource**

```java
/**
 * 用来储存请求与权限的对应关系
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
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 修改接口角色关系后重新加载
        if (CollectionUtils.isEmpty(urlRoleList)) {
            this.loadDataSource();
        }
        FilterInvocation fi = (FilterInvocation) object;
        // 获取用户请求方式
        String method = fi.getRequest().getMethod();
        // 获取用户请求Url
        String url = fi.getRequest().getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        // 获取接口角色信息，若无对应角色则禁止
        for (UrlRoleDTO urlRoleDTO : urlRoleList) {
            // 如果 url、method都相同
            if (antPathMatcher.match(urlRoleDTO.getUrl(), url) && urlRoleDTO.getRequestMethod().equals(method)) {
                List<String> roleList = urlRoleDTO.getRoleList();
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

-------------------------------------------------------------------------------------------
/**
 * 资源与角色的关系类，即访问一个资源需要用户哪些角色
 * 这里的资源由 url 和 requestMethod 确定
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
```

**自定决策器 AccessDecisionManagerImpl**

```java
/**
 * 决策器，由AbstractSecurityInterceptor调用，负责鉴定用户是否有访问对应资源（方法或URL）的权限。
 */
@Component
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    /**
     * 通过传递的参数来决定用户是否有访问对应受保护对象的权限
     *
     * @param authentication 包含了当前的用户信息，包括拥有的权限。这里的权限来源就是前面登录时UserDetailsService中设置的 authorities。
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
        // 判断该用户是否用户本次访问需要的权限
        for (ConfigAttribute item : configAttributes) {
            if (permissionList.contains(item.getAttribute())) {
                return;
            }
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
```

#### Redis 使用场景

**Redis 配置类**

```java
package top.bravecoder.blog.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 * @author zhangzhi
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

**Redis常量**

```java
package top.bravecoder.blog.constant;

/**
 * redis常量
 */
public class RedisPrefixConst {

    /**
     * 验证码过期时间
     */
    public static final long CODE_EXPIRE_TIME = 15 * 60 * 1000;

    /**
     * 验证码
     */
    public static final String CODE_KEY = "code_";

    /**
     * 博客总浏览量
     */
    public static final String BLOG_VIEWS_COUNT = "blog_views_count";

    /**
     * 文章浏览量
     */
    public static final String ARTICLE_VIEWS_COUNT = "article_views_count";

    /**
     * 文章点赞量
     */
    public static final String ARTICLE_LIKE_COUNT = "article_like_count";

    /**
     * 用户点赞文章
     */
    public static final String ARTICLE_USER_LIKE = "article_user_like";

    /**
     * 评论点赞量
     */
    public static final String COMMENT_LIKE_COUNT = "comment_like_count";

    /**
     * 用户点赞评论
     */
    public static final String COMMENT_USER_LIKE = "comment_user_like";

    /**
     * 关于我信息
     */
    public static final String ABOUT = "about";

    /**
     * 公告
     */
    public static final String NOTICE = "notice";

    /**
     * ip集合
     */
    public static final String IP_SET = "ip_set";
}
```

+ 用户注册时，存放验证码，**string**

  ```java
  // 将验证码存入redis，设置过期时间为15分钟
  redisTemplate.boundValueOps(CODE_KEY + username).set(code);
  redisTemplate.expire(CODE_KEY + username, CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
  ```

+ 存放博客的总浏览量，**string**

   ```java
   // 判断当前ip是否访问，增加访问量
    String ipAddr = IpUtil.getIpAddr(request);
    if (!ipAddr.equals(ip)) {
      session.setAttribute("ip", ipAddr);
      // 博客总浏览量 +1
      redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).increment(1);
    }
   ```

+ 存放文章的浏览量，**hash**

  ```java
  // 判断是否第一次访问，增加浏览量
  Set<Integer> set = (Set<Integer>) session.getAttribute("articleSet");
  if (Objects.isNull(set)) {
      set = new HashSet<>();
  }
  if (!set.contains(articleId)) {
      set.add(articleId);
      session.setAttribute("articleSet", set);
      // 浏览量+1
      redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).increment(articleId.toString(), 1);
  }
  ```

+ 存放所有文章各自的点赞量和用户点赞的文章集合，**hash**

```java
 public void saveArticleLike(Integer articleId) {
        // 查询当前用户点赞过的文章id集合
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(ARTICLE_USER_LIKE).get(UserUtil.getLoginUser().getUserInfoId().toString());
        // 第一次点赞则创建
        if (CollectionUtils.isEmpty(articleLikeSet)) {
            articleLikeSet = new HashSet<>();
        }
        // 判断是否点赞
        if (articleLikeSet.contains(articleId)) {
            // 点过赞则删除文章id
            articleLikeSet.remove(articleId);
            // 文章点赞量-1
            redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).increment(articleId.toString(), -1);
        } else {
            // 未点赞则增加文章id
            articleLikeSet.add(articleId);
            // 文章点赞量+1
            redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).increment(articleId.toString(), 1);
        }
        // 保存用户的点赞记录
  redisTemplate.boundHashOps(ARTICLE_USER_LIKE).put(UserUtil.getLoginUser().getUserInfoId().toString(), articleLikeSet);
    }
```

+ 存放所有评论各自的点赞量和用户点赞的评论集合，与上面类似，**hash**

```java
 public void saveCommentLike(Integer commentId) {
        // 查询当前用户点赞过的评论id集合
        HashSet<Integer> commentLikeSet = (HashSet<Integer>) redisTemplate.boundHashOps(COMMENT_USER_LIKE).get(UserUtil.getLoginUser().getUserInfoId().toString());
        // 第一次点赞则创建
        if (CollectionUtils.isEmpty(commentLikeSet)) {
            commentLikeSet = new HashSet<>();
        }
        // 判断是否点赞
        if (commentLikeSet.contains(commentId)) {
            // 点过赞则删除评论id
            commentLikeSet.remove(commentId);
            // 评论点赞量-1
            redisTemplate.boundHashOps(COMMENT_LIKE_COUNT).increment(commentId.toString(), -1);
        } else {
            // 未点赞则增加评论id
            commentLikeSet.add(commentId);
            // 评论点赞量+1
            redisTemplate.boundHashOps(COMMENT_LIKE_COUNT).increment(commentId.toString(), 1);
        }
        // 保存点赞记录
  redisTemplate.boundHashOps(COMMENT_USER_LIKE).put(UserUtil.getLoginUser().getUserInfoId().toString(), commentLikeSet);
    }
```

+ 存放关于我、公告信息，**string**

  ```java
  public void updateAbout(String aboutContent) {
      redisTemplate.boundValueOps(ABOUT).set(aboutContent);
  }

  public void updateNotice(String notice) {
      redisTemplate.boundValueOps(NOTICE).set(notice);
  }

  ```

+ 存放ip，统计每日用户量，**set**

  暂时存储每天的ip集合，定时把当天的访问量存储到数据库，定时删除

  ```java

  /**
   * request监听
   */
  @Component
  public class ServletRequestListenerImpl implements ServletRequestListener {
      @Autowired
      private RedisTemplate redisTemplate;

      @Override
      public void requestInitialized(ServletRequestEvent sre) {
          HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
          HttpSession session = request.getSession();
          String ip = (String) session.getAttribute("ip");
          // 判断当前ip是否访问，增加访问量
          String ipAddr = IpUtil.getIpAddr(request);
          if (!ipAddr.equals(ip)) {
              session.setAttribute("ip", ipAddr);
              redisTemplate.boundValueOps(BLOG_VIEWS_COUNT).increment(1);
          }
          // 将ip存入redis，统计每日用户量
          redisTemplate.boundSetOps(IP_SET).add(ipAddr);
      }

      // 每天凌晨0点1分执行一次
      @Scheduled(cron = " 0 1 0 * * ?")
      private void clear() {
          // 清空redis中的ip
          redisTemplate.delete(IP_SET);
      }
  }

  -----------------------------------------------------------
  @Service
  public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewDao, UniqueView> implements UniqueViewService {
      @Autowired
      private RedisTemplate redisTemplate;
      @Autowired
      private UniqueViewDao uniqueViewDao;

      // 每天 0 点执行一次
      @Scheduled(cron = " 0 0 0 * * ?")
      @Override
      public void saveUniqueView() {
          // 获取每天用户量
          Long count = redisTemplate.boundSetOps("ip_set").size();
          // 获取昨天日期插入数据
          UniqueView uniqueView = UniqueView.builder()
                  .createTime(DateUtil.getSomeDay(new Date(), -1))
                  .viewsCount(Objects.nonNull(count) ? count.intValue() : 0).build();
          uniqueViewDao.insert(uniqueView);
      }
  }
  ```

#### RabbitMQ使用场景

**常量类**

```java
/**
 * MQ常量
 */
public class MQPrefixConst {

    /**
     * email交换机
     */
    public static final String EMAIL_EXCHANGE = "send";

    /**
     * 邮件队列
     */
    public static final String EMAIL_QUEUE = "email";
}
```

**RabbitMQ配置类**

```java
/**
 * Rabbitmq配置类
 */
@Configuration
public class RabbitConfig {

    // 创建队列
    @Bean
    public Queue emailQueue() {
        return new Queue(MQPrefixConst.EMAIL_QUEUE, true);
    }

    // 创建交换机
    @Bean
    public FanoutExchange emailExchange() {
        return new FanoutExchange(MQPrefixConst.EMAIL_EXCHANGE, true, false);
    }

    // 把队列和交换机绑定在一起
    @Bean
    public Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }
}
```

**消费者 EmailReceiver**

```java
/**
 * 监听EMAIL_QUEUE，发送邮件
 */
@Component
@RabbitListener(queues = MQPrefixConst.EMAIL_QUEUE)
public class EmailReceiver {
    /**
     * 邮箱号
     */
    @Value("${spring.mail.username}")
    private String email;

    @Autowired
    private JavaMailSender javaMailSender;

    @RabbitHandler
    public void process(byte[] data) {
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(emailDTO.getEmail());
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        javaMailSender.send(message);
    }
}
```

**场景一**

用户注册时，对邮箱发送验证码

```java
rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), 
                new MessageProperties()));
```

**场景二**

用户的评论收到回复时，发邮件进行通知

```java
/**
 * 通知评论用户
 */
@Async
public void notice(CommentVO commentVO) {
    // 判断是回复用户还是评论作者
    Integer userId = Objects.nonNull(commentVO.getReplyId()) ? commentVO.getReplyId() : BLOGGER_ID;
    // 查询邮箱号
    String email = userInfoDao.selectById(userId).getEmail();
    if (StringUtils.isNotBlank(email)) {
        // 判断页面路径
        String url = Objects.nonNull(commentVO.getArticleId()) ? URL + ARTICLE_PATH + commentVO.getArticleId() : URL + LINK_PATH;
        // 发送消息
        EmailDTO emailDTO = EmailDTO.builder()
            .email(email)
            .subject("评论提醒")
            .content("您收到了一条新的回复，请前往" + url + "\n页面查看")
            .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", 
                       new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
    }
}
```

