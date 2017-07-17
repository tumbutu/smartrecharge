package aaronichie.smartrecharge;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by aaronichie on 5/31/2017.
 */

public class deviceData{
    Context con;
    String countryCode,netOpr;

    public deviceData(Context c){
        con = c;
    }

    public List<String> getOperatorInfo()
    {
        List<String> netInfo = new ArrayList<>();
        TelephonyManager tMgr = (TelephonyManager) con.getSystemService(TELEPHONY_SERVICE);

        countryCode = tMgr.getNetworkCountryIso();
        netOpr = tMgr.getNetworkOperatorName();

        if (netOpr.isEmpty())
            netOpr = "Aaron";

        netInfo.add(netOpr);                        //  network Opr
        netInfo.add(countryCode);                   //  Country
        //netInfo.add(countryCode);                   //  countryFlag


        return netInfo;
    }

    /////////////////////////////////////////////////
    public final void setLanguage(String language){
        Locale myLocale = new Locale(language);

        languagePreference(language);                        //Anytime the language is changed update the pref language to overwrite

        Resources res = con.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale);
        }else {
            conf.locale = myLocale;
        }
        res.updateConfiguration(conf,dm);
    }

    private void languagePreference(String lang){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(con.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang_key", lang);
        editor.apply();
    }


    public List<Integer> splashDisplay(String netOpr) {
        List<Integer> splashItems = new ArrayList<>();
        switch (netOpr.toLowerCase()){
            case "tigo":
                splashItems.add(R.color.tigoThemeColor);
                splashItems.add(R.raw.tigosound);
                break;
            case "mtn":
                splashItems.add(R.color.mtnThemeColor);
                splashItems.add(R.raw.mtnsound);
                break;
            case "lg u+":
                splashItems.add(R.color.tigoThemeColor);
                splashItems.add(R.raw.mtnsound);
                break;
            case "glo":
                splashItems.add(R.color.gloThemeColor);
                splashItems.add(R.raw.glosound);
                break;
            case "vodaphone":
                splashItems.add(R.color.vodaThemeColor);
                splashItems.add(R.raw.vodasound);
                break;
            default:
                splashItems.add(R.color.defaultThemeColor);
                splashItems.add(R.raw.defaultsound);
                break;
        }
        return splashItems;
    }

}
