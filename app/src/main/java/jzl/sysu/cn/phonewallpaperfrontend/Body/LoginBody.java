package jzl.sysu.cn.phonewallpaperfrontend.Body;

import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;

public class LoginBody {
    private String openid;
    private String accessToken;
    private Long expiresTime;
    private String auth;

    public LoginBody(String openid, String accessToken, Long expiresTime, String auth) {
        this.openid = openid;
        this.accessToken = accessToken;
        this.expiresTime = expiresTime;
        this.auth = auth;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Long expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
