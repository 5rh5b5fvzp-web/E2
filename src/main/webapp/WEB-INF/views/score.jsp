<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>成绩管理 | 学生成绩管理 MIS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/mis.css">
</head>
<body>
<div class="topbar">
    <div class="brand">学生成绩管理 MIS<small>SSM 全注解整合 · 实验二</small></div>
    <nav>
        <a id="navStudents" href="${pageContext.request.contextPath}/students">学生管理</a>
        <a class="active" href="${pageContext.request.contextPath}/scores">成绩管理</a>
    </nav>
    <div class="user">
        <span id="userName">加载中...</span>
        <button id="logoutBtn" type="button">退出登录</button>
    </div>
</div>

<div class="container">
    <!-- 查询区 -->
    <div class="card" id="searchCard">
        <div class="card-title"><span>成绩查询</span></div>
        <form id="searchForm" class="search-form" autocomplete="off">
            <div class="field">
                <label for="qStuNo">学号</label>
                <input id="qStuNo" type="text" placeholder="支持模糊查询">
            </div>
            <div class="field">
                <label for="qStuName">姓名</label>
                <input id="qStuName" type="text" placeholder="支持模糊查询">
            </div>
            <div class="field">
                <label for="qCourseId">课程</label>
                <select id="qCourseId"><option value="">全部</option></select>
            </div>
            <div class="field">
                <label for="qTerm">学期</label>
                <input id="qTerm" type="text" placeholder="如 2025-2026-1">
            </div>
            <button class="btn btn-primary" type="submit">查 询</button>
            <button class="btn btn-default" type="button" id="resetBtn">重 置</button>
        </form>
    </div>

    <!-- 列表区 -->
    <div class="card">
        <div class="table-toolbar">
            <button class="btn btn-primary" type="button" id="addBtn">+ 录入成绩</button>
            <button class="btn btn-danger" type="button" id="batchDeleteBtn">批量删除</button>
            <div class="spacer"></div>
            <div class="total" id="totalText">共 0 条</div>
        </div>
        <div class="stat-box" id="statBox"></div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th style="width:38px"><input type="checkbox" id="checkAll" title="全选本页"></th>
                    <th>学号</th>
                    <th>姓名</th>
                    <th>班级</th>
                    <th>课程</th>
                    <th>学分</th>
                    <th>学期</th>
                    <th>成绩</th>
                    <th>考试日期</th>
                    <th style="width:150px">操作</th>
                </tr>
                </thead>
                <tbody id="tbody"></tbody>
            </table>
        </div>
        <div class="pagination" id="pagination"></div>
    </div>
</div>

<!-- 录入 / 编辑弹窗 -->
<div class="modal-mask" id="modal">
    <div class="modal">
        <div class="modal-header">
            <span id="modalTitle">录入成绩</span>
            <button class="close" type="button" id="modalClose">&times;</button>
        </div>
        <form id="scoreForm" autocomplete="off">
            <div class="modal-body">
                <div class="field">
                    <label for="fStuId">学生 *</label>
                    <select id="fStuId"></select>
                </div>
                <div class="field">
                    <label for="fCourseId">课程 *</label>
                    <select id="fCourseId"></select>
                </div>
                <div class="field">
                    <label for="fTerm">学期 *（如 2025-2026-1）</label>
                    <input id="fTerm" type="text" maxlength="20" placeholder="2025-2026-1">
                </div>
                <div class="field">
                    <label for="fScore">成绩 *（0-100）</label>
                    <input id="fScore" type="number" min="0" max="100" step="0.5">
                </div>
                <div class="field full">
                    <label for="fExamDate">考试日期</label>
                    <input id="fExamDate" type="date">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-default" type="button" id="cancelBtn">取 消</button>
                <button class="btn btn-primary" type="submit" id="saveBtn">保 存</button>
            </div>
        </form>
    </div>
</div>

<div id="toast"></div>

<script>
    var CTX = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/static/js/common.js"></script>
<script>
    (function () {
        var state = {
            page: 1,
            size: 5,
            total: 0,
            records: [],
            selected: new Set(),
            editingId: null,
            students: [],
            courses: [],
            role: '',
            stuId: null,
            canEdit: false,
            canDelete: false
        };

        function $(id) {
            return document.getElementById(id);
        }

        function value(id) {
            return $(id).value;
        }

        function loadMe() {
            return api('/api/auth/me').then(function (admin) {
                var roleLabels = {ADMIN: '系统管理员', TEACHER: '教务老师', STUDENT: '学生'};
                $('userName').textContent = (admin.realName || admin.username)
                    + ' · ' + (roleLabels[admin.role] || admin.role);
                state.role = admin.role;
                state.stuId = admin.stuId;
                state.canEdit = admin.role === 'ADMIN' || admin.role === 'TEACHER';
                state.canDelete = admin.role === 'ADMIN';

                if (admin.role === 'STUDENT') {
                    $('navStudents').style.display = 'none';
                    $('addBtn').style.display = 'none';
                    $('batchDeleteBtn').style.display = 'none';
                    $('searchCard').style.display = 'none';
                    $('checkAll').style.display = 'none';
                    if (admin.stuId) {
                        showStat(admin.stuId);
                    }
                } else if (admin.role === 'TEACHER') {
                    $('batchDeleteBtn').style.display = 'none';
                }
            }).catch(function () {
                /* 401 已统一跳转 */
            });
        }

        function loadOptions() {
            var studentsPromise = state.role === 'STUDENT'
                ? Promise.resolve()
                : api('/api/students?page=1&size=100').then(function (page) {
                    state.students = page.records || [];
                });
            return Promise.all([
                studentsPromise,
                api('/api/courses').then(function (list) {
                    state.courses = list || [];
                })
            ]).then(function () {
                var courseSearch = '<option value="">全部</option>';
                var courseForm = '';
                state.courses.forEach(function (course) {
                    var option = '<option value="' + course.courseId + '">'
                        + escapeHtml(course.courseName) + '</option>';
                    courseSearch += option;
                    courseForm += option;
                });
                $('qCourseId').innerHTML = courseSearch;
                $('fCourseId').innerHTML = courseForm;

                var studentForm = '';
                state.students.forEach(function (student) {
                    studentForm += '<option value="' + student.stuId + '">'
                        + escapeHtml(student.stuNo) + ' ' + escapeHtml(student.stuName) + '</option>';
                });
                $('fStuId').innerHTML = studentForm;
            });
        }

        function loadScores() {
            var query = buildQuery({
                stuNo: value('qStuNo').trim(),
                stuName: value('qStuName').trim(),
                courseId: value('qCourseId'),
                term: value('qTerm').trim(),
                page: state.page,
                size: state.size
            });
            api('/api/scores?' + query).then(function (page) {
                state.records = page.records || [];
                state.total = page.total || 0;
                state.selected.clear();
                renderRows();
                renderPagination();
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function scoreClass(score) {
            if (score === null || score === undefined) {
                return '';
            }
            if (Number(score) >= 90) {
                return 'score-high';
            }
            if (Number(score) < 60) {
                return 'score-low';
            }
            return '';
        }

        function renderRows() {
            var tbody = $('tbody');
            if (!state.records.length) {
                tbody.innerHTML = '<tr><td class="empty" colspan="10">暂无数据</td></tr>';
                $('totalText').textContent = '共 0 条';
                return;
            }
            var html = '';
            state.records.forEach(function (row) {
                var actions = '';
                if (state.canEdit) {
                    actions += '<button class="btn-link" type="button" data-action="edit" data-id="' + row.scoreId + '">编辑</button>';
                }
                actions += '<button class="btn-link" type="button" data-action="stat" data-id="' + row.stuId + '">统计</button>';
                if (state.canDelete) {
                    actions += '<button class="btn-link danger" type="button" data-action="delete" data-id="' + row.scoreId + '">删除</button>';
                }
                var checkbox = state.role === 'STUDENT'
                    ? ''
                    : '<input type="checkbox" class="row-check" data-id="' + row.scoreId + '">';
                html += '<tr>'
                    + '<td>' + checkbox + '</td>'
                    + '<td>' + escapeHtml(row.stuNo) + '</td>'
                    + '<td>' + escapeHtml(row.stuName) + '</td>'
                    + '<td>' + escapeHtml(row.className || '-') + '</td>'
                    + '<td>' + escapeHtml(row.courseName) + '</td>'
                    + '<td>' + escapeHtml(row.credit === null ? '-' : row.credit) + '</td>'
                    + '<td>' + escapeHtml(row.term) + '</td>'
                    + '<td class="' + scoreClass(row.score) + '">' + escapeHtml(row.score) + '</td>'
                    + '<td>' + escapeHtml(row.examDate || '-') + '</td>'
                    + '<td>' + actions + '</td>'
                    + '</tr>';
            });
            tbody.innerHTML = html;
            $('totalText').textContent = '共 ' + state.total + ' 条';
            $('checkAll').checked = false;
        }

        function renderPagination() {
            var pageCount = Math.max(1, Math.ceil(state.total / state.size));
            var html = '<button type="button" data-page="' + (state.page - 1) + '"'
                + (state.page <= 1 ? ' disabled' : '') + '>上一页</button>';
            var start = Math.max(1, state.page - 2);
            var end = Math.min(pageCount, start + 4);
            start = Math.max(1, end - 4);
            for (var i = start; i <= end; i++) {
                html += '<button type="button" data-page="' + i + '"'
                    + (i === state.page ? ' class="active"' : '') + '>' + i + '</button>';
            }
            html += '<button type="button" data-page="' + (state.page + 1) + '"'
                + (state.page >= pageCount ? ' disabled' : '') + '>下一页</button>';
            html += '<span class="page-info">第 ' + state.page + ' / ' + pageCount + ' 页，每页 '
                + state.size + ' 条</span>';
            $('pagination').innerHTML = html;
        }

        function openModal(row) {
            state.editingId = row ? row.scoreId : null;
            $('modalTitle').textContent = row ? '编辑成绩' : '录入成绩';
            if (row) {
                $('fStuId').value = String(row.stuId);
                $('fCourseId').value = String(row.courseId);
                $('fTerm').value = row.term;
                $('fScore').value = row.score;
                $('fExamDate').value = row.examDate || '';
            } else {
                $('fTerm').value = '2025-2026-1';
                $('fScore').value = '';
                $('fExamDate').value = '';
            }
            $('modal').className = 'modal-mask show';
        }

        function closeModal() {
            $('modal').className = 'modal-mask';
            state.editingId = null;
        }

        function submitScore(event) {
            event.preventDefault();
            var payload = {
                stuId: Number(value('fStuId')),
                courseId: Number(value('fCourseId')),
                term: value('fTerm').trim(),
                score: value('fScore') === '' ? null : Number(value('fScore')),
                examDate: value('fExamDate') || null
            };
            if (state.editingId) {
                payload.scoreId = state.editingId;
            }
            $('saveBtn').disabled = true;
            api('/api/scores', {
                method: state.editingId ? 'PUT' : 'POST',
                body: payload
            }).then(function () {
                toast(state.editingId ? '修改成功' : '录入成功', 'success');
                closeModal();
                loadScores();
            }).catch(function (error) {
                toast(error.message, 'error');
            }).finally(function () {
                $('saveBtn').disabled = false;
            });
        }

        function deleteScore(id) {
            if (!window.confirm('确定删除这条成绩记录吗？')) {
                return;
            }
            api('/api/scores/' + id, {method: 'DELETE'}).then(function () {
                toast('删除成功', 'success');
                if (state.records.length === 1 && state.page > 1) {
                    state.page -= 1;
                }
                loadScores();
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function batchDelete() {
            var ids = Array.from(state.selected);
            if (!ids.length) {
                toast('请先勾选要删除的成绩', 'error');
                return;
            }
            if (!window.confirm('确定批量删除选中的 ' + ids.length + ' 条成绩吗？')) {
                return;
            }
            api('/api/scores', {method: 'DELETE', body: ids}).then(function () {
                toast('批量删除成功', 'success');
                loadScores();
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function showStat(stuId) {
            api('/api/scores/stat/' + stuId).then(function (stat) {
                var student = null;
                state.students.forEach(function (item) {
                    if (item.stuId === stuId) {
                        student = item;
                    }
                });
                var name = student ? student.stuName : ('学生#' + stuId);
                $('statBox').innerHTML = '成绩统计 · <strong>' + escapeHtml(name) + '</strong>：共 '
                    + stat.courseCount + ' 门课程，总分 <strong>' + stat.totalScore
                    + '</strong>，平均分 <strong>' + stat.avgScore + '</strong>，最高分 <strong>'
                    + stat.maxScore + '</strong>，最低分 <strong>' + stat.minScore + '</strong>';
                $('statBox').className = 'stat-box show';
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function bindEvents() {
            $('searchForm').addEventListener('submit', function (event) {
                event.preventDefault();
                state.page = 1;
                loadScores();
            });
            $('resetBtn').addEventListener('click', function () {
                $('searchForm').reset();
                state.page = 1;
                loadScores();
            });
            $('addBtn').addEventListener('click', function () {
                openModal(null);
            });
            $('batchDeleteBtn').addEventListener('click', batchDelete);
            $('modalClose').addEventListener('click', closeModal);
            $('cancelBtn').addEventListener('click', closeModal);
            $('scoreForm').addEventListener('submit', submitScore);

            $('checkAll').addEventListener('change', function () {
                var checked = this.checked;
                state.selected.clear();
                var checks = document.querySelectorAll('.row-check');
                for (var i = 0; i < checks.length; i++) {
                    checks[i].checked = checked;
                    if (checked) {
                        state.selected.add(Number(checks[i].getAttribute('data-id')));
                    }
                }
            });

            $('tbody').addEventListener('change', function (event) {
                if (!event.target.classList.contains('row-check')) {
                    return;
                }
                var id = Number(event.target.getAttribute('data-id'));
                if (event.target.checked) {
                    state.selected.add(id);
                } else {
                    state.selected.delete(id);
                }
                event.target.closest('tr').className = event.target.checked ? 'selected' : '';
                $('checkAll').checked = state.selected.size === state.records.length;
            });

            $('tbody').addEventListener('click', function (event) {
                var button = event.target.closest('button[data-action]');
                if (!button) {
                    return;
                }
                var action = button.getAttribute('data-action');
                var id = Number(button.getAttribute('data-id'));
                if (action === 'delete') {
                    deleteScore(id);
                } else if (action === 'stat') {
                    showStat(id);
                } else {
                    var row = null;
                    state.records.forEach(function (item) {
                        if (item.scoreId === id) {
                            row = item;
                        }
                    });
                    openModal(row);
                }
            });

            $('pagination').addEventListener('click', function (event) {
                var button = event.target.closest('button[data-page]');
                if (!button || button.disabled) {
                    return;
                }
                state.page = Number(button.getAttribute('data-page'));
                loadScores();
            });

            $('logoutBtn').addEventListener('click', function () {
                api('/api/auth/logout', {method: 'POST'}).then(function () {
                    window.location.href = CTX + '/login';
                }).catch(function () {
                    window.location.href = CTX + '/login';
                });
            });
        }

        document.addEventListener('DOMContentLoaded', function () {
            bindEvents();
            loadMe()
                .then(loadOptions)
                .then(loadScores)
                .catch(function (error) {
                    toast(error.message, 'error');
                });
        });
    })();
</script>
</body>
</html>