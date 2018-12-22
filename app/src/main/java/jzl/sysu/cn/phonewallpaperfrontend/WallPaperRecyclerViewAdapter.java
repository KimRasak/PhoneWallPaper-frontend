package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WallPaperRecyclerViewAdapter extends RecyclerView.Adapter<WallPaperRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<WallPaperDataItem> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private int spanCount;
    private int imgWidth;
    private int imgHeight;
    // data is passed into the constructor
    WallPaperRecyclerViewAdapter(Context context, ArrayList<WallPaperDataItem> data, int spanCount) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.spanCount = spanCount;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wallpaper, parent, false);
        int parentW = parent.getMeasuredWidth();
        int parentH = parent.getMeasuredHeight();
        int lWidth = view.getLayoutParams().width;


        int margin = 1;
        float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, margin * 4,context.getResources().getDisplayMetrics());
        view.getLayoutParams().height =  (int)(((float)parent.getMeasuredWidth() - pxMargin) / 3); // 宽比高=1.5:1，每行两个图，margin有4dp，但此处按
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        holder.wallpaper.setMaxWidth(imgWidth);
//        holder.wallpaper.setMaxHeight(imgHeight);
        holder.wallpaper.setImageResource(R.drawable.ic_launcher_foreground);
//        int width = holder.wallpaper.getWidth();
//        holder.wallpaper.setMinimumHeight(width);
        WallPaperDataItem dataItem = data.get(position);
        byte[] imgBytes = dataItem.getImgBytes();
        if (imgBytes == null) {
            // 下载图片
            Log.i("RepoFragment", "pos: " + position + " download image. Data num:" + data.size() + "---------------");
            String imgSrc = dataItem.getImgSrc();
            // Uri imgUri = Uri.fromFile( new File(imgSrc)); // 图片链接为本地时用
            // String imgSrc = "https://sjbz-fd.zol-img.com.cn/t_s320x510c/g5/M00/04/0F/ChMkJlwCgPOIDfFwABOYC3Cio-IAAth8wOz1nYAE5gj262.jpg"; // 暂时代替！
            Glide.with(context)
                    .load(imgSrc)
                    .into(holder.wallpaper);

        } else {
            Log.i("RepoFragment", "pos: " + position +" Decode bitmap image.");
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            // bitmap = scaleBitmap(bitmap, imgWidth, imgHeight);
            holder.wallpaper.setImageBitmap(bitmap);
        }

//        int nWidth = holder.wallpaper.getWidth();
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

    // total number of cells
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

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
