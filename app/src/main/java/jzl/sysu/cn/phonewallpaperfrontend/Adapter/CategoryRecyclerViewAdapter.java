package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.DataItem.CategoryDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.R;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<CategoryDataItem> data;
    private LayoutInflater mInflater;
    private int spanCount = 3;
    private CategoryRecyclerViewAdapter.ItemClickListener mClickListener;

    public CategoryRecyclerViewAdapter(Context context, ArrayList<CategoryDataItem> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_category, parent, false);

        // 动态调整组件高度，而组件宽度由屏幕宽度决定。
        int margin = 4; // margin是1dp。
        float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, margin * (spanCount + 1), context.getResources().getDisplayMetrics()); // 每行两个图，margin总共有4dp
        view.getLayoutParams().height =  (int)(((float)parent.getMeasuredWidth() - pxMargin) / 3); // 图片的宽比高为1.5:1，
        int lWidth = view.getLayoutParams().width;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取数据。
        CategoryDataItem item = data.get(position);

        // 绑定视图。
        holder.name.setText(item.getName());
        holder.background.setImageResource(R.drawable.ic_launcher_foreground);

        Log.i("RepoPage", "load holder: " + position);
//        Glide.with(context)
//                .load(item.getBackground())
//                .into(holder.background);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getCategory(int i) { return data.get(i).getName(); }

    public void addDataItem(CategoryDataItem dataItem) { this.data.add(dataItem); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView background;
        public TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.category_background);
            name = itemView.findViewById(R.id.category_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(CategoryRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
