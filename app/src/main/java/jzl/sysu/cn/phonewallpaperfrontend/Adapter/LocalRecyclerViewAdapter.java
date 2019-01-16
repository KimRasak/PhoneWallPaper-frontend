package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.DataItem.WallPaperDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.R;

public class LocalRecyclerViewAdapter extends RecyclerView.Adapter<LocalRecyclerViewAdapter.ViewHolder>{
    private LocalRecyclerViewAdapter.ItemClickListener mClickListener;
    private List<WallPaperDataItem> data;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
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

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
