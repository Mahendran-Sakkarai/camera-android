package com.mahendran_sakkarai.camera.camera.widget;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.mahendran_sakkarai.camera.camera.CaptureState;
import com.mahendran_sakkarai.camera.capture.CaptureContract;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = CameraView.class.getSimpleName();

    protected final SurfaceHolder mHolder;
    private final Camera mCamera;
    private final CaptureContract.Presenter mPresenter;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public CameraView(Context context, Camera camera, CaptureContract.Presenter presenter) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mPresenter = presenter;
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rotation = display.getRotation();

        // If vertical, we fill 2/3 the height and all the width. If horizontal,
        // fill the entire height and 2/3 the width
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            mRatioWidth = screenWidth;
            mRatioHeight = screenHeight;
        } else {
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            mRatioWidth = screenWidth;
            mRatioHeight = screenHeight;
        }
        requestLayout();
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            (this).layout(0, 0, mRatioWidth, mRatioHeight);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Throwable e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null)
            return;
        try {
            mCamera.stopPreview();
        } catch (Exception ignored) {
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            mPresenter.performAction(CaptureState.WAITING_IN_CAMERA);
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}