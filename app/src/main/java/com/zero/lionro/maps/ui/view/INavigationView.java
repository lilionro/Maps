package com.zero.lionro.maps.ui.view;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;

/**
 * Created by king on 2017/8/23.
 */

public interface INavigationView {
    ImageButton transitBtn();
    ImageButton drivingBtn();
    ImageButton walkBtn();
    MapView mapView();
    RelativeLayout getBottomLayout();
    TextView getTimeDes();
    TextView getDetailDes();
}
