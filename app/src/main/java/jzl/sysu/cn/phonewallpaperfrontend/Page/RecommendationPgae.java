package jzl.sysu.cn.phonewallpaperfrontend.Page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import jzl.sysu.cn.phonewallpaperfrontend.Activity.ViewWallpaperActivity;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.WallPaperRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.WallPaperDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.WallPaperListContentFragment;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RecommendationPgae extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {

    private final static int SPAN_COUNT = 3;
    static final int PAGE_SIZE = 20;
    SwipeToLoadLayout swipeLayout;
    private WallPaperRecyclerViewAdapter adapter;

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
        RecyclerView rv = view.findViewById(R.id.swipe_target);
        swipeLayout = view.findViewById(R.id.wallpaper_swipe_layout);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        adapter = new WallPaperRecyclerViewAdapter(getActivity(), new ArrayList<WallPaperDataItem>(), SPAN_COUNT);
        rv.setAdapter(adapter);

        // 监听点击事件。
        adapter.setClickListener(this);

        // 设置上拉刷新的listener
        LoadWallpaperListener loadWallpaperListener = new LoadWallpaperListener();
        swipeLayout.setOnLoadMoreListener(loadWallpaperListener);
        return view;
    }

    private RequestBody createClickRequestBody(String wallpaperId) {
        JSONObject data = new JSONObject();
        try {
            data.put("wallpaperId", wallpaperId);
            // data.put("sort", sort);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("RepoPgae", data.toString());
        return Util.buildAuthRequestBody(data, getActivity().getApplicationContext());
    }

    private void click(final WallPaperDataItem dataItem) {
        String wallpaperId = dataItem.getId();
        RequestBody requestBody = createClickRequestBody(wallpaperId);
        String url = CLICK_URL;
        Request request = new Request.Builder().url(url).post(requestBody).build();

        // 接收壁纸信息的回调函数。
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject res = null;
                try {
                    res = new JSONObject(response.body().string());
                    Log.i("wallpaper click", res.toString());

                    if (res.getInt("code") != 0)
                        throw new IllegalArgumentException();

                    if (res.getInt("code") == 0) {
                        // 获取点击项的信息
                        String wallpaperId = dataItem.getId();
                        String wallpaperSrc = dataItem.getImgSrc();
                        int likeNum = dataItem.getLikeNum();

                        // 前往“查看图片”页面
                        Intent intent = new Intent(getActivity(), ViewWallpaperActivity.class);
                        intent.putExtra("wallpaperId", wallpaperId);
                        intent.putExtra("wallpaperSrc", wallpaperSrc);
                        intent.putExtra("likeNum", likeNum);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "点击失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
// 点击某张图片后执行。
        Log.i("fragment", "点击了第 " + position + "项（0开头）");
        boolean image_loaded = adapter.getItem(position).getImgBytes() != null;
        Toast.makeText(view.getContext(), "加载第" + position + "张图片.图片尺寸：" + view.getWidth() + " " + view.getHeight(), Toast.LENGTH_SHORT).show();

        WallPaperDataItem dataItem = adapter.getItem(position);
        click(dataItem);
    }

    private RequestBody createPageRequestBody(int pageNum, int pageSize) {
        JSONObject data = new JSONObject();
        try {
            data.put("pageNum", String.valueOf(pageNum));
            data.put("pageSize", String.valueOf(pageSize));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("RepoPgae", data.toString());
        // application/json
        return Util.buildRequestBody(data);
    }

    // 上拉刷新监听器
    public class LoadWallpaperListener implements OnLoadMoreListener {
        private final static String RECOMENDATION_URL = "http://" + Constants.PC_IP +":9090/recommend/get";

        @Override
        public void onLoadMore() {

        }


        public void loadWallPaperIfEmpty() {
            if (isEmpty())
                loadWallPaper();
        }

        public boolean isEmpty() {
            return adapter.getItemCount() == 0;
        }

        public void loadWallPaper() {
            int itemCount = adapter.getItemCount();
            int pageSize = PAGE_SIZE;
            int pageNum = itemCount / PAGE_SIZE;
            String url = RECOMENDATION_URL; // 手机应当连接本地wifi，并访问pc的本地ip

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = createPageRequestBody(pageNum, pageSize);
            Request request = new Request.Builder().url(url).post(requestBody).build();

            // 接收壁纸信息的回调函数。
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("RepoPgae", e.getMessage());
                    e.printStackTrace();
                    swipeLayout.setLoadingMore(false);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseString = response.body().string();
                        JSONArray wallPaperJsonArray = new JSONArray(responseString);

                        // 提取返回信息里的壁纸条目，添加进Adapter。
                        for (int i = 0; i < wallPaperJsonArray.length(); i++) {
                            JSONObject itemJsonObject = wallPaperJsonArray.getJSONObject(i);
                            String id = itemJsonObject.getString("id");
                            String category = itemJsonObject.getString("category");
                            String imgSrc = itemJsonObject.getString("path");
                            int likeNum = itemJsonObject.getInt("likeNum");

                            WallPaperDataItem dataItem = new WallPaperDataItem(id, category, imgSrc, likeNum);
                            adapter.addDataItem(dataItem);
                        }

                        Log.i("RepoPgae", "新收到的壁纸数: "   + wallPaperJsonArray.length());
                        Log.i("RepoPgae", "内容: " + responseString);
                        swipeLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("RepoPgae", "触发notifyDataSetChanged. Adpater内的壁纸数: " + adapter.getItemCount());
                                adapter.notifyDataSetChanged();
                                swipeLayout.setLoadingMore(false);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
