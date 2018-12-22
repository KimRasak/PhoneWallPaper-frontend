package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.AttributeSet;

public class AutofitRecyclerView extends RecyclerView {
    private LayoutManager manager;
    // 默认为-1
    private int mColumnWidth = 400; // 图片宽度为600px，高度为570px（高度在item_wallpaper.xml里设置）。
    private int spanCount = 3;

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public int getSpanCount() {
        return spanCount;
    }

    private void init(Context context) {
        manager = new GridLayoutManager(context, 1, VERTICAL, false); // spanCount暂定为1，将在onMeasure中重设
        setLayoutManager(manager);
    }

    public void setColumnWidth(int columnWidth) {
        mColumnWidth = columnWidth;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        // 动态确定每行的列数
        super.onMeasure(widthSpec, heightSpec);
        if (mColumnWidth > 0) {
            // spanCount = Math.max(3, getMeasuredWidth() / mColumnWidth);
            spanCount = 2;
            ((GridLayoutManager)getLayoutManager()).setSpanCount(spanCount);
            // manager.setSpanCount(spanCount);
        }
    }

}