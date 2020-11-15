package com.hejz.springbootstartersecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * 使用自己的AuthenticationManagerBuilder去覆盖父类AuthenticationManagerBuilder
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里配置类型身份验证
        auth.inMemoryAuthentication()
                .withUser("user").password("pass").roles("USER")
                //链式可以添加多用户
                .and()
                .withUser("admin").password("pass").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .and().formLogin()
                .and().logout();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        //作为开发前期可以使用明密码，后期可以更改
        return NoOpPasswordEncoder.getInstance();
    }
}
