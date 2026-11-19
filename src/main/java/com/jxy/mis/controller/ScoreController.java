package com.jxy.mis.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxy.mis.common.BusinessException;
import com.jxy.mis.common.RequireRole;
import com.jxy.mis.common.Result;
import com.jxy.mis.common.Role;
import com.jxy.mis.dto.ScoreQueryDTO;
import com.jxy.mis.dto.ScoreStatDTO;
import com.jxy.mis.entity.Admin;
import com.jxy.mis.entity.Score;
import com.jxy.mis.service.ScoreService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 成绩管理 RESTful 接口（实验二扩展模块）。
 */
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    /** 录入成绩：管理员与教务 */
    @PostMapping
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public Result<Score> add(@RequestBody @Valid Score score, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        scoreService.save(score);
        return Result.ok(score);
    }

    /** 修改成绩：管理员与教务 */
    @PutMapping
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public Result<Void> update(@RequestBody @Valid Score score, BindingResult bindingResult) {
        if (score.getScoreId() == null) {
            return Result.fail(400, "成绩编号不能为空");
        }
        if (bindingResult.hasErrors()) {
            return Result.fail(400, bindingResult.getFieldError().getDefaultMessage());
        }
        if (scoreService.getById(score.getScoreId()) == null) {
            throw new BusinessException(404, "成绩记录不存在");
        }
        scoreService.updateById(score);
        return Result.ok();
    }

    /** 单条删除：仅管理员 */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public Result<Void> delete(@PathVariable Integer id) {
        if (!scoreService.removeById(id)) {
            throw new BusinessException(404, "成绩记录不存在");
        }
        return Result.ok();
    }

    /** 批量删除：仅管理员 */
    @DeleteMapping
    @RequireRole(Role.ADMIN)
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        boolean removed = scoreService.removeBatch(ids);
        if (!removed) {
            throw new BusinessException(404, "未找到可删除的成绩记录");
        }
        return Result.ok();
    }

    /** 单条查询：管理员与教务 */
    @GetMapping("/{id}")
    @RequireRole({Role.ADMIN, Role.TEACHER})
    public Result<Score> getById(@PathVariable Integer id) {
        Score score = scoreService.getById(id);
        if (score == null) {
            throw new BusinessException(404, "成绩记录不存在");
        }
        return Result.ok(score);
    }

    /**
     * 列表 + 多条件 + 分页：三种角色都可访问。
     * 学生角色只能看到自己的成绩（后端强制过滤，前端隐藏条件无效）。
     */
    @GetMapping
    @RequireRole({Role.ADMIN, Role.TEACHER, Role.STUDENT})
    public Result<Page<Score>> page(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    ScoreQueryDTO query,
                                    HttpSession session) {
        Admin current = currentUser(session);
        if (isStudent(current)) {
            query.setStuId(current.getStuId());
        }
        return Result.ok(scoreService.pageQuery(page, size, query));
    }

    /** 学生成绩统计：学生只能查看自己的 */
    @GetMapping("/stat/{stuId}")
    @RequireRole({Role.ADMIN, Role.TEACHER, Role.STUDENT})
    public Result<ScoreStatDTO> stat(@PathVariable Integer stuId, HttpSession session) {
        Admin current = currentUser(session);
        if (isStudent(current) && !stuId.equals(current.getStuId())) {
            throw new BusinessException(403, "学生只能查看自己的成绩统计");
        }
        return Result.ok(scoreService.statByStudent(stuId));
    }

    private Admin currentUser(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (!(loginUser instanceof Admin)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return (Admin) loginUser;
    }

    private boolean isStudent(Admin admin) {
        return Role.STUDENT.name().equalsIgnoreCase(admin.getRole());
    }
}
