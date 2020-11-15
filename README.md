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
[spring boot集成spring security](spring-boot-starter-security/README.md)

[3、spring-security-jwt](spring-security-jwt/README.md)


