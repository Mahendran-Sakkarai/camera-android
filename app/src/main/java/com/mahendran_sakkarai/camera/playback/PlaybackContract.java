package com.mahendran_sakkarai.camera.playback;

import com.mahendran_sakkarai.camera.BasePresenter;
import com.mahendran_sakkarai.camera.BaseView;

/**
 * Created by Mahendran Sakkarai on 1/4/2017.
 */

public interface PlaybackContract {
    interface Presenter extends BasePresenter {

    }

    interface View extends BaseView<Presenter>{

    }
}
