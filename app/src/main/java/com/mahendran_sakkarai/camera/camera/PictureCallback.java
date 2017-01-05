package com.mahendran_sakkarai.camera.camera;

import android.content.Context;
import android.hardware.Camera;

import com.mahendran_sakkarai.camera.capture.CaptureContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */

public class PictureCallback implements Camera.PictureCallback {
    private final CaptureContract.Presenter mPresenter;

    public PictureCallback(CaptureContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        File pictureFile = mPresenter.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            mPresenter.showErrorMessage("Check storage permission.");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
