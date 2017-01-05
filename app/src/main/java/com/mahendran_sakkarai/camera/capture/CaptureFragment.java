package com.mahendran_sakkarai.camera.capture;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahendran_sakkarai.camera.R;
import com.mahendran_sakkarai.camera.widget.CameraView;

import java.util.List;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */
public class CaptureFragment extends Fragment implements CaptureContract.View {
    private TextView mMessageView;
    private CaptureContract.Presenter mPresenter;
    private ImageView mClickView;
    private CameraView mCameraView;
    private FrameLayout mPreviewFrame;
    private Camera mCamera;

    public static CaptureFragment newInstance() {
        return new CaptureFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.capture_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessageView = (TextView) view.findViewById(R.id.message_view);
        mClickView = (ImageView) view.findViewById(R.id.take_picture);
        mPreviewFrame = (FrameLayout) view.findViewById(R.id.frame);
        createPreview();
    }

    private void createPreview() {

    }

    @Override
    public void showMessage(String message) {
        if (message != null)
            mMessageView.setText(message);
    }

    @Override
    public void openCamera() {

    }

    @Override
    public void setPresenter(CaptureContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
