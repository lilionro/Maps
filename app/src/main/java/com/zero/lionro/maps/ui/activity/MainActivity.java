package com.zero.lionro.maps.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.MapView;
import com.avos.avoscloud.AVUser;
import com.zero.lionro.maps.R;
import com.zero.lionro.maps.databinding.ActivityMainBinding;
import com.zero.lionro.maps.presenter.impl.HelpYouPresenter;
import com.zero.lionro.maps.presenter.impl.ShowMapsPresenter;
import com.zero.lionro.maps.ui.view.IShowMapsView;
import com.zero.lionro.maps.utils.ToastUtils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements IShowMapsView {

    private ActivityMainBinding binding;
    private ShowMapsPresenter presenter;
    private static final String TAG="MainActivity";
    //定位需要的声明
    private AMapLocationClient locationClient;
    private HelpYouPresenter helpYouPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("获得的：："+sHA1(this));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new ShowMapsPresenter(this,this);
        //显示
       binding.map.onCreate(savedInstanceState);
        //得到用户的允许
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            //显示完整信息
            showSuccess();
        }
    }

    public void helpMe(View view){
        switch(view.getId()){
            case R.id.btn_helpMe:
                //跳转到"帮助我"
                MainToHelpMe();
                break;
            case R.id.btn_helpYou:
                //跳转到"帮助你"
                MainToHelpYou();
                break;
            case R.id.btn_cancel:
                presenter.showDialog();
                break;
            case R.id.btn_search:
                //跳转到搜索界面
                Intent intent = new Intent(this, PoiKeywordSearchActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    public void showSuccess() {
        presenter.showMap();
    }
    public static String sHA1(Context context) {
        try {
            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result =hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void showFailed() {

    }

    @Override
    public MapView getMapView() {
        return binding.map;
    }

    @Override
    public AMapLocationClient getLocationClient() {
        return locationClient;
    }
    //跳转到帮助我
    @Override
    public void MainToHelpMe() {
        //先判断用户是否登录
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser!=null){
            Intent intent = new Intent(this, HelpMeActivity.class);
            startActivity(intent);
            finish();
        }else {
            ToastUtils.showToast("请先登录");
        }

    }
    //跳转到帮助你
    @Override
    public void MainToHelpYou() {
        Intent intent = new Intent(this, HelpYouActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //重新绘制加载地图
        binding.map.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //暂停地图的绘制
        binding.map.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.map.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁地图
        binding.map.onDestroy();
    }


}
