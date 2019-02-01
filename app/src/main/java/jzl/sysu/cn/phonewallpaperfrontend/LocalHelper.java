package jzl.sysu.cn.phonewallpaperfrontend;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import jzl.sysu.cn.phonewallpaperfrontend.Model.LocalWallpaper;

public class LocalHelper {
    private static final String ROOT_FOLDER = Constants.ROOT_FOLDER;
    public static final String VERTICAL_FOLDER = ROOT_FOLDER + "/" + "vertical";

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    private static File getDir(String path) {
        // 确保图像文件存在于本地
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdir();
            return file;
        }
        return file;
    }

    private static File getImage(String name) {
        // 确保图像文件存在于本地
        File file = new File(Environment.getExternalStorageDirectory(), VERTICAL_FOLDER + "/" + name + ".jpg");
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

    private static boolean exist(ArrayList<LocalWallpaper> wallpapers, Long wallpaperId) {
        for (LocalWallpaper local : wallpapers) {
            Long id = local.getWallpaperId();
            if (id.equals(wallpaperId))
                return true;
        }
        return false;
    }

    private static String toJSON(ArrayList<LocalWallpaper> wallpapers) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < wallpapers.size(); i++) {
            LocalWallpaper wallpaper = wallpapers.get(i);
            try {
                JSONObject ob = new JSONObject();
                ob.put("imgSrc", wallpaper.getImgSrc());
                ob.put("wallpaperId", wallpaper.getWallpaperId());
                jsonArray.put(i, ob);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    public static ArrayList<LocalWallpaper> load(String folderPath) {
        Log.i("localHelper", folderPath);
        File folder = getDir(folderPath);
        ArrayList<LocalWallpaper> res = new ArrayList<>();

        // 找到所有.jpg, .jpeg结尾的文件。
        File[] images = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("jpg") || name.endsWith("jpeg");
            }
        });

        // 分析文件名，得到id。
        ArrayList<Long> ids = new ArrayList<>();
        for (File image : images) {
            String name = image.getName();
            if (name.lastIndexOf(".") > 0)
                name = name.substring(0, name.lastIndexOf("."));
            Long id;
            try {
                id = Long.valueOf(name);

            } catch (NumberFormatException e) {
                e.printStackTrace();
                id = -1L;
            }
            ids.add(id);
        }

        // 添加返回结果。
        int wallpaperNum = images.length;
        for (int i = 0; i < wallpaperNum; i++) {
            String src = images[i].getPath();
            Long id = ids.get(i);

            LocalWallpaper localWallpaper = new LocalWallpaper(src, id);
            res.add(localWallpaper);
        }

        return res;
    }

    public static void save(Context context, Bitmap bmp, Long wallpaperId) {;
        // 确保图像文件存在。
        File file = getImage(String.valueOf(wallpaperId));
        if (file == null)
            return;

        // 输出图片到文件。
        String path = output(bmp, file);
        Log.i("helper save", path);
    }

    public static void remove(String folderPath, Long wallpaperId) {
        // 取得对象，添加元素
        File folder = getDir(folderPath);
        ArrayList<LocalWallpaper> res = new ArrayList<>();

        // 找到所有.jpg, .jpeg结尾的文件。
        final String targetJPG= String.valueOf(wallpaperId) + ".jpg";
        final String targetJPEG = String.valueOf(wallpaperId) + ".jpeg";
        File[] images = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(targetJPG) || name.equals(targetJPEG);
            }
        });
        if (images.length > 0)
            images[0].delete();

    }
}
