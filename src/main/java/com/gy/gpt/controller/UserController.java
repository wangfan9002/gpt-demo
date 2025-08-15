package com.gy.gpt.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gy.gpt.api.LoginRequest;
import com.gy.gpt.api.LoginResponse;
import com.gy.gpt.common.Result;
import com.gy.gpt.po.User;
import com.gy.gpt.service.UserService;
import com.gy.gpt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "注册用户")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest user, HttpServletResponse response) {
        log.info("login:{}", user);
        User regUser = userService.regOrLogin(user.getMobile(), user.getValidCode(), user.getUnitName());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(regUser.getId());
        String account = StringUtils.isNotEmpty(regUser.getUnitName()) ? regUser.getUnitName() + "-" : "";
        account = account + user.getMobile();
        loginResponse.setAccount(account);

        // 生成JWT
        String token = jwtUtil.generateToken(regUser.getMobile(), regUser.getTokenSecret());
        // 创建Cookie
        Cookie cookie = new Cookie(this.jwtUtil.getCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(this.jwtUtil.isCookieSecure());
        cookie.setPath("/");
        cookie.setMaxAge(jwtUtil.getExpiration().intValue());
        if (this.jwtUtil.getCookieDomain() != null && !this.jwtUtil.getCookieDomain().isEmpty()) {
            cookie.setDomain(this.jwtUtil.getCookieDomain());
        }

        // 添加Cookie到响应
        response.addCookie(cookie);

        return Result.success(loginResponse);
    }

    // 新增用户
    @PostMapping("/save")
    public boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    // 更新用户
    @PostMapping("/update")
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return userService.removeById(id);
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // 查询所有用户
    @RequestMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    // 分页查询
    @GetMapping("/page")
    public Page<User> page(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.page(new Page<>(pageNum, pageSize));
    }

    // 条件查询
    @GetMapping("/search")
    public List<User> search(@RequestParam String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", keyword)
                .or()
                .like("email", keyword);
        return userService.list(queryWrapper);
    }
}
