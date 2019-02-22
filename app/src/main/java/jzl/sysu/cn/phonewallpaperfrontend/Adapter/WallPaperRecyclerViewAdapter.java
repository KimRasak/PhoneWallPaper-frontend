package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Util;

public class WallPaperRecyclerViewAdapter extends RecyclerView.Adapter<WallPaperRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<WallPaper> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int imgWidth;

    private String hostName;
    private int spanCount;
    private final double scale;

    public WallPaperRecyclerViewAdapter(Context context, ArrayList<WallPaper> data, int spanCount, double scale) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.spanCount = spanCount;
        this.scale = scale;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wallpaper, parent, false);

        // 设置图片宽高成比例（1.5:1）
        int margin = 1; // margin是1dp。
        float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, margin * (spanCount + 1), context.getResources().getDisplayMetrics()); // 每行两个图，margin总共有4dp
        imgWidth = (int)((float)parent.getMeasuredWidth() - pxMargin) / spanCount;
        view.getLayoutParams().height =  (int)(imgWidth * scale); // 图片的宽比高为1.5:1，
        return new ViewHolder(view);
    }

    // 绑定数据
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取数据
        WallPaper wallPaper = data.get(position);
        String path = wallPaper.getPath();
        String host = hostName;
        String tail = "x-oss-process=image/resize,w_" + imgWidth;
        String wallpaperSrc = "http://" + host + "/" + path;
        String thumb = wallpaperSrc + "?" + tail;
        holder.wallpaperSrc = wallpaperSrc;
        // 加载相应图片
        Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
        Glide.with(context)
                .load(thumb)
                .centerCrop()
                .into(holder.wallpaper);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        return Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public WallPaper get(int index) { return data.get(index); }

    public void add(WallPaper wallPaper) { data.add(wallPaper); }

    public void add(List<WallPaper> wallPapers) {
        data.addAll(wallPapers);
    }

    /* hostName 的 setter和getter */
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView wallpaper;
        String wallpaperSrc;

        ViewHolder(View itemView) {
            super(itemView);
            wallpaper = itemView.findViewById(R.id.wallpaper);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), this.wallpaperSrc);
        }

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position, String wallpaperSrc);
    }
}
