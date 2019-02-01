package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Model.Comment;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import me.codeboy.android.aligntextview.CBAlignTextView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<Comment> data;
    private CommentsAdapter.ItemClickListener mClickListener;

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public CommentsAdapter(Context context, List<Comment> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 绑定点击事件

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) { mClickListener.onItemClick(v, holder.pos); }
        };
        if (mClickListener != null) {
            holder.user_name.setOnClickListener(listener);
            holder.content.setOnClickListener(listener);
            view.setOnClickListener(listener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // 获取数据。
        Comment comment = data.get(position);

        // 绑定评论信息。
        holder.user_name.setText(comment.getUserName());
        if (comment.isReply()) {
            holder.reply.setVisibility(View.VISIBLE);
            holder.to_user_name.setText(comment.getToUserName());
        } else {
            holder.reply.setVisibility(View.GONE);
            holder.to_user_name.setVisibility(View.GONE);
        }

        holder.content.setText(comment.getContent());

        holder.pos = holder.getAdapterPosition();

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

    public Comment get(int position) { return data.get(position); }

    public void add(Comment comment) { data.add(comment); }
    public void add(List<Comment> comments) { data.addAll(comments); }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView user_icon;
        TextView user_name;
        TextView reply;
        TextView to_user_name;
        CBAlignTextView content;
        int pos;

        ViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            user_name = itemView.findViewById(R.id.user_name);
            reply = itemView.findViewById(R.id.reply);
            to_user_name = itemView.findViewById(R.id.to_user_name);
            content = itemView.findViewById(R.id.comment_content);
        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // 父Activity会实现该接口来监听点击事件。
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
