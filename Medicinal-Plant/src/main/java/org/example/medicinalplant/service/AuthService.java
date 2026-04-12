package org.example.medicinalplant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.medicinalplant.common.R;
import org.example.medicinalplant.dto.LoginResponse;
import org.example.medicinalplant.entity.SysUser;
import org.example.medicinalplant.mapper.SysUserMapper;
import org.example.medicinalplant.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public R<LoginResponse> register(String username, String password, String nickname) {
        if (username == null || username.length() < 3) {
            return R.fail("用户名至少 3 个字符");
        }
        if (password == null || password.length() < 6) {
            return R.fail("密码至少 6 位");
        }
        Long c = userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (c != null && c > 0) {
            return R.fail("用户名已存在");
        }
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setNickname(nickname == null || nickname.isBlank() ? username : nickname);
        userMapper.insert(u);
        String token = jwtUtils.createToken(u.getId(), u.getUsername());
        return R.ok(new LoginResponse(token, u.getId(), u.getUsername(), u.getNickname()));
    }

    public R<LoginResponse> login(String username, String password) {
        SysUser u =
                userMapper.selectOne(
                        new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (u == null || !passwordEncoder.matches(password, u.getPasswordHash())) {
            return R.fail(401, "用户名或密码错误");
        }
        String token = jwtUtils.createToken(u.getId(), u.getUsername());
        return R.ok(new LoginResponse(token, u.getId(), u.getUsername(), u.getNickname()));
    }
}
