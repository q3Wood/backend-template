package com.acha.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表：user
 */
@Data // Lombok注解：自动生成get/set方法
@TableName(value = "user") // MyBatisPlus注解：指定对应的数据库表名
@Schema(description = "用户实体") // Swagger注解
public class User implements Serializable {

    @Schema(description = "id")
    @TableId(type = IdType.AUTO) // 指定主键是自增的
    private Long id;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "用户密码")
    private String userPassword;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "用户角色：user/admin")
    private String userRole;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "是否删除")
    @TableLogic // 逻辑删除注解：删除时不真删，只是把这个字段标为 1
    private Integer isDelete;

    @TableField(exist = false) // 表示这个字段在数据库里不存在
    private static final long serialVersionUID = 1L;
}