package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.logging.Logger;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment{
    private OnFragmentInteractionListener mListener;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        return view;
    }

    @Override
    public void onResume() {

        // 未登录显示登陆界面， 登陆则显示用户界面。
        Context context = this.getContext().getApplicationContext();
        LoginHelper helper = LoginHelper.getInstance(context);

        // 登陆QQ
        Toast.makeText(getActivity(), "登录状态:" + helper.isLoggedIn(getActivity()) + " openid: " + helper.getTencent().getOpenId() + " session: " + helper.getTencent().loadSession(getString(R.string.APP_ID)), Toast.LENGTH_LONG).show();

        if (helper.isLoggedIn(getActivity())) {
            Fragment fragment = new UserInfoFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.user_info_container, fragment)
                    .commit();
        }
        else {
            Fragment fragment = new LoginFragment();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.user_info_container, fragment)
                    .commit();
        }

        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(JSONObject jsonObject);
    }
}
