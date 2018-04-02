package com.zero.lionro.maps.ui.view;


import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.MapView;


/**
 * Created by king on 2017/8/19.
 */

public interface IShowMapsView {
    void showSuccess();
    void showFailed();
    MapView getMapView();
    AMapLocationClient getLocationClient();
    //跳转到帮助我
    void MainToHelpMe();
    //跳转到帮助你
    void MainToHelpYou();
}
