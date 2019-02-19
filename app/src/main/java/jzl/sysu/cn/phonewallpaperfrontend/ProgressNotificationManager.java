package jzl.sysu.cn.phonewallpaperfrontend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import jzl.sysu.cn.phonewallpaperfrontend.Model.ProgressMessage;

public class ProgressNotificationManager {
    private static final String CHANNEL_ID = "uploadChannel";
    public static final String MODE_UPLOADING = "uploading";
    public static final String MODE_DOWNLOADING = "downloading";
    public static final String TEXT_UPLOADING = "正在上传图片";
    public static final String TEXT_DOWNLOADING = "正在下载图片";

    private Context mContext;
    private String mode;

    private NotificationManager notificationManager;
    NotificationChannel channel;
    private Notification.Builder builder;

    private int notificationId;

    public ProgressNotificationManager(Context context, String mode) {
        mContext = context;
        this.mode = mode;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mode.equals(MODE_UPLOADING)) {
            initBuilder(context, TEXT_UPLOADING);
            notificationId = Constants.NOTIFICATION_ID_UPLOAD;
        } else if (mode.equals(MODE_DOWNLOADING)) {
            initBuilder(context, TEXT_DOWNLOADING);
        }
    }

    private void initBuilder(Context context, String title) {
        builder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(false) // 不可取消
                .setSound(null) // 静音
                .setProgress(100, 0, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    MODE_UPLOADING,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            builder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(int progress) {
        if (mode.equals(MODE_UPLOADING))
            builder.setContentTitle(String.format(Locale.CHINA, "%s: %d%%", TEXT_UPLOADING, progress));
        builder.setProgress(100, progress, false);
        notificationManager.notify(notificationId, builder.build());
    }

    private void cancelNotification() {
        notificationManager.cancel(notificationId);
    }

    private Observer<ProgressMessage> ob = new Observer<ProgressMessage>() {
        private long lastTime;
        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onNext(ProgressMessage progressMessage) {
            int progress = progressMessage.getProgress();
            if (progress == 0) {
                lastTime = System.currentTimeMillis();
                showNotification(progress);
            } else if (progress > 0 && progress < 100) {
                long curTime = System.currentTimeMillis();
                if (curTime - lastTime < 500)
                    return;
                lastTime = curTime;

                showNotification(progress);
            } else if (progress == 100) {
                cancelNotification();
            }
        }

        @Override
        public void onError(Throwable e) {}

        @Override
        public void onComplete() {}
    };



    public Observer<ProgressMessage> getObserver() { return this.ob; }
}
