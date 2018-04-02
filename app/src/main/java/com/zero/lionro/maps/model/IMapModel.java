package com.zero.lionro.maps.model;



import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.MapView;
import com.zero.lionro.maps.presenter.HelpMeCallBack;
import com.zero.lionro.maps.presenter.HelpYouCallBack;
import com.zero.lionro.maps.presenter.IGetLatitudeAnd;
import com.zero.lionro.maps.presenter.LoginRequestCallBack;
import com.zero.lionro.maps.presenter.RegisterRequestCallBack;
import com.zero.lionro.maps.presenter.ShowMapsCallBack;


/**
 * Created by king on 2017/8/18.
 */

public interface IMapModel {
    void login(String username, String password, LoginRequestCallBack callBack);
    void register(String username, String password, Bitmap icon, RegisterRequestCallBack callBack);
    void maps(MapView mapView, AMapLocationClient mLocationClient, ShowMapsCallBack callBack);
    void helpMe(String helpInfo, String helpLocation, HelpMeCallBack callBack);
    void helpYou(RecyclerView recyclerView, HelpYouCallBack callBack);
    void helpYouLocation(IGetLatitudeAnd callBack);
}
