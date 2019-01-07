package jzl.sysu.cn.phonewallpaperfrontend.Page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.DataItem.CategoryDataItem;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.CategoryRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.GridLayoutRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.WallpaperListActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// 分类初始界面，可以在其中选择壁纸的分类。
public class RepoPgae extends Fragment implements CategoryRecyclerViewAdapter.ItemClickListener{
    private GridLayoutRecyclerView rvCategory;
    private CategoryRecyclerViewAdapter rvAdapter;
    private String CATEGORY_LIST_URL = "http://" + Constants.PC_IP +":9090/wallpaper/category";
    public RepoPgae() {
        // Required empty public constructor
    }

    public static RepoPgae newInstance() {
        RepoPgae fragment = new RepoPgae();
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_repo, container, false);

        rvCategory = view.findViewById(R.id.rv_categories);
        ArrayList<CategoryDataItem> emptyDataItems = new ArrayList<>();
        rvAdapter = new CategoryRecyclerViewAdapter(view.getContext(), emptyDataItems);
        rvCategory.setAdapter(rvAdapter);
        rvAdapter.setClickListener(this);
        loadCategories();
        return view;
    }

    private void loadCategories() {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = CATEGORY_LIST_URL;
        Request request = new Request.Builder().url(url).get().build();

        // 接收壁纸信息的回调函数。
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                //在子线程中直接去new 一个handler
                new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "加载分类列表失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("RepoPage", "返回信息：" + responseString);
                try {
                    JSONArray categoryJsonArray = new JSONArray(responseString);

                    // 提取每个分类信息
                    for (int i = 0; i < categoryJsonArray.length(); i++) {
                        JSONObject itemJsonObject = categoryJsonArray.getJSONObject(i);
                        String name = itemJsonObject.getString("name");
                        String background = itemJsonObject.getString("background");
                        CategoryDataItem dataItem = new CategoryDataItem(name, background);

                        // 添加分类数据
                        rvAdapter.addDataItem(dataItem);
                        Log.i("RepoPage", "加载类别：" + name);
                    }
                    rvCategory.post(new Runnable() {
                        @Override
                        public void run() {
                            rvAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "已有数据：" + rvAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), WallpaperListActivity.class);
        String category = rvAdapter.getCategory(position);
        intent.putExtra("category", category);
        startActivity(intent);
        Toast.makeText(view.getContext(), "点击" + category + " 数组长：" + rvAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
    }
}
