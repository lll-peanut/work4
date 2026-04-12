package com.peanut.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peanut.dao.UserDao;
import com.peanut.POJO.entity.CustomUserDetails;
import com.peanut.POJO.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在：" + username);
        }
        Collection<? extends GrantedAuthority> authorities =
                org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles("DEFAULT") // 现在能正确识别 withRoles 方法
                        .build()
                        .getAuthorities();
        return new CustomUserDetails(
                user.getId(), // 你的用户 ID（从数据库查询的 User 实体中获取）
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
