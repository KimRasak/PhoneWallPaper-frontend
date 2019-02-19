package jzl.sysu.cn.phonewallpaperfrontend.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.Activity.MainActivity;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.UserService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.LoginWXBody;
import jzl.sysu.cn.phonewallpaperfrontend.Constants;
import jzl.sysu.cn.phonewallpaperfrontend.LoginHelper;
import jzl.sysu.cn.phonewallpaperfrontend.Response.LoginResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LoginHelper.getInstance().getWX().handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Toast.makeText(this, "Test ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.v(Constants.LOG_TAG, String.format("is ok: %b, auth code: %s", baseResp.errCode == BaseResp.ErrCode.ERR_OK, ((SendAuth.Resp) baseResp).code));

        if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
        final String code = ((SendAuth.Resp) baseResp).code;//需要转换一下才可以
        UserService service = ApiManager.getInstance().getUserService();
            LoginWXBody body = new LoginWXBody(code);
            service.loginWX(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    private void fail() {
                        Util.showNetworkFailToast(WXEntryActivity.this);
                        finish();
                    }
                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        if (loginResponse.isFail()) {
                            Toast.makeText(WXEntryActivity.this, loginResponse.getMessage() + " " + loginResponse.getCode(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LoginHelper.getInstance().setUserInfo(loginResponse);
                        Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onComplete() {}
                });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
