# 演示spring boot集成spring security
## 1、建立新项目，引用`web`和`spring security`两个maven包

```xml
	<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
        </dependency>
```



## 2、仅使用`HomeController`演示可以访问路径："/"

```java
@RestController
public class HomeController {
    @GetMapping("/")
    private String hello(){
        return "<h1>欢迎</h1>";
    }
}
```

当我们进行访问时不能访问，原因**被spring security进行安全拦截，只要加入spring security依赖包，它就开始执行系统安全工作。**

spring security是一系列filters(过滤器)组成，filter在java的sevelet中。sevelet过滤url后才能到达应用程序。

- spring security在没有任何配置下会默认添加一些东西，它为所有url添加强制性身份验证的。

- 它会添加一个登陆表单

- 登陆错误会被拦截

- 创建一个用户（用户名：user）和密码(项目启动时打印出来)访问

可以通过配置`application.properties`中设置用户名和密码：

```properties
#设置security密码
spring.security.user.name=foo
spring.security.user.password=pass
```

