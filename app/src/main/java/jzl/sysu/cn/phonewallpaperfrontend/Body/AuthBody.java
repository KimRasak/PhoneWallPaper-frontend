package jzl.sysu.cn.phonewallpaperfrontend.Body;

import android.util.Log;

import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;

public class AuthBody {
    private String openId;
    private String accessToken;
    private String auth;

    AuthBody() {
        LoginHelper helper = LoginHelper.getInstance();
        openId = helper.getOpenId();
        accessToken = helper.getAccessToken();
        auth = helper.getAuth();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
