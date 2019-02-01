package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class RecBody {
    private Integer startNum; // 页码（0开头）
    private Integer pageSize; // 每页项目数

    public RecBody(Integer startNum, Integer pageSize) {
        this.startNum = startNum;
        this.pageSize = pageSize;
    }

    public Integer getStartNum() {
        return startNum;
    }

    public void setStartNum(Integer startNum) {
        this.startNum = startNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
