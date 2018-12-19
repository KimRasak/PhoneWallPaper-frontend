package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class WallPaperRecyclerViewAdapter extends RecyclerView.Adapter<WallPaperRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<WallPaperDataItem> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    WallPaperRecyclerViewAdapter(Context context, ArrayList<WallPaperDataItem> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.wallpaper.setImageResource(R.drawable.ic_launcher_foreground);
        WallPaperDataItem dataItem = data.get(position);
        byte[] imgBytes = dataItem.getImgBytes();
        if (imgBytes == null) {
            // 下载图片
            Log.i("onBindViewHolder", "pos: " + position + ", data num:" + data.size() + "---------------");
            String imgSrc = dataItem.getImgSrc();
            // imgSrc = "https://sjbz-fd.zol-img.com.cn/t_s320x510c/g5/M00/04/0F/ChMkJlwCgPOIDfFwABOYC3Cio-IAAth8wOz1nYAE5gj262.jpg"; // 暂时代替！
            Glide.with(context)
                    .load(imgSrc)
                    .into(holder.wallpaper);
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            holder.wallpaper.setImageBitmap(bitmap);
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return data.size();
    }

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
