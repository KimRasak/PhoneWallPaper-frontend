package jzl.sysu.cn.phonewallpaperfrontend.Model;

import java.io.Serializable;
import java.util.Date;

public class WallPaper implements Serializable {
    private Long id;
    private String category;
    private String path;
    private int likeNum;

    // 上传者信息
    private Long uploaderId;
    private String uploaderName;
    private Date date;

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

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
