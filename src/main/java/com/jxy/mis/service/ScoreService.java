package com.jxy.mis.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxy.mis.dto.ScoreQueryDTO;
import com.jxy.mis.dto.ScoreStatDTO;
import com.jxy.mis.entity.Score;

import java.util.List;

/**
 * 成绩业务接口。
 */
public interface ScoreService extends IService<Score> {

    /** 分页 + 多条件查询 */
    Page<Score> pageQuery(int page, int size, ScoreQueryDTO query);

    /** 批量删除 */
    boolean removeBatch(List<Integer> ids);

    /** 单个学生成绩统计 */
    ScoreStatDTO statByStudent(Integer stuId);
}
