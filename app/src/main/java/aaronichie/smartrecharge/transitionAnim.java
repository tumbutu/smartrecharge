package aaronichie.smartrecharge;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by aaronichie on 11/6/2016.
 */


public class transitionAnim
{
    public void fadeAnimation(AppCompatActivity a)
    {
        a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}