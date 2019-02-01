package jzl.sysu.cn.phonewallpaperfrontend.ApiService;

import java.util.List;

import io.reactivex.Observable;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Category;
import retrofit2.http.GET;

public interface CategoryService {

    @GET("wallpaper/category")
    Observable<List<Category>> getCategoryList();
}
