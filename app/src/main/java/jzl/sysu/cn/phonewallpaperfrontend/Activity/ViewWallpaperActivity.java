package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.WallpaperService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.ClickBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.GetCommentBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LikeBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.PutCommentBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.SetBody;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LocalHelper;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Comment;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.CommentsAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.LoadMoreFooterView;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Response.ClickResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;

public class ViewWallpaperActivity extends AppCompatActivity implements CommentsAdapter.ItemClickListener {
    // 壁纸
    ImageView wallpaper;
    Long wallpaperId;
    String wallpaperSrc;

    // 提交者栏
    LinearLayout uploaderLayout;
    TextView tvUploaderName;

    // 中间栏
    LinearLayout likeLayout;
    ImageView likeImage;
    boolean isLike = false;
    boolean isPostingLike = false;
    int likeNum;
    TextView tvLikeNum;
    LinearLayout downloadLayout;

    // 抬头区（位于评论区上方）
    ConstraintLayout commentsTitleLayout;
    Button btnAddComments;

    // 热门评论区
    RecyclerView hotComments;
    CommentsAdapter hotCommentsAdapter;

    // 评论区
    SwipeToLoadLayout commentsLayout;
    RecyclerView comments;
    CommentsAdapter commentsAdapter;
    LoadMoreFooterView swipe_load_more_footer;
    LoadCommentsListener loadCommentsListener;

    // 下载框
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        findView();

        // 从intent获取数据。
        Intent intent = getIntent();
        WallPaper wallPaper = (WallPaper) intent.getSerializableExtra("wallpaper");
        String uploaderName = wallPaper.getUploaderName();
        wallpaperId = wallPaper.getId();
        wallpaperSrc = intent.getStringExtra("wallpaperSrc");
        likeNum = wallPaper.getLikeNum();

        if (wallpaperSrc == null || wallpaperId == -1L || likeNum == -1) // 错误进入该页
            return;

        // 显示壁纸。
        wallpaper.post(new Runnable() {
            @Override
            public void run() {
                double scale = Util.getWindowScale(ViewWallpaperActivity.this);
                ViewGroup.LayoutParams lp = wallpaper.getLayoutParams();
                lp.height =  (int)(wallpaper.getMeasuredWidth() * scale );
                wallpaper.setLayoutParams(lp);
                Log.v(Constants.LOG_TAG, String.format("measured width: %s, height:%d", wallpaper.getMeasuredWidth(), lp.height));

            }
        });
        Glide.with(this).load(wallpaperSrc).into(wallpaper);

        initUploaderLayout(uploaderName);
        initMiddleLayout();
        initHotCommentsLayout();
        initCommentsLayout();
    }

    private void initUploaderLayout(String uploaderName) {
        if (uploaderName != null)
            tvUploaderName.setText(uploaderName);
        else
            uploaderLayout.setVisibility(View.GONE);

    }

    public void findView() {
        wallpaper = findViewById(R.id.wallpaper);

        // 提交者栏
        uploaderLayout = findViewById(R.id.uploaderLayout);
        tvUploaderName = findViewById(R.id.uploaderName);

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
        comments = findViewById(R.id.rvComments);
        swipe_load_more_footer = findViewById(R.id.swipe_load_more_footer);
    }

    /* 中间栏 */
    private void initMiddleLayout() {
        /*
            初始化中间栏。点赞面板先处于无法点击的状态，在加载了评论和点赞关系后，点赞面板才可以点击。
        */
        // 设置点赞面板不激活。
        disableLikeLayout();

        // 设置下载面板
        initDownloadLayout();

        // 评论面板
        initCommentsLayout();

        // 获取点赞、评论数据
        initData();
    }

    /* 点赞面板 */
    private void disableLikeLayout() {
        // 图标显示灰色
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_favorite_24px, getTheme());
        vectorDrawableCompat.setTint(getResources().getColor(R.color.gray));
        likeImage.setImageDrawable(vectorDrawableCompat);

        // 表示文字加载中
        tvLikeNum.setText(String.valueOf(likeNum));

        // 点击不响应
        likeLayout.setOnClickListener(null);
    }

    private void initLikeLayout(final boolean isLike, int likeNum) {
        changeLikeLayout(isLike, likeNum);
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeWallpaper(!ViewWallpaperActivity.this.isLike);
            }
        });
    }

    private void changeLikeLayout(boolean isLike, int likeNum) {
        if (isLike) {
            // 实心红色爱心
            VectorDrawableCompat vectorDrawableCompat = Util.getColoredVectorDrawable(this, R.drawable.ic_baseline_favorite_24px,  R.color.red);
            likeImage.setImageDrawable(vectorDrawableCompat);

        } else {
            // 空心黑色爱心
            VectorDrawableCompat vectorDrawableCompat = Util.getColoredVectorDrawable(this, R.drawable.ic_baseline_favorite_border_24px,  R.color.black);
            likeImage.setImageDrawable(vectorDrawableCompat);
        }

        tvLikeNum.setText(String.valueOf(likeNum));
    }

    private void likeWallpaper(final boolean isLike) {
        // 点赞/取消点赞
        final int newLikeNum = isLike ? (likeNum + 1) : (likeNum - 1);

        if (isPostingLike)
            return;

        isPostingLike = true;

        // 未登陆不可点赞。
        LoginHelper helper = LoginHelper.getInstance();
        if (!helper.isLoggedIn(this)) {
            Toast.makeText(ViewWallpaperActivity.this, "请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }

        // 先假装点击操作成功。
        changeLikeLayout(isLike, newLikeNum);

        // 发送请求
        WallpaperService service = ApiManager.getInstance().getWallpaperService();
        LikeBody body = new LikeBody(wallpaperId, isLike);
        Observable<CodeResponse> ob = service.like(body);
        ob.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<CodeResponse>() {
                private void fail() {
                    Util.showNetworkFailToast(ViewWallpaperActivity.this);
                    changeLikeLayout(!isLike, likeNum);
                    isPostingLike = false;
                }
                @Override
                public void onError(Throwable e) { fail(); }
                @Override
                public void onNext(CodeResponse codeResponse) {
                    if (codeResponse.isFail()) {
                        fail();
                        return;
                    }
                    ViewWallpaperActivity.this.isLike = !ViewWallpaperActivity.this.isLike;
                    likeNum = newLikeNum;
                    isPostingLike = false;
                }
                @Override
                public void onComplete() {}
                @Override
                public void onSubscribe(Disposable d) {}

        });
    }

    /* 下载面板 */
    private void initDownloadLayout() {
        downloadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setAsWallpaper(); }
        });
    }


    private void fail() { Util.showNetworkFailToast(ViewWallpaperActivity.this); }

    private void setAsWallpaper() {
        // 显示下载框
        showDownloadDialog(this);

        final DisplayMetrics metrics = Util.setWallpaperManagerFitScreen(ViewWallpaperActivity.this);
        final int width = metrics.widthPixels;
        final int height = metrics.heightPixels;

        // 设置图片链接
        ImageLoader imageLoader = ImageLoader.getInstance();
        // String tail = String.format(Locale.CHINA, "?x-oss-process=image/resize,m_fixed,w_%d,h_%d", width, height);
        String tail = String.format(Locale.CHINA, "?x-oss-process=image/resize,m_fixed,w_%d,h_%d", width, height);
        String scaleSrc = wallpaperSrc + tail;

        // 下载图片
        imageLoader.loadImage(wallpaperSrc, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, final Bitmap bitmap) {
                WallpaperService service = ApiManager.getInstance().getWallpaperService();
                SetBody body = new SetBody(wallpaperId);
                Observable<CodeResponse> ob = service.set(body);
                ob.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<CodeResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {}
                            @Override
                            public void onComplete() {}
                            @Override
                            public void onError(Throwable e) {
                                Util.showNetworkFailToast(ViewWallpaperActivity.this);
                                dismissDownloadDialog();
                            }
                            @Override
                            public void onNext(CodeResponse codeResponse) {
                                if (codeResponse.isFail())
                                    fail();

                                // 设为壁纸
                                Bitmap wallpaper = Util.centerCrop(bitmap, metrics);

                                // Bitmap wallpaper = Bitmap.createScaledBitmap(bitmap, width, height, true);
                                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(ViewWallpaperActivity.this);
                                try {
                                    wallpaperManager.setBitmap(wallpaper);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // 存入本地
                                LocalHelper.save(ViewWallpaperActivity.this, bitmap, wallpaperId);
                                dismissDownloadDialog();
                            }

                        });
            }
        });
    }

    private void showDownloadDialog(Context mContext) {
        // 弹出加载图片框
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("正在下载图片（约等待10秒）");	//设置内容
        progressDialog.setCancelable(false);//点击屏幕和按返回键都不能取消加载框
        progressDialog.show();
    }

    public void dismissDownloadDialog() {
        // 收回加载图片框
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /* 热门评论区 */
    private void initHotCommentsLayout() {
        ArrayList<Comment> data = new ArrayList<Comment>();
        hotCommentsAdapter = new CommentsAdapter(this, data);
        hotComments.setAdapter(hotCommentsAdapter);

        // 设置RecyclerView的LayoutManager。
        hotComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // 无热门评论时，隐藏hotComments。
        if (data.size() == 0)
            hotComments.setVisibility(View.GONE);

        hotCommentsAdapter.setOnItemClickListener(this);
    }

    /* 评论区 */
    private void initCommentsLayout() {
        // 设置RecyclerView的Adapter。
        commentsAdapter = new CommentsAdapter(this, new ArrayList<Comment>());
        comments.setAdapter(commentsAdapter);
        comments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // 设置RecyclerView点击回复
        commentsAdapter.setOnItemClickListener(this);
        SwipeToLoadLayout commentsSwipeLayout = findViewById(R.id.commentsSwipeLayout);

        // 评论区加载
        commentsLayout.setLoadMoreFooterView(swipe_load_more_footer);
        commentsLayout.setOnLoadMoreListener(new LoadCommentsListener());

        // 绑定“添加评论”按钮点击事件
        btnAddComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { popEditCommentWindow(-1L); } // -1代表被评论id为空
        });
    }

    @Override
    public void onItemClick(View view, int position) {Comment comment = commentsAdapter.get(position);
        popEditCommentWindow(comment.getCid());
    }

    private void popEditCommentWindow(final Long toCommentId) {
        // 弹出评论框。

        // 设置外观
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.layout_edit_comment, (ViewGroup) findViewById(R.id.commentEditLayout));

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewWallpaperActivity.this)
            .setTitle("请输入评论")
            .setView(layout)
            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText etContent = layout.findViewById(R.id.commentEditText);
                    String content = etContent.getText().toString();
                    putCommentToServer(toCommentId, content);
                }
            })
            .setNegativeButton("否", null);

        builder.show();
    }

    private void putCommentToServer(Long toCommentId, final String content) {
        WallpaperService service = ApiManager.getInstance().getWallpaperService();
        PutCommentBody body = new PutCommentBody(wallpaperId, toCommentId, content);
        Observable<Comment> ob = service.putComment(body);

        ob.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Comment>() {
                @Override
                public void onSubscribe(Disposable d) {}
                @Override
                public void onComplete() {}

                private void fail() {  Util.showNetworkFailToast(ViewWallpaperActivity.this); }
                @Override
                public void onError(Throwable e) { fail(); }
                @Override
                public void onNext(Comment comment) {
                    commentsAdapter.add(comment);
                    commentsAdapter.notifyDataSetChanged();
                }

            });
    }

    private void loadComments(int startNum, int pageSize) {
        WallpaperService service = ApiManager.getInstance().getWallpaperService();
        GetCommentBody body = new GetCommentBody(wallpaperId, startNum, pageSize);
        Observable<List<Comment>> ob = service.getComment(body);
        ob.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<Comment>>() {
                @Override
                public void onNext(List<Comment> comments) {
                    commentsAdapter.add(comments);
                    commentsAdapter.notifyDataSetChanged();
                    commentsLayout.setLoadingMore(false);
                }
                @Override
                public void onSubscribe(Disposable d) {}
                @Override
                public void onError(Throwable e) {}
                @Override
                public void onComplete() {}
            });


    }

    /* 获取壁纸点赞、评论信息 */
    private void initData() {
        WallpaperService service = ApiManager.getInstance().getWallpaperService();
        ClickBody body = new ClickBody(wallpaperId);
        Observable<ClickResponse> ob = service.click(body);
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ClickResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onNext(ClickResponse response) {
                        List<Comment> comments = response.getComments();
                        boolean isLike = response.getLike();

                        ViewWallpaperActivity.this.isLike = isLike;
                        initLikeLayout(isLike, likeNum);
                        commentsAdapter.add(comments);
                        commentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) { Util.showNetworkFailToast(ViewWallpaperActivity.this); }
                    @Override
                    public void onComplete() {}
                });
    }

    /* 评论上拉响应 */
    public class LoadCommentsListener implements OnLoadMoreListener {
        // 触发上拉加载事件时，调用该方法。
        @Override
        public void onLoadMore() {
            int startNum = commentsAdapter.getItemCount();
            int pageSize = Constants.COMMENT_PAGE_SIZE;
            loadComments(startNum, pageSize);
        }
    }
}
