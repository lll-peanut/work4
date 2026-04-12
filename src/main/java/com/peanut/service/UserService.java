package com.peanut.service;

import com.peanut.POJO.DTO.MFABindDTO;
import com.peanut.POJO.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    List<User> select();
    int insert(User user);
    User selectByUsernameANDPassword(String username, String password);
    void register(String username, String password);
    User getInfo(String id);
    String uploadAvatar(MultipartFile file, User user);

    /**
     * 通过用户名查找用户信息
     * @param userName
     * @return User
     */
    User selectByUserName(String userName);
    void validateId(String userId);

    void bindMfaSecret(String userId, MFABindDTO mfaBindDTO);

    String imageSearch(MultipartFile data, String userId);
}
