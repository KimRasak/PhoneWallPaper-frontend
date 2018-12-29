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

import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.DataItem.WallPaperDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.R;

public class WallPaperRecyclerViewAdapter extends RecyclerView.Adapter<WallPaperRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<WallPaperDataItem> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private int spanCount;

    public WallPaperRecyclerViewAdapter(Context context, ArrayList<WallPaperDataItem> data, int spanCount) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.spanCount = spanCount;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wallpaper, parent, false);
        int margin = 1; // margin是1dp。
        float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, margin * (spanCount + 1), context.getResources().getDisplayMetrics()); // 每行两个图，margin总共有4dp
        view.getLayoutParams().height =  (int)(((float)parent.getMeasuredWidth() - pxMargin) / 3); // 图片的宽比高为1.5:1，
        return new ViewHolder(view);
    }

    // 绑定数据
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取数据
        WallPaperDataItem dataItem = data.get(position);

        // 设置默认图片
        holder.wallpaper.setImageResource(R.drawable.ic_launcher_foreground);

        // 加载相应图片
        byte[] imgBytes = dataItem.getImgBytes();
        Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
        if (imgBytes == null) {
            // 下载图片
            Log.i("RepoPgae", "pos: " + position + " download image. Data num:" + data.size() + "---------------");
            String imgSrc = dataItem.getImgSrc();
            Glide.with(context)
                    .load(imgSrc)
                    .into(holder.wallpaper);

        } else {
            Log.i("RepoPgae", "pos: " + position +" Decode bitmap image.");
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            holder.wallpaper.setImageBitmap(bitmap);
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public WallPaperDataItem getItem(int index) { return data.get(index); }

    public void addDataItem(WallPaperDataItem dataItem) {
        this.data.add(dataItem);
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView wallpaper;

        ViewHolder(View itemView) {
            super(itemView);
            wallpaper = itemView.findViewById(R.id.wallpaper);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
