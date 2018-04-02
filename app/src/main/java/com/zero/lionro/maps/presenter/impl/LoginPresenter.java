package com.zero.lionro.maps.presenter.impl;


import com.zero.lionro.maps.model.impl.MapModel;
import com.zero.lionro.maps.presenter.LoginRequestCallBack;
import com.zero.lionro.maps.ui.view.ILoginView;

/**
 * Created by king on 2017/8/18.
 */

public class LoginPresenter {
    private ILoginView loginView;
    private MapModel iPersonModel;


    public LoginPresenter(ILoginView view){
        loginView=view;
        iPersonModel=new MapModel();
    }
    public void loginSystem(){
        iPersonModel.login(loginView.getUserName(), loginView.getPassWord(), new LoginRequestCallBack() {
            /*
            * 登陆成功
            * */
            @Override
            public void loginSuccess() {
                //跳转到主界面
                loginView.loginGotoMap();
                loginView.showSuccessInfo();
            }

            /*
            * 登录失败
            * */
            @Override
            public void loginFailed() {
                loginView.showFailedInfo();
            }
        });
    }
}
