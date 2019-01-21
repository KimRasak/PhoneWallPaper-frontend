package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.CommentDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.CommentsAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.LoadMoreFooterView;
import jzl.sysu.cn.phonewallpaperfrontend.LocalHelper;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
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

    // 下载框
    ProgressDialog progressDialog;

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
        likeLayout.setVisibility(View.INVISIBLE);
        // “点赞”按钮
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLike = (boolean)likeImage.getTag();
                if (!isLike) {
                    likeWallpaper(true);
                    setLikeImage(true);

                } else {
                    likeWallpaper(false);
                    setLikeImage(false);
                }
            }
        });

        // “设为壁纸”按钮
        downloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAsWallpaper();
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
        downloadLayout = findViewById(R.id.downloadLayout);
        likeImage = findViewById(R.id.likeImage);

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

    public void showProgressDialog(Context mContext, String text) {
        // 弹出加载图片框
        progressDialog = null;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(text);	//设置内容
        progressDialog.setCancelable(false);//点击屏幕和按返回键都不能取消加载框
        progressDialog.show();
    }

    public Boolean dismissProgressDialog() {
        // 收回加载图片框
        if (progressDialog != null){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
                return true;//取消成功
            }
        }
        return false;//已经取消过了，不需要取消
    }

    private void setAsWallpaper() {
        showProgressDialog(this, "正在下载图片（约等待10秒）");

        RequestBuilder builder = Glide.with(this).downloadOnly().load(wallpaperSrc).listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(resource);
                    Bitmap bmp = BitmapFactory.decodeStream(fis);
                    File file = new File(getFilesDir(), wallpaperId);

                    LocalHelper.save(ViewWallpaperActivity.this, bmp, wallpaperId);
                    dismissProgressDialog();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        builder.preload();
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
        Log.i("ViewWallpaper", wallpaperId);

        JSONObject data = new JSONObject();
        try {
            data.put("wallpaperId", wallpaperId);
            data.put("toCommentId", toCommentId);
            data.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = Util.buildAuthRequestBody(data, getApplicationContext());
        Request request = new Request.Builder().url(ADD_COMMENT_URL).post(requestBody).build();

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

    public void likeWallpaperFail(boolean isLike) {
        setLikeImage(!isLike);
        setLikeNum(!isLike);
        Toast.makeText(ViewWallpaperActivity.this, "点赞/取消点赞失败", Toast.LENGTH_SHORT).show();
    }

    public void likeWallpaper(final boolean isLike) {
        // 点击"点赞"按钮后执行。

        // 未登陆不可点赞。
        if (!LoginHelper.getInstance(ViewWallpaperActivity.this).isLoggedIn(this)) {
            Toast.makeText(ViewWallpaperActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }

        // 先假装点赞成功。
        setLikeNum(isLike);

        // 建立向服务器发送的Request
        String url = LIKE_WALLPAPER_URL;
        JSONObject data = new JSONObject();
        // Log.i("like_wallpaper", isLike + " ");
        try {
            data.put("wallpaperId", wallpaperId);
            data.put("like", isLike);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = Util.buildAuthRequestBody(data, getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(requestBody).build();

        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        likeWallpaperFail(isLike);
                    }
                });
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ViewWallpaperActivity.this, "点赞/取消点赞成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // 服务器返回失败条文，点赞失败。
                            Log.i("LikeWallpaper", msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    likeWallpaperFail(isLike);
                                }
                            });
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

    public void setMiddleLayoutState(Boolean isLike) {
        // 仅当查询用户与壁纸的点赞、收藏关系时调用。
        setLikeImage(isLike);
    }

    public void checkRelationshipState() {
        String url = GET_RELATIONSHIP_URL;
        JSONObject data = new JSONObject();
        try {
            data.put("wallpaperId", wallpaperId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = Util.buildUserRequestBody(data, getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                setMiddleLayoutState(false);
                Log.i("checkRelationshipState", "与服务器连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("checkRelationship", responseString);
                try {
                    JSONObject relationship = new JSONObject(responseString);
                    int code = relationship.getInt("code");
                    if (code == 0) {
                        final Boolean isLike = relationship.getBoolean("isLike");
                        final Boolean isCollect = relationship.getBoolean("isCollect");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                likeLayout.setVisibility(View.VISIBLE);
                                setMiddleLayoutState(isLike);
                            }
                        });
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
                String auth = helper.getAuth();

                requestJsonObject.put("openId", openid);
                requestJsonObject.put("accessToken", accessToken);
                requestJsonObject.put("auth", auth);
                requestJsonObject.put("wallPaperId", wallpaperId);
                requestJsonObject.put("toCommentId", toCommentId);
                requestJsonObject.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 测试输出
            Log.i("ViewActivity", requestJsonObject.toString());

            // 设置RequestBody。格式为application/json
            return RequestBody.create(FORM_CONTENT_TYPE, requestJsonObject.toString());
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
