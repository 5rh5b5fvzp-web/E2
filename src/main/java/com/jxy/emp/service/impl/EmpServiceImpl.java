package com.jxy.emp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxy.emp.common.BusinessException;
import com.jxy.emp.dto.EmpQueryDTO;
import com.jxy.emp.entity.Emp;
import com.jxy.emp.mapper.EmpMapper;
import com.jxy.emp.service.EmpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 员工业务实现。
 */
@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {

    private static final Logger log = LoggerFactory.getLogger(EmpServiceImpl.class);

    @Override
    public Page<Emp> pageQuery(int page, int size, EmpQueryDTO query) {
        if (page < 1) {
            throw new BusinessException(400, "页码必须大于等于 1");
        }
        if (size < 1 || size > 100) {
            throw new BusinessException(400, "每页条数必须在 1-100 之间");
        }
        EmpQueryDTO condition = query == null ? new EmpQueryDTO() : query;
        return baseMapper.selectPageByCondition(new Page<>(page, size), condition);
    }

    @Override
    public Page<Emp> pageQueryByWrapper(int page, int size, EmpQueryDTO query) {
        if (page < 1) {
            throw new BusinessException(400, "页码必须大于等于 1");
        }
        if (size < 1 || size > 100) {
            throw new BusinessException(400, "每页条数必须在 1-100 之间");
        }
        EmpQueryDTO condition = query == null ? new EmpQueryDTO() : query;
        LambdaQueryWrapper<Emp> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(condition.getName()), Emp::getEmpName, condition.getName())
                .eq(StringUtils.hasText(condition.getGender()), Emp::getGender, condition.getGender())
                .eq(StringUtils.hasText(condition.getDept()), Emp::getDept, condition.getDept())
                .eq(condition.getStatus() != null, Emp::getStatus, condition.getStatus())
                .orderByDesc(Emp::getEmpId);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public boolean removeBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "批量删除的员工编号不能为空");
        }
        return baseMapper.deleteBatchByIds(ids) > 0;
    }

    /**
     * 声明式事务：插入后抛出运行时异常，事务应整体回滚。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithTx(Emp emp) {
        baseMapper.insert(emp);
        log.info("[TX] 已插入员工 {}，即将抛出异常验证回滚", emp.getEmpName());
        int i = 1 / 0;
    }

    /**
     * 不带事务：插入语句自动提交，随后抛出的异常不会影响已提交的数据。
     */
    @Override
    public void saveWithoutTx(Emp emp) {
        baseMapper.insert(emp);
        log.info("[NO-TX] 已插入员工 {}，即将抛出异常验证未回滚", emp.getEmpName());
        int i = 1 / 0;
    }

    @Override
    public long countByDept(String dept) {
        return baseMapper.countByDept(dept);
    }
}
