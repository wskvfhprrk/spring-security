# spring-security-jwt

基于spring security加入jwt token验证

## 一、添加一个hello方法

目的，做为调用方法，只使用controller

## 二、添加security进行验证

添加maven依赖,在pom.xml中添加：

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```

`MyUserDetailsService`实现`UserDetailsService`做为`@Service`

```java
/**
 * UserDetailsService实现类
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //后期都需要从数据库中查出
        return new User("foo", "foo", new ArrayList<>());
    }

}
```

建`SecurityConfig`实现`WebSecurityConfigurerAdapter`适配器类：

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //使用明密码——实际中使用BCryptPasswordEncoder
        return NoOpPasswordEncoder.getInstance();
    }

}
```

## 三、使用jwt

### 1、加入maven依赖

```xml
<!--jwt依赖包-->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        <!--jsonwebtoken需要使用-->
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.1</version>
        </dependency>
```

### 2、添加`jwt`工具类

**注意要引用`jaxb-api`类**

```java
@Service
public class JwtUtil {

    private String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                //测试过期
//                .setExpiration(new Date(System.currentTimeMillis() + 10))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

### 3、添加获取jwt的token

在`helloController`中添加，需要注入`AuthenticationManager`——security验证管理器

```java
    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        //验证密码
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("密码错误", e);
        }
        //验证通过后生成jwt token
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
```

**重点说明：**

1、由security会拦截任何请求，需要把`/authenticate`请求不要拦截验证，在`SecurityConfig`加入：

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate").permitAll().
                anyRequest().authenticated();
    }
```

2、由于要`helloController`s 要注入`AuthenticationManager`，需要加载这个`Bean`,在`SecurityConfig` 中加入：

```java
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
```

现在可以使用postman工具使用post方法可以获取jwt的token值。

### 4、加入验证过滤器`JwtFilter`

```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //获取用户详细信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            //如果jwt的token验证通过的话，使用用户详情——userDetails，不仅仅使用username设置请求上下文
            if (jwtUtil.validateToken(jwt, userDetails)) {
                //如果它还没有过期，那么我要创建用户名密码,身份验证令牌，这是我们已经看到的默认令牌
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
           //usernamePasswordAuthenticationToken添加进上下文 
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}

```

**重要说明：**

`JwtFilter`需要添加到security过滤器链上，需要修改`SecurityConfig`中的方法

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate").permitAll().
                anyRequest().authenticated().and().
                exceptionHandling().and().sessionManagement()
                //验证时上下文不要设置httpSession
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //把jwtFilter加入过滤器链——置于UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

```

## 四、实际开发中所注意事项

1. 使用token的验证的头（是`Base`还是`Bearer`）可以配置；
2. `JwtUtil`中的`SECRET_KEY`可配置的；
3. token过期时间可以可配置；
4. `UserDetailsService`实现类`MyUserDetailsService`获取了真实的数据可以放在缓存中（key使用`username`）,提升系统效率；
5. token过期会报403错误：`403 Forbidden`如果需要，前端要配合添加刷新token地方法。

