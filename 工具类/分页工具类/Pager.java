package com.springjiemi.vo;

import java.util.List;

/**
 * 通过subList实现分页
 * Created by xhsf on 2019/2/17.
 */
public class Pager<T> {

    private int pageSize;
    private int pageNum;
    private int totalRecord;
    private int totalPage;
    private List<T> dataList;

    /**
     * 获取Pager对象
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param dataList 数据列表
     * @throws Exception
     */
    public Pager(int pageNum, int pageSize,  List<T> dataList) throws Exception {
        this.totalRecord = dataList.size();
        this.totalPage = (int) Math.ceil((double) totalRecord/ pageSize);
        this.pageSize = pageSize;
        this.pageNum = pageNum;

        if (totalPage > pageNum) {
            this.dataList = dataList.subList((pageNum - 1) * pageSize, pageNum * pageSize);
        } else if (totalPage == pageNum) {
            this.dataList = dataList.subList((pageNum - 1) * pageSize, totalRecord);
        } else {
            throw new Exception();
        }
    }

}
