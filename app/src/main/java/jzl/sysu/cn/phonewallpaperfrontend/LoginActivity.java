package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import de.hdodenhof.circleimageview.CircleImageView;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.MainActivity;

public class LoginActivity extends AppCompatActivity {
    CircleImageView tencent;
    CircleImageView wechat;

    LoginHelper.QQLoginListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tencent = findViewById(R.id.tencent);
        wechat = findViewById(R.id.wechat);

        final LoginHelper helper = LoginHelper.getInstance();
        tencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 listener = helper.new QQLoginListener(LoginActivity.this) {
                    @Override
                    public void onServerLoggedIn() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                };
                helper.logInQQ(LoginActivity.this, listener);
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
    }
}
