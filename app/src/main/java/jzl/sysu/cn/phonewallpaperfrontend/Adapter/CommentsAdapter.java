package jzl.sysu.cn.phonewallpaperfrontend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.DataItem.CommentDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import me.codeboy.android.aligntextview.CBAlignTextView;

public class CommentsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<CommentDataItem> data;

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public CommentsAdapter(Context context, List<CommentDataItem> data) {
        inflater = LayoutInflater.from(context);
        data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addDataItem(CommentDataItem dataItem) { data.add(dataItem); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_comment, parent, false); //加载布局
            holder = new ViewHolder();
            holder.user_icon = (ImageView) convertView.findViewById(R.id.user_icon);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.content = (CBAlignTextView) convertView.findViewById(R.id.comment_content);
            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        CommentDataItem comment = data.get(position);
        holder.user_name.setText(comment.getUserName());
        holder.content.setText(comment.getContent());

        // 加载人物头像。
        if (comment.getUserIconBytes() == null) {
            Context context = convertView.getContext();
            Glide.get(context).setMemoryCategory(MemoryCategory.HIGH);
            Glide.with(context)
                    .load(comment.getUserIcon())
                    .into(holder.user_icon);
        } else {
            //
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView user_icon;
        TextView user_name;
        CBAlignTextView content;
    }
}
