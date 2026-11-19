package com.jxy.mis.common;

/**
 * 系统角色。
 *
 * <p>与真实业务对应：
 * <ul>
 *   <li>ADMIN：系统管理员，拥有全部权限；</li>
 *   <li>TEACHER：教务/教师，可查看学生、录入与修改成绩；</li>
 *   <li>STUDENT：学生，只能查看自己的成绩与统计。</li>
 * </ul>
 */
public enum Role {

    ADMIN,
    TEACHER,
    STUDENT;

    /** 页面展示用中文名称 */
    public String label() {
        switch (this) {
            case ADMIN:
                return "系统管理员";
            case TEACHER:
                return "教务老师";
            case STUDENT:
                return "学生";
            default:
                return name();
        }
    }
}
