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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RepoFragment extends Fragment implements WallPaperRecyclerViewAdapter.ItemClickListener {
    private OnFragmentInteractionListener mListener;
    WallPaperRecyclerViewAdapter adapter;


    private static final String HOME_PC_IP = "192.168.31.246";
    private static final String SCHOOL_PC_IP = "192.168.199.181";
    private static final String WALLPAPER_LIST_URL = "http://" + SCHOOL_PC_IP +":9090/wallpaper/list";
    private int RV_COLUMN_WIDTH = 300; // recyclerView的图片宽度为300px，高度为520px（在item_wallpaper.xml里设置）。
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");
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
        rv_wallpapers.setHasFixedSize(true);

        // 设置adapter
        int spanCount = rv_wallpapers.getSpanCount();
        ArrayList<WallPaperDataItem> wallPaperDataItems = new ArrayList<>();
        adapter = new WallPaperRecyclerViewAdapter(view.getContext(), wallPaperDataItems, spanCount);
        adapter.setClickListener(this);
        rv_wallpapers.setAdapter(adapter);

        // 上拉刷新
        wallpaper_swipe_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                String category = "美女";
                int pageNum = 20;
                String url = WALLPAPER_LIST_URL; // 手机应当连接本地wifi，并访问pc的本地ip
                int pageSize = 10;
                OkHttpClient okHttpClient = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("category", category);
                    jsonObject.put("sort", "hot");
                    jsonObject.put("pageNum", String.valueOf(pageNum));
                    jsonObject.put("pageSize", String.valueOf(pageSize));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("RepoFragment", jsonObject.toString());
                // application/json
                RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, jsonObject.toString());
                Request request = new Request.Builder().url(url).post(requestBody).build();
                okhttp3.Response response = null;
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("RepoFragment", e.getMessage());
                        e.printStackTrace();
                        wallpaper_swipe_layout.setLoadingMore(false);

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String responseString = response.body().string();
                            JSONArray values = new JSONArray(responseString);
                            for (int i = 0; i < values.length(); i++) {
                                JSONObject item = values.getJSONObject(i);
                                String id = item.getString("id");
                                String category = item.getString("category");
                                String imgSrc = item.getString("path");
                                WallPaperDataItem dataItem = new WallPaperDataItem(id, category, imgSrc);
                                adapter.addDataItem(dataItem);
                            }
                            Log.i("RepoFragment", "values length: " + values.length());
                            Log.i("RepoFragment", responseString);
                            wallpaper_swipe_layout.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("RepoFragment", "notifyDataSetChanged. Adpater data size: " + adapter.getItemCount());
                                    adapter.notifyDataSetChanged();
                                    wallpaper_swipe_layout.setLoadingMore(false);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        boolean image_loaded = adapter.getItem(position).getImgBytes() != null;
        Toast.makeText(view.getContext(), "click: " + position + " image loaded: " + image_loaded  + "." + view.getWidth() + " " + view.getHeight(), Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
