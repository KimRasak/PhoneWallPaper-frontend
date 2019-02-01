package jzl.sysu.cn.phonewallpaperfrontend.Response;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;

public class PageResponse {
    private List<WallPaper> wallpapers;
    private String hostName;

    public List<WallPaper> getWallpapers() {
        return wallpapers;
    }

    public void setWallpapers(List<WallPaper> wallpapers) {
        this.wallpapers = wallpapers;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
