package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class UserNameBody extends AuthBody{
    private String userName;

    public UserNameBody(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
