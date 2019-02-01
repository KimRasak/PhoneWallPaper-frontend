package jzl.sysu.cn.phonewallpaperfrontend.Body;

public class SignatureBody extends AuthBody{
    private String signature;

    public SignatureBody(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
