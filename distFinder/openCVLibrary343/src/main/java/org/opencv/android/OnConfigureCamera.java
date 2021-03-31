package org.opencv.android;

import android.hardware.Camera;

public interface OnConfigureCamera {
    void onConfigure(Camera camera,  Camera.Parameters params);
}
