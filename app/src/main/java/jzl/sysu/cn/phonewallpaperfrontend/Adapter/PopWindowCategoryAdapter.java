package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.R;

public class PopWindowCategoryAdapter extends RecyclerView.Adapter<PopWindowCategoryAdapter.ViewHolder>{
    private ItemClickListener mClickListener;
    private Context context;
    private List<String> data;
    private LayoutInflater mInflater;

    public PopWindowCategoryAdapter(Context context, List<String> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_pop_window_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String categoryStr = data.get(position);
        if (position % 3 == 2) {
            holder.category.setBackground(context.getResources().getDrawable(R.drawable.item_pop_window_category_right_most));
        }
        holder.category.setText(categoryStr);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onCategoryItemClick(v, data.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView category;

        ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
        }
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mClickListener = listener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onCategoryItemClick(View view, String category);
    }

}
