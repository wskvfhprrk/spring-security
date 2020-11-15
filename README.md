# spring-security
spring security原理学习
学习资料来源于Java Brains的youtube教学视频（Spring Security Basics）

## 第一节课：security到底干什么？

系统安全需要大量的思考

### 系统需要多层安全保障

spring security的真正意义所在是什么？

- 应用程序安全并不是个小事情。
- 系统安全通常是一个事后想法。
- 系统安全的潜在原因是用户的失望。
- 系统安全威胁不断发展的。

### 它是为了应用安全框架：

- 登陆和注销功能；
- 允许/阻止对登陆用户的url访问
- 允许/阻止对登陆用户和特定角色的url访问

### security特点是什么：

- 灵活可制定的

### 处理常见的漏洞：

- 会话固定
- 点周劫持
- 点击站点请求伪造

### 广泛采用：

- 黑客常见的目标
- 漏洞得到最多的关注和快速响应——导致长期漏洞减少

### spring security能干什么：

- 用户名/密码身份验证
- SSO/Okta/LDAP
- 应用程序授权
- 像oauth一样的内部授权
- 微服务安全（使用令牌，jwt)
- 方法级安全应用



## 第二节课：五种spring安全概念 

### 五个关键术语

authentication(身份验证)，authorization(授权)，Principal（原则）,Granted Authority(授预权力),Roles(角色)

## authentication（认证） vs authorization（授权）

什么是认证和授权

认证是这个用户是不是系统用户检验——身份的验证

- 手机/文本信息（手机比密码其它位置示例更难窃取）
- 基于身份密钥key验证
- 基于多种身份验证

**身份认证是解决你是谁的问题**

授权是认证用户可以访问什么的问题，**解决你到底能干什么**

principal(原则)——身份认证后会把其身份绑定到访问上下文中,不需要用户再登陆验证了，程序应该记住用户。

## 第三课 如何将spring security添加到spring boot中
[spring boot集成spring security](../spring-boot-starter-security/README.md)

## 第四课 怎么让spring security对多用户进行身份验证和验证

身份证验管理器——`AuthenticationManager`,在spring security应用程序中管理身份验证的功能，它如果验证了身份，并且返回成功，表示身份验证成功，如果发生异常，表示身份无法验证。

如果您使用认证管理器构建器配置的管理器构建器——`AuthenticationManageBuilder`。



比如内存、jdbc等方式，如何调用`AuthentiationManageBuilder`——配置方法并传入身份验证管理器构建器——扩展此类并覆盖默认配置configure的方法（以第四课例子为基础修改）。

- 删除`application.properties`的用户名和密码配置
- 创建`SecurityConfiguration`继承`WebSecurityConfigurerAdapter`:

```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * 使用自己的AuthenticationManagerBuilder去覆盖父类AuthenticationManagerBuilder
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里配置类型身份验证
        auth.inMemoryAuthentication()
                .withUser("blah").password("blah").roles("USER")
            //链式可以添加多用户
        .and()
        .withUser("foo").password("foo").roles("USER");
    }
}
```

- 建密码`PasswordEncoder`的bean，spirng security必须要配置的密码验证类:

```java
	@Bean
    public PasswordEncoder getPasswordEncoder(){
        //作为开发前期可以使用明密码，后期可以更改
        return NoOpPasswordEncoder.getInstance();
    }
```

## 第五课 如何配置spring security安全授权

**即配置授权的方式**

举本个api具有不同级别的访问控制

| API    | 角色授权              |
| ------ | --------------------- |
| /      | 所有人都可以访问      |
| /user  | USER和ADMIN角色可访问 |
| /admin | 仅ADMIN角色可访问     |

（在上节课项目基础之上，`HomeController`中添加两个api）

```java
	@GetMapping("/admin")
    private String admin(){
        return "<h1>欢迎 管理员</h1>";
    }
    @GetMapping("/user")
    private String user(){
        return "<h1>欢迎 用户</h1>";
    }
```

在`SecurityConfiguration`配置访问限制和路径`HttpSecurity`覆盖原来的HttpSecurity

```java
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .and().formLogin()
                .and().logout();
    }
```

修改`AuthenticationManageBuilder`认证用户

```java
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里配置类型身份验证
        auth.inMemoryAuthentication()
                .withUser("user").password("pass").roles("USER")
                //链式可以添加多用户
        .and()
        .withUser("admin").password("pass").roles("ADMIN");
    }
```

## 第六课 spring security 身份验证如何工作的

在`AuthenticationManager`管理身份验证的方法由多个是`AuthenticationProvider`,

`AuthenticationProvider`:输入`Authentication`(credentials),验证成功输出的是`Authentication`(Principal)

credentials——它具有凭证对象的凭证信息；

Principal——身份验证包含原理，当前身份的详细信息，还有用户经过验证的原则的权限，以便后续流程可以查询用户的权限，

isAuthenticated——身份验证是否成功(成功即为True,没有成功即为Flase）

身份验证方式支持:

- SSO
- LDAP
- Oauth
- 等等

支持一个应用程序多个身份验证,无论哪个提供商支持身份验证，提供商管理器都不会自行完成工作,要根据身份验证类型与所有这些不同的通知提供商进行协调,可以使用`providerManager`实现`AuthenticationManager`根据不同提供商的身份验证类型使用不同的实现方法。

当一个请求过来时，它会访问各个`providerManager`,最终找到自己可以验证的`AuthenticationManager`使用`credentials`对用户进行验证，验证成功`isAuthenticated`即为true,否则为null,成功的话并把身份信息和权限放入`Principal`

身份验证后个人信息统一使用`UserDetailsService`中的loadUserByUserName()来检索，如果认证成功，身份信息和权限的对象`UserDetails`放入`Principal`。

每个`providerManager`使用`supports()`方法处理身份验证至到身份验证成功。