package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity implements BottomNavigationBar
        .OnTabSelectedListener, ViewPager.OnPageChangeListener,
        RecommendationPgae.OnFragmentInteractionListener, UserPgae.OnFragmentInteractionListener,
        LoginFragment.LoginFragmentListener,
        UserInfoFragment.OnFragmentInteractionListener{
    private ViewPager viewPager;
    private BottomNavigationBar bottomNavigationBar;
    private List fragments;

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
        fragments.add(new RecommendationPgae());
        fragments.add(new RepoPgae());
        fragments.add(new UserPgae());

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

    @Override
    public void doLogin() {
        // 登陆QQ
        final LoginHelper helper = LoginHelper.getInstance(getApplicationContext());

        listener = new LoginListener() {
            @Override
            protected void doComplete(JSONObject values) {
                // showResult("login");
                try {
                    String access_token = values.getString("access_token");
                    String open_id = values.getString("openid");
                    String expires_in = values.getString("expires_in"); // 90天
                    String expires_time = values.getString("expires_time"); // 90天后的时间
                    Log.i("logInfo", String.format("token: %s, id: %s, expires: %s, expires_time: %s", access_token, open_id, expires_in, expires_time));

                    helper.setQQInfo(open_id, access_token, expires_in);
                    helper.setQQLogStatusLocally(MainActivity.this, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // helper.getQQUserInfo(MainActivity.this, listener);
            }
        };
        helper.logInQQ(MainActivity.this, listener);
    }

    private class LoginListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            JSONObject jsonObject = (JSONObject)response;
            doComplete(jsonObject);
            // showResult(jsonObject.toString());
        }

        protected void doComplete(JSONObject values) {

        }
        @Override
        public void onError(UiError e) {
            showResult("onError: " + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
            showResult("取消登陆.");
        }
    }
}
