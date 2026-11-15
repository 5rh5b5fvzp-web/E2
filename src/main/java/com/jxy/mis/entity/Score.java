package com.jxy.mis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成绩实体，对应 t_score 表（实验二扩展模块）。
 */
@TableName("t_score")
public class Score {

    @TableId(value = "score_id", type = IdType.AUTO)
    private Integer scoreId;

    @NotNull(message = "学生不能为空")
    private Integer stuId;

    @NotNull(message = "课程不能为空")
    private Integer courseId;

    @NotBlank(message = "学期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{4}-[12]$", message = "学期格式应为 2025-2026-1")
    private String term;

    @NotNull(message = "成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能小于 0")
    @DecimalMax(value = "100.00", message = "成绩不能大于 100")
    private BigDecimal score;

    @PastOrPresent(message = "考试日期不能晚于今天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate examDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 以下为联表查询带出的展示字段，不是 t_score 表字段 */
    @TableField(exist = false)
    private String stuNo;

    @TableField(exist = false)
    private String stuName;

    @TableField(exist = false)
    private String className;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private BigDecimal credit;

    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "Score{" +
                "scoreId=" + scoreId +
                ", stuId=" + stuId +
                ", courseId=" + courseId +
                ", term='" + term + '\'' +
                ", score=" + score +
                ", examDate=" + examDate +
                '}';
    }
}
