package com.jxy.mis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxy.mis.entity.ClassInfo;

import java.util.List;

/**
 * 班级业务接口。
 */
public interface ClassService extends IService<ClassInfo> {

    /** 查询全部班级，用于前端下拉框 */
    List<ClassInfo> listAll();
}
