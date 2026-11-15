package com.jxy.mis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxy.mis.entity.ClassInfo;
import com.jxy.mis.mapper.ClassMapper;
import com.jxy.mis.service.ClassService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级业务实现。
 */
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, ClassInfo> implements ClassService {

    @Override
    public List<ClassInfo> listAll() {
        return list(new LambdaQueryWrapper<ClassInfo>().orderByAsc(ClassInfo::getClassId));
    }
}
