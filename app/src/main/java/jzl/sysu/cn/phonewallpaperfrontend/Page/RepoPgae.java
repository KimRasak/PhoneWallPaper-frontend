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
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.CategoryService;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Category;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.CategoryRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Comment;
import jzl.sysu.cn.phonewallpaperfrontend.RecyclerView.GridLayoutRecyclerView;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.WallpaperListActivity;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CategoryResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// 分类初始界面，可以在其中选择壁纸的分类。
public class RepoPgae extends Fragment implements CategoryRecyclerViewAdapter.ItemClickListener{
    private GridLayoutRecyclerView rvCategory;
    private CategoryRecyclerViewAdapter rvAdapter;

    public RepoPgae() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_repo, container, false);

        rvCategory = view.findViewById(R.id.rv_categories);
        ArrayList<Category> emptyDataItems = new ArrayList<>();
        rvAdapter = new CategoryRecyclerViewAdapter(view.getContext(), emptyDataItems);
        rvCategory.setAdapter(rvAdapter);
        rvAdapter.setClickListener(this);
        loadCategories();
        return view;
    }

    private void loadCategories() {
        CategoryService service = ApiManager.getInstance().getCategoryService();
        Observable<CategoryResponse> ob = service.getCategoryList();
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CategoryResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onComplete() {}

                    @Override
                    public void onNext(CategoryResponse res) {
                        rvAdapter.setHostName(res.getHostName());
                        rvAdapter.add(res.getCategories());
                        rvAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) { Util.showNetworkFailToast(getActivity()); }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        // 点击某个分类
        String category = rvAdapter.getCategory(position);
        Intent intent = new Intent(getActivity(), WallpaperListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
