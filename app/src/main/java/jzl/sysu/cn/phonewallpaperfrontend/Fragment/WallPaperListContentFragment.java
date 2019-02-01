package jzl.sysu.cn.phonewallpaperfrontend.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.ViewWallpaperActivity;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.WallpaperService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.ClickBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.PageBody;
import jzl.sysu.cn.phonewallpaperfrontend.RecyclerView.AutofitRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoadMoreFooterView;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.WallPaperRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Response.PageResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WallPaperListContentFragment extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {
    SwipeToLoadLayout wallpaper_swipe_layout;
    private WallPaperRecyclerViewAdapter adapter;
    private String category;
    private String sort;

    private static final String CLICK_URL = "http://" + Constants.PC_IP +":9090/wallpaper/click";

    public WallPaperListContentFragment() {
        // Required empty public constructor
    }

    public static WallPaperListContentFragment newInstance(String category, String sort) {
        WallPaperListContentFragment fragment = new WallPaperListContentFragment();
        fragment.category = category;
        fragment.sort = sort;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallpaper_list_content, container, false);

        // Find views by ids.
        wallpaper_swipe_layout = view.findViewById(R.id.wallpaper_swipe_layout);
        LoadMoreFooterView swipe_load_more_footer = view.findViewById(R.id.swipe_load_more_footer);
        AutofitRecyclerView rv_wallpapers = view.findViewById(R.id.swipe_target);

        // 设置滑动Layout的底部。
        wallpaper_swipe_layout.setLoadMoreFooterView(swipe_load_more_footer);

        // 设置RecyclerView为固定高度。
        rv_wallpapers.setHasFixedSize(true);

        // 设置RecyclerView的Adapter。
        int spanCount = rv_wallpapers.getSpanCount();
        ArrayList<WallPaper> wallPapers = new ArrayList<>();
        // double scale = Util.getWindowScale(getActivity());
        double scale = Constants.WALLPAPER_SCALE;
        adapter = new WallPaperRecyclerViewAdapter(view.getContext(), wallPapers, spanCount, scale);
        adapter.setClickListener(this); // 监听点击事件。
        rv_wallpapers.setAdapter(adapter);

        // 设置上拉刷新的listener
        WallPaperListContentFragment.LoadWallpaperListener loadWallpaperListener = new WallPaperListContentFragment.LoadWallpaperListener();
        wallpaper_swipe_layout.setOnLoadMoreListener(loadWallpaperListener);

        // 仅当用newInstance调入该Fragment时，category才会被赋值。xml的<fragment>标签会自动创造一次该fragment。
        if (category != null && sort != null)
            loadWallpaperListener.loadWallPaperIfEmpty();
        return view;
    }


    @Override
    public void onItemClick(View view, int position, String wallpaperSrc) {
        WallPaper wallPaper = adapter.get(position);
        Long wallpaperId = wallPaper.getId();

        // 前往“查看图片”页面
        Intent intent = new Intent(getActivity(), ViewWallpaperActivity.class);
        int likeNum = wallPaper.getLikeNum();
        intent.putExtra("wallpaperId", wallpaperId);
        intent.putExtra("wallpaperSrc", wallpaperSrc);
        intent.putExtra("likeNum", likeNum);
        startActivity(intent);
    }

    public class LoadWallpaperListener implements OnLoadMoreListener {
        // 触发上拉加载事件时，调用该方法。
        @Override
        public void onLoadMore() {
            loadWallPaper();
        };

        void loadWallPaperIfEmpty() {
            if (isEmpty())
                loadWallPaper();
        }

        boolean isEmpty() {
            return adapter.getItemCount() == 0;
        }

        void loadWallPaper() {
            int startNum = adapter.getItemCount();
            String category = WallPaperListContentFragment.this.category;
            String sort = WallPaperListContentFragment.this.sort;

            PageBody body = new PageBody(startNum, Constants.WALLPAPER_PAGE_SIZE, category, sort);
            WallpaperService service = ApiManager.getInstance().getWallpaperService();
            Observable<PageResponse> ob = service.getPage(body);

            ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onNext(PageResponse res) {
                        List<WallPaper> wallPapers = res.getWallpapers();
                        String hostName = res.getHostName();

                        adapter.setHostName(hostName);
                        adapter.add(wallPapers);
                        adapter.notifyDataSetChanged();
                        wallpaper_swipe_layout.setLoadingMore(false);

                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Util.showNetworkFailToast(getActivity());
                        wallpaper_swipe_layout.setLoadingMore(false);
                    }
                    @Override
                    public void onComplete() {}
                });
        }
    }
}
