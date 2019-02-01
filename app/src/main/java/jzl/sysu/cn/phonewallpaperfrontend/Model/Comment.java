package jzl.sysu.cn.phonewallpaperfrontend.Model;

public class Comment {
    // 评论信息
    private Long cid;
    private Long wallpaperId;
    private String content;
    private Long fromUesrId;

    // 评论人信息
    private String userIcon; // 头像链接
    private String userName;

    private Long toCommentId;
    private Long toUserId;
    private String toUserName;

    public Comment(Long cid, Long wallpaperId, String content, Long fromUesrId, String userIcon, String userName, Long toCommentId, Long toUserId, String toUserName) {
        this.cid = cid;
        this.wallpaperId = wallpaperId;
        this.content = content;
        this.fromUesrId = fromUesrId;
        this.userIcon = userIcon;
        this.userName = userName;
        this.toCommentId = toCommentId;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
    }

    public boolean isReply() { return this.toUserId != null; }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getFromUesrId() {
        return fromUesrId;
    }

    public void setFromUesrId(Long fromUesrId) {
        this.fromUesrId = fromUesrId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getToCommentId() {
        return toCommentId;
    }

    public void setToCommentId(Long toCommentId) {
        this.toCommentId = toCommentId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}
