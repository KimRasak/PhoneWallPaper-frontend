package jzl.sysu.cn.phonewallpaperfrontend.ApiService;

import java.util.List;

import io.reactivex.Observable;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Category;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CategoryResponse;
import retrofit2.http.GET;

public interface CategoryService {

    @GET("wallpaper/category")
    Observable<CategoryResponse> getCategoryList();
}
