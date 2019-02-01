package jzl.sysu.cn.phonewallpaperfrontend.ApiService;

import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static final String BASE_URL = "http://" + Constants.PC_IP +":9090";
    private static final ApiManager ourInstance = new ApiManager();

    private CategoryService categoryService;
    private WallpaperService wallpaperService;
    private UserService userService;


    private ApiManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        categoryService = retrofit.create(CategoryService.class);
        wallpaperService = retrofit.create(WallpaperService.class);
        userService = retrofit.create(UserService.class);
    }

    public static ApiManager getInstance() {
        return ourInstance;
    }

    public CategoryService getCategoryService() { return this.categoryService; }
    public WallpaperService getWallpaperService() { return this.wallpaperService; }
    public UserService getUserService() { return this.userService; }

}
