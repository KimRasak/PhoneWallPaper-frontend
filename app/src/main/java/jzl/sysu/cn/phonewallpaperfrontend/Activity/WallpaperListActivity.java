package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.Fragment.WallPaperListContentFragment;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.SectionsPagerAdapter;

public class WallpaperListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] sortMethods = new String[]{"rank", "new"};
    private String[] titles = new String[]{"热门","最新"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_list);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        tabLayout = findViewById(R.id.sort_method_tab_layout);
        viewPager = findViewById(R.id.wallpaper_list_content);

        for(int i = 0; i < titles.length; i++){
            fragments.add(WallPaperListContentFragment.newInstance(category, sortMethods[i]));
            tabLayout.addTab(tabLayout.newTab());
        }

        tabLayout.setupWithViewPager(viewPager,false);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

        for(int i = 0; i < titles.length; i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }

        // ReplaceRepoContent(WallPaperListContentFragment.newInstance(category));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void ReplaceRepoContent(Fragment fragment) {
//        FragmentManager fm = getSupportFragmentManager();
//        fm.beginTransaction()
//                .replace(R.id.repo_content_container, fragment)
//                .commit();
    }
}
