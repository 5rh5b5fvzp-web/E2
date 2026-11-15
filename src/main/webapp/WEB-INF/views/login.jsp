<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 | 学生成绩管理 MIS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/mis.css">
</head>
<body>
<div class="login-page">
    <div class="login-card">
        <h1>学生成绩管理 MIS</h1>
        <p class="sub">SSM 全注解整合实验二 · 请使用系统账号登录</p>

        <div id="loginError" class="login-error"></div>

        <form id="loginForm" autocomplete="off">
            <div class="field">
                <label for="username">用户名</label>
                <input id="username" name="username" type="text" placeholder="请输入用户名" required>
            </div>
            <div class="field">
                <label for="password">密码</label>
                <input id="password" name="password" type="password" placeholder="请输入密码" required>
            </div>
            <button id="loginBtn" class="btn btn-primary" type="submit">登 录</button>
        </form>
    </div>
</div>

<script>
    var CTX = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/static/js/common.js"></script>
<script>
    (function () {
        var form = document.getElementById('loginForm');
        var errorBox = document.getElementById('loginError');
        var button = document.getElementById('loginBtn');

        form.addEventListener('submit', function (event) {
            event.preventDefault();
            errorBox.className = 'login-error';
            button.disabled = true;
            button.textContent = '登录中...';

            api('/api/auth/login', {
                method: 'POST',
                body: {
                    username: document.getElementById('username').value.trim(),
                    password: document.getElementById('password').value
                }
            }).then(function () {
                window.location.href = CTX + '/students';
            }).catch(function (error) {
                errorBox.textContent = error.message;
                errorBox.className = 'login-error show';
                button.disabled = false;
                button.textContent = '登 录';
            });
        });
    })();
</script>
</body>
</html>
