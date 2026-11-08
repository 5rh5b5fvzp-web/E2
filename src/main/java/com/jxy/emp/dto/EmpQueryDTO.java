package com.jxy.emp.dto;

/**
 * 员工列表查询条件：姓名模糊、性别、部门、在职状态。
 */
public class EmpQueryDTO {

    private String name;
    private String gender;
    private String dept;
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
