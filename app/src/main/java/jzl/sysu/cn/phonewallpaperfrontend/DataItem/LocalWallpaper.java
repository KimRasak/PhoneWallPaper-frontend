package jzl.sysu.cn.phonewallpaperfrontend.DataItem;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalWallpaper {
    private String imgSrc;
    private String wallpaperId;

    public LocalWallpaper(String imgSrc, String wallpaperId) {
        this.imgSrc = imgSrc;
        this.wallpaperId = wallpaperId;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(String wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public static LocalWallpaper fromJSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String imgSrc = object.getString("imgSrc");
            String wallpaperId = object.getString("wallpaperId");

            return new LocalWallpaper(imgSrc, wallpaperId);
        } catch (JSONException e) {
            e.printStackTrace();
            return new LocalWallpaper("", "");
        }
    }
}
