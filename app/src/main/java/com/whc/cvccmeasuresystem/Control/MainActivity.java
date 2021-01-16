package com.whc.cvccmeasuresystem.Control;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.gson.Gson;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.OutDialogFragment;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep1;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep1;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.Control.History.HistoryMain;
import com.whc.cvccmeasuresystem.Control.Humidity.HumidityMain;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep1;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep1;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep1;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.FloatWindow.FloatWindowManager;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class MainActivity extends AppCompatActivity {

    public static boolean dropboxOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.dropboxOpen=false;
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        DataBase dataBase=new DataBase(this);
//        dataBase.getWritableDatabase().execSQL("delete from Humidity");
        Common.tableExist("PageCon", DataBase.Table_PageCon,dataBase.getWritableDatabase());
        Common.tableExist("Humidity", DataBase.Table_Humidity,dataBase.getWritableDatabase());
        Common.tableExist("HumidityVoltage", DataBase.Table_HumidityVoltage,dataBase.getWritableDatabase());

        Common.askPermissions(Manifest.permission.ACCESS_FINE_LOCATION,this,0);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(dropboxOpen){
            return;
        }

        FloatWindowManager.getInstance().dismissWindow();
        SharedPreferences sharedPreferences=this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        String finalFragment=sharedPreferences.getString(Common.finalFragment,"");
        boolean endModule=sharedPreferences.getBoolean(Common.endModule,true);
//        Map<String,Object> m= (HashMap<String, Object>) sharedPreferences.getAll();
//        Set<String> ss=m.keySet();
//        Iterator itr = ss.iterator();
//        while(itr.hasNext()) {
//            String element = (String) itr.next();
//            Object ee=m.get(element);
//            if(ee instanceof String)
//            {
//                String sss= (String) ee;
//                Log.d("Sample String ",element+" : "+sss);
//            }
//            if(ee instanceof Integer)
//            {
//                Integer sss= (Integer) ee;
//                Log.d("Sample Integer ",element+" : "+String.valueOf(sss));
//
//            }
//
//        }



        if(endModule)
        {

            if(finalFragment.equals(IonChannel3Set))
            {
                oldFragment=new ArrayList<>();
                oldFragment.add(IonChannel1);
                oldFragment.add(IonChannel2Set);
                switchFragment(new IonChannelStep3Main(), getSupportFragmentManager());
            }else{
                SignIn.signIn = false;
                switchFragment(new SignIn(), getSupportFragmentManager());
            }

        }else{
            SignIn.signIn = true;
            Gson gson = new Gson();
            String json = sharedPreferences.getString(finalPage, "");
            pageCon= gson.fromJson(json, PageCon.class);
            oldFragment=new ArrayList<>();
            oldFragment.add(CFName);
            switch (finalFragment) {
                case Drift2Set:

                    oldFragment.add(Drift1);
                    switchFragment(new DriftStep2Main(), getSupportFragmentManager());
                    break;
                case BS2:
                    oldFragment.add(BS1);
                    switchFragment(new BatchStep2Main(), getSupportFragmentManager());
                    break;
                case Hys2:
                    oldFragment.add(Hys1);
                    switchFragment(new HysteresisStep2Main(), getSupportFragmentManager());
                    break;
                case Sen2:
                    oldFragment.add(Sen2);
                    switchFragment(new SensitivityStep2Main(), getSupportFragmentManager());
                    break;
                case IonChannel1Set:
                    Fragment fragment=new IonChannelStep2Main();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(reBack,false);
                    fragment.setArguments(bundle);
                    oldFragment.add(IonChannel1);
                    switchFragment(fragment, getSupportFragmentManager());
                    break;
                case IonChannel3Set:
                    oldFragment.add(IonChannel1);
                    oldFragment.add(IonChannel2Set);
                    switchFragment(new IonChannelStep3Main(), getSupportFragmentManager());
                    break;
                case HumidityMainString:
                    oldFragment.add(HumidityMainString);
                    switchFragment(new HumidityMain(), getSupportFragmentManager());
                    break;
                  default:
                      switchFragment(new SignIn(), getSupportFragmentManager());
                      break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SignIn.signIn) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.popup_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (startMeasure) {
            Common.showToast(MainActivity.this, measureStartNotExist);
            return true;
        }
        switch (item.getItemId()) {
            case R.id.home:
                Common.switchFragment(new ChoiceFunction(), getSupportFragmentManager());
                break;
            case R.id.exit:
                MainActivity.this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        SharedPreferences sharedPreferences=this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(Common.endModule,true).apply();
        measureTimes=0;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment:fragments){
            if(fragment instanceof HistoryMain)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }







    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (startMeasure) {
                Common.showToast(MainActivity.this, measureStartNotExist);
                return true;
            }


            if (oldFragment != null && oldFragment.size() > 0) {
                String name = oldFragment.get(oldFragment.size() - 1);
                switch (name) {
                    case CFName:
                        Common.switchFragment(new ChoiceFunction(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        SharedPreferences sharedPreferences=this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean(Common.endModule,true).apply();
                        break;
                    case BS1:
                        Common.switchFragment(new BatchStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet = true;
                        break;
                    case Sen1:
                        Common.switchFragment(new SensitivityStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet = true;
                        break;
                    case Hys1:
                        Common.switchFragment(new HysteresisStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet = true;
                        break;
                    case Drift1:
                        Common.switchFragment(new DriftStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet = true;
                        break;
                    case IonChannel1:
                        switchFragment(new IonChannelStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet = true;
                        break;
                    case IonChannel2Set:
                        Fragment fragment=new IonChannelStep2Main();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable(Common.reBack,true);
                        fragment.setArguments(bundle);
                        oldFragment.add(Common.Drift1);
                        switchFragment(fragment, getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        break;
                    case HistoryMain:
                        switchFragment(new HistoryMain(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        break;
                }


            } else {
                OutDialogFragment aa = new OutDialogFragment();
                aa.setObject(MainActivity.this);
                aa.show(this.getSupportFragmentManager(), "show");
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                permissions();
                break;
        }
    }

    private void permissions()
    {

        String permission=Manifest.permission.ACCESS_FINE_LOCATION;
        Activity activity=this;
        int result = ContextCompat.checkSelfPermission(activity, permission);
        if (result != PackageManager.PERMISSION_GRANTED) {

            String remain;
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                remain = "沒有位置權限，無法使用。\n要使用此功能請按\"YES\"，並允許位置權限!\n不使用請按\"NO\"!";
            } else {
                remain = "沒有位置權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開位置權限!\n不使用此功能請按\"NO\"。";
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)) {
                        Common.askPermissions(permission, activity, 0);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 0);
                    }
                }
            };

            DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  activity.finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("無法使用CVCC量測系統!")
                    .setMessage(remain)
                    .setPositiveButton("YES", listener)
                    .setNegativeButton("NO", nolistener)
                    .show();


        }


    }


}
