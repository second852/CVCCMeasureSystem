package com.whc.cvccmeasuresystem.Control;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Clent.TCPClient;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.DataAdapter;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SampleDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;


import java.util.ArrayList;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;

public class BatchStep2 extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton con1, con2, con3, con4, start;
    private BootstrapEditText ion1, ion2, ion3, ion4, measureTime;
    private SharedPreferences sharedPreferences;
    private Sample sample1, sample2, sample3, sample4;
    private DataBase dataBase;
    private String mTime;
    private ListView data;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        activity.setTitle("BatchStep2 Monitor");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.batch_step2, container, false);
        dataBase = new DataBase(activity);
        findViewById();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        setSample();
        start.setOnClickListener(new startMeasure());
    }

    private void setSample() {
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        SampleDB sampleDB = new SampleDB(dataBase.getReadableDatabase());

        //sample 1
        int sampleID = sharedPreferences.getInt(Common.sample1, 0);
        sample1 = sampleDB.findOldSample(sampleID);
        con1.setText(sample1.getIonType());

        //sample 2
        sampleID = sharedPreferences.getInt(Common.sample2, 0);
        sample2 = sampleDB.findOldSample(sampleID);
        con2.setText(sample2.getIonType());

        //sample 3
        sampleID = sharedPreferences.getInt(Common.sample3, 0);
        sample3 = sampleDB.findOldSample(sampleID);
        con3.setText(sample3.getIonType());

        //sample 4
        sampleID = sharedPreferences.getInt(Common.sample4, 0);
        sample4 = sampleDB.findOldSample(sampleID);
        con4.setText(sample4.getIonType());
    }


    private void findViewById() {
        con1 = view.findViewById(R.id.con1);
        con2 = view.findViewById(R.id.con2);
        con3 = view.findViewById(R.id.con3);
        con4 = view.findViewById(R.id.con4);
        ion1 = view.findViewById(R.id.ion1);
        ion2 = view.findViewById(R.id.ion2);
        ion3 = view.findViewById(R.id.ion3);
        ion4 = view.findViewById(R.id.ion4);
        measureTime = view.findViewById(R.id.measureTime);
        start = view.findViewById(R.id.start);
        data = view.findViewById(R.id.data);

    }


    private Runnable measureThread = new Runnable() {
        @Override
        public void run() {
            new TCPClient("1", "1", handlerMessage).run();
        }
    };

    //setList
    private Handler handlerMessage = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Solution> solutions=new ArrayList<>();
            solutions.add(new Solution());
            data.setAdapter(new DataAdapter(activity,solutions));
            data.invalidate();
        }
    };


    private class startMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String ionOne = ion1.getText().toString();
            String ionTwo = ion2.getText().toString();
            String ionThree = ion3.getText().toString();
            String ionFour = ion4.getText().toString();
            mTime = measureTime.getText().toString();
            //one
            if (Common.checkViewIon(ion1, ionOne)) {

                return;
            }
            ionOne = ionOne.trim();

            //two
            if (Common.checkViewIon(ion2, ionTwo)) {
                return;
            }
            ionTwo = ionTwo.trim();

            //three
            if (Common.checkViewIon(ion3, ionThree)) {
                return;
            }
            ionThree = ionThree.trim();

            //four
            if (Common.checkViewIon(ion4, ionFour)) {
                return;
            }
            ionFour = ionFour.trim();

            //measureTime
            if (mTime == null) {
                measureTime.setError(needInt);
                return;
            }
            mTime = mTime.trim();
            if (mTime.length() <= 0) {
                measureTime.setError(needInt);
                return;
            }
            try {

                new Double(mTime);
            } catch (Exception e) {
                measureTime.setError(needInt);
                return;
            }
            //check Wifi
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssId = wifiInfo.getSSID();
            if (ssId == null || ssId.indexOf("BCS_Device") == -1) {
                Common.showToast(activity, "Please connect BCS_Device");
                return;
            }
            //conection
            new Thread(measureThread).start();
        }
    }
}
