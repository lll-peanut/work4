package com.peanut.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peanut.Dao.UserDao;
import com.peanut.POJO.User;
import com.peanut.expection.BusinessException;
import com.peanut.expection.SystemException;
import com.peanut.service.UserService;
import com.peanut.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> select() {
        return userDao.selectList(null);
    }

    @Override
    public int insert(User user) {
        if (userDao.insert(user) > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public User selectByUsernameANDPassword(String username, String password) {
        List<User> users = userDao.selectList(new QueryWrapper<User>().eq("username", username).eq("password", password));
        if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new BusinessException("帐号或密码错误，用户登录失败");
        }
    }

    @Override
    public void register(String username, String password) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        if (userDao.selectList(userQueryWrapper).size() > 0) {
            throw new BusinessException("用户帐号已注册");
        } else {
            password = passwordEncoder.encode(password);
            User user = new User(username, password);
            LocalDateTime time = LocalDateTime.now();
            user.setCreatedAt(time);
            user.setUpdatedAt(time);
            int insert = userDao.insert(user);
            if (insert <= 0) {
                throw new SystemException("系统异常，注册失败");
            }
        }
    }

    @Override
    public User getInfo(String id) {
        User user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException("该用户不存在");
        }
        return user;
    }

    /**
     * 作用： 上传用户头像
     * 主要功能： 验证文件是否存在合规，将头像保存在本地，并将路径插入到数据库中
     * @param file
     * @param user
     * @return
     */
    @Override
    public String uploadAvatar(MultipartFile file, User user) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不存在");
        }

        String originalFileName = file.getOriginalFilename().trim();
        boolean isAllowed = originalFileName.toLowerCase().endsWith(".jpg")
                || originalFileName.toLowerCase().endsWith(".jpeg")
                || originalFileName.toLowerCase().endsWith(".png")
                || originalFileName.toLowerCase().endsWith(".gif");

        if (!isAllowed) {
            throw new BusinessException("文件需要以图片形式上传");
        }

        String avatarPath = FileUtil.rootPath + File.separator + "file" + File.separator + "avatar" + File.separator;
        String FileName = UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf("."));
        String FilePath = avatarPath + FileName;
        File dest = new File(FilePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            user.setAvatarUrl(FilePath);
            int i = userDao.updateById(user);
            if (i <= 0) {
                throw new SystemException("插入失败");
            }
            file.transferTo(dest);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return FilePath;
    }

    @Override
    public User selectByUserName(String userName) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq("username", userName));
        if (user == null) {
            throw new BusinessException("用户名不存在");
        }
        return user;
    }

    @Override
    public void validateId(String userId) {
        if (userDao.selectById(userId) == null) {
            throw new BusinessException("User doesn't exist");
        }
    }
}
