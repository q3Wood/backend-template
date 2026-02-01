package com.acha.project.mapper;

import com.acha.project.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户数据库操作接口
 * 继承 BaseMapper 后，自动拥有增删改查能力
 */

public interface UserMapper extends BaseMapper<User> {
    // 没错，里面什么都不用写！
    // MyBatis Plus 会自动帮你生成 sql 语句
}