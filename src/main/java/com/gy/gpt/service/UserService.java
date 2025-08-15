package com.gy.gpt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gy.gpt.po.User;

public interface UserService extends IService<User> {
    // 可以在这里添加自定义方法
    User regOrLogin(String mobile, String validCode, String unitName);
}
