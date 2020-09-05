package com.lvhao.nowcodercommunity.entity;

/**
 * 封装分页相关的信息
 */
public class Page {

    /**
     * 当前页码
     */
    private int current = 1;

    /**
     * 每页的记录数
     */
    private int limit = 10;

    /**
     * 记录总数
     */
    private int rows;

    /**
     * 查询路径(用于复用分页查询)
     */
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit > 0 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return 当前页的起始行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * 获取当前展示的起始页码
     *
     * <p>...5 6 7 8 9..., 则起始页码为5
     *
     * @return 当前展示的起始页码
     */
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * 获取当前展示的终止页码
     *
     * <p>...5 6 7 8 9..., 则终止页码为9
     *
     * @return 当前展示的终止页码
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
