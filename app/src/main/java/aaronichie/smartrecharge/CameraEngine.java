package aaronichie.smartrecharge;

import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aaronichie on 6/15/2017.
 */
public class CameraEngine extends Activity{

    boolean on, isFlashOn=false;
    private Timer timer;
    private TimerTask timerTask;
    private static final long AUTO_FOCUS_INTERVAL_MS = 2000;

    Camera.Parameters params;

    Camera camera;
    SurfaceHolder surfaceHolder;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, final Camera camera) {


            //timerTask = new TimerTask() {
            // @Override
            // public void run() {
            //camera.autoFocus(autoFocusCallback);
            //camera.autoFocus(this);
            // }
           // };
           // timer.schedule(timerTask, AUTO_FOCUS_INTERVAL_MS);
            /////////////////////////
        }
    };

    public boolean isOn() {
        return on;
    }

    private CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    static public CameraEngine New(SurfaceHolder surfaceHolder){
        return  new CameraEngine(surfaceHolder);
    }

    public void requestFocus() {
        if (camera == null)
            return;

        if (isOn()) {
            camera.autoFocus(autoFocusCallback);
        }
    }

    public void start() {
        this.camera = CameraUtils.getCamera();

        if (this.camera == null)
            return;

        try {

            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);

            //this.camera.setParameters(turnOnFlash());
            this.camera.startPreview();

            on = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){

        if(camera != null){
            camera.release();
            camera = null;
        }
        on = false;
    }

    public void takeShot(Camera.ShutterCallback shutterCallback,
                         Camera.PictureCallback rawPictureCallback,
                         Camera.PictureCallback jpegPictureCallback ){
        if(isOn()){
            camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }


    public void turnOnFlash(){
        if (this.camera == null)
            return;

        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        isFlashOn = true;
    }

    public void turnOffFlash(){
        if (this.camera == null || params == null)
            return;

        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        isFlashOn = false;
    }

}
