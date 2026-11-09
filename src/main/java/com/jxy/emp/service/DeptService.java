package com.jxy.emp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxy.emp.entity.Dept;

import java.util.List;

/**
 * 部门业务层。
 */
public interface DeptService extends IService<Dept> {

    List<Dept> listAll();
}
