/* ============================================================
   实验二 公共脚本：统一请求封装、提示条、转义
   ============================================================ */
(function (global) {
    'use strict';

    /**
     * 统一请求：自动 JSON 序列化、统一解析 Result、401 跳登录。
     * 返回 Promise，成功时 resolve(data)，失败时 reject(Error)。
     */
    function api(url, options) {
        var opts = options || {};
        var fetchOptions = {
            method: opts.method || 'GET',
            headers: {'Content-Type': 'application/json;charset=UTF-8'},
            credentials: 'same-origin'
        };
        if (opts.body !== undefined && opts.body !== null) {
            fetchOptions.body = typeof opts.body === 'string'
                ? opts.body
                : JSON.stringify(opts.body);
        }

        return fetch(CTX + url, fetchOptions).then(function (response) {
            if (response.status === 401) {
                window.location.href = CTX + '/login';
                throw new Error('未登录或登录已过期');
            }
            return response.text().then(function (text) {
                var json;
                try {
                    json = text ? JSON.parse(text) : {};
                } catch (e) {
                    throw new Error('服务端返回格式异常：' + text.substring(0, 80));
                }
                if (json.code !== 200) {
                    throw new Error(json.message || '请求失败');
                }
                return json.data;
            });
        });
    }

    var toastTimer = null;

    /** 顶部轻提示 */
    function toast(message, type) {
        var el = document.getElementById('toast');
        if (!el) {
            el = document.createElement('div');
            el.id = 'toast';
            document.body.appendChild(el);
        }
        el.className = 'show' + (type ? ' ' + type : '');
        el.textContent = message;
        if (toastTimer) {
            clearTimeout(toastTimer);
        }
        toastTimer = setTimeout(function () {
            el.className = '';
        }, 2600);
    }

    /** HTML 转义，避免把数据库内容当标签渲染 */
    function escapeHtml(value) {
        if (value === null || value === undefined) {
            return '';
        }
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    /** 查询参数拼装：跳过空值 */
    function buildQuery(params) {
        var search = new URLSearchParams();
        Object.keys(params).forEach(function (key) {
            var value = params[key];
            if (value !== undefined && value !== null && String(value).trim() !== '') {
                search.set(key, value);
            }
        });
        return search.toString();
    }

    global.api = api;
    global.toast = toast;
    global.escapeHtml = escapeHtml;
    global.buildQuery = buildQuery;
})(window);