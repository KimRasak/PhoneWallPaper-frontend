package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RepoFragment extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {
    private OnFragmentInteractionListener mListener;
    WallPaperRecyclerViewAdapter adapter;
    private int RV_COLUMN_WIDTH = 300; // recyclerView的图片宽度为300px，高度为520px（在item_wallpaper.xml里设置）。

    public RepoFragment() {
        // Required empty public constructor
    }

    public static RepoFragment newInstance(String param1, String param2) {
        RepoFragment fragment = new RepoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo, container, false);

        final SwipeToLoadLayout wallpaper_swipe_layout = view.findViewById(R.id.wallpaper_swipe_layout);
        LoadMoreFooterView swipe_load_more_footer = view.findViewById(R.id.swipe_load_more_footer);
        wallpaper_swipe_layout.setLoadMoreFooterView(swipe_load_more_footer);
        // 设置recyclerView
        // RecyclerView rv_wallpapers = view.findViewById(R.id.swipe_target);

        AutofitRecyclerView rv_wallpapers = view.findViewById(R.id.swipe_target);
        adapter = new WallPaperRecyclerViewAdapter(view.getContext());
        adapter.setClickListener(this);
        rv_wallpapers.setAdapter(adapter);

        // 上拉刷新
        wallpaper_swipe_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                int limit = 20;
                int skip = 10;
                String url = "http://www.baidu.com";
                OkHttpClient okHttpClient = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("category", "美女")
                        .add("sort", "hot")
                        .add("limit", String.valueOf(limit))
                        .add("skip", String.valueOf(skip))
                        .build();
                Request request = new Request.Builder().url(url).post(formBody).build();
                okhttp3.Response response = null;
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("RepoFragment", response.body().string());
                        wallpaper_swipe_layout.setLoadingMore(false);
                    }
                });
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void onItemClick(View view, int position) {
        Log.i("fragment", "click: " + position);
        Toast.makeText(view.getContext(), "click: " + position, Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
