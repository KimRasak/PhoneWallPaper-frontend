package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Fragment.LoginFragment;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.UserInfoFragment;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.Page.RecommendationPgae;
import jzl.sysu.cn.phonewallpaperfrontend.Page.RepoPgae;
import jzl.sysu.cn.phonewallpaperfrontend.Page.UserPgae;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.SectionsPagerAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar
        .OnTabSelectedListener, ViewPager.OnPageChangeListener,
        RecommendationPgae.OnFragmentInteractionListener, UserPgae.OnFragmentInteractionListener,
        LoginFragment.LoginFragmentListener,
        UserInfoFragment.OnFragmentInteractionListener{
    private ViewPager viewPager;
    private BottomNavigationBar bottomNavigationBar;
    private List fragments;

    // 各个页面

    private RecommendationPgae recommendationPgae;
    private RepoPgae repoPgae;
    private UserPgae userPgae;


    private String LOGIN_URL = "http://" + jzl.sysu.cn.phonewallpaperfrontend.Constants.PC_IP + ":9090/user/login";
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");
    LoginListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initBottomNavitionBar();
        initViewPager();
        initLoginHelper();
    }

    private void initLoginHelper() {
        LoginHelper helper = LoginHelper.getInstance(getApplicationContext());
        helper.loadLocalUserId(MainActivity.this);
        helper.initTencent();
    }

    private void findView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        bottomNavigationBar = (BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
    }

    private void initBottomNavitionBar() {
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_outline_favorite_border_24px, "推荐"))
                .addItem(new BottomNavigationItem(R.drawable.ic_outline_color_lens_24px, "图库"))
                .addItem(new BottomNavigationItem(R.drawable.ic_outline_person_pin_24px, "我的"))
                .initialise();
    }

    private void initViewPager() {
        fragments = new ArrayList<Fragment>();

        // 初始化各个页面
        recommendationPgae = new RecommendationPgae();
        repoPgae = new RepoPgae();
        userPgae = new UserPgae();

        // 添加页面
        fragments.add(recommendationPgae);
        fragments.add(repoPgae);
        fragments.add(userPgae);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        bottomNavigationBar.selectTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void showResult(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(JSONObject jsonObject) {
        Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
    }

    // 当点击某个登陆按键时处理。
    @Override
    public void doLogin(String auth) {
        if (auth.equals(jzl.sysu.cn.phonewallpaperfrontend.Constants.AUTH_QQ))
            doLoginQQ();
    }

    public void doLoginQQ() {
        // 登陆QQ
        final LoginHelper helper = LoginHelper.getInstance(getApplicationContext());

        listener = new LoginListener() {
            @Override
            protected void doComplete(JSONObject values) {
                // showResult("login");
                try {
                    Log.i("logInfo", "values: " + values.toString());
                    String access_token = values.getString("access_token");
                    String open_id = values.getString("openid");
                    long expires_in = values.getLong("expires_in"); // 90天
                    long expires_time = values.getLong("expires_time"); // 90天后的时间
                    // long user_id = values.getLong("userId");
                    Log.i("logInfo", String.format("token: %s, id: %s, expires_in: %d, expires_time: %d", access_token, open_id, expires_in, expires_time));
                    // Log.i("logInfo", "user_id: " + user_id);

                    // helper.setUserId(user_id);
                    loginServer(open_id, access_token, expires_time, expires_in, jzl.sysu.cn.phonewallpaperfrontend.Constants.AUTH_QQ);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // helper.getQQUserInfo(MainActivity.this, listener);
            }
        };
        helper.logInQQ(MainActivity.this, listener);
    }

    // 到服务器后台登陆
    public void loginServer(final String openid, final String accessToken, long expiresTime, final long expires_in, String auth) {
        String url = LOGIN_URL;
        RequestBody requestBody = createLoginRequestBody(openid, accessToken, expiresTime, auth);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        OkHttpClient okHttpClient = new OkHttpClient();

        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                try {
                    JSONObject responseJsonObject = new JSONObject(responseBody.string());
                    int code = responseJsonObject.getInt("code");
                    Long userId = responseJsonObject.getLong("userId");
                    if (code == 0) {
                        // 登陆成功
                        final LoginHelper helper = LoginHelper.getInstance(getApplicationContext());

                        helper.setQQInfo(openid, accessToken, expires_in);
                        helper.setQQLogStatusLocally(MainActivity.this, true);
                        helper.setUserId(userId);
                        helper.setUserIdLocally(MainActivity.this, userId);
                        userPgae.changeUserFragment(true);

                    };
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public RequestBody createLoginRequestBody(String openid, String accessToken, long expiresTime, String auth) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("openid", openid);
            requestJsonObject.put("accessToken", accessToken);
            requestJsonObject.put("expiresTime", expiresTime);
            requestJsonObject.put("auth", auth);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 设置RequestBody。格式为application/json
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
        return requestBody;

    }

    private abstract class LoginListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            JSONObject jsonObject = (JSONObject)response;
            doComplete(jsonObject);
            // showResult(jsonObject.toString());
        }

        protected abstract void doComplete(JSONObject values);
        @Override
        public void onError(UiError e) {
            showResult("qq login, onError: " + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
            showResult("取消登陆.");
        }
    }
}
