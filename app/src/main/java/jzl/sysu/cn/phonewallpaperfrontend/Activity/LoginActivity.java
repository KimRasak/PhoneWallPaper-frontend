package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.Tencent;

import de.hdodenhof.circleimageview.CircleImageView;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;

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
                listener = helper.new QQLoginListener(LoginActivity.this);
                helper.logInQQ(LoginActivity.this, listener);
            }
        });

        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "none";
                LoginHelper.getInstance().getWX().sendReq(req);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.v(jzl.sysu.cn.phonewallpaperfrontend.Constants.LOG_TAG, "LoginAc");
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
    }
}
