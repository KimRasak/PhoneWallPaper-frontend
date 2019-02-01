package jzl.sysu.cn.phonewallpaperfrontend.ApiService;

import android.graphics.pdf.PdfDocument;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import jzl.sysu.cn.phonewallpaperfrontend.Body.ClickBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.GetCommentBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LikeBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.PageBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.PutCommentBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.RecBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.SetBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.TestBody;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Category;
import jzl.sysu.cn.phonewallpaperfrontend.Model.Comment;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.Response.ClickResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Response.PageResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WallpaperService {
    @POST("recommend/get")
    Observable<PageResponse> getRec(@Body RecBody body);

    @POST("wallpaper/list")
    Observable<PageResponse> getPage(@Body PageBody body);

    @POST("wallpaper/click")
    Observable<ClickResponse> click(@Body ClickBody body);

    @POST("wallpaper/set")
    Observable<CodeResponse> set(@Body SetBody body);

    @POST("comment/view")
    Observable<List<Comment>> getComment(@Body GetCommentBody body);

    @POST("relationship/like")
    Observable<CodeResponse> like(@Body LikeBody body);

    @POST("comment/put")
    Observable<Comment> putComment(@Body PutCommentBody body);

}
