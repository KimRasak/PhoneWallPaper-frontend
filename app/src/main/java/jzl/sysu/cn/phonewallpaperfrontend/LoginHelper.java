package jzl.sysu.cn.phonewallpaperfrontend;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

public class LoginHelper {
    /*
    * 关于QQ：QQ的第三方登陆是利用一个tencent对象。当用户用QQ第三方登陆后，会缓存openid和access_token。
    * 由于其设计缺陷，当用户退出登陆时，其本地缓存的openid和access_token并不会删除。
    * 因此，需要用SharedPreferences保存一个boolean变量，来表示QQ是否登陆。
    * 初次打开APP时，要检查并载入Token缓存。
    * */
    private static final LoginHelper instance = new LoginHelper();

    private final String SCOPE_QQ = "get_simple_userinfo";
    private final String IS_QQ_LOGGED = "is_qq_logged";
    private final String SHARE_PREFERENCE_NAME = "login_helper";
    private final String USER_ID = "user_id";
    static Tencent tencent;
    Long userId;
    String auth;
    static Context context;
    static String APP_ID;



    public static LoginHelper getInstance(Context applicationContext) {
        context  = applicationContext;
        APP_ID = context.getString(R.string.APP_ID);
        tencent = Tencent.createInstance(APP_ID, context);
        return instance;
    }

    private LoginHelper() {

    }

    public void logOut(Context activity) {
        if (getQQLogStatusLocally(activity)) {
            logOutQQ(activity);
        }
        setUserId(null);
        setUserIdLocally(activity, null);
    }

    public void logInQQ(Activity activity, IUiListener listener) {
        tencent.login(activity, SCOPE_QQ, listener);
    }

    public void logOutQQ(Context activity) {
        setQQLogStatusLocally(activity, false);
        tencent.logout(activity);
    }

    public boolean isLoggedIn(Activity activity) {
        return checkQQLoggedIn(activity);
    }

    private boolean checkQQLoggedIn(Context activity) {
        // 判断已登陆的条件有两个：本地保存的session有效，且本地存储的登陆状态为“是”。
        boolean isLoggedIn = tencent.checkSessionValid(APP_ID) && getQQLogStatusLocally(activity);
        if (isLoggedIn && tencent.getOpenId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void setQQLogStatusLocally(Context activity, boolean isLogged) {
        SharedPreferences sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= sp.edit();
        edit.putBoolean(IS_QQ_LOGGED, isLogged);
        edit.commit();
    }

    private boolean getQQLogStatusLocally(Context activity) {
        SharedPreferences sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_QQ_LOGGED, false);
    }

    public void initTencent() {
        // APP启动时调用，查看session是否合法，并读取。
        boolean sessionValid = tencent.checkSessionValid(APP_ID);
        if (sessionValid) {
            JSONObject session = tencent.loadSession(APP_ID);
            tencent.initSessionCache(session);
        }
    }

    public void setQQInfo(String open_id, String acess_token, long expires_in) {
        String expiresIn = String.valueOf(expires_in);
        tencent.setOpenId(open_id);
        tencent.setAccessToken(acess_token, expiresIn);
    }

    public void getQQUserInfo(Context context, IUiListener listener) {
        UserInfo info = new UserInfo(context, tencent.getQQToken());
        info.getUserInfo(listener);
    }

    public static Tencent getTencent() {
        return tencent;
    }

    public String getOpenId() {
        return tencent.getOpenId();
    }

    public String getAccessToken() {
        return tencent.getAccessToken();
    }


    public void setUserIdLocally(Context activity, Long userId) {
        SharedPreferences sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= sp.edit();
        if (userId == null)
            edit.remove(USER_ID);
        else
            edit.putLong(USER_ID, userId);
        edit.apply();
    }

    public void loadLocalUserId(Context activity) {
        SharedPreferences sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Long userId = sp.getLong(USER_ID, 1);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
