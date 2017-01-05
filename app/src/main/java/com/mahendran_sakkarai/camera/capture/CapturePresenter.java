package com.mahendran_sakkarai.camera.capture;

import android.app.Fragment;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */
public class CapturePresenter implements CaptureContract.Presenter{
    private final CaptureContract.View mView;

    public CapturePresenter(CaptureContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void showErrorMessage(String message) {
        mView.showMessage(message);
    }

    @Override
    public void openCamera() {

    }
}
