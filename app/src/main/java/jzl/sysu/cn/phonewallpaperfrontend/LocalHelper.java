package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.Adapter.LocalRecyclerViewAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.DataItem.LocalWallpaper;

public class LocalHelper {

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    private static File getFile(Context context, String name) {
        // 确保图像文件存在于本地
        File file = new File(context.getFilesDir(), name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static String output(Bitmap bmp, File file) {
        // 输出bmp到文件
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            return file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<LocalWallpaper> load(Context context) {
        SharedPreferences sp = getSP(context);
        String localWallpaperStr = sp.getString(Constants.LOCAL_WALLPAPER, "[]");

        ArrayList<LocalWallpaper> wallpapers = new ArrayList<>();
        try {
            // 添加本地壁纸元素
            JSONArray localWallpapers = new JSONArray(localWallpaperStr);
            for (int i = 0; i < localWallpapers.length(); i++) {
                String rawString = localWallpapers.getString(i);
                LocalWallpaper wallpaper = LocalWallpaper.fromJSON(rawString);
                wallpapers.add(wallpaper);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wallpapers;
    }

    private static boolean exist(ArrayList<LocalWallpaper> wallpapers, String wallpaperId) {
        for (LocalWallpaper local : wallpapers) {
            String id = local.getWallpaperId();
            if (id.equals(wallpaperId))
                return true;
        }
        return false;
    }

    private static String toJSON(ArrayList<LocalWallpaper> wallpapers) {
        JSONArray jsonArray = new JSONArray(wallpapers);
        return jsonArray.toString();
    }

    public static void save(Context context, Bitmap bmp, String wallpaperId) {
        SharedPreferences sp = getSP(context);

        // 确保文件存在。
        File file = getFile(context, wallpaperId);
        if (file == null)
            return;

        // 输出图片到文件。
        String path = output(bmp, file);
        if (path == null)
            return;

        // 取得对象，添加元素
        LocalWallpaper wallpaper = new LocalWallpaper(path, wallpaperId);
        ArrayList<LocalWallpaper> wallpapers = load(context);
        if (exist(wallpapers, wallpaperId))
            return;
        wallpapers.add(wallpaper);

        // 序列化，写回shared preferences
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.LOCAL_WALLPAPER, toJSON(wallpapers));
        editor.apply();
    }
}
