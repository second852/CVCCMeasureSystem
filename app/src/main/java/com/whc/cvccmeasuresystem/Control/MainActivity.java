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
import com.whc.cvccmeasuresystem.R;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        SignIn.signIn=false;
        finishToSave=true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment fragment = new BatchStep2Main();
        fragment=new SignIn();
        switchFragment(fragment, getSupportFragmentManager());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SignIn.signIn)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.popup_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(startMeasure)
        {
            Common.showToast(MainActivity.this,meaureStartNotExist);
            return true;
        }

        if(!finishToSave)
        {
            Common.showToast(MainActivity.this,fileNotSave);
            return true;
        }

        if(tcpClient!=null)
        {
            tcpClient.cancelHomeTcpClient();
            tcpClient=null;
        }

        switch (item.getItemId()) {
            case R.id.home:
                Common.switchFragment(new ChoiceFunction(),getSupportFragmentManager());
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
        if(tcpClient!=null)
        {
            tcpClient.cancelHomeTcpClient();
            tcpClient=null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(startMeasure)
            {
                Common.showToast(MainActivity.this,meaureStartNotExist);
                return true;
            }
        }
        return true;
    }
}
