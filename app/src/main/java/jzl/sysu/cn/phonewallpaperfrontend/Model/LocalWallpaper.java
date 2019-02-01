package jzl.sysu.cn.phonewallpaperfrontend.Model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalWallpaper {
    private String imgSrc;
    private Long wallpaperId;

    public LocalWallpaper(String imgSrc, Long wallpaperId) {
        this.imgSrc = imgSrc;
        this.wallpaperId = wallpaperId;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Long getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(Long wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public static LocalWallpaper fromJSON(String json) {
        Log.i("helper load", json);
        try {
            JSONObject object = new JSONObject(json);
            String imgSrc = object.getString("imgSrc");
            Long wallpaperId = object.getLong("wallpaperId");

            return new LocalWallpaper(imgSrc, wallpaperId);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
