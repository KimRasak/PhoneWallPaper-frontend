package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewWallpaperActivity extends AppCompatActivity implements CommentsAdapter.ItemClickListener {
    // 壁纸
    ImageView wallpaper;
    String wallpaperId;
    String wallpaperSrc;

    // 中间栏
    LinearLayout likeLayout;
    ImageView likeImage;
    int likeNum;
    TextView tvLikeNum;
    LinearLayout collectLayout;
    ImageView collectImage;
    LinearLayout downloadLayout;

    // 抬头区（位于评论区上方）
    ConstraintLayout commentsTitleLayout;
    Button btnAddComments;

    // 热门评论、评论区
    RecyclerView hotComments;
    SwipeToLoadLayout commentsLayout;
    RecyclerView comments;
    LoadMoreFooterView swipe_load_more_footer;
    LoadCommentsListener loadCommentsListener;

    final static String LIKE_WALLPAPER_URL = "http://" + Constants.PC_IP + ":9090/relationship/like";
    final static String GET_RELATIONSHIP_URL = "http://" + Constants.PC_IP + ":9090/relationship/get";
    final static String ADD_COMMENT_URL = "http://" + Constants.PC_IP +":9090/comment/put";

    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        wallpaper = findViewById(R.id.wallpaper);

        findView();

        // 从intent获取数据。
        Intent intent = getIntent();
        wallpaperId = intent.getStringExtra("wallpaperId");
        wallpaperSrc = intent.getStringExtra("wallpaperSrc");
        likeNum = intent.getIntExtra("likeNum", 0);

        if (wallpaperSrc == null)
            return;

        // 获取壁纸。
        Glide.with(this).load(wallpaperSrc).into(wallpaper);

        // 中间栏
        // 设置点赞、收藏图片的激活状态。
        checkRelationshipState();
        setLikeNum(likeNum);
        // “点赞”按钮
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLike = (boolean)likeImage.getTag();
                if (!isLike) {
                    setLikeImage(true);
                    likeWallpaper(true);

                } else {
                    setLikeImage(false);
                    likeWallpaper(false);
                }
            }
        });

        // “收藏”按钮
        collectImage.setTag(false); // 是否已收藏
        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // “设为壁纸”按钮
        downloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 设置滑动Layout的底部。
        commentsLayout.setLoadMoreFooterView(swipe_load_more_footer);

        // 设置RecyclerView的Adapter。
        ArrayList<CommentDataItem> hotCommentsData = new ArrayList<>();
        ArrayList<CommentDataItem> commentsData = new ArrayList<>();
        CommentsAdapter hotCommentsAdapter = new CommentsAdapter(this, hotCommentsData);
        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentsData);
        hotComments.setAdapter(hotCommentsAdapter);
        comments.setAdapter(commentsAdapter);

        // 设置RecyclerView的LayoutManager。
        hotComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        comments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // 设置RecyclerView点击回复
        hotCommentsAdapter.setClickListener(this);
        commentsAdapter.setClickListener(this);

        // 无热门评论时，隐藏hotComments。
        if (commentsData.size() == 0)
            hotComments.setVisibility(View.GONE);

        // 绑定“添加评论”按钮点击事件
        btnAddComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popEditCommentWindow();
            }
        });

        loadCommentsListener = new LoadCommentsListener(commentsLayout, commentsAdapter);
        commentsLayout.setOnLoadMoreListener(loadCommentsListener);
        loadCommentsListener.loadWallPaperIfEmpty();
    }

    public void findView() {
        // 中间栏
        likeLayout = findViewById(R.id.likeLayout);
        tvLikeNum = findViewById(R.id.likeNum);
        collectLayout = findViewById(R.id.collectLayout);
        downloadLayout = findViewById(R.id.downloadLayout);
        likeImage = findViewById(R.id.likeImage);
        collectImage = findViewById(R.id.collectImage);

        // 抬头区
        commentsTitleLayout = findViewById(R.id.commentsTitleLayout);
        btnAddComments = findViewById(R.id.btnAddComments);

        // 热门评论区
        hotComments = findViewById(R.id.hotComments);

        // 评论区
        commentsLayout = findViewById(R.id.commentsSwipeLayout);
        comments = findViewById(R.id.swipe_target);
        swipe_load_more_footer = findViewById(R.id.swipe_load_more_footer);
    }

    @Override
    public void onItemClick(View view, int position) {
        popEditCommentWindow();
    }

    public void popEditCommentWindow() {
        // 弹出评论框。
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewWallpaperActivity.this);
        builder.setTitle("请输入评论");
        LayoutInflater inflater = getLayoutInflater();
        final View editCommentLayout = inflater.inflate(R.layout.layout_edit_comment, (ViewGroup) findViewById(R.id.commentEditLayout));
        builder.setView(editCommentLayout);

        // 点击“是”时，发表评论。
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = editCommentLayout.findViewById(R.id.commentEditText);
                String content = editText.getText().toString();
                putCommentToServer(null, content);
            }
        });
        builder.setNegativeButton("否", null);
        builder.show();
    }

    public void putCommentToServer(String toCommentId, final String content) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = ADD_COMMENT_URL;
        Log.i("ViewWallpaper", wallpaperId);
        RequestBody requestBody = createAddCommentRequestBody(wallpaperId, toCommentId, content);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // 显示评论信息
                Looper.prepare();
                if (response.body() != null)
                    Toast.makeText(ViewWallpaperActivity.this, response.body().string() + "\n" + content, Toast.LENGTH_LONG).show();
                loadCommentsListener.loadComments();
                Looper.loop();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                e.printStackTrace();
                Toast.makeText(ViewWallpaperActivity.this, "添加评论失败", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

        });
    }

    public void setLikeImage(Boolean isLike) {
        likeImage.setTag(isLike);
        if (isLike) {
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_favorite_24px, getTheme());
            //你需要改变的颜色
            vectorDrawableCompat.setTint(getResources().getColor(R.color.red));
            likeImage.setImageDrawable(vectorDrawableCompat);
        } else {
            likeImage.setImageResource(R.drawable.ic_baseline_favorite_border_24px);
        }
    }

    public void setCollectImage(Boolean isCollect) {
        collectImage.setTag(isCollect);
        if (isCollect) {

        } else {

        }
    }

    public void likeWallpaperFail(boolean isLike) {
        Looper.prepare();
        setLikeImage(!isLike);
        setLikeNum(!isLike);
        Toast.makeText(ViewWallpaperActivity.this, "点赞/取消点赞失败", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public void likeWallpaper(final boolean isLike) {
        // 点击"点赞"按钮后执行。

        // 未登陆不可点赞。
        if (!LoginHelper.getInstance(ViewWallpaperActivity.this).isLoggedIn()) {
            Toast.makeText(ViewWallpaperActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
        }

        // 先假装点赞成功。
        setLikeNum(isLike);

        // 建立向服务器发送的Request
        String url = LIKE_WALLPAPER_URL;
        RequestBody requestBody = createLikeRequestBody(wallpaperId, isLike);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(requestBody).build();


        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                likeWallpaperFail(isLike);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    String responseString = response.body().string();
                    Log.i("LikeWallpaper", responseString);
                    try {
                        JSONObject responseJsonObject = new JSONObject(responseString);
                        int code = responseJsonObject.getInt("code");
                        String msg = responseJsonObject.getString("message");

                        if (code == 0) {
                            Looper.prepare();
                            Toast.makeText(ViewWallpaperActivity.this, "点赞/取消点赞成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else {
                            // 服务器返回失败条文，点赞失败。
                            Log.i("LikeWallpaper", msg);
                            likeWallpaperFail(isLike);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(ViewWallpaperActivity.this, "点赞/取消点赞失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }
            }
        });
    }

    public RequestBody createLikeRequestBody(String wallpaperId, boolean isLike) {
        JSONObject requestJsonObject = new JSONObject();
        try {
            // 获取授权信息
            LoginHelper helper = LoginHelper.getInstance(getApplicationContext());
            String openId = helper.getOpenId();
            String accessToken = helper.getAccessToken();

            requestJsonObject.put("openId", openId);
            requestJsonObject.put("accessToken", accessToken);
            requestJsonObject.put("wallpaperId", wallpaperId);
            requestJsonObject.put("like", isLike);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("likeWallpaper", requestJsonObject.toString());
        // 设置RequestBody。格式为application/json
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
        return requestBody;
    }

    public void changeLikeNumText(int delta) {
        int num = getLikeNum();
        setLikeNum(num + delta);
    }

    public int getLikeNum() {
        return Integer.valueOf(tvLikeNum.getText().toString());
    }

    public void setLikeNum(boolean isLike) {
        int delta = isLike ? 1 : -1;
        likeNum = likeNum + delta;
        setLikeNum(likeNum);
    }

    public void setLikeNum(int num) {
        likeNum = num;
        tvLikeNum.setText(String.valueOf(num));
    }

    public void setMiddleLayoutState(Boolean isLike, Boolean isCollect) {
        // 仅当查询用户与壁纸的点赞、收藏关系时调用。
        collectImage.setTag(isCollect);
        setLikeImage(isLike);
        setCollectImage(isCollect);
    }

    public void checkRelationshipState() {
        String url = GET_RELATIONSHIP_URL;
        RequestBody requestBody = createGetRelationshipBody(wallpaperId);
        OkHttpClient okHttpClient = new OkHttpClient();

        if (requestBody == null) {
            setMiddleLayoutState(false, false);
            Log.i("checkRelationshipState", "用户未登陆");
            return;
        }

        Request request = new Request.Builder().url(url).post(requestBody).build();

        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                setMiddleLayoutState(false, false);
                Log.i("checkRelationshipState", "与服务器连接失败");
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("checkRelationship", responseString);
                try {
                    JSONObject relationship = new JSONObject(responseString);
                    int code = relationship.getInt("code");
                    if (code == 0) {
                        Boolean isLike = relationship.getBoolean("isLike");
                        Boolean isCollect = relationship.getBoolean("isCollect");
                        setMiddleLayoutState(isLike, isCollect);
                    } else {
                        String msg = relationship.getString("message");
                        Log.i("ViewWallpaper", "get relationship, error: " + msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public RequestBody createGetRelationshipBody(String wallpaperId) {
        // 获取点赞和收藏情况。

        // 获取授权信息
        LoginHelper helper = LoginHelper.getInstance(getApplicationContext());
        Long userId = helper.getUserId();

        if (userId == null) {
            return null;
        }

        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("userId", userId);
            requestJsonObject.put("wallpaperId", wallpaperId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 测试输出
        Log.i("ViewActivity", requestJsonObject.toString());

        // 设置RequestBody。格式为application/json
        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
        return requestBody;
    }

    public RequestBody createAddCommentRequestBody(String wallpaperId, String toCommentId, String content) {
        if (toCommentId == null || toCommentId.equals("")) {
            // 是对帖子的评论。
            toCommentId = "-1";
            // 设置JSONObject
            JSONObject requestJsonObject = new JSONObject();
            try {
                // 获取授权信息
                LoginHelper helper = LoginHelper.getInstance(getApplicationContext());
                String openid = helper.getOpenId();
                String accessToken = helper.getAccessToken();

                requestJsonObject.put("openId", openid);
                requestJsonObject.put("accessToken", accessToken);
                requestJsonObject.put("wallPaperId", wallpaperId);
                requestJsonObject.put("toCommentId", toCommentId);
                requestJsonObject.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 测试输出
            Log.i("ViewActivity", requestJsonObject.toString());

            // 设置RequestBody。格式为application/json
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
            return requestBody;
        } else {
            // 是对评论的回复。
            // 设置JSONObject
            JSONObject requestJsonObject = new JSONObject();
            try {
                requestJsonObject.put("wallpaperId", wallpaperId);
                requestJsonObject.put("toCommentId", toCommentId);
                requestJsonObject.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 测试输出
            Log.i("ViewActivity", requestJsonObject.toString());

            // 设置RequestBody。格式为application/json
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
            return requestBody;
        }
    }

    public class LoadCommentsListener implements OnLoadMoreListener {
        private static final String COMMENTS_LIST_URL = "http://" + Constants.PC_IP +":9090/comment/view";
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
            loadComments();
        }

        public void loadWallPaperIfEmpty() {
            if (isEmpty())
                loadComments();
        }

        public boolean isEmpty() {
            return adapter.getItemCount() == 0;
        }


        public void loadComments() {
            String wallpaperId = ViewWallpaperActivity.this.wallpaperId;
            int itemCount = adapter.getItemCount();
            int pageSize = PAGE_SIZE;
            int startNum = itemCount;
            String url = COMMENTS_LIST_URL; // 手机应当连接本地wifi，并访问pc的本地ip

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = createLoadCommentRequestBody(wallpaperId, startNum, pageSize);
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
                        JSONArray commentJsonArray = new JSONArray(responseString);

                        for (int i = 0; i < commentJsonArray.length(); i++) {
                            JSONObject itemJsonObject = commentJsonArray.getJSONObject(i);

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

                            Log.i("ViewActivity", content);


                            adapter.addDataItem(dataItem);
                        }
                        Log.i("ViewActivity", "评论数：" + commentJsonArray.length());
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

        public RequestBody createLoadCommentRequestBody(String wallpaperId, int startNum, int pageSize) {
            JSONObject requestJsonObject = new JSONObject();
            try {
                requestJsonObject.put("id", wallpaperId);
                requestJsonObject.put("startNum", startNum);
                requestJsonObject.put("pageSize", pageSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("ViewActivity", requestJsonObject.toString());
            //            // application/json
            RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
            return requestBody;
        }
    };
}
