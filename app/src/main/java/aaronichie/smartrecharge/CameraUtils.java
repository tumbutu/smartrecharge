package aaronichie.smartrecharge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

/**
 * Created by aaronichie on 6/15/2017.
 */

public class CameraUtils {
    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
