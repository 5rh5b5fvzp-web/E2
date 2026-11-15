package com.jxy.mis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 系统用户实体，对应 t_admin 表。
 * 密码只保存 SHA-256 摘要，返回前端时忽略 password 字段。
 */
@TableName("t_admin")
public class Admin {

    @TableId(value = "admin_id", type = IdType.AUTO)
    private Integer adminId;

    private String username;

    @JsonIgnore
    private String password;

    private String realName;

    private Integer status;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", status=" + status +
                '}';
    }
}
