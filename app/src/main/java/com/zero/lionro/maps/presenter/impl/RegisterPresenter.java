package com.zero.lionro.maps.presenter.impl;


import com.zero.lionro.maps.model.impl.MapModel;
import com.zero.lionro.maps.presenter.RegisterRequestCallBack;
import com.zero.lionro.maps.ui.view.IRegisterView;

/**
 * Created by king on 2017/8/18.
 */

public class RegisterPresenter {
    private IRegisterView iregisterView;
    private final MapModel personModel;


    public RegisterPresenter(IRegisterView view) {
        iregisterView=view;
        personModel = new MapModel();
    }

    public void registerSystem() {
        personModel.register(iregisterView.getUserName(), iregisterView.getPassWord(),iregisterView.getPhoto(), new RegisterRequestCallBack() {
            @Override
            public void registerSuccess() {
                //跳转到登录界面
                iregisterView.registerGotoLogin();
                //注册成功
                iregisterView.showSuccessInfo();

            }

            @Override
            public void registerFailed() {
                //注册失败
                iregisterView.showFailedInfo();
            }
        });
    }

}
