package com.zero.lionro.maps.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.zero.lionro.maps.R;
import com.zero.lionro.maps.databinding.ActivityNavigationBinding;
import com.zero.lionro.maps.presenter.impl.NavigationPresenter;
import com.zero.lionro.maps.ui.view.INavigationView;


public class NavigationActivity extends AppCompatActivity implements INavigationView {

    private static final String TAG = "NavigationActivity";

    private ActivityNavigationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_navigation);


        binding.routeMap.onCreate(savedInstanceState);
        NavigationPresenter navigationPresenter = new NavigationPresenter(this,this);
        navigationPresenter.getLocation();

    }


    @Override
    protected void onPause() {
        super.onPause();
        binding.routeMap.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.routeMap.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.routeMap.onDestroy();
    }


    @Override
    public ImageButton transitBtn() {
        return binding.imagebtnRoadsearchTabTransit;
    }

    @Override
    public ImageButton drivingBtn() {
        return binding.imagebtnRoadsearchTabDriving;
    }

    @Override
    public ImageButton walkBtn() {
        return binding.imagebtnRoadsearchTabWalk;
    }

    @Override
    public MapView mapView() {
        return binding.routeMap;
    }

    @Override
    public RelativeLayout getBottomLayout() {
        return binding.bottomLayout;
    }

    @Override
    public TextView getTimeDes() {
        return binding.firstline;
    }

    @Override
    public TextView getDetailDes() {
        return binding.secondline;
    }
}
