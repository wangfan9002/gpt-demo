package com.gy.gpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gy.gpt.exception.BusinessException;
import com.gy.gpt.exception.ErrorCode;
import com.gy.gpt.mapper.UserMapper;
import com.gy.gpt.po.User;
import com.gy.gpt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 继承了ServiceImpl，已经实现了IService接口的所有方法

    @Override
    public User regOrLogin(String mobile, String validCode, String unitName) {
        log.info("mobile:{},code:{},unitName:{}", mobile, validCode, unitName);
        User user = this.lambdaQuery().eq(User::getMobile, mobile).one();
        if (user == null) {
            if (StringUtils.isEmpty(unitName)) {
                throw new BusinessException(ErrorCode.UNIT_NAME_NOT_EMPTY);
            }
            User regUser = new User();
            regUser.setMobile(mobile);
            regUser.setUnitName(unitName);
            regUser.setPassword(UUID.randomUUID().toString());
            regUser.setTokenSecret(validCode);
            this.save(regUser);
            return regUser;
        }

        if (StringUtils.isNotEmpty(unitName)) {
            user.setUnitName(unitName);
            this.updateById(user);
        }
        return user;
    }
}