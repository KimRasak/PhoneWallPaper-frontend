package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Model.LocalWallpaper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Util;

public class LocalRecyclerViewAdapter extends RecyclerView.Adapter<LocalRecyclerViewAdapter.ViewHolder>{
    private ItemClickListener mClickListener;
    private Context context;
    private List<LocalWallpaper> data;
    private LayoutInflater mInflater;

    private int spanCount;
    private double scale;

    public LocalRecyclerViewAdapter(Context context, ArrayList<LocalWallpaper> data, int spanCount, double scale) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.spanCount = spanCount;
        this.scale = scale;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_local_wallpaper, parent, false);

        // 设置图片宽高成比例（1.5:1）
        int margin = 4; // margin是1dp。
        float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, margin * (spanCount + 1), context.getResources().getDisplayMetrics()); // 每行两个图，margin总共有4dp
        int width = (int)(parent.getMeasuredWidth() - pxMargin) / spanCount;
        view.getLayoutParams().height =  (int)(width * scale); // 每行spanCount个，且图片的宽比高为1.5:1，

        return new LocalRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        LocalWallpaper wallpaper = data.get(position);
        String imgSrc = wallpaper.getImgSrc();

        // String path = context.getFilesDir().getAbsolutePath();

        Bitmap bitmap = BitmapFactory.decodeFile(imgSrc);
        holder.wallpaper.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addDataItem(LocalWallpaper item) { this.data.add(item); }

    public LocalWallpaper get(int pos) { return this.data.get(pos); }

    public void remove(int pos) { this.data.remove(pos); }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView wallpaper;

        ViewHolder(View itemView) {
            super(itemView);
            wallpaper = itemView.findViewById(R.id.wallpaper);
        }
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mClickListener = listener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
