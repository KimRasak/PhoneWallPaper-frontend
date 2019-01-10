package jzl.sysu.cn.phonewallpaperfrontend;

import okhttp3.MediaType;

public class Constants {
    public static final String HOME_PC_IP = "192.168.101.2";
    public static final String SCHOOL_PC_IP = "192.168.199.181";
    public static final String PC_IP = SCHOOL_PC_IP;

    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    public static final int REQUEST_CHANGE_USER_CONFIG = 12;
    public final static int REQUEST_LOAD_IMAGE = 13;
    public final static int REQUEST_CAMERA_IMAGE = 14;
}
