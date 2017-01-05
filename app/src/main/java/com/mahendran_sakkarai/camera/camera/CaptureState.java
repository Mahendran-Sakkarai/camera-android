package com.mahendran_sakkarai.camera.camera;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */

public enum CaptureState {
    IDLE,
    // Camera Related actions
    START_CAMERA,
    WAITING_IN_CAMERA,
    TAKE_PICTURE,
    SAVE_PICTURE,
    // Video Related actions
    START_VIDEO,
    WAITING_IN_VIDEO,
    START_VIDEO_RECORD,
    VIDEO_RECORD_IN_PROGRESS,
    STOP_VIDEO_RECORD,
    SAVE_VIDEO
}
