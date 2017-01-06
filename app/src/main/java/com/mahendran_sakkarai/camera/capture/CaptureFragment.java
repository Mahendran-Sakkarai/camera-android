package com.mahendran_sakkarai.camera.capture;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahendran_sakkarai.camera.R;
import com.mahendran_sakkarai.camera.camera.widget.CameraView;
import com.mahendran_sakkarai.camera.profile.ProfileActivity;
import com.mahendran_sakkarai.camera.utils.AppUtil;
import com.mahendran_sakkarai.camera.utils.CameraUtil;
import com.mahendran_sakkarai.camera.utils.PermissionUtil;

import java.io.File;

import static com.mahendran_sakkarai.camera.camera.CaptureState.SAVE_PICTURE;
import static com.mahendran_sakkarai.camera.camera.CaptureState.START_CAMERA;
import static com.mahendran_sakkarai.camera.camera.CaptureState.START_VIDEO;
import static com.mahendran_sakkarai.camera.camera.CaptureState.START_VIDEO_RECORD;
import static com.mahendran_sakkarai.camera.camera.CaptureState.STOP_VIDEO_RECORD;
import static com.mahendran_sakkarai.camera.camera.CaptureState.TAKE_PICTURE;
import static com.mahendran_sakkarai.camera.camera.CaptureState.VIDEO_RECORD_IN_PROGRESS;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */
public class CaptureFragment extends Fragment implements CaptureContract.View, PermissionUtil.PermissionResult, DialogInterface.OnClickListener {
    private TextView mMessageView;
    private CaptureContract.Presenter mPresenter;
    private CameraView mCameraView;
    private FrameLayout mPreviewFrame;
    private CameraView mCameraPreview;
    private ImageView mCaptureAction;
    private ImageView mActionSwapper;
    private Point mWindowSize;
    private RelativeLayout mCameraHolder;
    String[] mPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private AlertDialog mDialog;

    public static CaptureFragment newInstance() {
        return new CaptureFragment();
    }

    public CaptureFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mPermissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.PERMISSION_REQUEST:
                PermissionUtil.validatePermissionResult(this, mPermissions, permissions, grantResults);
        }
    }

    @Override
    public void permissionGranted() {
        mPresenter.openCamera();
    }

    @Override
    public void permissionNotGranted(String... permissions) {
        boolean isDenied = false;
        StringBuilder permissionRequired = new StringBuilder();

        for (int i =0; i < permissions.length; i++) {
            String permission = mPermissions[i];
            permissionRequired.append(permission.substring(permission.lastIndexOf(".") + 1, permission.length()));
            if (i != (permissions.length - 1))
                permissionRequired.append(", ");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                isDenied = true;
            }
        }
        permissionRequired.append(" permissions are required to run this application.");

        if (isDenied) {
            showPermissionRequiredDialog(permissionRequired.toString());
        } else {
            showMessage(permissionRequired.toString());
        }
    }

    private void showPermissionRequiredDialog(String message) {
        mDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", this)
                .setMessage(message).create();
        mDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int flag) {
        switch (flag) {
            case DialogInterface.BUTTON_POSITIVE:
                PermissionUtil.requestPermissions(getActivity(), mPermissions);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
                showMessage(getResources().getString(R.string.enable_permission_in_setting));
                break;
        }
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
        mCaptureAction = (ImageView) view.findViewById(R.id.action_holder);
        mActionSwapper = (ImageView) view.findViewById(R.id.action_swapper);
        mPreviewFrame = (FrameLayout) view.findViewById(R.id.frame);
        mCameraHolder = (RelativeLayout) view.findViewById(R.id.camera_container);

        mActionSwapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mPresenter.getActiveCaptureType()) {
                    case CAMERA:
                        mPresenter.performAction(START_VIDEO);
                        break;
                    case VIDEO:
                        mPresenter.performAction(START_CAMERA);
                        break;
                }
            }
        });

        mCaptureAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mPresenter.getActiveCaptureState()) {
                    // TAKE PICTURE
                    case WAITING_IN_CAMERA:
                        mPresenter.performAction(TAKE_PICTURE);
                        break;
                    // RECORD VIDEO
                    case WAITING_IN_VIDEO:
                        mPresenter.performAction(START_VIDEO_RECORD);
                        break;
                    // STOP RECORDING VIDEO
                    case VIDEO_RECORD_IN_PROGRESS:
                    case START_VIDEO_RECORD:
                        mPresenter.performAction(STOP_VIDEO_RECORD);
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.releaseMediaRecorder();
        mPresenter.releaseCamera();
    }

    @Override
    public void createPreview() {
        if (mPresenter.getCameraInstance() != null) {
            Activity activity = getActivity();
            if (activity == null) return;
            if (mWindowSize == null)
                mWindowSize = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                activity.getWindowManager().getDefaultDisplay().getSize(mWindowSize);
            }
            mCameraPreview = new CameraView(getActivity(), mPresenter.getCameraInstance(), mPresenter);
            mPreviewFrame.addView(mCameraPreview);
            mCameraPreview.setAspectRatio(mWindowSize.x, mWindowSize.y);
            showCamera();
        } else {
            showMessage("Camera not available now!");
        }
    }

    @Override
    public CameraView getCameraPreview(){
        return mCameraPreview;
    }

    @Override
    public void openMediaPlayer(String mSavedVideoPath) {
        Intent viewMediaIntent = new Intent();
        viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(mSavedVideoPath);
        viewMediaIntent.setDataAndType(Uri.fromFile(file), "video/*");
        viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(viewMediaIntent);
    }

    @Override
    public void requestPermission() {
        if (!PermissionUtil.checkPermissions(getActivity(), mPermissions)) {
            PermissionUtil.requestPermissions(getActivity(), mPermissions);
        } else {
            mPresenter.openCamera();
        }
    }

    @Override
    public void showToastMessage(String mediaFile) {
        Toast.makeText(getActivity(), mediaFile, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProfileView(String fileLocation) {
        Intent i = new Intent(getActivity(), ProfileActivity.class);
        i.putExtra(AppUtil.PICTURE_LOCATION, fileLocation);
        startActivity(i);
    }

    private void showCamera() {
        mCameraHolder.setVisibility(View.VISIBLE);
        mMessageView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        if (mMessageView != null && message != null) {
            mMessageView.setText(message);

            mCameraHolder.setVisibility(View.GONE);
            mMessageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateCaptureIcons() {
        switch (mPresenter.getActiveCaptureType()) {
            case CAMERA:
                mActionSwapper.setImageResource(R.drawable.ic_video);
                break;
            case VIDEO:
                mActionSwapper.setImageResource(R.drawable.ic_camera);
                break;
        }

        if (mPresenter.getActiveCaptureState() == SAVE_PICTURE) {
            mCaptureAction.setVisibility(View.GONE);
            mActionSwapper.setVisibility(View.GONE);
        } else if (mPresenter.getActiveCaptureState() == VIDEO_RECORD_IN_PROGRESS) {
            mCaptureAction.setVisibility(View.VISIBLE);
            mCaptureAction.setImageResource(R.drawable.ic_capture_stop);
            mActionSwapper.setVisibility(View.GONE);
        } else {
            mCaptureAction.setImageResource(R.drawable.ic_capture_start);
            mCaptureAction.setVisibility(View.VISIBLE);
            mActionSwapper.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPresenter(CaptureContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
