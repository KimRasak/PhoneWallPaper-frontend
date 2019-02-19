package jzl.sysu.cn.phonewallpaperfrontend.ApiService;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static jzl.sysu.cn.phonewallpaperfrontend.Constants.LOGIN_HELPER;
import static jzl.sysu.cn.phonewallpaperfrontend.Constants.LOGIN_HELPER_COOKIE;

public class ApiManager {
    private static final String BASE_URL = "http://" + Constants.PC_IP +":9090";
    private static final ApiManager ourInstance = new ApiManager();

    private CategoryService categoryService;
    private WallpaperService wallpaperService;
    private UserService userService;

    private Context applicationContext;

    private ApiManager() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AddCookiesInterceptor()) //这部分
                .addInterceptor(new ReceivedCookiesInterceptor()) //这部分
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        categoryService = retrofit.create(CategoryService.class);
        wallpaperService = retrofit.create(WallpaperService.class);
        userService = retrofit.create(UserService.class);
    }

    public static ApiManager getInstance() {
        return ourInstance;
    }

    public void init(Context applicationContext) { this.applicationContext = applicationContext; }

    public CategoryService getCategoryService() { return this.categoryService; }
    public WallpaperService getWallpaperService() { return this.wallpaperService; }
    public UserService getUserService() { return this.userService; }

    public static void clearCookies(Context applicationContext) {
        writeCookies(new ArrayList<String>(), applicationContext);
    }

    public static void writeCookies(List<String> cookieList, Context applicationContext) {
        HashSet<String> cookies = new HashSet<>(cookieList);
        SharedPreferences.Editor config = applicationContext.getSharedPreferences(LOGIN_HELPER, Context.MODE_PRIVATE)
                .edit();
        config.putStringSet(LOGIN_HELPER_COOKIE, cookies);
        config.commit();
    }

    public static HashSet<String> readCookies(Context applicationContext) {
        HashSet<String> cookies = (HashSet<String>) applicationContext.getSharedPreferences(LOGIN_HELPER,
                Context.MODE_PRIVATE).getStringSet(LOGIN_HELPER_COOKIE, null);
        return cookies;
    }

    //cookie 读取拦截器
    public class ReceivedCookiesInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            List<String> cookieList = originalResponse.headers("Set-Cookie");

            if (!cookieList.isEmpty())
                writeCookies(cookieList, applicationContext);
            return originalResponse;
        }
    }

    public class AddCookiesInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            HashSet<String> cookies = readCookies(applicationContext);
            if (cookies != null) {
                for (String cookie : cookies) {
                    builder.addHeader("Cookie", cookie);
                    Log.v(Constants.LOG_TAG, "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
                }
            }
            return chain.proceed(builder.build());
        }
    }

}
