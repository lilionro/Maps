package com.zero.lionro.maps.ui.view;

/**
 * Created by king on 2017/8/18.
 */

public interface ILoginView {
    //获取用户名
    String getUserName();
    //获取密码
    String getPassWord();

    void showSuccessInfo();

    void showFailedInfo();
    //跳转
    void loginGotoMap();
}
