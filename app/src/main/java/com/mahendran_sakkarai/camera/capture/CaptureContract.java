package com.mahendran_sakkarai.camera.capture;

import com.mahendran_sakkarai.camera.BasePresenter;
import com.mahendran_sakkarai.camera.BaseView;

/**
 * Created by Mahendran Sakkarai on 1/4/2017.
 */

public interface CaptureContract {
    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter> {

    }
}
