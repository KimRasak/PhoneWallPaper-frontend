package jzl.sysu.cn.phonewallpaperfrontend.DataItem;

public class WallPaperDataItem {
    private String id;
    private String category;
    private String imgSrc;
    private byte[] imgBytes;

    public WallPaperDataItem(String id, String category, String imgSrc) {
        this.id = id;
        this.category = category;
        this.imgSrc = imgSrc;
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
