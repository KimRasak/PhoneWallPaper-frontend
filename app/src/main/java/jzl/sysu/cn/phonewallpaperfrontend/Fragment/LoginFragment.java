package jzl.sysu.cn.phonewallpaperfrontend.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jzl.sysu.cn.phonewallpaperfrontend.Activity.MainActivity;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class LoginFragment extends Fragment {
    // login buttons.
    ImageButton btn_qq;
    ImageButton btn_weixin;

    // private String LOGIN_URL = "http://" + jzl.sysu.cn.phonewallpaperfrontend.Constants.PC_IP + ":9090/user/login";

    private LoginFragmentListener mListener;
    // LoginListener listener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btn_qq =(ImageButton)view.findViewById(R.id.qq);
        btn_weixin = (ImageButton)view.findViewById(R.id.weixin);

        btn_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.doLogin(LoginHelper.AUTH_QQ);
                }
            }
        });

        btn_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appId = getString(R.string.APP_ID);
                Tencent tencent = Tencent.createInstance(appId, getActivity());
                // tencent.logout(getA);
                JSONObject jsonObject = tencent.loadSession(appId);

                Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            mListener = (LoginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface LoginFragmentListener {
        void doLogin(String auth);
    }
}
