package com.jxy.emp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxy.emp.entity.Dept;
import com.jxy.emp.mapper.DeptMapper;
import com.jxy.emp.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门业务实现。
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<Dept> listAll() {
        return list(Wrappers.<Dept>lambdaQuery().orderByAsc(Dept::getDeptNo));
    }
}
