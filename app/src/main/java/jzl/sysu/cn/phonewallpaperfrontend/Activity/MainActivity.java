package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mobstat.StatService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Fragment.LoginFragment;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.Page.RecommendationPgae;
import jzl.sysu.cn.phonewallpaperfrontend.Page.RepoPgae;
import jzl.sysu.cn.phonewallpaperfrontend.Page.UserPgae;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar
        .OnTabSelectedListener, ViewPager.OnPageChangeListener,
        LoginFragment.LoginFragmentListener{
    private ViewPager viewPager;
    private BottomNavigationBar bottomNavigationBar;

    // 各个页面

    private RecommendationPgae recommendationPgae;
    private RepoPgae repoPgae;
    private UserPgae userPgae;

    private LoginHelper.QQLoginListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initBottomNavitionBar();
        initViewPager();
        initBaidu();
    }

    private void initBaidu() {
        // 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造成启动次数偏高
        // 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
        StatService.start(this);
        StatService.setDebugOn(true);
    }

    private void findView() {
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
    }

    private void initBottomNavitionBar() {
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // bottomNavigationBar.setPadding(0, 10, 10, 0);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_outline_favorite_border_24px, "推荐"))
                .addItem(new BottomNavigationItem(R.drawable.ic_outline_color_lens_24px, "图库"))
                .addItem(new BottomNavigationItem(R.drawable.ic_outline_person_pin_24px, "我的"))
                .initialise();
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();

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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {
        bottomNavigationBar.selectTab(position);
    }
    @Override
    public void onPageScrollStateChanged(int state) {}
    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position);
    }
    @Override
    public void onTabUnselected(int position) {}
    @Override
    public void onTabReselected(int position) {}

    // 当点击某个登陆按键时处理。
    @Override
    public void doLogin(String auth) {
        if (auth.equals(LoginHelper.AUTH_QQ))
            doLoginQQ();
    }

    public void doLoginQQ() {
        // 登陆QQ
        final LoginHelper helper = LoginHelper.getInstance();
        helper.logInQQ(MainActivity.this, listener);
    }

    public static final String TAG_EXIT = "exit";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }
}
