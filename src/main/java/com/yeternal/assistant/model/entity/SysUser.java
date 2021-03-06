package com.yeternal.assistant.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yeternal.assistant.common.constants.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;
import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE;

/**
 * <p>
 * 系统用户实体类
 * </p>
 *
 * @author eternallove
 * @date Created in 2019/9/17 15:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = -8250490250545406986L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 加密使用的盐
     */
    private String salt;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 状态
     * {@link Constant#USER_DELETE}:逻辑删除
     * {@link Constant#USER_DISABLE}:禁用
     * {@link Constant#USER_ENABLE}:启用
     */
    private Integer status;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(fill = INSERT)
    private Date createTime;


    /**
     * 上次更新时间
     */
    @TableField(fill = INSERT_UPDATE)
    private Date updateTime;
}