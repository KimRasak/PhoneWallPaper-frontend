package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.DataItem.CommentDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import me.codeboy.android.aligntextview.CBAlignTextView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<CommentDataItem> data;
    private CommentsAdapter.ItemClickListener mClickListener;

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public CommentsAdapter(Context context, List<CommentDataItem> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取数据。
        CommentDataItem comment = data.get(position);

        // 绑定评论信息。
        holder.user_name.setText(comment.getUserName());
        holder.content.setText(comment.getContent());

        // 加载人物头像。
        Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
        Glide.with(context)
                .load(comment.getUserIcon())
                .into(holder.user_icon);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addDataItem(CommentDataItem dataItem) { data.add(dataItem); }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView user_icon;
        TextView user_name;
        CBAlignTextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            user_name = itemView.findViewById(R.id.user_name);
            content = itemView.findViewById(R.id.comment_content);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(CommentsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
