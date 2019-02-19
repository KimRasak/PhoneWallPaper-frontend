package jzl.sysu.cn.phonewallpaperfrontend.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.UserConfigActivity;


public class UserInfoFragment extends Fragment {
    private String userIconSrc;
    private String userName;
    private String signature;

    private ImageView userIcon;
    private TextView tvUserName;
    private TextView tvSignature;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    public static UserInfoFragment newInstance(String userIconSrc, String userName, String signature) {
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.userIconSrc = userIconSrc;
        fragment.userName = userName;
        fragment.signature = signature;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        // “帐号设置”。
        TextView user_config = (TextView)view.findViewById(R.id.user_config);
        user_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserConfigActivity.class);
                intent.putExtra("userIconSrc", userIconSrc);
                startActivityForResult(intent, Constants.REQUEST_CHANGE_USER_CONFIG);
            }
        });

        // 绑定视图。
        findView(view);

        // 用户头像。
        if (userIconSrc != null)
            loadUserIcon(userIconSrc);

        // 用户名
        if (userName != null)
            setTvUserName(userName);

        // 用户签名
        if (signature != null)
            setTvSignature(signature);

        return view;
    }

    private void setTvSignature(String signature) {
        tvSignature.setText(signature);
    }

    private void setTvUserName(String userName) {
        tvUserName.setText(userName);
    }

    private void findView(View view) {
        userIcon = view.findViewById(R.id.userIcon);
        tvUserName = view.findViewById(R.id.user_name);
        tvSignature = view.findViewById(R.id.user_signature);
    }

    private void loadUserIcon(String userIconSrc) {
        Log.v(Constants.LOG_TAG, String.format("userIcon url: " + userIconSrc));
        Glide.with(this).load(userIconSrc).into(userIcon);
    }
}
