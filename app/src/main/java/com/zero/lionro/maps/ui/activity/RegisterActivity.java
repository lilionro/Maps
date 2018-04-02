package com.zero.lionro.maps.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.zero.lionro.maps.R;
import com.zero.lionro.maps.databinding.ActivityRegisterBinding;
import com.zero.lionro.maps.presenter.impl.RegisterPresenter;
import com.zero.lionro.maps.ui.view.IRegisterView;
import com.zero.lionro.maps.ui.view.ScalePopup;
import com.zero.lionro.maps.utils.ToastUtils;

import static com.zero.lionro.maps.ui.view.ScalePopup.CHOOSE_PHOTO;
import static com.zero.lionro.maps.ui.view.ScalePopup.TAKE_PHOTO;


public class RegisterActivity extends AppCompatActivity implements IRegisterView {

    private ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;
    private RegisterPresenter registerPresenter;
    private ScalePopup popup;
    public static Bitmap img;
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            hideProgressDialog();

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);
        registerPresenter = new RegisterPresenter(this);
        //更改状态栏
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }



        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_white);
            actionBar.setHomeButtonEnabled(true);

        }
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tx_1:
                System.out.println("拍照");
                break;
            case R.id.tx_2:
                System.out.println("相册");
                break;
        }

        registerPresenter.registerSystem();
        //跳转到登录界面

    }
    //设置头像
    public void takePhoto(View view){
        //打开一个popupWindow
        popup = new ScalePopup(this);
        popup.showPopupWindow();

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    //获取注册的用户名
    @Override
    public String getUserName() {
        return binding.etUsername.getText().toString().trim();
    }

    //密码
    @Override
    public String getPassWord() {
        return binding.etPassword.getText().toString().trim();
    }

    @Override
    public Bitmap getPhoto()     {
        return img;
    }


    @Override
    public void showSuccessInfo() {
        registerGotoLogin();
        ToastUtils.showToast("注册成功");

    }

    @Override
    public void showFailedInfo() {
        ToastUtils.showToast("注册失败");
    }

    @Override
    public void registerGotoLogin() {

        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {

                        // 将拍摄的照片显示出来
                        Bitmap bitmap =  BitmapFactory.decodeStream(getContentResolver().openInputStream(ScalePopup.imageUri));
                        binding.registerSettingPhoto.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            img=bitmap;
            binding.registerSettingPhoto.setImageBitmap(bitmap);
        } else {
            ToastUtils.showToast("failed to get image");
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    popup.openAlbum();
                } else {
                    ToastUtils.showToast("You denied the permission");
                }
                break;
            default:
        }
    }


    /*
     * 提示加载
     */
    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(RegisterActivity.this,
                    title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }

    /*
     * 隐藏提示加载
     */
    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }
}
