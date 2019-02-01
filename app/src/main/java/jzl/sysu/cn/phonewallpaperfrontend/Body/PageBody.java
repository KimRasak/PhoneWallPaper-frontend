package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class PageBody {
    private int startNum; // 起始序号
    private int pageSize; // 每页项目数
    private String category;  // 壁纸类型（美女、影视...)
    private String sort; // 排序方式（按热度/下载数/最新发布...）

    public PageBody(int startNum, int pageSize, String category, String sort) {
        this.startNum = startNum;
        this.pageSize = pageSize;
        this.category = category;
        this.sort = sort;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
