<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学生管理 | 学生成绩管理 MIS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/mis.css">
</head>
<body>
<div class="topbar">
    <div class="brand">学生成绩管理 MIS<small>SSM 全注解整合 · 实验二</small></div>
    <nav>
        <a class="active" href="${pageContext.request.contextPath}/students">学生管理</a>
        <a href="${pageContext.request.contextPath}/scores">成绩管理</a>
    </nav>
    <div class="user">
        <span id="userName">加载中...</span>
        <button id="logoutBtn" type="button">退出登录</button>
    </div>
</div>

<div class="container">
    <!-- 查询区 -->
    <div class="card">
        <div class="card-title"><span>学生查询</span></div>
        <form id="searchForm" class="search-form" autocomplete="off">
            <div class="field">
                <label for="qStuName">姓名</label>
                <input id="qStuName" type="text" placeholder="支持模糊查询">
            </div>
            <div class="field">
                <label for="qStuNo">学号</label>
                <input id="qStuNo" type="text" placeholder="支持模糊查询">
            </div>
            <div class="field">
                <label for="qGender">性别</label>
                <select id="qGender">
                    <option value="">全部</option>
                    <option value="男">男</option>
                    <option value="女">女</option>
                </select>
            </div>
            <div class="field">
                <label for="qClassId">班级</label>
                <select id="qClassId"><option value="">全部</option></select>
            </div>
            <div class="field">
                <label for="qStatus">状态</label>
                <select id="qStatus">
                    <option value="">全部</option>
                    <option value="1">在读</option>
                    <option value="0">休学</option>
                </select>
            </div>
            <button class="btn btn-primary" type="submit">查 询</button>
            <button class="btn btn-default" type="button" id="resetBtn">重 置</button>
        </form>
    </div>

    <!-- 列表区 -->
    <div class="card">
        <div class="table-toolbar">
            <button class="btn btn-primary" type="button" id="addBtn">+ 新增学生</button>
            <button class="btn btn-danger" type="button" id="batchDeleteBtn">批量删除</button>
            <div class="spacer"></div>
            <div class="total" id="totalText">共 0 条</div>
        </div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th style="width:38px"><input type="checkbox" id="checkAll" title="全选本页"></th>
                    <th>学号</th>
                    <th>姓名</th>
                    <th>性别</th>
                    <th>班级</th>
                    <th>手机号</th>
                    <th>邮箱</th>
                    <th>入学日期</th>
                    <th>状态</th>
                    <th style="width:120px">操作</th>
                </tr>
                </thead>
                <tbody id="tbody"></tbody>
            </table>
        </div>
        <div class="pagination" id="pagination"></div>
    </div>
</div>

<!-- 新增 / 编辑弹窗 -->
<div class="modal-mask" id="modal">
    <div class="modal">
        <div class="modal-header">
            <span id="modalTitle">新增学生</span>
            <button class="close" type="button" id="modalClose">&times;</button>
        </div>
        <form id="studentForm" autocomplete="off">
            <div class="modal-body">
                <div class="field">
                    <label for="fStuNo">学号 *（6-20 位数字）</label>
                    <input id="fStuNo" type="text" maxlength="20">
                </div>
                <div class="field">
                    <label for="fStuName">姓名 *（2-20 个字符）</label>
                    <input id="fStuName" type="text" maxlength="20">
                </div>
                <div class="field">
                    <label for="fGender">性别</label>
                    <select id="fGender">
                        <option value="男">男</option>
                        <option value="女">女</option>
                    </select>
                </div>
                <div class="field">
                    <label for="fClassId">班级 *</label>
                    <select id="fClassId"></select>
                </div>
                <div class="field">
                    <label for="fPhone">手机号</label>
                    <input id="fPhone" type="text" maxlength="11" placeholder="11 位手机号">
                </div>
                <div class="field">
                    <label for="fEmail">邮箱</label>
                    <input id="fEmail" type="text" maxlength="100">
                </div>
                <div class="field">
                    <label for="fEnrollDate">入学日期</label>
                    <input id="fEnrollDate" type="date">
                </div>
                <div class="field">
                    <label for="fStatus">状态</label>
                    <select id="fStatus">
                        <option value="1">在读</option>
                        <option value="0">休学</option>
                    </select>
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
            editingId: null
        };

        function $(id) {
            return document.getElementById(id);
        }

        function value(id) {
            return $(id).value;
        }

        function loadMe() {
            api('/api/auth/me').then(function (admin) {
                $('userName').textContent = (admin.realName || admin.username);
            }).catch(function () {
                /* 401 已由 api() 统一跳转 */
            });
        }

        function loadClasses() {
            api('/api/classes').then(function (list) {
                var searchSelect = $('qClassId');
                var formSelect = $('fClassId');
                var searchHtml = '<option value="">全部</option>';
                var formHtml = '';
                list.forEach(function (item) {
                    var option = '<option value="' + item.classId + '">'
                        + escapeHtml(item.className) + '</option>';
                    searchHtml += option;
                    formHtml += option;
                });
                searchSelect.innerHTML = searchHtml;
                formSelect.innerHTML = formHtml;
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function loadStudents() {
            var query = buildQuery({
                stuName: value('qStuName').trim(),
                stuNo: value('qStuNo').trim(),
                gender: value('qGender'),
                classId: value('qClassId'),
                status: value('qStatus'),
                page: state.page,
                size: state.size
            });
            api('/api/students?' + query).then(function (page) {
                state.records = page.records || [];
                state.total = page.total || 0;
                state.selected.clear();
                renderRows();
                renderPagination();
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function renderRows() {
            var tbody = $('tbody');
            if (!state.records.length) {
                tbody.innerHTML = '<tr><td class="empty" colspan="10">暂无数据</td></tr>';
                $('totalText').textContent = '共 0 条';
                return;
            }
            var html = '';
            state.records.forEach(function (student) {
                var genderClass = student.gender === '女' ? 'female' : 'male';
                var statusBadge = student.status === 1
                    ? '<span class="badge">在读</span>'
                    : '<span class="badge off">休学</span>';
                html += '<tr>'
                    + '<td><input type="checkbox" class="row-check" data-id="' + student.stuId + '"></td>'
                    + '<td>' + escapeHtml(student.stuNo) + '</td>'
                    + '<td>' + escapeHtml(student.stuName) + '</td>'
                    + '<td><span class="badge ' + genderClass + '">' + escapeHtml(student.gender) + '</span></td>'
                    + '<td>' + escapeHtml(student.className || '-') + '</td>'
                    + '<td>' + escapeHtml(student.phone || '-') + '</td>'
                    + '<td>' + escapeHtml(student.email || '-') + '</td>'
                    + '<td>' + escapeHtml(student.enrollDate || '-') + '</td>'
                    + '<td>' + statusBadge + '</td>'
                    + '<td>'
                    + '<button class="btn-link" type="button" data-action="edit" data-id="' + student.stuId + '">编辑</button>'
                    + '<button class="btn-link danger" type="button" data-action="delete" data-id="' + student.stuId + '">删除</button>'
                    + '</td>'
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

        function openModal(student) {
            state.editingId = student ? student.stuId : null;
            $('modalTitle').textContent = student ? '编辑学生' : '新增学生';
            $('fStuNo').value = student ? student.stuNo : '';
            $('fStuName').value = student ? student.stuName : '';
            $('fGender').value = student && student.gender ? student.gender : '男';
            $('fClassId').value = student && student.classId ? String(student.classId) : '';
            $('fPhone').value = student && student.phone ? student.phone : '';
            $('fEmail').value = student && student.email ? student.email : '';
            $('fEnrollDate').value = student && student.enrollDate ? student.enrollDate : '';
            $('fStatus').value = student && student.status !== null && student.status !== undefined
                ? String(student.status) : '1';
            $('modal').className = 'modal-mask show';
        }

        function closeModal() {
            $('modal').className = 'modal-mask';
            state.editingId = null;
        }

        function submitStudent(event) {
            event.preventDefault();
            var payload = {
                stuNo: value('fStuNo').trim(),
                stuName: value('fStuName').trim(),
                gender: value('fGender'),
                phone: value('fPhone').trim(),
                email: value('fEmail').trim(),
                classId: Number(value('fClassId')),
                enrollDate: value('fEnrollDate') || null,
                status: Number(value('fStatus'))
            };
            var method = state.editingId ? 'PUT' : 'POST';
            if (state.editingId) {
                payload.stuId = state.editingId;
            }
            $('saveBtn').disabled = true;
            api('/api/students', {method: method, body: payload}).then(function () {
                toast(state.editingId ? '修改成功' : '新增成功', 'success');
                closeModal();
                loadStudents();
            }).catch(function (error) {
                toast(error.message, 'error');
            }).finally(function () {
                $('saveBtn').disabled = false;
            });
        }

        function deleteStudent(id) {
            if (!window.confirm('确定删除该学生吗？删除后不可恢复。')) {
                return;
            }
            api('/api/students/' + id, {method: 'DELETE'}).then(function () {
                toast('删除成功', 'success');
                if (state.records.length === 1 && state.page > 1) {
                    state.page -= 1;
                }
                loadStudents();
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function batchDelete() {
            var ids = Array.from(state.selected);
            if (!ids.length) {
                toast('请先勾选要删除的学生', 'error');
                return;
            }
            if (!window.confirm('确定批量删除选中的 ' + ids.length + ' 名学生吗？')) {
                return;
            }
            api('/api/students', {method: 'DELETE', body: ids}).then(function () {
                toast('批量删除成功', 'success');
                loadStudents();
            }).catch(function (error) {
                toast(error.message, 'error');
            });
        }

        function bindEvents() {
            $('searchForm').addEventListener('submit', function (event) {
                event.preventDefault();
                state.page = 1;
                loadStudents();
            });
            $('resetBtn').addEventListener('click', function () {
                $('searchForm').reset();
                state.page = 1;
                loadStudents();
            });
            $('addBtn').addEventListener('click', function () {
                openModal(null);
            });
            $('batchDeleteBtn').addEventListener('click', batchDelete);
            $('modalClose').addEventListener('click', closeModal);
            $('cancelBtn').addEventListener('click', closeModal);
            $('studentForm').addEventListener('submit', submitStudent);

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
                var id = Number(button.getAttribute('data-id'));
                if (button.getAttribute('data-action') === 'delete') {
                    deleteStudent(id);
                    return;
                }
                var student = null;
                state.records.forEach(function (item) {
                    if (item.stuId === id) {
                        student = item;
                    }
                });
                openModal(student);
            });

            $('pagination').addEventListener('click', function (event) {
                var button = event.target.closest('button[data-page]');
                if (!button || button.disabled) {
                    return;
                }
                state.page = Number(button.getAttribute('data-page'));
                loadStudents();
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
            loadMe();
            loadClasses();
            loadStudents();
        });
    })();
</script>
</body>
</html>