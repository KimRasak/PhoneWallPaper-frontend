package jzl.sysu.cn.phonewallpaperfrontend.RecyclerView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class GridLayoutRecyclerView extends RecyclerView {
    private LayoutManager manager;
    private int spanCount = 3;

    public GridLayoutRecyclerView(Context context, int spanCount) {
        super(context);
        init(context);
        this.spanCount = spanCount;
    }

    public GridLayoutRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridLayoutRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public int getSpanCount() {
        return spanCount;
    }

    private void init(Context context) {
        manager = new GridLayoutManager(context, spanCount, VERTICAL, false);
        setLayoutManager(manager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        // 用于动态确定每行的列数，但目前每行固定为2列。
        super.onMeasure(widthSpec, heightSpec);
        ((GridLayoutManager)getLayoutManager()).setSpanCount(spanCount);
    }
}
