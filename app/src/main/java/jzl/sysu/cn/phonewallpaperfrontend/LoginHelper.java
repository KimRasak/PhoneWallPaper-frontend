package jzl.sysu.cn.phonewallpaperfrontend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import com.tencent.mm.opensdk.openapi.*;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.MainActivity;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.UserService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LoginBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LoginQQBody;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Response.LoginResponse;

import static jzl.sysu.cn.phonewallpaperfrontend.Constants.LOGIN_HELPER;

public class LoginHelper {
    /*
    * 关于QQ：QQ的第三方登陆是利用一个tencent对象。当用户用QQ第三方登陆后，会缓存openid和access_token。
    * 由于其设计缺陷，当用户退出登陆时，其本地缓存的openid和access_token并不会删除。
    * 因此，需要用SharedPreferences保存一个boolean变量，来表示QQ是否登陆。
    * 初次打开APP时，要检查并载入Token缓存。
    * */
    private static final LoginHelper instance = new LoginHelper();

    private static String QQ_APP_ID;
    private static String WX_APP_ID;

    // shared preference属性名
    private final String IS_QQ_LOGGED = "is_qq_logged"; // shared preference 属性

    private static Tencent tencent;
    private IWXAPI wxApi; // IWXAPI 是第三方app和微信通信的openApi接口

    /* 用户个人信息 */
    private Long userId;
    private String userIcon;
    private String userName;
    private String signature;

    private String auth;
    public final static String AUTH_QQ = "qq";

    public static LoginHelper getInstance() {
        return instance;
    }

    private LoginHelper() {

    }

    /* 初始化相关 */
    public boolean init(Activity activity) {
        // APP开启时调用。
        // 初始化tencent用户信息（如果有session则载入）

        // APP启动时调用，检查tencent接口的session是否合法，
        // 若合法则读取，若已过期/不合法则不读取。
        QQ_APP_ID = activity.getString(R.string.QQ_APP_ID);
        WX_APP_ID = activity.getString(R.string.WX_APP_ID);
        tencent = Tencent.createInstance(QQ_APP_ID, activity.getApplicationContext());
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID, true);
        wxApi.registerApp(WX_APP_ID);


        if (loadQQSessionIfValid() && isQQLocalLoggedIn(activity)) {
            setAuth(AUTH_QQ);
            return true;
        } else
            return false;
    }

    public void logOut(Activity activity) {
        // 用户登出。

        // 若登陆QQ，则登出QQ。
        if (isQQLoggedIn(activity)) {
            logOutQQ(activity);
        }

        logOutServer(activity);
    }
    public boolean isLoggedIn(Activity activity) {
        return isServerLoggedIn();
    }

    public void setAuth(String auth) { this.auth = auth; }
    public String getAuth() { return auth; }

    /* 服务器相关登录接口 */
    private boolean isServerLoggedIn() {
        return userId != null;
    }
    private void logOutServer(final Context context) {
        // 清空userId。
        UserService service = ApiManager.getInstance().getUserService();
        Observable<CodeResponse> ob = service.logout();
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CodeResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(CodeResponse codeResponse) {
                        if (codeResponse.isFail()) {
                            return;
                        }
                        ApiManager.clearCookies(context);
                        Log.v(Constants.LOG_TAG, "logout success.");

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(MainActivity.TAG_EXIT, true);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {}
                });
        this.userId = null;
    }

    /* QQ相关的登录接口 */
    public Tencent getTencent() { return tencent; }

    public void logInQQ(Activity activity, QQLoginListener listener) {
        if (listener == null)
            listener = new QQLoginListener(activity);
        String SCOPE_QQ = "get_simple_userinfo";
        tencent.login(activity, SCOPE_QQ, listener);
    }
    private void logOutQQ(Activity activity) {
        setQQLocalLoggedIn(activity, false);
        tencent.logout(activity);
    }

    // QQ session。
    private boolean isQQSessionValid() {
        // 检测QQ session的合法性、是否过期。
        return tencent.checkSessionValid(QQ_APP_ID);
    }
    private boolean isQQSessionLoaded() {
        // 若tencent缓存了token，则已加载session。
        return tencent.getQQToken() != null;
    }
    private void loadQQSession() {
        JSONObject session = tencent.loadSession(QQ_APP_ID);
        tencent.initSessionCache(session);
    }
    private boolean loadQQSessionIfValid() {
        boolean isSessionValid = tencent.checkSessionValid(QQ_APP_ID);
        if (isSessionValid) {
            JSONObject session = tencent.loadSession(QQ_APP_ID);
            tencent.initSessionCache(session);
            return true;
        }
        return false;
    }

    // QQ本地登陆状态
    private void setQQLocalLoggedIn(Activity activity, boolean isLogged) {
        SharedPreferences sp = activity.getSharedPreferences(LOGIN_HELPER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= sp.edit();
        edit.putBoolean(IS_QQ_LOGGED, isLogged);
        edit.commit();
    }
    private boolean isQQLocalLoggedIn(Activity activity) {
        // 返回是否已用QQ第三方登陆
        SharedPreferences sp = activity.getSharedPreferences(LOGIN_HELPER, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_QQ_LOGGED, false);
    }

    // 判断是否QQ已完成第三方登录。
    public boolean isQQLoggedIn(Activity activity) {
        return userId == null && auth.equals(AUTH_QQ);
    }

    // 设置QQ第三方登录信息
    private void setQQInfo(String openid, String accessToken, long expiresInLongValue) {
        // 为tencent接口设置openid和access_token
        String expiresIn = String.valueOf(expiresInLongValue);
        tencent.setOpenId(openid);
        tencent.setAccessToken(accessToken, expiresIn);
    }

    /* 微信相关的登录接口 */
    public IWXAPI getWX() { return this.wxApi; }

    /* 用户openid、accessToken信息 */
    // 私有属性的getter和setter
    public String getOpenId() { return tencent.getOpenId(); }
    public String getAccessToken() { return tencent.getAccessToken(); }

    // userId
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }

    private long getExpiredIn() { return tencent.getExpiresIn(); }
    public long getExpiresTime() { return System.currentTimeMillis() / 1000 + getExpiredIn(); } // 默认过期时间总是大于当前时间。

    /* 用户个人信息 */
    public String getUserIcon() { return userIcon; }
    public void setUserIcon(String userIcon) { this.userIcon = userIcon; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public void setUserInfo(LoginResponse res) {
        setAuth(res.getAuth());
        setUserId(res.getUserId());
        setUserName(res.getNickname());
        setSignature(res.getSignature());
        setUserIcon(res.getIconPath());
    }

    public class QQLoginListener implements IUiListener {
        protected Activity activity;

        public QQLoginListener(Activity activity) {
            this.activity = activity;
        }
        @Override
        public void onError(UiError uiError) {}
        @Override
        public void onCancel() {}

        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject)o;
            doComplete(response);
        }

        public void doComplete(JSONObject values) {
            // 从QQ端读取QQ用户信息，并登陆后台。
            try {
                Log.i(Constants.LOG_TAG, "values: " + values.toString());
                String accessToken = values.getString("access_token");
                String openid = values.getString("openid");
                long expiresIn = values.getLong("expires_in"); // qq默认为90天
                long expiresTime = values.getLong("expires_time"); // 90天后的时间

                // 登陆成功
                final LoginHelper helper = LoginHelper.this;

                // 设置QQ登录信息
                helper.setQQInfo(openid, accessToken, expiresIn);
                helper.setQQLocalLoggedIn(activity, true);

                Log.i(Constants.LOG_TAG, String.format("token: %s, id: %s, expires_in: %d, expires_time: %d", accessToken, openid, expiresIn, expiresTime));
                loginServer(openid, accessToken, expiresTime, AUTH_QQ);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 到服务器后台登陆
        public void loginServer(final String openid, final String accessToken, long expiresTime, final String auth) {
            UserService service = ApiManager.getInstance().getUserService();
            LoginQQBody body = new LoginQQBody(openid, accessToken, expiresTime, auth);
            Observable<LoginResponse> ob = service.loginQQ(body);
            ob.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LoginResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {}
                        @Override
                        public void onComplete() {}
                        private void fail() { Util.showNetworkFailToast(activity); }
                        @Override
                        public void onNext(LoginResponse loginResponse) {
                            if (loginResponse.isFail()) {
                                fail();
                                return;
                            }

                            // 设置用户个人信息
                            LoginHelper.this.setUserInfo(loginResponse);

                            Log.i(Constants.LOG_TAG, "begin onQQLoggedIn");
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                        @Override
                        public void onError(Throwable e) { fail(); }
                    });
        }
    }
}
