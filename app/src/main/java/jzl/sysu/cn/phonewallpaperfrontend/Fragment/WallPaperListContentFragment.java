package jzl.sysu.cn.phonewallpaperfrontend.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import jzl.sysu.cn.phonewallpaperfrontend.AutofitRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoadMoreFooterView;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.WallPaperDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.WallPaperRecyclerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WallPaperListContentFragment extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {
    private WallPaperRecyclerViewAdapter adapter;
    private String category;
    private String sort;

    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

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
        final SwipeToLoadLayout wallpaper_swipe_layout = view.findViewById(R.id.wallpaper_swipe_layout);
        LoadMoreFooterView swipe_load_more_footer = view.findViewById(R.id.swipe_load_more_footer);
        AutofitRecyclerView rv_wallpapers = view.findViewById(R.id.swipe_target);

        // 设置滑动Layout的底部。
        wallpaper_swipe_layout.setLoadMoreFooterView(swipe_load_more_footer);

        // 设置RecyclerView为固定高度。
        rv_wallpapers.setHasFixedSize(true);

        // 设置RecyclerView的Adapter。
        int spanCount = rv_wallpapers.getSpanCount();
        ArrayList<WallPaperDataItem> wallPaperDataItems = new ArrayList<>();
        adapter = new WallPaperRecyclerViewAdapter(view.getContext(), wallPaperDataItems, spanCount);
        adapter.setClickListener(this); // 监听点击事件。
        rv_wallpapers.setAdapter(adapter);

        // 设置上拉刷新的listener
        WallPaperListContentFragment.LoadWallpaperListener loadWallpaperListener = new WallPaperListContentFragment.LoadWallpaperListener(wallpaper_swipe_layout, adapter);
        wallpaper_swipe_layout.setOnLoadMoreListener(loadWallpaperListener);

        // 仅当用newInstance调入该Fragment时，category才会被赋值。xml的<fragment>标签会自动创造一次该fragment。
        if (category != null && sort != null)
            loadWallpaperListener.loadWallPaperIfEmpty();
        return view;
    }

    private RequestBody createPageRequestBody(String category, int pageNum, int pageSize, String sort) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("category", category);
            requestJsonObject.put("sort", sort);
            requestJsonObject.put("pageNum", String.valueOf(pageNum));
            requestJsonObject.put("pageSize", String.valueOf(pageSize));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("RepoPgae", requestJsonObject.toString());
        // application/json
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
        return requestBody;
    }

    @Override
    public void onItemClick(View view, int position) {
        // 点击某张图片后执行。
        Log.i("fragment", "点击了第 " + position + "项（0开头）");
        boolean image_loaded = adapter.getItem(position).getImgBytes() != null;
        Toast.makeText(view.getContext(), "加载第" + position + "张图片.图片尺寸：" + view.getWidth() + " " + view.getHeight(), Toast.LENGTH_SHORT).show();

        // 获取WallPaperDataItem
        WallPaperDataItem dataItem = adapter.getItem(position);
        String wallpaperId = dataItem.getId();
        String wallpaperSrc = dataItem.getImgSrc();
        int likeNum = dataItem.getLikeNum();

        // 创建Intent
        Intent intent = new Intent(getActivity(), ViewWallpaperActivity.class);
        intent.putExtra("wallpaperId", wallpaperId);
        intent.putExtra("wallpaperSrc", wallpaperSrc);
        intent.putExtra("likeNum", likeNum);
        startActivity(intent);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class LoadWallpaperListener implements OnLoadMoreListener {
        private static final String WALLPAPER_LIST_URL = "http://" + Constants.PC_IP +":9090/wallpaper/list";
        static final int PAGE_SIZE = 20;
        private SwipeToLoadLayout wallpaper_swipe_layout;
        private WallPaperRecyclerViewAdapter rv_adapter; // recyclerView的adapter。

        public LoadWallpaperListener (SwipeToLoadLayout wallpaper_swipe_layout, WallPaperRecyclerViewAdapter adapter) {
            this.wallpaper_swipe_layout = wallpaper_swipe_layout;
            this.rv_adapter = adapter;
        }

        // 触发上拉加载事件时，调用该方法。
        @Override
        public void onLoadMore() {
            loadWallPaper();
        };

        public void loadWallPaperIfEmpty() {
            if (isEmpty())
                loadWallPaper();
        }

        public boolean isEmpty() {
            return adapter.getItemCount() == 0;
        }

        public void loadWallPaper() {
            String category = WallPaperListContentFragment.this.category;
            int itemCount = adapter.getItemCount();
            int pageSize = PAGE_SIZE;
            int pageNum = itemCount / PAGE_SIZE;
            String sort = WallPaperListContentFragment.this.sort;
            String url = WALLPAPER_LIST_URL; // 手机应当连接本地wifi，并访问pc的本地ip

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = createPageRequestBody(category, pageNum, pageSize, sort);
            Request request = new Request.Builder().url(url).post(requestBody).build();

            // 接收壁纸信息的回调函数。
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("RepoPgae", e.getMessage());
                    e.printStackTrace();
                    wallpaper_swipe_layout.setLoadingMore(false);

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
                        wallpaper_swipe_layout.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("RepoPgae", "触发notifyDataSetChanged. Adpater内的壁纸数: " + adapter.getItemCount());
                                adapter.notifyDataSetChanged();
                                wallpaper_swipe_layout.setLoadingMore(false);
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
