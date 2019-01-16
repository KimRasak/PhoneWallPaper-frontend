package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Util {
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    public static RequestBody buildRequestBody(JSONObject data){
        return RequestBody.create(FORM_CONTENT_TYPE, data.toString());
    }

    public static RequestBody buildAuthRequestBody(JSONObject data, Context applicationContext){
        LoginHelper helper = LoginHelper.getInstance(applicationContext);

        String openid = helper.getOpenId();
        String accessToken = helper.getAccessToken();
        String auth = helper.getAuth();

        try {
            data.put("openId", openid);
            data.put("accessToken", accessToken);
            data.put("auth", auth);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(FORM_CONTENT_TYPE, data.toString());
    }
}
