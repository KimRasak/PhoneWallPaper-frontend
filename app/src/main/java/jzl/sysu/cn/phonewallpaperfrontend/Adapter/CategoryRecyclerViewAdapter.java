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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Category;
import jzl.sysu.cn.phonewallpaperfrontend.R;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private String hostName;
    private List<Category> data;
    private LayoutInflater mInflater;
    private int spanCount = 2;
    private CategoryRecyclerViewAdapter.ItemClickListener mClickListener;

    public CategoryRecyclerViewAdapter(Context context, ArrayList<Category> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_repo_category, parent, false);

        // 动态调整组件高度，而组件宽度由屏幕宽度决定。
        int margin = 4; // margin是1dp。
        float pxMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, margin * (spanCount + 1), context.getResources().getDisplayMetrics()); // 每行两个图，margin总共有4dp
        view.getLayoutParams().height =  (int)(((float)parent.getMeasuredWidth() - pxMargin) / spanCount / 1.5); // 图片的宽比高为1.5:1
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取数据。
        Category item = data.get(position);

        // 绑定视图。
        holder.name.setText(item.getName());
        String background = "http://" + hostName + "/" + item.getBackground();
        Glide.with(context)
                .load(background)
                .into(holder.background);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setHostName(String hostName) { this.hostName = hostName;}

    public String getCategory(int i) { return data.get(i).getName(); }

    public void add(Category category) { this.data.add(category); }

    public void add(List<Category> categories) { this.data.addAll(categories); }

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
