package com.whc.cvccmeasuresystem.Control;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.whc.cvccmeasuresystem.Common.Common.mainFunctionImage;
import static com.whc.cvccmeasuresystem.Common.Common.mainFunctionName;
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
        Fragment fragment=new SignIn();
        fragment=new Batch();
        switchFragment(fragment,getSupportFragmentManager().beginTransaction());
    }


}
