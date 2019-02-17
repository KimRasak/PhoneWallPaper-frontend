package jzl.sysu.cn.phonewallpaperfrontend.Body;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jzl.sysu.cn.phonewallpaperfrontend.ProgressNotificationManager;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;

public class UploadWallpaperBody{
    /** 用于图片上传
     * @param wallpaper 要上传的壁纸图片
     * @return 用于retrofit请求的参数
     * */
    public static Map<String, RequestBody> createParams(Context context, File wallpaper, String name, String category) {
        Map<String, RequestBody> params = new HashMap<>();
        AuthBody authBody = new AuthBody();
        params.put("openId", Util.toRequestBodyOfText(authBody.getOpenId()));
        params.put("accessToken", Util.toRequestBodyOfText(authBody.getAccessToken()));
        params.put("auth", Util.toRequestBodyOfText(authBody.getAuth()));
        params.put("name", Util.toRequestBodyOfText(name));
        params.put("category", Util.toRequestBodyOfText(category));

        ProgressNotificationManager manager = new ProgressNotificationManager(context, ProgressNotificationManager.MODE_UPLOADING);
        params.put("wallpaper\"; filename=\"" + "file0.png", Util.toRequestBodyOfImage(wallpaper, manager));
        return params;
    }

    private static MultipartBody fileToMultipartBody(File file) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        builder.addFormDataPart("wallpaper", file.getName(), requestBody);

        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    private static MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            builder.addFormDataPart("file", file.getName(), requestBody);
        }

        builder.setType(MultipartBody.FORM);
        return builder.build();
    }
}
