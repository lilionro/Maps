package com.zero.lionro.maps.ui.view;

import android.graphics.Bitmap;

/**
 * Created by king on 2017/8/18.
 */

public interface IRegisterView {
    //跳转
    void registerGotoLogin();
    //获取用户名
    String getUserName();
    //获取密码
    String getPassWord();
    //获取头像
    Bitmap getPhoto();

    void showSuccessInfo();

    void showFailedInfo();

}
