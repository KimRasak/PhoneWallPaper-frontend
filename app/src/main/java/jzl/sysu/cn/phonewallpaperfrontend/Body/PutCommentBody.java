package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class PutCommentBody extends AuthBody {
    private Long wallPaperId;
    private Long toCommentId; // 可为空，当值为-1时表示为空。
    private String content;

    public PutCommentBody(Long wallPaperId, Long toCommentId, String content) {
        super();
        this.wallPaperId = wallPaperId;
        this.toCommentId = toCommentId;
        this.content = content;
    }

    public Long getWallPaperId() {
        return wallPaperId;
    }

    public void setWallPaperId(Long wallPaperId) {
        this.wallPaperId = wallPaperId;
    }

    public Long getToCommentId() {
        return toCommentId;
    }

    public void setToCommentId(Long toCommentId) {
        this.toCommentId = toCommentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
