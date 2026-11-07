-- ============================================================
-- EMP 员工管理 MIS 系统 数据库初始化脚本
-- 数据库：ssm_emp_mis
-- 字符集：utf8mb4
-- 说明：脚本可重复执行，会先删除旧表再重建（仅用于课程实验环境）
-- ============================================================

CREATE DATABASE IF NOT EXISTS ssm_emp_mis
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE ssm_emp_mis;

-- 先删子表，再删主表，避免外键约束影响重复执行
DROP TABLE IF EXISTS emp;
DROP TABLE IF EXISTS dept;

-- ------------------------------------------------------------
-- 部门表：一个部门对应多名员工（一对多）
-- ------------------------------------------------------------
CREATE TABLE dept (
    dept_id     INT          PRIMARY KEY AUTO_INCREMENT COMMENT '部门编号',
    dept_no     VARCHAR(10)  NOT NULL COMMENT '部门编码',
    dept_name   VARCHAR(50)  NOT NULL COMMENT '部门名称',
    location    VARCHAR(80)  DEFAULT NULL COMMENT '办公地点',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT uk_dept_no UNIQUE (dept_no),
    CONSTRAINT uk_dept_name UNIQUE (dept_name)
) ENGINE = InnoDB COMMENT '部门表';

-- ------------------------------------------------------------
-- 员工表
-- ------------------------------------------------------------
CREATE TABLE emp (
    emp_id      INT           PRIMARY KEY AUTO_INCREMENT COMMENT '员工编号',
    emp_name    VARCHAR(50)   NOT NULL COMMENT '姓名',
    gender      CHAR(1)       NOT NULL DEFAULT '男' COMMENT '性别',
    dept        VARCHAR(50)   NOT NULL COMMENT '部门名称（关联 dept.dept_name）',
    post        VARCHAR(50)   NOT NULL COMMENT '岗位',
    salary      DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '薪资',
    hire_date   DATE          NOT NULL COMMENT '入职日期',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1在职 0离职',
    email       VARCHAR(100)  DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_emp_name (emp_name),
    KEY idx_emp_dept (dept),
    CONSTRAINT fk_emp_dept FOREIGN KEY (dept) REFERENCES dept (dept_name)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_emp_salary CHECK (salary >= 0),
    CONSTRAINT chk_emp_status CHECK (status IN (0, 1))
) ENGINE = InnoDB COMMENT '员工表';

-- ------------------------------------------------------------
-- 部门初始化数据
-- ------------------------------------------------------------
INSERT INTO dept (dept_no, dept_name, location) VALUES
('D01', '研发中心', '上海市浦东新区'),
('D02', '产品设计部', '杭州市西湖区'),
('D03', '市场营销部', '北京市朝阳区'),
('D04', '财务共享中心', '深圳市南山区'),
('D05', '人力资源部', '上海市徐汇区');

-- ------------------------------------------------------------
-- 员工初始化数据（与指导书示例数据不同）
-- ------------------------------------------------------------
INSERT INTO emp (emp_name, gender, dept, post, salary, hire_date, status, email, phone) VALUES
('陈亦航', '男', '研发中心',     'Java 架构师',   26500.00, '2019-03-18', 1, 'chenyihang@xinghai-tech.com',  '13800001001'),
('林书瑶', '女', '产品设计部',   '产品经理',       19800.00, '2020-07-06', 1, 'linshuyao@xinghai-tech.com',   '13800001002'),
('周子墨', '男', '研发中心',     '前端工程师',     17500.00, '2021-09-13', 1, 'zhouzimo@xinghai-tech.com',    '13800001003'),
('许清和', '女', '财务共享中心', '财务专员',        9800.00, '2022-04-25', 1, 'xuqinghe@xinghai-tech.com',    '13800001004'),
('苏晚晴', '女', '人力资源部',   '招聘主管',       13200.00, '2020-11-02', 1, 'suwanqing@xinghai-tech.com',   '13800001005'),
('顾言',   '男', '市场营销部',   '渠道经理',       15600.00, '2019-08-19', 1, 'guyan@xinghai-tech.com',       '13800001006'),
('沈知夏', '女', '研发中心',     '测试工程师',     14500.00, '2022-02-14', 1, 'shenzhixia@xinghai-tech.com',  '13800001007'),
('韩沐辰', '男', '研发中心',     '运维工程师',     16200.00, '2021-06-07', 0, 'hanmuchen@xinghai-tech.com',   '13800001008'),
('唐以宁', '女', '市场营销部',   '品牌策划',       11800.00, '2023-03-21', 1, 'tangyining@xinghai-tech.com',  '13800001009'),
('江照野', '男', '产品设计部',   'UI 设计师',      12800.00, '2023-10-09', 1, 'jiangzhaoye@xinghai-tech.com', '13800001010'),
('温屿',   '女', '财务共享中心', '审计专员',       10500.00, '2021-12-16', 0, 'wenyu@xinghai-tech.com',        '13800001011'),
('陆星阑', '男', '人力资源部',   '培训专员',        9600.00, '2024-05-27', 1, 'luxinglan@xinghai-tech.com',   '13800001012');

-- ------------------------------------------------------------
-- 验证数据
-- ------------------------------------------------------------
SELECT d.dept_no, d.dept_name, d.location, COUNT(e.emp_id) AS emp_count
FROM dept d
LEFT JOIN emp e ON e.dept = d.dept_name
GROUP BY d.dept_id, d.dept_no, d.dept_name, d.location
ORDER BY d.dept_no;
