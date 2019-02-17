package jzl.sysu.cn.phonewallpaperfrontend;

import okhttp3.MediaType;

public class Constants {
    public static final String EMULATOR_PC_IP = "10.0.2.2";
    public static final String HOME_PC_IP = "192.168.101.2";
    public static final String SCHOOL_PC_IP = "192.168.199.181";
    public static final String PC_IP = HOME_PC_IP;

    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    // activity request code
    public static final int REQUEST_CHANGE_USER_CONFIG = 12;
    public final static int REQUEST_LOAD_IMAGE = 13;
    public final static int REQUEST_CAMERA_IMAGE = 14;

    // 评论区一次加载的数量
    public final static int WALLPAPER_PAGE_SIZE = 21;
    public final static int COMMENT_PAGE_SIZE = 10;

    // shared preferences names.
    public final static String LOGIN_HELPER = "login_helper";
    public final static String LOGIN_HELPER_COOKIE = "cookie";
    public final static String ROOT_FOLDER = "my_wallpaper";
    public final static String LOCAL_WALLPAPER = "local_wallpaper";
    public final static double WALLPAPER_SCALE = 1.6;

    // handler
    public final static int UPLOAD_HANDLER_INDEX = 1;

    // notification id
    public static final int NOTIFICATION_ID_UPLOAD = 1;
}
