package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Model.MenuItem;
import jzl.sysu.cn.phonewallpaperfrontend.R;

public class UserMenuRecyclerViewAdapter extends RecyclerView.Adapter<UserMenuRecyclerViewAdapter.ViewHolder>{
    public static final int ITEM_UPLOAD_WALLPAPER = 0;

    private UserMenuRecyclerViewAdapter.ItemClickListener mClickListener;
    private List<MenuItem> data;
    private LayoutInflater mInflater;

    public UserMenuRecyclerViewAdapter(Context context, List<MenuItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_user_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // 绑定视图
        holder.icon.setImageResource(data.get(position).getIcon());
        holder.title.setText(data.get(position).getTitle());

        // 绑定点击事件
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onMenuItemClick(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.menu_item_icon);
            title = itemView.findViewById(R.id.menu_item_title);
        }
    }

    public void setOnItemClickListener(UserMenuRecyclerViewAdapter.ItemClickListener listener) {
        this.mClickListener = listener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onMenuItemClick(View view, int position);
    }
}
