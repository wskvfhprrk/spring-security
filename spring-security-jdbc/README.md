# spring security jdbc

## 导入依赖包web、spring secuity和jdbc api及mysql driver ，注意不要使用h2依赖

**在测试中按照h2方法测试，使用数据库默认测试都出现了问题，这是spring secruity框架的问题，测试的spring boot版本有2.1.17.RELEASE和2.3.4.RELEASE都存在有问题。故不使用h2数据库，直接使用jdbc的外数据库测试。**



## 2、复制之前的`HomeController`

要达到授权为：

| API    | 角色授权              |
| ------ | --------------------- |
| /      | 所有人都可以访问      |
| /user  | USER和ADMIN角色可访问 |
| /admin | 仅ADMIN角色可访问     |

3、spring security jdbc主要`SecurityConfigurer`：

```java
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
        .dataSource(dataSource)
        //使用h2数据库——由于h2表中有users，与其冲突不能建表，此方法不能使用
//        .withDefaultSchema()
//        .withUser(User.builder().username("user").password("pass").roles("USER"))
//        .withUser(User.builder().username("admin").password("pass").roles("ADMIN"))
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/user").hasAnyRole("USER","ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .and().formLogin();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
```

## 4、配置datasource配置

在`application.properties`中配置：

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/security
spring.datasource.username=root
spring.datasource.password=123456
```

## 5、建立数据库

脚本：`schema.sql`和`data.sql`,来自于spring官网

运行脚本文件`schema.sql`——建立数据库，运行`data.sql`——建立用户和权限信息

## 6、如果建立自定义数据需要修改`SecurityConfigurer`，需要加入根据username查询用户和权限的语句

```java
@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
        .dataSource(dataSource)
         //使用自定义数据库时需要加入根据username查询用户和权限的语句
        .authoritiesByUsernameQuery("SELECT username,password, enabled FROM users WHERE username=?")
        .groupAuthoritiesByUsername("SELECT username,authority FROM authorities WHERE username=?");
    }

```

