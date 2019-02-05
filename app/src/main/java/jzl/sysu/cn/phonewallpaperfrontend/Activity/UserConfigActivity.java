package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.UserService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.SignatureBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.UserNameBody;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserConfigActivity extends AppCompatActivity {
    Button btn_logout;

    // 头像
    ImageView userIcon;
    TextView tvChangeUserIcon;
    String photoUri;

    // 用户名
    EditText etUserName;
    TextView tvChangeUserName;

    // 个人签名
    EditText etSignature;
    TextView tvChangeSignature;

    String CHANGE_USER_ICON_URL = "http://" + Constants.PC_IP + ":9090/user/userIcon";
    String CHANGE_USER_NAME_URL = "http://" + Constants.PC_IP + ":9090/user/userName";
    String CHANGE_SIGNATURE_URL = "http://" + Constants.PC_IP + ":9090/user/signature";


    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);

        findView();

        // 加载头像
        String userIconSrc = getIntent().getStringExtra("userIconSrc");
        Glide.with(this).load(userIconSrc).into(userIcon);

        final LoginHelper helper = LoginHelper.getInstance();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               logOut();
            }
        });

        tvChangeUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });

        tvChangeUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                commmitNewUserName(userName);
            }
        });

        tvChangeSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signature = etSignature.getText().toString();
                commitNewSignature(signature);
            }
        });
    }

    private void findView() {
        btn_logout = findViewById(R.id.btn_logout);
        userIcon = findViewById(R.id.userIcon);
        tvChangeUserIcon = findViewById(R.id.changeUserIcon);
        etUserName = findViewById(R.id.userName);
        tvChangeUserName = findViewById(R.id.changeUserName);
        etSignature = findViewById(R.id.signature);
        tvChangeSignature = findViewById(R.id.changeSignature);
    }

    private void logOut() {
        LoginHelper helper = LoginHelper.getInstance();
        helper.logOut(UserConfigActivity.this);
        System.exit(0);
    }

    private void showPopWindow(){
        View popView = View.inflate(this,R.layout.pop_window_chose_image,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels /3;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.REQUEST_LOAD_IMAGE);
                popupWindow.dismiss();
            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCamera(Constants.REQUEST_CAMERA_IMAGE);
                popupWindow.dismiss();

            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    private void takeCamera(int num) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            }
        }

        startActivityForResult(takePictureIntent, num);//跳转界面传回拍照所得数据
    }

    private File createImageFile() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = null;
        try {
            image = File.createTempFile(
                    generateFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        photoUri = image.getAbsolutePath();
        return image;
    }

    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOAD_IMAGE)
            loadAlbumImage();
        else if (requestCode == Constants.REQUEST_CAMERA_IMAGE)
            loadCameraImage(data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private void loadCameraImage(Intent data) {
        byte[] imageBytes = File2byte(photoUri);
    }

    private void loadAlbumImage() {

    }

    /* 修改个人昵称 */
    // 提交修改请求
    private void commmitNewUserName(final String userName) {
        // 构建请求
        // 构建请求
        UserService service = ApiManager.getInstance().getUserService();
        UserNameBody body = new UserNameBody(userName);
        Observable<CodeResponse> ob = service.changeUserName(body);
        ob.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<CodeResponse>() {
                @Override
                public void onComplete() {}
                @Override
                public void onSubscribe(Disposable d) {}

                private void fail() { Toast.makeText(UserConfigActivity.this, "修改个人昵称失败", Toast.LENGTH_SHORT).show(); }
                @Override
                public void onNext(CodeResponse codeResponse) {
                    if (codeResponse.isFail()) {
                        fail();
                        return;
                    }

                    LoginHelper helper = LoginHelper.getInstance();
                    helper.setUserName(userName);
                    Toast.makeText(UserConfigActivity.this, "修改用户名完成", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) { fail(); }

            });
    }

    private void commitNewSignature(final String signature) {
        // 构建请求
        UserService service = ApiManager.getInstance().getUserService();
        SignatureBody body = new SignatureBody(signature);
        Observable<CodeResponse> ob = service.changeSignature(body);
        ob.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<CodeResponse>() {
                @Override
                public void onSubscribe(Disposable d) {}
                private void fail() { Toast.makeText(UserConfigActivity.this, "修改签名失败", Toast.LENGTH_SHORT).show(); }
                @Override
                public void onNext(CodeResponse codeResponse) {
                    if (codeResponse.isFail()) {
                        fail();
                        return;
                    }

                    final LoginHelper helper = LoginHelper.getInstance();
                    helper.setSignature(signature);
                    Toast.makeText(UserConfigActivity.this, "修改签名完成", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) { fail(); }

                @Override
                public void onComplete() {}
            });
    }
}
