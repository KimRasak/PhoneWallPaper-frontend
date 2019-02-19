package jzl.sysu.cn.phonewallpaperfrontend.Model;

public class LocalWallpaper {
    private String imgSrc;
    private Long wallpaperId;

    public LocalWallpaper(String imgSrc, Long wallpaperId) {
        this.imgSrc = imgSrc;
        this.wallpaperId = wallpaperId;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Long getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

}
