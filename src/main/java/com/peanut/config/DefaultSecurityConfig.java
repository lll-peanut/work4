package com.peanut.config;

import com.peanut.common.filter.JwtAuthenticationFilter;
import com.peanut.common.handler.LoginFailureHandler;
import com.peanut.common.handler.LoginSuccessHandler;
import com.peanut.security.service.Imp.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 配置springSecurity
 *
 * @author: peanut
 * @date: 2025/12/19
 * @version:1.0
 */
@EnableWebSecurity
@Configuration
public class DefaultSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Lazy
    @Autowired
    private UserDetailService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
    DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher(ApplicationEventPublisher delegate) {
        return new DefaultAuthenticationEventPublisher(delegate);
    }


    //需要放行的页面
    private static final String[] PERMIT_ALL_PATHS = {
            "/user/login",          // 实际登录校验
            "/user/register",       // 注册
            "/token/refresh",       // 令牌刷新
            "/user/info",           // 查看指定用户信息
            "/video/list",          // 根据 user_id 查看指定人的发布列表
            "/video/popular",       // 根据点击量（visit_count）获取排行榜数据
            "/video/search",        // 搜索指定关键字的视频
            "/like/list",           // 返回指定用户点赞的视频
            "/following/list",      // 根据 user_id 查看指定人的关注列表
            "/follower/list",       // 根据 user_id 查看指定人的粉丝列表
    };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 核心：配置登录行为，指定你的自定义 /login 页 并且放行页面
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // 测试环境简化，生产环境可开启
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                        .anyRequest().authenticated() // 其他所有接口需要登录
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/user/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":40122,\"msg\":\"请先登录\"}");
                        })
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("ROLE_");
    }

    @Bean
    static RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("""
                ROLE_ADMIN > ROLE_USER
                """);
        return roleHierarchy;
    }
}