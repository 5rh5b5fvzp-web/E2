-- ============================================================
-- 实验二：学生成绩管理 MIS 系统 数据库初始化脚本
-- 项目：E2
-- 数据库：ssm_e2_mis
-- 表：t_class / t_student / t_course / t_score / t_admin
--
-- 执行方式（PowerShell）：
--   Get-Content -Raw -Encoding UTF8 .\sql\init.sql |
--     mysql --default-character-set=utf8mb4 -h127.0.0.1 -uroot -p
-- ============================================================
DROP DATABASE IF EXISTS ssm_e2_mis;
CREATE DATABASE ssm_e2_mis DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssm_e2_mis;

-- ------------------------------------------------------------
-- 1. 班级表
-- ------------------------------------------------------------
CREATE TABLE t_class (
    class_id     INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name   VARCHAR(30) NOT NULL COMMENT '班级名称',
    head_teacher VARCHAR(50) COMMENT '班主任',
    classroom    VARCHAR(30) COMMENT '固定教室'
) COMMENT='班级信息表';

INSERT INTO t_class (class_name, head_teacher, classroom) VALUES
('软工2401', '张老师', '教A201'),
('计科2401', '李老师', '教B302'),
('人工智能2401', '王老师', '教C405'),
('数据科学2401', '赵老师', '教D107'),
('软工2402', '钱老师', '教A202'),
('网络工程2401', '孙老师', '教B401');

-- ------------------------------------------------------------
-- 2. 学生表（实验二主业务表）
-- ------------------------------------------------------------
CREATE TABLE t_student (
    stu_id      INT PRIMARY KEY AUTO_INCREMENT COMMENT '学生主键ID',
    stu_no      VARCHAR(20)  NOT NULL COMMENT '学号',
    stu_name    VARCHAR(50)  NOT NULL COMMENT '姓名',
    gender      CHAR(1)      DEFAULT '男' COMMENT '性别：男/女',
    phone       VARCHAR(20)  COMMENT '手机号',
    email       VARCHAR(100) COMMENT '邮箱',
    class_id    INT          NOT NULL COMMENT '所属班级ID',
    enroll_date DATE         COMMENT '入学日期',
    status      TINYINT      DEFAULT 1 COMMENT '状态：1在读 0休学',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_no (stu_no),
    KEY idx_student_name (stu_name),
    KEY idx_student_class (class_id),
    CONSTRAINT fk_student_class FOREIGN KEY (class_id) REFERENCES t_class (class_id)
) COMMENT='学生信息表';

INSERT INTO t_student (stu_no, stu_name, gender, phone, email, class_id, enroll_date, status) VALUES
('2024010201', '张小明', '男', '13800000001', 'zhangxm@example.com', 1, '2024-09-01', 1),
('2024010202', '李思思', '女', '13800000002', 'lisisi@example.com', 2, '2024-09-01', 1),
('2024010203', '王大力', '男', '13800000003', 'wangdl@example.com', 1, '2023-09-01', 1),
('2024010204', '赵小雪', '女', '13800000004', 'zhaoxx@example.com', 3, '2024-09-01', 0),
('2024010205', '陈浩然', '男', '13800000005', 'chenhr@example.com', 4, '2024-09-01', 1),
('2024010206', '刘雨桐', '女', '13800000006', 'liuyt@example.com', 2, '2024-09-01', 1),
('2024010207', '孙一鸣', '男', '13800000007', 'sunym@example.com', 3, '2024-09-01', 1),
('2024010208', '周可欣', '女', '13800000008', 'zhoukx@example.com', 4, '2023-09-01', 1),
('2024010209', '吴俊杰', '男', '13800000009', 'wujj@example.com', 1, '2024-09-01', 0),
('2024010210', '郑雅雯', '女', '13800000010', 'zhengyw@example.com', 2, '2024-09-01', 1),
('2024010211', '何俊熙', '男', '13800000011', 'hejx@example.com', 1, '2024-09-01', 1),
('2024010212', '冯若曦', '女', '13800000012', 'fengrx@example.com', 2, '2024-09-01', 1),
('2024010213', '邓子涵', '男', '13800000013', 'dengzh@example.com', 3, '2024-09-01', 1),
('2024010214', '许安然', '女', '13800000014', 'xuar@example.com', 4, '2024-09-01', 1),
('2024010215', '马思远', '男', '13800000015', 'masy@example.com', 5, '2024-09-01', 1),
('2024010216', '高欣怡', '女', '13800000016', 'gaoxy@example.com', 6, '2024-09-01', 1),
('2024010217', '罗嘉豪', '男', '13800000017', 'luojh@example.com', 5, '2024-09-01', 1),
('2024010218', '谢婉清', '女', '13800000018', 'xiewq@example.com', 6, '2024-09-01', 1),
('2024010219', '唐博文', '男', '13800000019', 'tangbw@example.com', 1, '2023-09-01', 0),
('2024010220', '韩雪晴', '女', '13800000020', 'hanxq@example.com', 2, '2024-09-01', 1);

-- ------------------------------------------------------------
-- 3. 课程表
-- ------------------------------------------------------------
CREATE TABLE t_course (
    course_id   INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(50) NOT NULL COMMENT '课程名称',
    credit      DECIMAL(3,1) COMMENT '学分',
    teacher     VARCHAR(50) COMMENT '授课教师'
) COMMENT='课程信息表';

INSERT INTO t_course (course_name, credit, teacher) VALUES
('Java程序设计', 3.5, '张老师'),
('数据库原理', 3.0, '李老师'),
('数据结构', 4.0, '王老师'),
('操作系统', 3.5, '赵老师'),
('软件工程导论', 2.0, '刘老师'),
('计算机网络', 3.0, '陈老师'),
('软件测试', 2.5, '周老师');

-- ------------------------------------------------------------
-- 4. 成绩表（扩展模块）
-- ------------------------------------------------------------
CREATE TABLE t_score (
    score_id    INT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    stu_id      INT          NOT NULL COMMENT '学生ID',
    course_id   INT          NOT NULL COMMENT '课程ID',
    term        VARCHAR(20)  NOT NULL COMMENT '学期，如 2025-2026-1',
    score       DECIMAL(5,2) COMMENT '成绩（0-100）',
    exam_date   DATE         COMMENT '考试日期',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    UNIQUE KEY uk_score (stu_id, course_id, term),
    KEY idx_score_student (stu_id),
    KEY idx_score_course (course_id),
    CONSTRAINT fk_score_student FOREIGN KEY (stu_id) REFERENCES t_student (stu_id),
    CONSTRAINT fk_score_course FOREIGN KEY (course_id) REFERENCES t_course (course_id)
) COMMENT='学生成绩表';

INSERT INTO t_score (stu_id, course_id, term, score, exam_date) VALUES
(1, 1, '2025-2026-1', 92.00, '2026-01-10'),
(1, 2, '2025-2026-1', 85.00, '2026-01-12'),
(1, 3, '2025-2026-2', 88.00, '2026-06-20'),
(2, 2, '2025-2026-1', 95.00, '2026-01-12'),
(2, 4, '2025-2026-2', 79.00, '2026-06-22'),
(3, 1, '2025-2026-1', 76.00, '2026-01-10'),
(3, 5, '2025-2026-2', 82.00, '2026-06-18'),
(4, 1, '2025-2026-1', 68.00, '2026-01-10'),
(5, 3, '2025-2026-1', 90.00, '2026-01-15'),
(5, 5, '2025-2026-2', 87.00, '2026-06-18'),
(6, 2, '2025-2026-1', 91.00, '2026-01-12'),
(7, 3, '2025-2026-2', 84.50, '2026-06-20'),
(8, 4, '2025-2026-1', 88.00, '2026-01-18'),
(9, 1, '2025-2026-1', 59.50, '2026-01-10'),
(10, 2, '2025-2026-2', 93.00, '2026-06-22'),
(11, 1, '2025-2026-1', 87.00, '2026-01-10'),
(11, 2, '2025-2026-1', 90.00, '2026-01-12'),
(11, 6, '2025-2026-2', 83.00, '2026-06-24'),
(12, 2, '2025-2026-1', 78.00, '2026-01-12'),
(12, 4, '2025-2026-2', 85.00, '2026-06-22'),
(13, 3, '2025-2026-1', 92.00, '2026-01-15'),
(13, 5, '2025-2026-2', 88.00, '2026-06-18'),
(14, 1, '2025-2026-1', 66.00, '2026-01-10'),
(14, 7, '2025-2026-2', 74.00, '2026-06-26'),
(15, 6, '2025-2026-1', 81.00, '2026-01-20'),
(15, 2, '2025-2026-2', 89.00, '2026-06-22'),
(16, 7, '2025-2026-1', 95.00, '2026-01-22'),
(16, 4, '2025-2026-2', 80.00, '2026-06-22'),
(17, 3, '2025-2026-1', 58.00, '2026-01-15'),
(17, 1, '2025-2026-2', 72.00, '2026-06-20'),
(18, 5, '2025-2026-1', 86.00, '2026-01-18'),
(18, 6, '2025-2026-2', 91.00, '2026-06-24'),
(19, 2, '2025-2026-2', 77.00, '2026-06-22'),
(20, 7, '2025-2026-1', 94.00, '2026-01-22'),
(20, 3, '2025-2026-2', 87.00, '2026-06-20'),
(2, 6, '2025-2026-2', 90.00, '2026-06-24'),
(6, 7, '2025-2026-1', 88.00, '2026-01-22'),
(8, 1, '2025-2026-2', 84.00, '2026-06-20');

-- ------------------------------------------------------------
-- 5. 系统用户表
--    密码存储：SHA-256(明文 + ":" + 用户名)
--    角色：ADMIN 管理员 / TEACHER 教务 / STUDENT 学生
--    root、teacher、student01、student02 密码均为 123456
-- ------------------------------------------------------------
CREATE TABLE t_admin (
    admin_id  INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username  VARCHAR(50) NOT NULL COMMENT '登录账号',
    password  VARCHAR(64) NOT NULL COMMENT 'SHA-256 密码摘要',
    real_name VARCHAR(50) COMMENT '姓名',
    status    TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    role      VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色：ADMIN/TEACHER/STUDENT',
    stu_id    INT NULL COMMENT '学生账号关联的学生ID（仅 STUDENT 使用）',
    UNIQUE KEY uk_admin_username (username),
    KEY idx_admin_role (role)
) COMMENT='系统用户表';

INSERT INTO t_admin (username, password, real_name, status, role, stu_id) VALUES
('root', '3813cc6b6f416c1a7435857359338cd23e02fd42e332c43aa7ab6403b4c6b90e', '系统管理员', 1, 'ADMIN', NULL),
('teacher', '4908e6a9292cce9d125694351a442751fcb21b90a53999b594260f2f1a79c0bc', '教务老师', 1, 'TEACHER', NULL),
('student01', '43473628dd9a2e73917a88ba6891b332f099df7041eb9b661207a757a11e21e8', '张小明', 1, 'STUDENT', 1),
('student02', '3834786625d7aff7e207be6b76df53e1d95e0938b387aec491db0e141ed615d7', '李思思', 1, 'STUDENT', 2);
