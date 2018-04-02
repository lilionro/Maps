package com.zero.lionro.maps.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.zero.lionro.maps.R;
import com.zero.lionro.maps.databinding.ActivityLoginBinding;
import com.zero.lionro.maps.presenter.impl.LoginPresenter;
import com.zero.lionro.maps.presenter.impl.RegisterPresenter;
import com.zero.lionro.maps.ui.view.ILoginView;
import com.zero.lionro.maps.ui.view.IRegisterView;
import com.zero.lionro.maps.utils.ToastUtils;


public class LoginActivity extends AppCompatActivity implements ILoginView,IRegisterView {

    private ActivityLoginBinding binding;
    private LoginPresenter loginPresenter;
    private RegisterPresenter registerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginPresenter = new LoginPresenter(this);
        registerPresenter = new RegisterPresenter(this);
        //更改状态栏

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setSupportActionBar(binding.toolbar);
    }
        public void onClick(View view){
            switch (view.getId()){
                case R.id.bt_login:
                    //登录点击事件
                    loginPresenter.loginSystem();

                    break;
                case R.id.tv_register:
                    //注册点击事件
                    registerGotoLogin();
                    break;
                default:
                    break;
            }
        }
/*
* 返回用户名信息
* */
    @Override
    public String getUserName() {
        return binding.etUsername.getText().toString().trim();
    }
/*
* 返回密码信息
* */
    @Override
    public String getPassWord() {
        return binding.etPassword.getText().toString().trim();
    }

    @Override
    public Bitmap getPhoto() {
        return null;
    }

    /*
    * 登陆成功回调
    * */
    @Override
    public void showSuccessInfo() {
        ToastUtils.showToast("登陆成功");
    }
/*
* 登录失败回调
* */
    @Override
    public void showFailedInfo() {
        ToastUtils.showToast("登录失败");
    }

    @Override
    public void registerGotoLogin() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }
    //成功就跳转到主界面
    @Override
    public void loginGotoMap() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
