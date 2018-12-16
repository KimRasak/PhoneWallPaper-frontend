package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

public class LoadMoreFooterView extends AppCompatTextView implements SwipeTrigger, SwipeLoadMoreTrigger {
    public LoadMoreFooterView(Context context) {
        super(context);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onLoadMore() {
        setText("正在加载");
    }

    @Override
    public void onPrepare() {
        setText("");
    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled <= -getHeight()) {
                setText("释放并加载");
            } else {
                setText("下滑加载");
            }
        } else {
            setText("加载完成");
        }
    }

    @Override
    public void onRelease() {
        setText("已释放加载");
    }

    @Override
    public void onComplete() {
        setText("加载完成");
    }

    @Override
    public void onReset() {
        setText("");
    }
}
