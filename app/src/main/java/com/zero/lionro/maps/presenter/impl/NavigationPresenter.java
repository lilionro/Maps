package com.zero.lionro.maps.presenter.impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.zero.lionro.maps.R;
import com.zero.lionro.maps.app.MyLeanCloudApp;
import com.zero.lionro.maps.model.impl.MapModel;
import com.zero.lionro.maps.presenter.IGetLatitudeAnd;
import com.zero.lionro.maps.ui.activity.DriveRouteDetailActivity;
import com.zero.lionro.maps.ui.activity.NavigationActivity;
import com.zero.lionro.maps.ui.activity.WalkRouteDetailActivity;
import com.zero.lionro.maps.ui.view.INavigationView;
import com.zero.lionro.maps.utils.ToastUtils;

import static com.zero.lionro.maps.utils.ToastUtils.showToast;


/**
 * Created by king on 2017/8/23.
 */

public class NavigationPresenter implements RouteSearch.OnRouteSearchListener{

    private static final String TAG = "NavigationPresrnter";
    private final MapModel mapModel;
    private NavigationActivity activity;
    private INavigationView view;

    private int busMode = RouteSearch.BUS_DEFAULT;// 公交默认模式
    private int drivingMode = RouteSearch.DRIVING_SINGLE_DEFAULT;// 驾车默认模式
    private int walkMode = RouteSearch.WALK_DEFAULT;// 步行默认模式
    private RouteSearch routeSearch;
    private ImageButton drivingBtn;
    private MapView mapView;
    private ImageButton transitBtn;
    private ImageButton walkBtn;
    private AMap aMap;

    private BusRouteResult busRouteResult;// 公交模式查询结果
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private RelativeLayout mBottomLayout;
    private TextView mTimeDes;
    private TextView mDetailDes;
    private ProgressDialog progDialog = null;// 搜索时进度条

    public NavigationPresenter(INavigationView view,NavigationActivity activity) {
        mapModel = new MapModel();
        this.activity=activity;
        this.view=view;
        //初始化控件
        init();
    }


    private void init() {
        drivingBtn = view.drivingBtn();
        mapView = view.mapView();
        transitBtn = view.transitBtn();
        walkBtn = view.walkBtn();
        mBottomLayout = view.getBottomLayout();
        mTimeDes = view.getTimeDes();
        mDetailDes = view.getDetailDes();


        routeSearch = new RouteSearch(MyLeanCloudApp.getContext());
        routeSearch.setRouteSearchListener(this);

        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 设置地图可视缩放大小
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }



    public void getLocation(){
        mapModel.helpYouLocation(new IGetLatitudeAnd() {
            @Override
            public void success(final double latitude, final double longitude) {


                //需要帮助的人的坐标
                double needLatitude = activity.getIntent().getDoubleExtra("needLatitude", 0.2);
                double needLongitude = activity.getIntent().getDoubleExtra("needLongitude", 0.3);


                LatLonPoint startPoint = new LatLonPoint(latitude, longitude);
                LatLonPoint endPoint = new LatLonPoint(needLatitude, needLongitude);
                final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);

                aMap.addMarker(new MarkerOptions()
                        .position(ToastUtils.convertToLatLng(startPoint))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
                aMap.addMarker(new MarkerOptions()
                        .position(ToastUtils.convertToLatLng(endPoint))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stop)));
                //公交
                transitBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        transitBtn.setImageResource(R.drawable.route_bus_select);
                        drivingBtn.setImageResource(R.drawable.route_drive_normal);
                        walkBtn.setImageResource(R.drawable.route_walk_normal);
                        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, busMode, "重庆市", 1);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
                        routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
                    }
                });
                //驾车
                drivingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog();
                        drivingBtn.setImageResource(R.drawable.route_drive_select);
                        transitBtn.setImageResource(R.drawable.route_bus_normal);
                        walkBtn.setImageResource(R.drawable.route_walk_normal);
                        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
                        routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
                    }
                });
                //走路
                walkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog();

                        walkBtn.setImageResource(R.drawable.route_walk_select);
                        transitBtn.setImageResource(R.drawable.route_bus_normal);
                        drivingBtn.setImageResource(R.drawable.route_drive_normal);
                        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, walkMode);
                        routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询

                    }
                });

            }


        });

    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(activity);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }
    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    /*
    * 公交车
    * */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物

        if (rCode == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                busRouteResult = result;
                BusPath busPath = busRouteResult.getPaths().get(0);
                BusRouteOverlay routeOverlay = new BusRouteOverlay(MyLeanCloudApp.getContext(), aMap, busPath, busRouteResult.getStartPos(), busRouteResult.getTargetPos());
                routeOverlay.removeFromMap();
                routeOverlay.addToMap();
                routeOverlay.zoomToSpan();
                mBottomLayout.setVisibility(View.INVISIBLE);
            } else {
                showToast("对不起，没有搜索到相关数据！");
                mBottomLayout.setVisibility(View.INVISIBLE);
            }
        } else if (rCode ==  1802) {
            showToast("搜索失败,请检查网络连接！");
            mBottomLayout.setVisibility(View.INVISIBLE);
        } else if (rCode == 1002) {
            showToast("key验证无效！");
        } else{
            showToast("未知错误，请稍后重试!错误码为" + rCode);
            mBottomLayout.setVisibility(View.INVISIBLE);

        }
    }


    /*
    *
    * 驾车路线
    * */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物

        if (rCode == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                driveRouteResult = result;
                final DrivePath drivePath = driveRouteResult.getPaths().get(0);
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(MyLeanCloudApp.getContext(), aMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());

                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
                mBottomLayout.setVisibility(View.VISIBLE);
                int dis = (int) drivePath.getDistance();
                int dur = (int) drivePath.getDuration();
                String des = ToastUtils.getFriendlyTime(dur)+"("+ToastUtils.getFriendlyLength(dis)+")";
                mTimeDes.setText(des);
                mDetailDes.setVisibility(View.VISIBLE);
                int taxiCost = (int) driveRouteResult.getTaxiCost();
                mDetailDes.setText("打车约"+taxiCost+"元");
                mBottomLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity,
                                DriveRouteDetailActivity.class);
                        intent.putExtra("drive_path", drivePath);
                        intent.putExtra("drive_result",
                                driveRouteResult);
                        activity.startActivity(intent);
                    }
                });
            } else {
                showToast("对不起，没有搜索到相关数据！");
                mBottomLayout.setVisibility(View.INVISIBLE);
            }
        } else if (rCode == 1802) {
            showToast("搜索失败,请检查网络连接！");
            mBottomLayout.setVisibility(View.INVISIBLE);
        } else if (rCode == 1002) {
            showToast("key验证无效！");
        } else {
            showToast("未知错误，请稍后重试!错误码为" + rCode);
            mBottomLayout.setVisibility(View.INVISIBLE);
        }
    }

    /*
    * 步行路线
    * */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (rCode == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                walkRouteResult = result;
                final WalkPath walkPath = walkRouteResult.getPaths().get(0);

                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(MyLeanCloudApp.getContext(), aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
                mBottomLayout.setVisibility(View.VISIBLE);
                int dis = (int) walkPath.getDistance();
                int dur = (int) walkPath.getDuration();
                String des = ToastUtils.getFriendlyTime(dur)+"("+ToastUtils.getFriendlyLength(dis)+")";
                mTimeDes.setText(des);
                mDetailDes.setVisibility(View.GONE);
                mBottomLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity,
                                WalkRouteDetailActivity.class);
                        intent.putExtra("walk_path", walkPath);
                        intent.putExtra("walk_result",
                                walkRouteResult);
                        activity.startActivity(intent);
                    }
                });
            } else {
                mBottomLayout.setVisibility(View.INVISIBLE);
                showToast("对不起，没有搜索到相关数据！");
            }
        } else if (rCode == 1802) {
            mBottomLayout.setVisibility(View.INVISIBLE);
            showToast("搜索失败,请检查网络连接！");
        } else if (rCode == 1002) {
            mBottomLayout.setVisibility(View.INVISIBLE);
            showToast("key验证无效！");
        }else if (rCode==3003){
            showToast("步行算路起点、终点距离过长导致算路失败");
            mBottomLayout.setVisibility(View.INVISIBLE);
        }
        else {
            mBottomLayout.setVisibility(View.INVISIBLE);
            showToast("未知错误，请稍后重试!错误码为" + rCode);

        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult result, int rCode) {

    }
}
