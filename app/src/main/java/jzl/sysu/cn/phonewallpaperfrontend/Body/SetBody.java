package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class SetBody extends AuthBody{
    private Long wallpaperId;

    public SetBody(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public Long getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }
}
