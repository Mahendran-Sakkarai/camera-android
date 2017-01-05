package com.mahendran_sakkarai.camera.capture;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.mahendran_sakkarai.camera.R;
import com.mahendran_sakkarai.camera.utils.CameraUtil;
import com.mahendran_sakkarai.camera.utils.PermissionUtil;

public class CaptureActivity extends Activity implements
        PermissionUtil.PermissionResult, DialogInterface.OnClickListener {
    private static final String CAPTURE_FRAGMENT = "CAPTURE_FRAGMENT";
    String[] mPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private AlertDialog mDialog;
    private CaptureFragment mCaptureFragment;
    private CapturePresenter mCapturePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mPermissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
        }

        // Initializing fragment
        FragmentManager fragmentManager = getFragmentManager();
        mCaptureFragment = (CaptureFragment) fragmentManager.findFragmentByTag(CAPTURE_FRAGMENT);
        if (mCaptureFragment == null) {
            mCaptureFragment = CaptureFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.container, mCaptureFragment, CAPTURE_FRAGMENT).commit();
        }

        // InitializePresenter
        mCapturePresenter = new CapturePresenter(mCaptureFragment);

        if (!PermissionUtil.checkPermissions(this, mPermissions)) {
            PermissionUtil.requestPermissions(this, mPermissions);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        if (CameraUtil.checkCameraHardware(this)) {
            mCapturePresenter.openCamera();
        } else {
            showErrorMessage(getString(R.string.camera_is_not_available));
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
        openCamera();
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                isDenied = true;
            }
        }
        permissionRequired.append(" permissions are required to run this application.");

        if (isDenied) {
            showPermissionRequiredDialog(permissionRequired.toString());
        } else {
            showErrorMessage(permissionRequired.toString());
        }
    }

    private void showPermissionRequiredDialog(String message) {
        mDialog = new AlertDialog.Builder(this)
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", this)
                .setMessage(message).create();
        mDialog.show();
    }

    private void showErrorMessage(String message) {
        mCapturePresenter.showErrorMessage(message);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int flag) {
        switch (flag) {
            case DialogInterface.BUTTON_POSITIVE:
                PermissionUtil.requestPermissions(this, mPermissions);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
                showErrorMessage(getResources().getString(R.string.enable_permission_in_setting));
                break;
        }
    }
}