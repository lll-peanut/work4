package com.peanut.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peanut.dao.ImageDao;
import com.peanut.dao.UserDao;
import com.peanut.POJO.DTO.MFABindDTO;
import com.peanut.POJO.User;
import com.peanut.expection.BusinessException;
import com.peanut.expection.SystemException;
import com.peanut.service.UserService;
import com.peanut.utils.MFATOTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ImageDao imageDao;

    @Value("${file.avator.upload.path}")
    private String avatorPath;

    @Value("${base.path}")
    private String SystemPath;

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
     *
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

        String avatarPath = SystemPath + avatorPath;
        String FileName = UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf("."));
        String FilePath = avatarPath + File.separator + FileName;
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

    @Override
    public void bindMfaSecret(String userId, MFABindDTO mfaBindDTO) {
        String code = mfaBindDTO.getCode();
        String secret = mfaBindDTO.getSecret();
        String redisKey = "mfa:bind:secret:" + userId;

        // 1) 从 Redis 取出绑定期 secret（获取二维码时写入）
        String cachedSecret = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedSecret == null || cachedSecret.isBlank()) {
            throw new BusinessException("MFA 绑定已过期，请重新获取二维码");
        }

        // 3) 用自写 TOTP 工具校验 code（允许时间窗容错由 Util 决定）
        boolean ok = MFATOTPUtil.verifyCode(cachedSecret, code);
        if (!ok) {
            throw new BusinessException("验证码错误");
        }

        // 4) 绑定：把 secret 入库
        userDao.updateMFASecret(userId, secret);

        // 5) 删除 Redis 绑定期 secret，避免重放
        stringRedisTemplate.delete(redisKey);
    }

    @Override
    public String imageSearch(MultipartFile data, String userId) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("图片数据不能为空");
        }

        String md5 = md5Hex(data);

        // 这里示例：按 hash 精确匹配一条图片 URL
        // 你也可以改成：返回相似度最高的一条、或返回列表
        String url = imageDao.findUrlByMd5(md5);
        if (url == null || url.isBlank()) {
            // 没搜到的兜底策略：返回空或固定提示图，看你接口约定
            return "";
        }
        return url;
    }

    private static String md5Hex(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) {
                md.update(buf, 0, n);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算图片特征失败", e);
        }
    }
}
