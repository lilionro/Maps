package com.zero.lionro.maps.presenter;

import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by king on 2017/8/19.
 */
public interface HelpYouCallBack {
    void success(List<AVObject> lists);
    void failed();
}
