package com.peanut.security.service.Imp;

import com.peanut.POJO.User;
import com.peanut.security.LoginUser;
import com.peanut.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: peanut
 * @date: 2026/3/9
 * @version:1.0
 */
@Service
public class UserDetailService implements UserDetailsService {

    private final UserService userService;

    public UserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 示例：按你实际的角色/权限来源填充
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new LoginUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}