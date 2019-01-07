package jzl.sysu.cn.phonewallpaperfrontend.DataItem;

public class WallPaperDataItem {
    private String id;
    private String category;
    private String imgSrc;
    private int likeNum;
    private byte[] imgBytes;

    public WallPaperDataItem(String id, String category, String imgSrc, int likeNum) {
        this.id = id;
        this.category = category;
        this.imgSrc = imgSrc;
        this.likeNum = likeNum;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public void setImgBytes(byte[] imgBytes) {
        this.imgBytes = imgBytes;
    }
}
