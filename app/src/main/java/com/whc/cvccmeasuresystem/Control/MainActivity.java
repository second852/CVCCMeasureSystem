package com.whc.cvccmeasuresystem.Control;



import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.whc.cvccmeasuresystem.R;

import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Fragment fragment=new BatchStep2Main();
        switchFragment(fragment,getSupportFragmentManager());
    }


}
