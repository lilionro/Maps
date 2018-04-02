package com.zero.lionro.maps.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;

import com.zero.lionro.maps.R;
import com.zero.lionro.maps.presenter.impl.HelpYouPresenter;
import com.zero.lionro.maps.ui.view.IHelpYouView;


public class HelpYouActivity extends AppCompatActivity implements IHelpYouView {


    private RecyclerView rv_helpInfo;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_youctivity);
        rv_helpInfo = (RecyclerView) findViewById(R.id.rv_helpInfo);
        HelpYouPresenter helpYouPresenter = new HelpYouPresenter(this,this);
        helpYouPresenter.HelpInfo();



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //更改状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_white);
            actionBar.setHomeButtonEnabled(true);
        }
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
    public RecyclerView getRecyclerView() {
        return rv_helpInfo;
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
