package aaronichie.smartrecharge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aaronichie on 5/31/2017.
 */

public class Splash extends AppCompatActivity{

    deviceData dd;
    List<String> netInfo;
    List<Integer> netOprSplash;
    String netOpr = "null";
    Integer oprThemeColor;
    Integer oprThemeSound;
    MediaPlayer sound;
    View splash;
    TextView netOprDis;
    LVWifi mLVWifi;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_direct:
                    switchIntent(".MainActivity",0);
                    return true;
                case R.id.navigation_dashboard:
                    switchIntent(".MainActivity",1);
                    return true;
                case R.id.navigation_ocr:
                    switchIntent(".MainActivity",2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);

        netOprDis = (TextView) findViewById(R.id.netOprDis);

        deviceData devData = new deviceData(getApplicationContext());
        netInfo = devData.getOperatorInfo();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        netOpr = netInfo.get(0);
        if ((netOpr != null)) {
            netOprSplash = devData.splashDisplay(netOpr);

            oprThemeColor = netOprSplash.get(0);
            oprThemeSound = netOprSplash.get(1);

            splash = findViewById(R.id.splashScreen);
            splash.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),oprThemeColor));
            navigation.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),oprThemeColor));

            mLVWifi = (LVWifi) findViewById(R.id.lv_wifi);

            mLVWifi.startAnim();

            sound = MediaPlayer.create(Splash.this,oprThemeSound);
            sound.start();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pref_lang = prefs.getString("text",null);
        //if(pref_lang != null){
          //  String lang_key = prefs.getString("lang_key","en");
            //dd.setLanguage(lang_key);
            //recreate();
        //}

        waitAwhile(2000,1);
        try
        {
            transitionAnim anim = new transitionAnim();
            anim.getClass().getMethod("fadeAnimation", AppCompatActivity.class).invoke(anim,this);
        }
        catch (Exception e) {
            e.toString();
        }
    }

    public void waitAwhile(final int msTime, final int enterOrExit)
    {
        Thread timer = new Thread()
        {
            public void run(){
                try{
                    sleep(msTime);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(enterOrExit == 1) {
                                //mLVWifi.stopAnim();
                                netOprDis.setText(netInfo.get(0)+" | "+netInfo.get(1));
                                waitAwhile(2000,0);
                            }else{
                                mLVWifi.stopAnim();
                            }
                        }
                    });

                }
            }
        };
        timer.start();
    }

    public void switchIntent(String target, int whichScan){
        Intent switchTo = new Intent("aaronichie.smartrecharge"+target);
        if(whichScan == 1){
            switchTo.putExtra("scanType", whichScan);
        }else{
            switchTo.putExtra("scanType", whichScan);
        }
        switchTo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(switchTo);
    }

   /*
   @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (netOprDis != null) {
            outState.putString(netOpr, netOprDis.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }
   */
}
