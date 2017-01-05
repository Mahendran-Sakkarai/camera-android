package com.mahendran_sakkarai.camera.capture;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.mahendran_sakkarai.camera.camera.CaptureState;
import com.mahendran_sakkarai.camera.camera.CaptureType;
import com.mahendran_sakkarai.camera.camera.PictureCallback;
import com.mahendran_sakkarai.camera.utils.CameraUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */
public class CapturePresenter implements CaptureContract.Presenter{
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private final CaptureContract.View mView;
    private PictureCallback mPictureCallback;
    private CaptureType mActiveType;
    private CaptureState mCurrentState;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    public CapturePresenter(CaptureContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        mActiveType = CaptureType.CAMERA;
        mCurrentState = CaptureState.IDLE;
    }

    @Override
    public void start() {
        mView.requestPermission();
    }

    @Override
    public void showErrorMessage(String message) {
        mView.showMessage(message);
    }

    @Override
    public void openCamera() {
        performAction(CaptureState.START_CAMERA);
        mCamera = CameraUtil.getCameraInstance();
        mPictureCallback = new PictureCallback(this);
        mView.createPreview();
    }

    @Override
    public CaptureType getActiveCaptureType() {
        return mActiveType;
    }

    @Override
    public CaptureState getActiveCaptureState() {
        return mCurrentState;
    }

    @Override
    public void performAction(CaptureState state) {
        this.mCurrentState = state;
        switch (state) {
            // Actions related to camera
            case START_CAMERA:
                mActiveType = CaptureType.CAMERA;
                break;
            case WAITING_IN_CAMERA:
                mActiveType = CaptureType.CAMERA;
                break;
            case TAKE_PICTURE:
                mActiveType = CaptureType.CAMERA;
                mCamera.takePicture(null, null, mPictureCallback);
                break;
            case SAVE_PICTURE:
                // PictureCallback taking care about that. so nothing to do here.
                break;

            // Actions related to video
            case START_VIDEO:
                mActiveType = CaptureType.VIDEO;
                if(prepareMediaRecorder())
                    performAction(CaptureState.WAITING_IN_VIDEO);
                break;
            case WAITING_IN_VIDEO:
                mActiveType = CaptureType.VIDEO;
                break;
            case START_VIDEO_RECORD:
                mActiveType = CaptureType.VIDEO;
                if (prepareMediaRecorder()) {
                    mMediaRecorder.start();
                    performAction(CaptureState.VIDEO_RECORD_IN_PROGRESS);
                } else {
                    releaseMediaRecorder();
                }
                break;
            case VIDEO_RECORD_IN_PROGRESS:
                mActiveType = CaptureType.VIDEO;
                break;
            case STOP_VIDEO_RECORD:
                mActiveType = CaptureType.VIDEO;
                mMediaRecorder.stop();
                releaseMediaRecorder();
                mCamera.lock();
                performAction(CaptureState.START_VIDEO);
                break;
            case SAVE_VIDEO:

                break;
        }

        mView.updateCaptureIcons();
    }

    @Override
    public boolean prepareMediaRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mView.getCameraPreview().getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            mView.showToastMessage("IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            mView.showToastMessage("IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    public void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    @Override
    public Uri getOutputMediaFileUri(int mediaTypeImage) {
        return Uri.fromFile(getOutputMediaFile(mediaTypeImage));
    }

    @Override
    public File getOutputMediaFile(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (mediaType == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(mediaType == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void updateState(CaptureState state) {
        this.mCurrentState = state;
        mView.updateCaptureIcons();
    }

    @Override
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public Camera getCameraInstance() {
        return mCamera;
    }

    @Override
    public void showProfileView(String fileLocation) {
        mView.showProfileView(fileLocation);
    }
}
