package jzl.sysu.cn.phonewallpaperfrontend.Page;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.Adapter.LocalRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.RecyclerView.AutofitRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.LocalWallpaper;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.LoginFragment;
import jzl.sysu.cn.phonewallpaperfrontend.LocalHelper;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Fragment.UserInfoFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserPgae.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserPgae#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPgae extends Fragment{
    private OnFragmentInteractionListener mListener;

    // 本地壁纸栏
    private LinearLayout localLayout; // 有壁纸时显示
    private AutofitRecyclerView rvLocal;
    private LocalRecyclerViewAdapter localAdapter;
    private ConstraintLayout noWallpaperLayout; // 无壁纸时显示

    public UserPgae() {
        // Required empty public constructor
    }

    public static UserPgae newInstance(String param1, String param2) {
        return new UserPgae();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.page_user, container, false);

        // find views.
        rvLocal = view.findViewById(R.id.rv_local);
        localLayout = view.findViewById(R.id.local_layout);
        noWallpaperLayout = view.findViewById(R.id.no_wallpaper_layout);

        return view;
    }

    @Override
    public void onResume() {

        // 未登录显示登陆界面， 登陆则显示用户界面。
        Context context = this.getContext().getApplicationContext();
        LoginHelper helper = LoginHelper.getInstance(context);

        // 登陆QQ
        Toast.makeText(getActivity(), "登录状态:" + helper.isLoggedIn(getActivity()) + " openid: " + helper.getTencent().getOpenId() + " session: " + helper.getTencent().loadSession(getString(R.string.APP_ID)), Toast.LENGTH_LONG).show();

        changeUserFragment(helper.isLoggedIn(getActivity()));
        setLocalLayout();
        super.onResume();
    }

    public void changeUserFragment(boolean isLoggedIn) {
        if (isLoggedIn) {
            LoginHelper helper = LoginHelper.getInstance(getActivity());
            Fragment fragment = UserInfoFragment.newInstance(helper.getUserIcon(), helper.getUserName(), helper.getSignature());
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
    }

    public void setLocalLayout() {
        // 每行3列图片
        int spanCount = 3;

        // 设置Adapter
        ArrayList<LocalWallpaper> data = LocalHelper.load(getActivity());
        localAdapter = new LocalRecyclerViewAdapter(getContext(), data, spanCount);
        rvLocal.setAdapter(localAdapter);

        // 设置layout Manager
        GridLayoutManager manager = new GridLayoutManager(getActivity(), spanCount, RecyclerView.VERTICAL, false);
        rvLocal.setLayoutManager(manager);

        // 刷新Adapter
        localAdapter.notifyDataSetChanged();

        // 如果没有本地下载，则不展示该layout
        boolean hasLocal = data.size() > 0;
        displayLocalLayout(hasLocal);
    }

    private void displayLocalLayout(boolean hasLocal) {
        if (hasLocal) {
            localLayout.setVisibility(View.VISIBLE);
            noWallpaperLayout.setVisibility(View.GONE);
        } else {
            noWallpaperLayout.setVisibility(View.VISIBLE);
            localLayout.setVisibility(View.GONE);
        }
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
