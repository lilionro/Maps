package com.zero.lionro.maps.ui.view;

/**
 * Created by king on 2017/8/19.
 */

public interface IHelpMeView {
    //得到求救信息
    String getHelpInfo();
    //得到位置信息
    String getLocationInfo();

    void showSuccessInfo();

    void showFailedInfo();
}
