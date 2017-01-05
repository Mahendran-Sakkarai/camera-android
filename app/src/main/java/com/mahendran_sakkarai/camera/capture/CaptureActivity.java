package com.mahendran_sakkarai.camera.capture;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.mahendran_sakkarai.camera.R;

public class CaptureActivity extends Activity {
    private static final String CAPTURE_FRAGMENT = "CAPTURE_FRAGMENT";
    private CaptureFragment mCaptureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing fragment
        FragmentManager fragmentManager = getFragmentManager();
        mCaptureFragment = (CaptureFragment) fragmentManager.findFragmentByTag(CAPTURE_FRAGMENT);
        if (mCaptureFragment == null) {
            mCaptureFragment = CaptureFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.container, mCaptureFragment, CAPTURE_FRAGMENT).commit();
        }

        // InitializePresenter
        new CapturePresenter(mCaptureFragment);
    }
}