package com.zero.lionro.maps.model.impl;


import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.zero.lionro.maps.app.MyLeanCloudApp;
import com.zero.lionro.maps.model.IMapModel;
import com.zero.lionro.maps.presenter.HelpMeCallBack;
import com.zero.lionro.maps.presenter.HelpYouCallBack;
import com.zero.lionro.maps.presenter.IGetLatitudeAnd;
import com.zero.lionro.maps.presenter.LoginRequestCallBack;
import com.zero.lionro.maps.presenter.RegisterRequestCallBack;
import com.zero.lionro.maps.presenter.ShowMapsCallBack;
import com.zero.lionro.maps.ui.activity.HelpMeActivity;
import com.zero.lionro.maps.ui.activity.MainActivity;
import com.zero.lionro.maps.ui.activity.NavigationActivity;
import com.zero.lionro.maps.utils.ToastUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;


/**
 * 用户相关业务逻辑实现类
 */
public class MapModel extends MainActivity implements IMapModel,AMapLocationListener,LocationSource {
    public static final String TAG="PersonModel";
    //定位需要的声明
    private AMapLocationClient mLocationClient;//定位发起端
    private AMapLocationClientOption mLocationOption ;//定位参数
    private OnLocationChangedListener mListener ;//定位监听器
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private static String location = null;
    private static double latitude=0.0;
    private static double longitude=0.0;
    private MainActivity activity;

    private AMap aMap;
    private AVUser userInfo;
    private AVFile avFile ;

    public MapModel(){
        activity=new MainActivity();
    }

    @Override
    public void login(final String username, final String password, final LoginRequestCallBack callBack) {
        //开始进行登录
        //先进行判断是否为空
        if ((username.isEmpty()) || (password.isEmpty())) {
            ToastUtils.showToast("用户名和密码不为空");
        }else {
            AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        //登陆成功
                        callBack.loginSuccess();
                    } else if (e != null) {
                        callBack.loginFailed();
                    }
                }
            });
        }
    }

    @Override
    public void register(final String username, String password, Bitmap icon, final RegisterRequestCallBack callBack)  {
        //开始进行注册
        if (username.isEmpty()|| password.isEmpty()){
            ToastUtils.showToast("用户名和密码不能为空");
        } else if (icon==null){
            ToastUtils.showToast("请选择你要使用的头像");
        }
        else {
            userInfo = new AVUser();

            //将用户名和密码上传到云服务器并且保存
            userInfo.setUsername(username);
            userInfo.setPassword(password);
            //保存头像
            String iconPath = saveBitmap(icon, username);

            try {
                avFile = AVFile.withAbsoluteLocalPath(username, iconPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (iconPath != null) {
                userInfo.put("head", avFile);
            }
            userInfo.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                        callBack.registerSuccess();
                    } else {
                        // 失败的原因可能有多种，常见的是用户名已经存在。
                        callBack.registerFailed();
                    }
                }
            });
            userInfo.saveInBackground();
        }
    }

    private String saveBitmap(Bitmap bitmap, String name) {
        FileOutputStream foutput = null;
        String imagePath = null;
        try {
            File appDir = new File(Environment.getExternalStorageDirectory(), "GaoMaps");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File headIcons = new File(appDir, "head");
            if (!headIcons.exists()) {
                headIcons.mkdir();
            }
            File file = new File(headIcons, name + ".jpg");
            foutput = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
            imagePath = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    //地图处理
    @Override
    public void maps(MapView mapView, AMapLocationClient mLocationClient, ShowMapsCallBack callBack) {
        //定义一个UiSettings对象
        UiSettings mUiSettings;
        aMap = mapView.getMap();
        /*-------------------显示定位蓝点------------- */
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
        //开始定位
        initLoc();
            /*-------------------控件交互------------- */

        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //控制比例尺控件是否显示
        mUiSettings.setScaleControlsEnabled(true);
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        mUiSettings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
    }


    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(MyLeanCloudApp.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener=onLocationChangedListener;

    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                Date date = new Date(amapLocation.getTime());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    df.format(date);//定位时间
                }
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                //具体地址
                location=amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum();
                //经纬度
                latitude=amapLocation.getLatitude();
                longitude=amapLocation.getLongitude();


                Intent intent = new Intent(MyLeanCloudApp.getContext(), HelpMeActivity.class);
                intent.putExtra("locationInfo", amapLocation.getCity()  + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());

                Intent intent1 = new Intent(MyLeanCloudApp.getContext(), NavigationActivity.class);
                intent1.putExtra("Latitude",latitude);
                intent1.putExtra("Longitude",longitude);

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                     isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location_jpg Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void deactivate() {
        mListener=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deactivate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    //发布求救信息
    @Override
    public void helpMe(String helpInfo, String helpLocation, final HelpMeCallBack callBack) {
        //将输入求救信息保存到云端
        AVObject object = new AVObject("HelpMe");
        //得到用户名
        AVUser currentUser = AVUser.getCurrentUser();
        String username = currentUser.getUsername();
        AVFile avFile = (AVFile) currentUser.get("head");

        Log.d(TAG, "onLocationChanged: latitude"+latitude+"   "+"longitude"+longitude);
        Log.d(TAG, "helpMe:head "+avFile);
        if (latitude==0||longitude==0||helpInfo.isEmpty()||username.isEmpty()
        ){
            ToastUtils.showToast("发布消息失败，请退出重试!!!");
        } else {
            object.put("HelpInfo", helpInfo);
            //具体位置
            helpLocation=location;
            object.put("Location", helpLocation);
            object.put("UserName", username);
            object.put("Latitude",latitude) ;
            object.put("Longitude",longitude);
            object.put("Head",avFile);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        //保存成功
                        callBack.success();
                    } else {
                        callBack.failed();
                    }
                }
            });
        }
    }

    //查看别人的求救信息
    @Override
    public void helpYou( RecyclerView recyclerView,final HelpYouCallBack callBack) {
        //查询云端的数据
        final AVQuery<AVObject> query = new AVQuery<>("HelpMe");
        query.whereContains("HelpInfo", "");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null) {
                    for (int i = 0; i < list.size(); i++) {
                        callBack.success(list);

                    }
                }else {
                    callBack.failed();
                }

            }
        });
    }

    @Override
    public void helpYouLocation( IGetLatitudeAnd callBack) {
        callBack.success(latitude,longitude);
    }
}
