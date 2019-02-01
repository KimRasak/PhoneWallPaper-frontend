package jzl.sysu.cn.phonewallpaperfrontend.Model;

public class WallPaper {
    private Long id;
    private String category;
    private String path;
    private int likeNum;

    public WallPaper(Long id, String category, String path, int likeNum) {
        this.id = id;
        this.category = category;
        this.path = path;
        this.likeNum = likeNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
}
