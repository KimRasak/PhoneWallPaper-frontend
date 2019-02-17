package jzl.sysu.cn.phonewallpaperfrontend.Body;

import jzl.sysu.cn.phonewallpaperfrontend.Constants;

public class ClickBody {
    private Long wallpaperId;
    private int startNum = 0;
    private int pageSize = Constants.COMMENT_PAGE_SIZE;

    public ClickBody(long wallpaperId) {
        super();
        this.wallpaperId = wallpaperId;
    }

    public long getWallPaperId() {
        return wallpaperId;
    }

    public void setWallPaperId(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public int getStartNum() {
        return startNum;
    }

    public int getPageSize() {
        return pageSize;
    }
}
