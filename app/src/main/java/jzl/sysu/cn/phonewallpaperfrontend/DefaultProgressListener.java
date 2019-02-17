package jzl.sysu.cn.phonewallpaperfrontend;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.Model.ProgressMessage;

public class DefaultProgressListener implements ProgressRequestBody.ProgressListener {

    private Handler mHandler;

    //多文件上传时，index作为上传的位置的标志
    private int mIndex;

    private Observer<ProgressMessage> observer;

    public DefaultProgressListener(Observer<ProgressMessage> observer) {
        this.observer = observer;
    }

    @Override
    public void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish) {
        Log.i("progressListen", "----the current " + hasWrittenLen + "----" + totalLen + "-----" + (hasWrittenLen * 100 / totalLen));
        int percent = (int) (hasWrittenLen * 100 / totalLen);
        if (percent > 100) percent = 100;
        if (percent < 0) percent = 0;

        // 传递信息
        ProgressMessage msg = new ProgressMessage(percent);
        Observable<ProgressMessage> observable = Observable.just(msg);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public Observer<ProgressMessage> getObserver() { return observer; }
}
