package aaronichie.smartrecharge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hanks.htextview.base.HTextView;
import com.hanks.htextview.line.LineTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private LinearLayout llSearch, giftScrollInnerVert;
    deviceData dd;
    private LineTextView hTextView3;
    TextView rechargeTitle;
    String content, clickedBtnTag, phoneNum = "junka ", name = "junkB";
    boolean isContent = false;
    List<String> netInfo;
    List<Integer> netOprSplash;
    String netOpr = "null";
    int oprThemeColor;
    private static final int PHOTO_REQUEST = 20, RESULT_PICK_CONTACT=220;
    private TextRecognizer detector;
    Button button, phNum, butt;
    ListView lv,lv_summary;
    int receivers = 0;
    receiverAdapter giftListAdapter;
    txnSummaryAdapter tnxListAdapter;
    EditText edt;

    HashMap<String,String> hm = new HashMap<>();
    HashMap<String,String> EtHm = new HashMap<>();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home2:
                    switchIntent();
                    return true;
                case R.id.navigation_gift:
                    if (isContent){
                        button.setVisibility(View.VISIBLE);
                        button.performClick();
                    }else{
                        Toast.makeText(MainActivity.this, R.string.no_recharge_code, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.navigation_recharge:
                    summaryAlert();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        content = getString(R.string.nothingScan);

        giftScrollInnerVert = (LinearLayout) findViewById(R.id.giftScrollInnerVert);
        lv = (ListView) findViewById(R.id.giftList);

        button = (Button) findViewById(R.id.addView);

        rechargeTitle = (TextView) findViewById(R.id.rechargeTitle);

        giftListAdapter = new receiverAdapter(getApplicationContext(), receivers);
        lv.setAdapter(giftListAdapter);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                receivers +=1;
                giftScrollInnerVert.setVisibility(View.VISIBLE);
                button.setText(String.format("%s(%s)", getString(R.string.add), receivers));
                giftListAdapter = new receiverAdapter(getApplicationContext(), receivers);
                lv.setAdapter(giftListAdapter);
            }
        });


        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        int getEx = getIntent().getIntExtra("scanType",1);

        if (getEx == 0){
            getCodeFromDialogBox();
        }
        else if (getEx == 1){
            scanWithZing();
        }else if (getEx == 2) {
            scanWithOCR();
        }



        hTextView3 = (LineTextView) findViewById(R.id.tvScanContent);
        //hTextView3.setLineColor(R.color.textColor);
        hTextView3.setOnClickListener(new ClickListener());

        BottomNavigationView navigation2 = (BottomNavigationView) findViewById(R.id.navigation2);
        navigation2.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        deviceData dd = new deviceData(getApplicationContext());
        netInfo = dd.getOperatorInfo();

        netOpr = netInfo.get(0);
        if ((netOpr != null)) {

            netOprSplash = dd.splashDisplay(netOpr);
            oprThemeColor = netOprSplash.get(0);


            int barColor = oprThemeColor;                   // using variable oprThemeColor was not working for statusBar and ActionBar

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               this.getWindow().setStatusBarColor(getResources().getColor(barColor));
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(barColor)));
            }

            llSearch.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),oprThemeColor));
            navigation2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),oprThemeColor));
        }

        try
        {
            transitionAnim anim = new transitionAnim();
            anim.getClass().getMethod("fadeAnimation", AppCompatActivity.class).invoke(anim,this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        waitAwhile(3000,hTextView3);
    }

    private void getCodeFromDialogBox() {
         AlertBox();
    }

    public void scanWithZing(){
        llSearch.setVisibility(View.GONE);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt(getString(R.string.scanMsg));
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

        //        Use this for more customization
        //        IntentIntegrator integrator = new IntentIntegrator(this);
        //        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        //        integrator.setPrompt("Scan a barcode");
        //        integrator.setCameraId(0);  // Use a specific camera of the device
        //        integrator.setBeepEnabled(false);
        //        integrator.setBarcodeImageEnabled(true);
        //        integrator.initiateScan();
    }

    public void scanWithOCR() {
        llSearch.setVisibility(View.GONE);
        Intent intent = new Intent("aaronichie.smartrecharge.camActivity");
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    public void onClick(View v) {
        scanWithZing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case 49374:                     // ZXing requestCode 49374 in dec originally set in hex
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        llSearch.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        llSearch.setVisibility(View.VISIBLE);
                        content = result.getContents();
                        hTextView3.setText(content);
                        isContent = true;
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            break;
            case PHOTO_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        llSearch.setVisibility(View.VISIBLE);
                        Bitmap bitmap = data.getParcelableExtra("pix");

                        if (detector.isOperational() && bitmap != null) {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> textBlocks = detector.detect(frame);
                            //String blocks = "";
                            String lines = "";
                            //String words = "";
                            for (int index = 0; index < textBlocks.size(); index++) {
                                //extract scanned text blocks here
                                TextBlock tBlock = textBlocks.valueAt(index);
                                ///blocks = blocks + tBlock.getValue() + "\n" + "\n";
                                for (Text line : tBlock.getComponents()) {
                                    //extract scanned text lines here
                                    lines = lines + line.getValue();
                                    ///for (Text element : line.getComponents()) {
                                    //extract scanned text words here
                                    ///   words = words + element.getValue() + ", ";
                                    ///}
                                }
                            }
                            if (textBlocks.size() == 0) {
                                hTextView3.setText(R.string.failedScan);
                            } else {
                                content = lines;
                                hTextView3.setText(content);
                                isContent = true;

                            }
                        } else {
                            hTextView3.setText(R.string.failedDection);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, R.string.failedImgLoad, Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    llSearch.setVisibility(View.VISIBLE);
                    Toast.makeText(this, R.string.cancelledImg, Toast.LENGTH_LONG).show();
                }
            break;
            case RESULT_PICK_CONTACT:
                goGetContactNumber(data);
            break;
            default:
            break;
        }
    }

    private void goGetContactNumber(Intent data) {
        Cursor cursor = null;

        try{
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri,null,null,null,null);
            if (cursor != null) {
                cursor.moveToFirst();
                int phIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                phoneNum = cursor.getString(phIdx);
                name = cursor.getString(nameIdx);

                hm.put(clickedBtnTag,name+"("+phoneNum+")");

                giftListAdapter.notifyDataSetChanged();
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void waitAwhile(final int msTime, final View vvw)
    {
        Thread timer = new Thread()
        {
            public void run(){
                try{
                    sleep(msTime);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vvw.performClick();
                        }
                    });
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    waitAwhile(msTime,vvw);
                }
            }
        };
        timer.start();
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v instanceof HTextView) {
                ((HTextView) v).animateText(content);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void switchIntent(){
        Intent switchTo = new Intent(this,Splash.class);
        switchTo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(switchTo);
    }

    public void AlertBox(){

        LayoutInflater infla = LayoutInflater.from(getApplicationContext());
        View promptView = infla.inflate(R.layout.direct_code_input,null);


        AlertDialog.Builder alertBuild = new AlertDialog.Builder(MainActivity.this);
        alertBuild.setView(promptView);

        final EditText et = (EditText) promptView.findViewById(R.id.direct_code_input);
        final EditText ed = (EditText) promptView.findViewById(R.id.direct_amt_input);

        alertBuild
                  .setCancelable(false)
                  .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          String input = et.getText().toString();
                          String amt_in = ed.getText().toString();
                          if (input.trim().length() > 0){
                             content = input;
                              if(amt_in.trim().length() > 0){
                                  rechargeTitle.setText(getString(R.string.rechargeTitle)+ "(GHC " + amt_in +")");
                              }
                              hTextView3.setText(content);
                              isContent=true;
                          }
                          dialog.dismiss();
                      }
                  })
                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          dialog.cancel();
                      }
                  });

        AlertDialog alertBox = alertBuild.create();
        alertBox.show();
    }

    private void summaryAlert() {

        AlertDialog.Builder alertBuild = new AlertDialog.Builder(MainActivity.this);
        ////////////////give a txn summary /////////////////////
        if (hm.size() == EtHm.size()){
            LayoutInflater txnSummary = LayoutInflater.from(getApplicationContext());
            View promptView = txnSummary.inflate(R.layout.txn_summary,null);
            alertBuild.setView(promptView);
            lv_summary = (ListView) promptView.findViewById(R.id.txn_summary_lv);
            tnxListAdapter = new txnSummaryAdapter(getApplicationContext());
            lv_summary.setAdapter(tnxListAdapter);

            alertBuild
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }else if (hm.size() < EtHm.size()){
            alertBuild.setTitle(R.string.txnErr);
            alertBuild.setMessage(R.string.recvErr);
            alertBuild
                    .setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }else {
            alertBuild.setTitle(R.string.txnErr);
            alertBuild.setMessage(R.string.amtErr);
            alertBuild
                    .setCancelable(false)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }

        AlertDialog txnRecvBox = alertBuild.create();
        txnRecvBox.show();
        /////////////////////////////////////
    }

    public class receiverAdapter extends BaseAdapter{
        private Context mContext;
        private int totalRxn;

        public receiverAdapter (Context context, int receivers){
            this.mContext = context;
            this.totalRxn = receivers;
        }

        @Override
        public int getCount() {
            return totalRxn;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            List <Button> buttons = new ArrayList<>();
            final List <EditText> editTexts = new ArrayList<>();

            i += 1;
            if(view == null){
                LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.gift_list, null);
            }

            phNum = (Button) view.findViewById(R.id.phNum);
            final String valueOf_I = String.valueOf(i);
            phNum.setText(String.format("%s%s", valueOf_I,getString(R.string.pick_number)));

            edt = (EditText) view.findViewById(R.id.tv);
            edt.setTag(valueOf_I);

            if (hm.containsKey(valueOf_I)){
                phNum.setText(String.format("%s%s%s",valueOf_I,": ",hm.get(valueOf_I)));
            }

            buttons.add(phNum);
            editTexts.add(edt);

            for (final Button bb: buttons){
                bb.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str = (String) bb.getText();
                        String [] parts = str.split(":");
                        clickedBtnTag = parts[0];

                            Intent contactPicker = new Intent(Intent.ACTION_PICK,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                            startActivityForResult(contactPicker, RESULT_PICK_CONTACT);

                    }
                });

                bb.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String str = (String) bb.getText();
                        String [] parts = str.split(":");
                        clickedBtnTag = parts[0];

                        if (hm.containsKey(clickedBtnTag) || EtHm.containsKey(clickedBtnTag)){
                            hm.remove(clickedBtnTag);
                            EtHm.remove(clickedBtnTag);
                        }

                        HashMap<String,String> hm2 = new HashMap<>();

                        // amt edittext is also controlled here
                        HashMap<String,String> EtHm2 = new HashMap<>();

                        for (Object key : hm.keySet()) {
                            String keyStr = key.toString();
                            Integer keyInt = Integer.valueOf(keyStr);
                            Integer clickedBtnTag_INT = Integer.valueOf(clickedBtnTag);

                            if(clickedBtnTag_INT > keyInt){
                                hm2.put(keyStr,hm.get(key.toString()));

                                // amt edittext is also controlled here
                                EtHm2.put(keyStr,EtHm.get(key.toString()));
                            }else {
                                String tempKey = String.valueOf(keyInt-1);
                                hm2.put(tempKey,hm.get(key.toString()));

                                // amt edittext is also controlled here
                                EtHm2.put(tempKey,EtHm.get(key.toString()));
                            }
                        }

                        hm.clear();
                        hm.putAll(hm2);

                        EtHm.clear();
                        EtHm.putAll(EtHm2);

                        notifyDataSetChanged();

                        totalRxn -=1;
                        receivers = totalRxn;
                        button.setText(String.format("%s(%s)", getString(R.string.add), totalRxn));
                        return true;
                    }
                });
            }

            for (final EditText ets: editTexts) {
                ets.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b){
                            String edtTag = String.valueOf(ets.getTag());
                            String edtText = String.valueOf(ets.getText());

                            EtHm.put(edtTag,edtText);

                        }

                    }
                });

                if (EtHm.size()>0){
                    for (Object key : EtHm.keySet()) {
                        ets.setText(EtHm.get(edt.getTag().toString()));
                    }
                }

            }
            return view;
        }
    }

    public class txnSummaryAdapter extends BaseAdapter{

        private Context mContext;

        public txnSummaryAdapter (Context context){
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return hm.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            i += 1;
            if(view == null){
                LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.txn_summary_list, null);
            }

            String I_Str = String.valueOf(i);
            TextView recv = (TextView) view.findViewById(R.id.recv);
            recv.setText(hm.get(I_Str));
            TextView recvAmt = (TextView) view.findViewById(R.id.recvAmt);
            recvAmt.setText(String.format("GHC %s", EtHm.get(I_Str)));
            return view;
        }
    }
}