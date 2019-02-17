package jzl.sysu.cn.phonewallpaperfrontend.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jzl.sysu.cn.phonewallpaperfrontend.Adapter.PopWindowCategoryAdapter;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.ApiManager;
import jzl.sysu.cn.phonewallpaperfrontend.ApiService.WallpaperService;
import jzl.sysu.cn.phonewallpaperfrontend.Body.UploadWallpaperBody;
import jzl.sysu.cn.phonewallpaperfrontend.R;
import jzl.sysu.cn.phonewallpaperfrontend.Response.CodeResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Response.UploadResponse;
import jzl.sysu.cn.phonewallpaperfrontend.Util;
import okhttp3.RequestBody;
import retrofit2.http.PartMap;

public class UploadActivity extends AppCompatActivity implements PopWindowCategoryAdapter.ItemClickListener{
    private static final int SELECT_PIC_BY_PICK_PHOTO = 1;
    private static String TV_CATEGORY_DEFAULT_TEXT;

    private Button upload;
    private EditText etWallpaperName;
    private TextView tvCategory;
    AlertDialog dialog;
    private ConstraintLayout wallpaperLayout;
    private ImageView wallpaperSelected;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        TV_CATEGORY_DEFAULT_TEXT = getResources().getString(R.string.tv_category_default);
        upload = findViewById(R.id.upload);
        etWallpaperName = findViewById(R.id.etWallpaperName);
        tvCategory = findViewById(R.id.tvCategory);
        wallpaperLayout = findViewById(R.id.wallpaperLayout);
        wallpaperSelected = findViewById(R.id.wallpaperSelected);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadWallpaper(picPath);
            }
        });

        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCategory();
            }
        });

        wallpaperLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });

        wallpaperSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e("uploadActivity","ActivityResult resultCode error");
            return;
        }

        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                picPath = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));

                Log.d("图片路径啊啊啊啊啊啊", picPath);
                showSelectedWallpaper(picPath);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void chooseCategory() {
        final AlertDialog.Builder alertDialog7 = new AlertDialog.Builder(this);
        View dialogView = View.inflate(this, R.layout.layout_choose_category, null);

        // 设置类别列表。
        RecyclerView rvCategories = dialogView.findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        String []categories = getResources().getStringArray(R.array.categories);
        List<String> data = Arrays.asList(categories);
        PopWindowCategoryAdapter adapter = new PopWindowCategoryAdapter(dialogView.getContext(), data);
        rvCategories.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        alertDialog7
                .setTitle("请选择类别")
                .setView(dialogView)
                .create();
        dialog = alertDialog7.show();
        adapter.notifyDataSetChanged();
        Log.i("popWindow", "" + adapter.getItemCount());
    }

    private void showSelectedWallpaper(String picPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        wallpaperSelected.setImageBitmap(bitmap);
        wallpaperLayout.setVisibility(View.GONE);
        wallpaperSelected.setVisibility(View.VISIBLE);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private boolean isUploadParamsValid() {
        String name = etWallpaperName.getText().toString();
        String category = tvCategory.getText().toString();

        if (name.length() > 16) {
            showToast("名称过长");
            return false;
        }
        if (name.length() == 0) {
            showToast("请填写壁纸的名称");
            return false;
        }

        if (category.equals(TV_CATEGORY_DEFAULT_TEXT)) {
            showToast("请选择分类");
            return false;
        }

        return true;
    }

    private void uploadWallpaper(String picPath) {
        if (!isUploadParamsValid()) return;

        String name = etWallpaperName.getText().toString();
        String category = tvCategory.getText().toString();

        showToast("开始上传");
        WallpaperService service = ApiManager.getInstance().getWallpaperService();
        Map<String, RequestBody> params = UploadWallpaperBody.createParams(this, new File(picPath), name, category);
        Observable<UploadResponse> ob = service.uploadWallpaper(params);
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UploadResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {}
                    @Override
                    public void onComplete() {}
                    @Override
                    public void onError(Throwable e) {
                        Util.showNetworkFailToast(UploadActivity.this);
                    }
                    @Override
                    public void onNext(UploadResponse response) {
                        if (response.isFail())
                            Util.showNetworkFailToast(UploadActivity.this);
                        else
                            Toast.makeText(UploadActivity.this, "成功上传, 当天剩余上传次数: " + response.getLeftCound(), Toast.LENGTH_SHORT).show();
                    }
                });
        finish();
    }

    @Override
    public void onCategoryItemClick(View view, String category) {
        dialog.dismiss();
        tvCategory.setText(category);
    }
}
