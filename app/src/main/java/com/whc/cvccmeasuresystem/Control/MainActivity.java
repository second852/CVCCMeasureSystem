package com.whc.cvccmeasuresystem.Control;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.gson.Gson;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.OutDialogFragment;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep1;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep1;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.Control.History.HistoryMain;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep1;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep1;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep1;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.PagConDB;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        DataBase dataBase=new DataBase(this);
        Common.tableExist("PageCon", DataBase.Table_PageCon,dataBase.getWritableDatabase());
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences=this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        String finalFragment=sharedPreferences.getString(Common.finalFragment,"");
        boolean endModule=sharedPreferences.getBoolean(Common.endModule,true);

        if(endModule)
        {

            if(finalFragment.equals(IonChannel3Set))
            {
                oldFragment=new ArrayList<>();
                oldFragment.add(Common.IonChannel1);
                oldFragment.add(Common.IonChannel2Set);
                switchFragment(new IonChannelStep3Main(), getSupportFragmentManager());
            }else{
                SignIn.signIn = false;
                switchFragment(new SignIn(), getSupportFragmentManager());
            }

        }else{
            SignIn.signIn = true;
            Gson gson = new Gson();
            String json = sharedPreferences.getString(Common.finalPage, "");
            Common.pageCon= gson.fromJson(json, PageCon.class);
            oldFragment=new ArrayList<>();
            oldFragment.add(Common.CFName);
            switch (finalFragment) {
                case Common.Drift2Set:

                    oldFragment.add(Common.Drift1);
                    switchFragment(new DriftStep2Main(), getSupportFragmentManager());
                    break;
                case Common.BS2:
                    oldFragment.add(Common.BS1);
                    switchFragment(new BatchStep2Main(), getSupportFragmentManager());
                    break;
                case Common.Hys2:
                    oldFragment.add(Common.Hys1);
                    switchFragment(new HysteresisStep2Main(), getSupportFragmentManager());
                    break;
                case Common.Sen2:
                    oldFragment.add(Common.Sen2);
                    switchFragment(new SensitivityStep2Main(), getSupportFragmentManager());
                    break;
                case Common.IonChannel1Set:
                    Fragment fragment=new IonChannelStep2Main();
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(Common.reBack,false);
                    fragment.setArguments(bundle);
                    oldFragment.add(Common.IonChannel1);
                    switchFragment(fragment, getSupportFragmentManager());
                    break;
                case Common.IonChannel3Set:
                    oldFragment.add(Common.IonChannel1);
                    oldFragment.add(Common.IonChannel2Set);
                    switchFragment(new IonChannelStep3Main(), getSupportFragmentManager());
                    break;
                case Common.HumidityMain:
                    oldFragment.add(Common.HistoryMain);
                    switchFragment(new HistoryMain(), getSupportFragmentManager());
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
}
