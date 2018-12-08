package com.whc.cvccmeasuresystem.Control.Batch;

import android.app.Activity;
import android.content.Context;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Clent.TCPClient;
import com.whc.cvccmeasuresystem.Common.Common;

import com.whc.cvccmeasuresystem.Common.FinishDialogFragment;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;




import static com.whc.cvccmeasuresystem.Common.Common.*;


public class BatchStep2Set extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton con1, con2, con3, con4, start,stop,finish,step01,step03;
    private BootstrapEditText ion1, ion2, ion3, ion4, measureTime;
    private String mTime;





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.batch_step2_set, container, false);
        findViewById();
        setIon();
        return view;
    }

    private void setIon() {
        con1.setText(sample1.getIonType());
        con2.setText(sample2.getIonType());
        con3.setText(sample3.getIonType());
        con4.setText(sample4.getIonType());
    }


    @Override
    public void onStart() {
        super.onStart();
        start.setOnClickListener(new startMeasure());
        stop.setOnClickListener(new stopMeasure());
        finish.setOnClickListener(new finishFragment());
        step01.setOnClickListener(new step01OnClick());
        step03.setOnClickListener(new finishFragment());
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
        stop=view.findViewById(R.id.stop);
        finish=view.findViewById(R.id.finish);
        step01=view.findViewById(R.id.step01);
        step03=view.findViewById(R.id.step03);
    }


    private Runnable measureThread = new Runnable() {
        @Override
        public void run() {
            tcpClient=new TCPClient("1", mTime, BatchStep2Main.handlerMessage,BatchStep2Set.this);
            tcpClient.run();
        }
    };




    private class startMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //防止重複量測
            if(startMeasure)
            {
                Common.showToast(activity,"Please wait util this measurement stop ");
                return;
            }

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
            solution1=new Solution(ionOne,sample1.getID());
            solution2=new Solution(ionTwo,sample2.getID());
            solution3=new Solution(ionThree,sample3.getID());
            solution4=new Solution(ionFour,sample4.getID());
            Common.showToast(activity,"Wifi Connecting");
            measureTimes=0;
            new Thread(measureThread).start();
        }
    }

    private class stopMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(tcpClient!=null)
            {
                tcpClient.cancelTcpClient();
                tcpClient=null;
            }
        }
    }

    public void finishMeasure()
    {
        DataBase dataBase=new DataBase(activity);
        SolutionDB solutionDB=new SolutionDB(dataBase.getReadableDatabase());
        for(Sample sample:dataMap.keySet())
        {
            for (Solution solutions:dataMap.get(sample))
            {
                solutionDB.insert(solutions);
            }
        }
        Common.finishToSave=true;
        Common.switchFragment(new BatchStep1(),getFragmentManager());
        tcpClient=null;
    }



    private class finishFragment implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(startMeasure)
            {
                Common.showToast(activity,meaureStartNotExist);
                return;
            }
            FinishDialogFragment aa= new FinishDialogFragment();
            aa.setObject(BatchStep2Set.this);
            aa.show(getFragmentManager(),"show");
        }
    }

    private class step01OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(startMeasure)
            {
                Common.showToast(activity,meaureStartNotExist);
                return;
            }
            if(tcpClient!=null)
            {
                tcpClient.cancelHomeTcpClient();
                tcpClient=null;
            }
            BatchStep1 batchStep1=new BatchStep1();
            Common.switchFragment(batchStep1,getFragmentManager());
        }
    }
}