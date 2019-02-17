package jzl.sysu.cn.phonewallpaperfrontend.ApiService;

import java.util.List;

import io.reactivex.Observable;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LoginBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LoginQQBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LoginWXBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.RecBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.SignatureBody;
import jzl.sysu.cn.phonewallpaperfrontend.Body.UserNameBody;
import jzl.sysu.cn.phonewallpaperfrontend.Model.WallPaper;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Response.LoginResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("user/checkLogin")
    Observable<LoginResponse> checkLogin();

    @POST("user/loginQQ")
    Observable<LoginResponse> loginQQ(@Body LoginQQBody body);

    @POST("user/loginWX")
    Observable<LoginResponse> loginWX(@Body LoginWXBody body);

    @POST("user/logout")
    Observable<CodeResponse> logout();

    @POST("user/userName")
    Observable<CodeResponse> changeUserName(@Body UserNameBody body);

    @POST("user/signature")
    Observable<CodeResponse> changeSignature(@Body SignatureBody body);

}
