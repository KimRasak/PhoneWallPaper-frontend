package jzl.sysu.cn.phonewallpaperfrontend.Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.WallPaperRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.WallpaperService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.RecBody;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.WallPaperListContentFragment;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.RecyclerView.GridLayoutRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.Response.PageResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RecommendationPgae extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {

    private final static int SPAN_COUNT = 3;
    private SwipeToLoadLayout swipeLayout;
    private WallPaperRecyclerViewAdapter adapter;

    private TextView tvNetworkError;

    private static final String CLICK_URL = "http://" + Constants.PC_IP +":9090/wallpaper/click";

    public RecommendationPgae() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.page_recommendation, container, false);
        GridLayoutRecyclerView rv = view.findViewById(R.id.swipe_target);
        swipeLayout = view.findViewById(R.id.wallpaper_swipe_layout);
        tvNetworkError = view.findViewById(R.id.tvNetworkError);

        // double scale = Util.getWindowScale(getActivity());
        double scale = Constants.WALLPAPER_SCALE;
        adapter = new WallPaperRecyclerViewAdapter(getActivity(), new ArrayList<WallPaper>(), SPAN_COUNT,  scale);
        rv.setAdapter(adapter);

        // 监听点击事件。
        adapter.setClickListener(this);

        // 设置上拉刷新的listener
        final LoadWallpaperListener loadWallpaperListener = new LoadWallpaperListener();
        swipeLayout.setOnLoadMoreListener(loadWallpaperListener);
        loadWallpaperListener.loadWallPaperIfEmpty();

        // 切换界面，加载图片
        tvNetworkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWallpaperListener.loadWallPaperIfEmpty();
                swipeLayout.setVisibility(View.VISIBLE);
                tvNetworkError.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onItemClick(View view, int position, String wallpaperSrc) {
        // 点击某张图片后执行。
        WallPaper wallPaper = adapter.get(position);

        // 前往“查看图片”页面
        Intent intent = new Intent(getActivity(), ViewWallpaperActivity.class);
        intent.putExtra("wallpaper", wallPaper);
        intent.putExtra("wallpaperSrc", wallpaperSrc);
        startActivity(intent);
    }

    // 上拉刷新监听器
    public class LoadWallpaperListener implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            loadWallPaper(true );
        }

        public void loadWallPaperIfEmpty() {
            if (isEmpty()) {
                loadWallPaper(false);
            }
        }

        public boolean isEmpty() {
            return adapter.getItemCount() == 0;
        }

        public void loadWallPaper(final boolean makeToast) {
            int startNum = adapter.getItemCount();

            WallpaperService service = ApiManager.getInstance().getWallpaperService();
            RecBody body = new RecBody(startNum, Constants.WALLPAPER_PAGE_SIZE);
            Observable<PageResponse> ob = service.getRec(body);
            ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onComplete() {}

                    private void showText() {
                        swipeLayout.setVisibility(View.GONE);
                        tvNetworkError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(PageResponse res) {
                        List<WallPaper> wallPapers = res.getWallpapers();
                        String hostName = res.getHostName();

                        adapter.setHostName(hostName);
                        adapter.add(wallPapers);
                        adapter.notifyDataSetChanged();
                        swipeLayout.setLoadingMore(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (makeToast) {
                            Util.showNetworkFailToast(getActivity());
                            swipeLayout.setLoadingMore(false);
                        }
                        else showText();
                    }

                });
        }
    }
}
