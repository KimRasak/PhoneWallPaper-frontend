package jzl.sysu.cn.phonewallpaperfrontend.Page;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Activity.UploadActivity;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.ViewWallpaperActivity;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.LocalRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.UserMenuRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.Model.MenuItem;
import jzl.sysu.cn.phonewallpaperfrontend.RecyclerView.AutofitRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.Model.LocalWallpaper;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.LoginFragment;
import jzl.sysu.cn.phonewallpaperfrontend.LocalHelper;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.UserInfoFragment;
import jzl.sysu.cn.phonewallpaperfrontend.Util;

public class UserPgae extends Fragment implements LocalRecyclerViewAdapter.ItemClickListener, UserMenuRecyclerViewAdapter.ItemClickListener {

    // 用户菜单栏
    private RecyclerView rvUserMenu;

    // 本地壁纸栏
    private LinearLayout localLayout; // 有壁纸时显示
    private AutofitRecyclerView rvLocal;
    private LocalRecyclerViewAdapter localAdapter; // 本地壁纸栏目
    private ConstraintLayout noWallpaperLayout; // 无壁纸时显示

    public UserPgae() {
        // Required empty public constructor
    }

    public static UserPgae newInstance(String param1, String param2) {
        return new UserPgae();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.page_user, container, false);

        // find views.
        rvUserMenu = view.findViewById(R.id.user_menu);
        rvLocal = view.findViewById(R.id.rv_local);
        localLayout = view.findViewById(R.id.local_layout);
        noWallpaperLayout = view.findViewById(R.id.no_wallpaper_layout);

        return view;
    }

    @Override
    public void onResume() {

        // 未登录显示登陆界面， 登陆则显示用户界面。
        LoginHelper helper = LoginHelper.getInstance();

        // 登陆QQ
        if (helper.getTencent() != null)
            Log.v(Constants.LOG_TAG, String.format("登陆状态: %b, openid: %s", helper.isLoggedIn(getActivity()), helper.getTencent().getOpenId()));

        changeUserFragment(helper.isLoggedIn(getActivity()));
        initUserMenu();
        initLocalLayout();
        super.onResume();
    }

    public void changeUserFragment(boolean isLoggedIn) {
        if (isLoggedIn) {
            LoginHelper helper = LoginHelper.getInstance();
            Fragment fragment = UserInfoFragment.newInstance(helper.getUserIcon(), helper.getUserName(), helper.getSignature());
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.user_info_container, fragment)
                    .commit();
        }
        else {
            Fragment fragment = new LoginFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.user_info_container, fragment)
                    .commit();
        }
    }
    private void initUserMenu() {
        rvUserMenu.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // 设置Adapter
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.drawable.ic_baseline_upload_24px, "上传壁纸"));
        UserMenuRecyclerViewAdapter adapter = new UserMenuRecyclerViewAdapter(getContext(), menuItems);
        adapter.setOnItemClickListener(this);
        rvUserMenu.setAdapter(adapter);
    }

    public void initLocalLayout() {
        // 每行3列图片
        int spanCount = 3;

        // 设置Adapter
        ArrayList<LocalWallpaper> data = LocalHelper.load(LocalHelper.VERTICAL_FOLDER);
        // localAdapter = new LocalRecyclerViewAdapter(getContext(), data, spanCount, Util.getWindowScale(getActivity()));
        localAdapter = new LocalRecyclerViewAdapter(getContext(), data, spanCount, Constants.WALLPAPER_SCALE);
        rvLocal.setAdapter(localAdapter);
        localAdapter.setOnItemClickListener(this);

        // 设置layout Manager
        GridLayoutManager manager = new GridLayoutManager(getActivity(), spanCount, RecyclerView.VERTICAL, false);
        rvLocal.setLayoutManager(manager);

        // 刷新Adapter
        localAdapter.notifyDataSetChanged();

        // 如果没有本地下载，则不展示该layout
        boolean hasLocal = data.size() > 0;
        displayLocalLayout(hasLocal);
    }

    private void displayLocalLayout(boolean hasLocal) {
        if (hasLocal) {
            localLayout.setVisibility(View.VISIBLE);
            noWallpaperLayout.setVisibility(View.GONE);
        } else {
            noWallpaperLayout.setVisibility(View.VISIBLE);
            localLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWallpaperItemClick(View view, int position) {
        LocalWallpaper wallpaper = localAdapter.get(position);
        String path = wallpaper.getImgSrc();
        final Bitmap bitmap = BitmapFactory.decodeFile(path);
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("是否设置为壁纸？")
            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                DisplayMetrics metrics = Util.setWallpaperManagerFitScreen(getActivity());
                // Bitmap containScreen = Util.scaleBitmapToContainScreen(bitmap, metrics);
                Bitmap wallpaper = Util.centerCrop(bitmap, metrics);
                // Bitmap wallpaper = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels, metrics.heightPixels, true);
                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                try {
                    wallpaperManager.setBitmap(wallpaper);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }).setNegativeButton("否", null);
        normalDialog.show();
    }

    @Override
    public void onWallpaperItemLongClick(View view, final int position) {
        final Long wallpaperId = localAdapter.get(position).getWallpaperId();

        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("是否删除该壁纸？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocalHelper.remove(LocalHelper.VERTICAL_FOLDER, wallpaperId);
                        localAdapter.remove(position);
                        localAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("否", null);
        normalDialog.show();

    }

    @Override
    public void onMenuItemClick(View view, int position) {
        if (position == UserMenuRecyclerViewAdapter.ITEM_UPLOAD_WALLPAPER) {
            Intent intent = new Intent(getActivity(), UploadActivity.class);
            startActivity(intent);
        }
    }
}
