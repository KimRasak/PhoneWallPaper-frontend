package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.Adapter.WallPaperRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.CommentDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.CommentsAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.WallPaperDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.LoadMoreFooterView;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewWallpaperActivity extends AppCompatActivity {
    // 壁纸
    ImageView wallpaper;
    String wallpaperId;
    String wallpaperSrc;

    // 热门评论、评论区
    ListView hotComments;
    SwipeToLoadLayout commentsLayout;
    ListView comments;
    LoadMoreFooterView swipe_load_more_footer;


    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        wallpaper = findViewById(R.id.wallpaper);
        hotComments = findViewById(R.id.hotComments);
        commentsLayout = findViewById(R.id.commentsSwipeLayout);
        comments = findViewById(R.id.swipe_target);
        swipe_load_more_footer = findViewById(R.id.swipe_load_more_footer);

        // 获取壁纸
        Intent intent = getIntent();
        wallpaperId = intent.getStringExtra("wallpaperId");
        wallpaperSrc = intent.getStringExtra("wallpaper");
        if (wallpaperSrc == null)
            return;
        Glide.with(this).load(wallpaperSrc).into(wallpaper);

        // 设置滑动Layout的底部。
        commentsLayout.setLoadMoreFooterView(swipe_load_more_footer);

        // 设置RecyclerView的Adapter。
        ArrayList<CommentDataItem> hotCommentsData = new ArrayList<>();
        ArrayList<CommentDataItem> commentsData = new ArrayList<>();
        CommentsAdapter hotCommentsAdapter = new CommentsAdapter(this, hotCommentsData);
        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentsData);
        comments.setAdapter(commentsAdapter);


        // 无热门评论时，隐藏hotComments。
        if (commentsData.size() == 0)
            hotComments.setVisibility(View.GONE);

    }

    public class LoadCommentsListener implements OnLoadMoreListener {
        private static final String COMMENTS_LIST_URL = "http://" + Constants.SCHOOL_PC_IP +":9090/comments";
        static final int PAGE_SIZE = 20;
        private SwipeToLoadLayout commentsLayout;
        private CommentsAdapter adapter; // recyclerView的adapter。

        public LoadCommentsListener(SwipeToLoadLayout commentsLayout, CommentsAdapter adapter) {
            this.commentsLayout = commentsLayout;
            this.adapter = adapter;
        }

        // 触发上拉加载事件时，调用该方法。
        @Override
        public void onLoadMore() {

        }

        public void loadWallPaperIfEmpty() {
            if (isEmpty())
                loadWallPaper();
        }

        public boolean isEmpty() {
            return adapter.getCount() == 0;
        }


        public void loadWallPaper() {
            String wallpaperId = ViewWallpaperActivity.this.wallpaperId;
            String wallpaperSrc = ViewWallpaperActivity.this.wallpaperSrc;
            int itemCount = adapter.getCount();
            int pageSize = PAGE_SIZE;
            int pageNum = itemCount / PAGE_SIZE;
            String url = COMMENTS_LIST_URL; // 手机应当连接本地wifi，并访问pc的本地ip

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = createCommentRequestBody(wallpaperId, pageNum, pageSize);
            Request request = new Request.Builder().url(url).post(requestBody).build();

            // 接收壁纸信息的回调函数。
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("ViewActivity", e.getMessage());
                    e.printStackTrace();
                    commentsLayout.setLoadingMore(false);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    try {
                        JSONArray wallPaperJsonArray = new JSONArray(responseString);

                        for (int i = 0; i < wallPaperJsonArray.length(); i++) {
                            JSONObject itemJsonObject = wallPaperJsonArray.getJSONObject(i);

                            // 评论信息
                            String cid = itemJsonObject.getString("cid");
                            String wallpaperId = itemJsonObject.getString("wallpaperId");
                            String content = itemJsonObject.getString("content");
                            String toCommentId = itemJsonObject.getString("toCommentId");
                            String fromUserId = itemJsonObject.getString("fromUserId");
                            String toUserId = itemJsonObject.getString("toUserId");

                            // 评论人信息
                            String userIcon = itemJsonObject.getString("userIcon");
                            String userName = itemJsonObject.getString("userName");
                            String toUserName = itemJsonObject.getString("toUserName");

                            CommentDataItem dataItem = new CommentDataItem(cid, wallpaperId, content,
                                toCommentId, fromUserId, toUserId,
                                userIcon, userName, toUserName);


                            adapter.addDataItem(dataItem);
                        }

                        commentsLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                commentsLayout.setLoadingMore(false);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public RequestBody createCommentRequestBody(String wallpaperId, int pageNum, int pageSize) {
            JSONObject requestJsonObject = new JSONObject();
            try {
                requestJsonObject.put("id", wallpaperId);
                requestJsonObject.put("pageNum", pageNum);
                requestJsonObject.put("pageSize", pageSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("ViewActivity", requestJsonObject.toString());
            // application/json
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
            return requestBody;
        }
    };
}
