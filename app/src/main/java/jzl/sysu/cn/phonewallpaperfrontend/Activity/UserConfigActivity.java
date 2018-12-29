package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;

public class UserConfigActivity extends AppCompatActivity {
    Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);

        final LoginHelper helper = LoginHelper.getInstance(getApplicationContext());

        btn_logout = (Button)findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginHelper.getInstance(getApplicationContext()).logOutQQ(UserConfigActivity.this);
                setResult(0);
                finish();
            }
        });
    }


}