package com.mahendran_sakkarai.camera.capture;

import android.hardware.Camera;
import android.net.Uri;

import com.mahendran_sakkarai.camera.BasePresenter;
import com.mahendran_sakkarai.camera.BaseView;
import com.mahendran_sakkarai.camera.camera.CaptureState;
import com.mahendran_sakkarai.camera.camera.CaptureType;
import com.mahendran_sakkarai.camera.camera.widget.CameraView;

import java.io.File;

/**
 * Created by Mahendran Sakkarai on 1/4/2017.
 */

public interface CaptureContract {
    interface Presenter extends BasePresenter {
        void showErrorMessage(String message);

        void openCamera();

        CaptureType getActiveCaptureType();

        CaptureState getActiveCaptureState();

        void performAction(CaptureState state);

        Uri getOutputMediaFileUri(int mediaType);

        File getOutputMediaFile(int mediaType);

        void updateState(CaptureState state);

        void releaseCamera();

        Camera getCameraInstance();

        void showProfileView(String fileLocation);

        boolean prepareMediaRecorder();

        void releaseMediaRecorder();
    }

    interface View extends BaseView<Presenter> {
        void showMessage(String message);

        void updateCaptureIcons();

        void createPreview();

        void requestPermission();

        void showToastMessage(String mediaFile);

        void showProfileView(String fileLocation);

        CameraView getCameraPreview();
    }
}
