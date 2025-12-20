package com.peanut.utils;

import com.peanut.expection.BusinessException;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {

    public static final String ARGS_NOT_EXIST = "页码或条数不存在";

    public static final String PAGE_NUM_ERROR = "页码必须大于等于0";

    public static final String PAGE_SIZE_ERROR = "条数必须大于等于1， 小于等于最大条数";

    private static final int DEFAULT_PAGE_NUM = 1;    // 默认页码：第1页
    private static final int DEFAULT_PAGE_SIZE = 10;  // 默认每页条数：10条
    private static final int MAX_PAGE_SIZE = 50;      // 最大每页条数：防止查太多数据

    public static int getPageNum(int pageNum) {
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        return  pageNum ;
    }

    public static int getPageNum(Integer pageNum, Integer pageSize) {
        return  pageNum * pageSize;
    }
    public static void validate(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            throw new BusinessException(ARGS_NOT_EXIST);
        }
        if (pageNum < 0) {
            throw new BusinessException(PAGE_NUM_ERROR);
        }
        if (pageSize < 1 ||  pageSize > MAX_PAGE_SIZE) {
            throw new BusinessException(PAGE_SIZE_ERROR + MAX_PAGE_SIZE);
        }
    }

    /**
     * 处理分页参数（兜底异常值，返回合法的pageNum和pageSize）
     * @param pageSize 前端传入的每页条数（可能为null/-1/0等）
     * @return 数组：[合法的pageNum, 合法的pageSize]
     */
    public static int getPageSize(Integer pageSize) {
        int validPageSize;
        if (pageSize == null || pageSize <= 0) {
            validPageSize = DEFAULT_PAGE_SIZE;
        } else {
            validPageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        }
        return validPageSize;
    }
}
