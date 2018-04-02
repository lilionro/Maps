package com.zero.lionro.maps.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.zero.lionro.maps.R;
import com.zero.lionro.maps.databinding.ActivityHelpMectivityBinding;
import com.zero.lionro.maps.presenter.impl.HelpMePresenter;
import com.zero.lionro.maps.ui.view.IHelpMeView;
import com.zero.lionro.maps.utils.ToastUtils;


public class HelpMeActivity extends AppCompatActivity implements IHelpMeView {

    private ActivityHelpMectivityBinding binding;
    private HelpMePresenter presenter;
    private final String TAG="HelpMeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help_mectivity);
        presenter = new HelpMePresenter(this);

        //更改状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_white);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void share(View view) {
        presenter.helpMeSystem();
    }
    @Override
    public String getHelpInfo() {
        return binding.etHelpMe.getText().toString().trim();
    }

    @Override
    public String getLocationInfo() {
        return binding.etHelpMeLocation.getText().toString().trim();
    }

    @Override
    public void showSuccessInfo() {
        ToastUtils.showToast("发布成功");
    }

    @Override
    public void showFailedInfo() {
        ToastUtils.showToast("发布失败，请稍后重试");
    }

    //显示返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
