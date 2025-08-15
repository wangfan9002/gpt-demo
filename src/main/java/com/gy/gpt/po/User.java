package com.gy.gpt.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user") // 指定表名，如果表名与类名一致可以省略
public class User {
    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;
    private Integer userType;
    private String userName;
    private String unitName;
    private String password;
    private String email;
    private String mobile;
    private String tokenSecret;
    private Date createTime;
    private Date lastUpdateTime;
}