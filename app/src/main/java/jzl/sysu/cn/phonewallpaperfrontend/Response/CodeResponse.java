package jzl.sysu.cn.phonewallpaperfrontend.Response;

public class CodeResponse {
	protected final static int CODE_SUCCESS = 0;
	protected final static String MESSAGE_SUCCESS = "success";
	
	protected int code;
	protected String message;

	public boolean isSuccess() {
	    return code == CODE_SUCCESS;
    }

    public boolean isFail() {
	    return code < CODE_SUCCESS;
    }

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
