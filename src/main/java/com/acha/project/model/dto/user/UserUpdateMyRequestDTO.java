package com.acha.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateMyRequestDTO implements Serializable {

    /**
     * 用户昵称 (如果不传，则不修改)
     */
    private String userName;

    /**
     * 用户头像 (如果不传，则不修改)
     */
    private String userAvatar;

    // 其它字段如 gender, userProfile, email, phone 由于 User 实体中没有，已删除
}
