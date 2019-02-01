package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class LikeBody extends AuthBody {
    private long wallpaperId;
    private boolean like;

    public LikeBody(long wallpaperId, boolean like) {
        super();
        this.wallpaperId = wallpaperId;
        this.like = like;
    }

    public long getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
