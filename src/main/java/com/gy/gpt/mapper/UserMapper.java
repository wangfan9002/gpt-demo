package com.gy.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gy.gpt.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标识为MyBatis的Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承了BaseMapper，已经包含了基本的CRUD方法
}