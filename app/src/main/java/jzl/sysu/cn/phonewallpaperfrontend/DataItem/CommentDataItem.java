package jzl.sysu.cn.phonewallpaperfrontend.DataItem;

public class CommentDataItem {
    // 评论信息
    private String cid;
    private String wallpaperId;
    private String content;
    private String toCommentId;
    private String fromUesrId;
    private String toUserId;

    // 评论人信息
    private String userIcon; // 头像链接
    private String userName;
    private String toUserName;
    private byte[] userIconBytes;

    public CommentDataItem(String cid, String wallpaperId, String content,
                           String toCommentId, String fromUesrId, String toUserId,
                           String userIcon, String userName, String toUserName) {
        this.cid = cid;
        this.wallpaperId = wallpaperId;
        this.content = content;
        this.toCommentId = toCommentId;
        this.fromUesrId = fromUesrId;
        this.toUserId = toUserId;
        this.userIcon = userIcon;
        this.userName = userName;
        this.toUserName = toUserName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(String wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToCommentId() {
        return toCommentId;
    }

    public void setToCommentId(String toCommentId) {
        this.toCommentId = toCommentId;
    }

    public String getFromUesrId() {
        return fromUesrId;
    }

    public void setFromUesrId(String fromUesrId) {
        this.fromUesrId = fromUesrId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
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

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public byte[] getUserIconBytes() {
        return userIconBytes;
    }

    public void setUserIconBytes(byte[] userIconBytes) {
        this.userIconBytes = userIconBytes;
    }
}
