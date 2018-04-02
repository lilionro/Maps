package com.zero.lionro.maps.presenter.impl;


import com.zero.lionro.maps.model.impl.MapModel;
import com.zero.lionro.maps.presenter.HelpMeCallBack;
import com.zero.lionro.maps.ui.view.IHelpMeView;

/**
 * Created by king on 2017/8/19.
 */

public class HelpMePresenter {
    private IHelpMeView view;
    private final MapModel mapModel;

    public HelpMePresenter(IHelpMeView view) {
        this.view=view;
        mapModel = new MapModel();
    }

    public void helpMeSystem(){
        mapModel.helpMe(view.getHelpInfo(), view.getLocationInfo(), new HelpMeCallBack() {
            @Override
            public void success() {
                view.showSuccessInfo();
            }

            @Override
            public void failed() {
                view.showFailedInfo();
            }
        });
    }
}
