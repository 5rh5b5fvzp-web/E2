package com.jxy.mis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxy.mis.common.BusinessException;
import com.jxy.mis.dto.ScoreQueryDTO;
import com.jxy.mis.dto.ScoreStatDTO;
import com.jxy.mis.entity.ClassInfo;
import com.jxy.mis.entity.Course;
import com.jxy.mis.entity.Score;
import com.jxy.mis.entity.Student;
import com.jxy.mis.mapper.ClassMapper;
import com.jxy.mis.mapper.CourseMapper;
import com.jxy.mis.mapper.ScoreMapper;
import com.jxy.mis.mapper.StudentMapper;
import com.jxy.mis.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 成绩业务实现。
 *
 * <p>分页查询使用 LambdaQueryWrapper + Page；
 * 学生/课程/班级名称在结果集上批量补齐，避免 N+1。
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final ClassMapper classMapper;

    public ScoreServiceImpl(StudentMapper studentMapper, CourseMapper courseMapper, ClassMapper classMapper) {
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.classMapper = classMapper;
    }

    @Override
    public Page<Score> pageQuery(int page, int size, ScoreQueryDTO query) {
        checkPage(page, size);
        ScoreQueryDTO condition = query == null ? new ScoreQueryDTO() : query;

        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        boolean hasStudentCondition = StringUtils.hasText(condition.getStuNo())
                || StringUtils.hasText(condition.getStuName());
        if (hasStudentCondition) {
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.like(StringUtils.hasText(condition.getStuNo()),
                            Student::getStuNo, condition.getStuNo())
                    .like(StringUtils.hasText(condition.getStuName()),
                            Student::getStuName, condition.getStuName());
            List<Integer> stuIds = studentMapper.selectList(studentWrapper).stream()
                    .map(Student::getStuId)
                    .collect(Collectors.toList());
            if (stuIds.isEmpty()) {
                return new Page<>(page, size);
            }
            wrapper.in(Score::getStuId, stuIds);
        }

        wrapper.eq(condition.getStuId() != null, Score::getStuId, condition.getStuId())
                .eq(condition.getCourseId() != null, Score::getCourseId, condition.getCourseId())
                .eq(StringUtils.hasText(condition.getTerm()), Score::getTerm, condition.getTerm())
                .orderByDesc(Score::getTerm)
                .orderByAsc(Score::getScoreId);

        Page<Score> result = page(new Page<>(page, size), wrapper);
        fillReferences(result.getRecords());
        return result;
    }

    @Override
    public boolean removeBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "批量删除的成绩编号不能为空");
        }
        return removeByIds(ids);
    }

    @Override
    public ScoreStatDTO statByStudent(Integer stuId) {
        if (stuId == null || studentMapper.selectById(stuId) == null) {
            throw new BusinessException(404, "学生不存在");
        }
        List<Score> scores = list(Wrappers.<Score>lambdaQuery().eq(Score::getStuId, stuId));
        List<BigDecimal> values = scores.stream()
                .map(Score::getScore)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        BigDecimal total = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        ScoreStatDTO stat = new ScoreStatDTO();
        stat.setCourseCount(values.size());
        stat.setTotalScore(total);
        stat.setAvgScore(values.isEmpty() ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP));
        stat.setMaxScore(values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        stat.setMinScore(values.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        return stat;
    }

    /** 录入成绩前校验学生与课程存在 */
    @Override
    public boolean save(Score entity) {
        checkReferences(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Score entity) {
        if (entity.getScoreId() == null) {
            throw new BusinessException(400, "成绩编号不能为空");
        }
        checkReferences(entity);
        return super.updateById(entity);
    }

    private void fillReferences(List<Score> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Set<Integer> stuIds = records.stream().map(Score::getStuId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Integer> courseIds = records.stream().map(Score::getCourseId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Integer, Student> studentMap = stuIds.isEmpty() ? Collections.emptyMap()
                : studentMapper.selectBatchIds(stuIds).stream()
                .collect(Collectors.toMap(Student::getStuId, Function.identity(), (a, b) -> a));
        Map<Integer, Course> courseMap = courseIds.isEmpty() ? Collections.emptyMap()
                : courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getCourseId, Function.identity(), (a, b) -> a));

        Set<Integer> classIds = studentMap.values().stream().map(Student::getClassId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, String> classNameMap = classIds.isEmpty() ? Collections.emptyMap()
                : classMapper.selectBatchIds(classIds).stream()
                .collect(Collectors.toMap(ClassInfo::getClassId, ClassInfo::getClassName, (a, b) -> a));

        for (Score score : records) {
            Student student = studentMap.get(score.getStuId());
            if (student != null) {
                score.setStuNo(student.getStuNo());
                score.setStuName(student.getStuName());
                score.setClassName(classNameMap.get(student.getClassId()));
            }
            Course course = courseMap.get(score.getCourseId());
            if (course != null) {
                score.setCourseName(course.getCourseName());
                score.setCredit(course.getCredit());
            }
        }
    }

    private void checkReferences(Score score) {
        if (score.getStuId() == null || studentMapper.selectById(score.getStuId()) == null) {
            throw new BusinessException(400, "学生不存在，无法录入成绩");
        }
        if (score.getCourseId() == null || courseMapper.selectById(score.getCourseId()) == null) {
            throw new BusinessException(400, "课程不存在，无法录入成绩");
        }
    }

    private void checkPage(int page, int size) {
        if (page < 1) {
            throw new BusinessException(400, "页码必须大于等于 1");
        }
        if (size < 1 || size > 100) {
            throw new BusinessException(400, "每页条数必须在 1-100 之间");
        }
    }
}
