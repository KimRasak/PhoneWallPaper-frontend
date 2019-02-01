package jzl.sysu.cn.phonewallpaperfrontend.Response;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Model.Comment;

public class ClickResponse {
    private List<Comment> comments;
    private Boolean like;
    private Boolean collect;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public Boolean getCollect() {
        return collect;
    }

    public void setCollect(Boolean collect) {
        this.collect = collect;
    }
}
