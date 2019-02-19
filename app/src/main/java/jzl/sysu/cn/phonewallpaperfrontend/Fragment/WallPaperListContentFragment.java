package jzl.sysu.cn.phonewallpaperfrontend.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

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
import jzl.sysu.cn.phonewallpaperfrontend.Body.PageBody;
import jzl.sysu.cn.phonewallpaperfrontend.RecyclerView.AutofitRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoadMoreFooterView;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.WallPaperRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Response.PageResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;

public class WallPaperListContentFragment extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {
    SwipeToLoadLayout wallpaperSwipeLayout;
    private WallPaperRecyclerViewAdapter adapter;
    private String category;
    private String sort;

    private TextView tvNetworkError;
    boolean flag = true;

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
        wallpaperSwipeLayout = view.findViewById(R.id.wallpaper_swipe_layout);
        LoadMoreFooterView swipe_load_more_footer = view.findViewById(R.id.swipe_load_more_footer);
        AutofitRecyclerView rv_wallpapers = view.findViewById(R.id.swipe_target);
        tvNetworkError = view.findViewById(R.id.tvNetworkError);

        // 设置滑动Layout的底部。
        wallpaperSwipeLayout.setLoadMoreFooterView(swipe_load_more_footer);

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
        final LoadWallpaperListener loadWallpaperListener = new LoadWallpaperListener();
        wallpaperSwipeLayout.setOnLoadMoreListener(loadWallpaperListener);
        // 仅当用newInstance调入该Fragment时，category才会被赋值。xml的<fragment>标签会自动创造一次该fragment。
        if (category != null && sort != null) {
            loadWallpaperListener.loadWallPaperIfEmpty();
        }

        // 切换界面，加载图片
        tvNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWallpaperListener.loadWallPaperIfEmpty();
                wallpaperSwipeLayout.setVisibility(View.VISIBLE);
                tvNetworkError.setVisibility(View.GONE);
            }
        });
        return view;
    }


    @Override
    public void onItemClick(View view, int position, String wallpaperSrc) {
        WallPaper wallPaper = adapter.get(position);
        Long wallpaperId = wallPaper.getId();

        // 前往“查看图片”页面
        Intent intent = new Intent(getActivity(), ViewWallpaperActivity.class);
        intent.putExtra("wallpaper", wallPaper);
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
            loadWallPaper(true);
        };

        void loadWallPaperIfEmpty() {
            if (isEmpty())
                loadWallPaper(false);
        }

        boolean isEmpty() {
            return adapter.getItemCount() == 0;
        }

        void loadWallPaper(final boolean makeToast) {
            int startNum = adapter.getItemCount();
            String category = WallPaperListContentFragment.this.category;
            String sort = WallPaperListContentFragment.this.sort;

            PageBody body = new PageBody(startNum, Constants.WALLPAPER_PAGE_SIZE, category, sort);
            WallpaperService service = ApiManager.getInstance().getWallpaperService();
            Observable<PageResponse>  ob = service.getPage(body);

            ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    private void showText() {
                        wallpaperSwipeLayout.setVisibility(View.GONE);
                        tvNetworkError.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onNext(PageResponse res) {
                        List<WallPaper> wallPapers = res.getWallpapers();
                        String hostName = res.getHostName();

                        adapter.setHostName(hostName);
                        adapter.add(wallPapers);
                        adapter.notifyDataSetChanged();
                        wallpaperSwipeLayout.setLoadingMore(false);

                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (makeToast) {
                            Util.showNetworkFailToast(getActivity());
                            wallpaperSwipeLayout.setLoadingMore(false);
                        } else showText();
                    }
                    @Override
                    public void onComplete() {}
                });
        }
    }
}
