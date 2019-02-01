package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class GetCommentBody {
    private long wallPaperId;
    private int startNum;
    private int pageSize;

    public GetCommentBody(long wallPaperId, int startNum, int pageSize) {
        this.wallPaperId = wallPaperId;
        this.startNum = startNum;
        this.pageSize = pageSize;
    }

    public long getWallPaperId() {
        return wallPaperId;
    }

    public void setWallPaperId(long wallPaperId) {
        this.wallPaperId = wallPaperId;
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
}
