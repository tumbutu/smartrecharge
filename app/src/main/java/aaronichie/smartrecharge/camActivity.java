package aaronichie.smartrecharge;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by aaronichie on 6/15/2017.
 */

public class camActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener,
        Camera.PictureCallback, Camera.ShutterCallback {

    Button shutterButton;
    Button focusButton, flashButton;
    FocusBoxView focusBox;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;
    boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cam);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }

        if (cameraEngine != null && cameraEngine.isOn()) {
            return;
        }

        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
        shutterButton = (Button) findViewById(R.id.shutter_button);
        focusBox = (FocusBoxView) findViewById(R.id.focus_box);
        focusButton = (Button) findViewById(R.id.focus_button);
        flashButton = (Button) findViewById(R.id.flash_button);

        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);
        flashButton.setOnClickListener(this);


        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraFrame.setOnClickListener(this);

        focusCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        if(v == shutterButton){
            if(cameraEngine != null && cameraEngine.isOn()){
                cameraEngine.takeShot(this, this, this);
            }
        }

        if(v == focusButton){
            focusCamera();
        }

        if(v == flashButton){
            if(isFlashOn){
                cameraEngine.turnOffFlash();
                isFlashOn = false;
            }else{
                cameraEngine.turnOnFlash();
                isFlashOn = true;
            }
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data == null) {
            return;
        }

        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getBox());

        Intent IntentWithResult = new Intent();
        IntentWithResult.putExtra("pix",bmp);
        setResult(Activity.RESULT_OK,IntentWithResult);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onShutter() {
        //AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //mgr.setStreamMute(AudioManager.STREAM_SYSTEM,true);
    }

    public void focusCamera(){
        if(cameraEngine!=null && cameraEngine.isOn()){
            cameraEngine.requestFocus();
        }
    }

}
