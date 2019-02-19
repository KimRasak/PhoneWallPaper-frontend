package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.UserService;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Response.LoginResponse;

public class WelcomeActivity extends AppCompatActivity {
    private String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };
    private static final int REQUEST_PERMISSION = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //去掉状态栏，实现全屏
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onResume() {
        Log.v(Constants.LOG_TAG, "welcomeAc");
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isPermitted = checkPermissions();
        if (!isPermitted) {
            startRequestPermission();
        } else {
            initAndLeave();
        }
    }

    private void initAndLeave() {
        initApiManager();
        initImageLoader();
        initLoginHelper();
    }

    private void initApiManager() {
        ApiManager.getInstance().init(this.getApplicationContext());
    }

    private void initImageLoader() {
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    private void initLoginHelper() {
        final LoginHelper helper = LoginHelper.getInstance();
        helper.init(this);

        UserService service = ApiManager.getInstance().getUserService();
        service.checkLogin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    private void fail() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 300);
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        if (loginResponse.isFail()) {
                            fail();
                            return;
                        }
                        // 设置用户个人信息
                        helper.setUserInfo(loginResponse);

                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("OkHttp", e.getMessage());
                        fail();
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    private boolean checkPermissions() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            int l = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || l != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                return false;
            } else
                return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
                    Toast.makeText(this, "获取权限失败，请在\"设置\"开启权限", Toast.LENGTH_LONG).show();
                    System.exit(0);
                } else {
                    // 获取权限成功，进入界面
                    initAndLeave();
                }
            }
        }
    }
}
