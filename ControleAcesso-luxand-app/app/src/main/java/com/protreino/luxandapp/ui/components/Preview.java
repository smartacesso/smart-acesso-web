package com.protreino.luxandapp.ui.components;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.protreino.luxandapp.R;
import com.protreino.luxandapp.main.App;
import com.protreino.luxandapp.ui.home.HomeActivity;

import java.util.List;

// Show video from camera and pass frames to ProcessImageAndDraw class
@SuppressLint("ViewConstructor")
public class Preview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private int cameraId;
    private Camera camera;
    private ProcessImageAndDrawResults draw;
    private boolean finished;
    private boolean isCameraOpen = false;
    private int preferredCamera;
    private HomeActivity homeActivity;
    private Camera.PreviewCallback previewCallback;

    public Preview(HomeActivity homeActivity, ProcessImageAndDrawResults draw) {
        super(homeActivity.getApplicationContext());
        this.homeActivity = homeActivity;
        this.draw = draw;
        this.draw.setPreview(this);

        //Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);

        preferredCamera = App.getPreferenceAsInteger(R.string.pref_camera);
        if (preferredCamera == -1) {
            preferredCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
            App.savePreferenceAsInteger(R.string.pref_camera, preferredCamera);
        }

        previewCallback = new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (finished || draw.blockNewFrames)
                    return;

                if (draw.dataYUV == null) {
                    Camera.Parameters params = camera.getParameters();
                    draw.previewWidth = params.getPreviewSize().width;
                    draw.previewHeight = params.getPreviewSize().height;
                    draw.dataRGB = new byte[3 * draw.previewWidth * draw.previewHeight];
                    draw.dataYUV = new byte[data.length];
                }

                System.arraycopy(data, 0, draw.dataYUV, 0, data.length);
                draw.invalidate();
            }
        };
    }

    public void flipCamera() {
        if (preferredCamera == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            preferredCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        else {
            preferredCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        App.savePreferenceAsInteger(R.string.pref_camera, preferredCamera);

        draw.invalidateForced();

        openPreferredCamera();
    }

    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (isCameraOpen)
            return; // surfaceCreated can be called several times
        finished = false;
        openPreferredCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // nao faz nada
    }

    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        finished = true;
        closeCamera();
    }

    public void releaseCallbacks() {
        if (camera != null) {
            camera.setPreviewCallback(null);
        }
        if (holder != null) {
            holder.removeCallback(this);
        }
        draw = null;
        holder = null;
    }

    private void openPreferredCamera() {
        try {
            homeActivity.showLoading();

            if (isCameraOpen) {
                closeCamera();
            }

            cameraId = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == preferredCamera) {
                    cameraId = i;
                    break;
                }
            }

            camera = Camera.open(cameraId);
            isCameraOpen = true;

            camera.setPreviewDisplay(holder);

            // Preview callback used whenever new frame is available
            camera.setPreviewCallback(previewCallback);

            configureCamera();

            camera.startPreview();
        }
        catch (Exception exception) {
            homeActivity.showAlert("Não foi possível abrir a câmera", new Runnable() {
                @Override
                public void run() {
                    closeCamera();
                }
            });
        }
        finally {
            homeActivity.hideLoading();
        }
    }

    private void closeCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        isCameraOpen = false;
    }

    private void configureCamera() {
        try {
            Camera.Parameters parameters = camera.getParameters();

            setCameraDisplayOrientation();

            float cameraAspectRatio;
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                draw.imageRotation = 0;
                draw.mirrorVertical = preferredCamera == Camera.CameraInfo.CAMERA_FACING_FRONT;
                draw.mirrorHorizontal = false;
                cameraAspectRatio = 1.33f;
            }
            else {
                draw.imageRotation = 90;
                draw.mirrorHorizontal = preferredCamera == Camera.CameraInfo.CAMERA_FACING_FRONT;
                draw.mirrorVertical = false;
                cameraAspectRatio = 0.75f;
            }

            // trata o reverse landscape
            int rotation = homeActivity.getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_270) {
                draw.mirrorVertical = preferredCamera == Camera.CameraInfo.CAMERA_FACING_BACK;
                draw.mirrorHorizontal = true;
            }

            // choose preview size closer to 640x480 for optimal performance
            List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
            int width = 0;
            int height = 0;
            for (Camera.Size s: supportedSizes) {
                if ((width - 640)*(width - 640) + (height - 480)*(height - 480) >
                        (s.width - 640)*(s.width - 640) + (s.height - 480)*(s.height - 480)) {
                    width = s.width;
                    height = s.height;
                }
            }

            if (width * height > 0) {
                parameters.setPreviewSize(width, height);
            }

            camera.setParameters(parameters);

            makeResizeForCameraAspect(cameraAspectRatio);

        }
        catch (Exception e) {
            e.printStackTrace();
            homeActivity.showAlert("Não foi possível configurar a câmera", new Runnable() {
                @Override
                public void run() {
                    closeCamera();
                }
            });
        }
    }

    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = homeActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void makeResizeForCameraAspect(float cameraAspectRatio){
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        int matchParentWidth = this.getWidth();
        int newHeight = (int)(matchParentWidth/cameraAspectRatio);
        if (newHeight != layoutParams.height) {
            layoutParams.height = newHeight;
            layoutParams.width = matchParentWidth;
            this.setLayoutParams(layoutParams);
            this.invalidate();
        }
    }

    public void pause() {
        if (camera != null)
            camera.stopPreview();
    }

    public void resume() {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback(previewCallback);
                camera.startPreview();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
