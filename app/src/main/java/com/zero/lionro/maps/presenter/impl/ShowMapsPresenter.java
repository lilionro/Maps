package com.zero.lionro.maps.presenter.impl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.amap.api.maps2d.model.LatLng;
import com.avos.avoscloud.AVUser;
import com.zero.lionro.maps.model.impl.MapModel;
import com.zero.lionro.maps.presenter.ShowMapsCallBack;
import com.zero.lionro.maps.ui.activity.LoginActivity;
import com.zero.lionro.maps.ui.activity.MainActivity;
import com.zero.lionro.maps.ui.view.IShowMapsView;
import com.zero.lionro.maps.utils.ToastUtils;


/**
 * Created by king on 2017/8/19.
 */

public class ShowMapsPresenter extends MainActivity {
    private static final String TAG="ShowMapsPresenter";
    private IShowMapsView view;
    private MapModel iPersonModel;
    private MainActivity activity;
    private LatLng stopLatLng;


    public ShowMapsPresenter(IShowMapsView view, MainActivity activity){
        this.view=view;
        this.activity=activity;
        iPersonModel=new MapModel();
    }
    public void showMap(){
        iPersonModel.maps(view.getMapView(),view.getLocationClient() ,new ShowMapsCallBack() {
            @Override
            public void showSuccess() {

            }

            @Override
            public void showFailed() {

            }
        });
    }
    public void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("注销登录");
        dialog.setMessage("你确定要注销账号吗");

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //注销登录
                AVUser.getCurrentUser().logOut();
                //返回到登录界面
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
                ToastUtils.showToast("您已注销");
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

}
