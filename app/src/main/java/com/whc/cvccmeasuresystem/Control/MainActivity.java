package com.whc.cvccmeasuresystem.Control;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.OutDialogFragment;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep1;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep1;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep1;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep1;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep1;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.R;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        SignIn.signIn = false;
        finishToSave = true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment fragment = new BatchStep2Main();
        fragment = new SignIn();
        fragment=new IonChannelStep3Main();
        switchFragment(fragment, getSupportFragmentManager());
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

        if (!finishToSave) {
            Common.showToast(MainActivity.this, fileNotSave);
            return true;
        }

        if (tcpClient != null) {
            tcpClient.cancelHomeTcpClient();
            tcpClient = null;
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
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tcpClient != null) {
            tcpClient.cancelHomeTcpClient();
            tcpClient = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (startMeasure) {
                Common.showToast(MainActivity.this, measureStartNotExist);
                return true;
            }
            if (!finishToSave) {
                Common.showToast(MainActivity.this, fileNotSave);
                return true;
            }
            if (oldFragment!=null&&oldFragment.size() > 0) {
                String name = oldFragment.get(oldFragment.size() - 1);
                switch (name) {
                    case CFName:
                        Common.switchFragment(new ChoiceFunction(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        tcpClient=null;
                        break;
                    case BS1:
                        Common.switchFragment(new BatchStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet=true;
                        tcpClient=null;
                        break;
                    case Sen1:
                        Common.switchFragment(new SensitivityStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        tcpClient=null;
                        needSet=true;
                        break;
                    case Hys1:
                        Common.switchFragment(new HysteresisStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        tcpClient=null;
                        needSet=true;
                        break;
                    case Drift1:
                        Common.switchFragment(new DriftStep1(), getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet=true;
                        tcpClient=null;
                        break;
                    case IonChannel1:
                        switchFragment(new IonChannelStep1(),getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        needSet=true;
                        tcpClient=null;
                        break;
                    case IonChannel2Set:
                        switchFragment(new IonChannelStep2Main(),getSupportFragmentManager());
                        oldFragment.remove(oldFragment.size() - 1);
                        IonChannelStep2Main.initParameter=false;
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
