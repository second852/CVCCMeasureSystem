package com.whc.cvccmeasuresystem.Control.ionChannel;

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

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Clent.TCPClient;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.FinishDialogFragment;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep1;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Set;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.whc.cvccmeasuresystem.Common.Common.IonChannel2Set;
import static com.whc.cvccmeasuresystem.Common.Common.dataMap;
import static com.whc.cvccmeasuresystem.Common.Common.finishToSave;
import static com.whc.cvccmeasuresystem.Common.Common.measureStartNotExist;
import static com.whc.cvccmeasuresystem.Common.Common.measureTimes;
import static com.whc.cvccmeasuresystem.Common.Common.needInt;
import static com.whc.cvccmeasuresystem.Common.Common.needSet;
import static com.whc.cvccmeasuresystem.Common.Common.oldFragment;
import static com.whc.cvccmeasuresystem.Common.Common.pageCon;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.solution1;
import static com.whc.cvccmeasuresystem.Common.Common.solution2;
import static com.whc.cvccmeasuresystem.Common.Common.solution3;
import static com.whc.cvccmeasuresystem.Common.Common.solution4;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.switchFragment;
import static com.whc.cvccmeasuresystem.Common.Common.tcpClient;


public class IonChannelStep2Set extends Fragment {

    private View view;
    private Activity activity;
    private BootstrapButton conF1, conF2, conF3, conF4, startF, stopF, step01;
    private BootstrapButton conS1, conS2, conS3, conS4, startS, stopS, next, warring,clearF,clearS;
    private BootstrapEditText ionF1, ionF2, ionF3, ionF4;
    private BootstrapEditText ionS1, ionS2, ionS3, ionS4;

    private AwesomeTextView nameF1, nameF2, nameF3, nameF4;
    private AwesomeTextView nameS1, nameS2, nameS3, nameS4;

    public static PageCon pageCon1, pageCon2;


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
        view = inflater.inflate(R.layout.ion_channel_step2_cal, container, false);
        findViewById();
        setIonType();
        setCon();
        return view;
    }

    private void setCon() {
        if(pageCon1!=null)
        {
            if(pageCon1.getCon1()!=null)
            {
                ionF1.setText(pageCon1.getCon1());
            }
            if(pageCon1.getCon2()!=null)
            {
                ionF2.setText(pageCon1.getCon2());
            }
            if(pageCon1.getCon3()!=null)
            {
                ionF3.setText(pageCon1.getCon3());
            }
            if(pageCon1.getCon4()!=null)
            {
                ionF4.setText(pageCon1.getCon4());
            }
        }

        if(pageCon2!=null)
        {
            if(pageCon2.getCon1()!=null)
            {
                ionS1.setText(pageCon2.getCon1());
            }
            if(pageCon2.getCon2()!=null)
            {
                ionS2.setText(pageCon2.getCon2());
            }
            if(pageCon2.getCon3()!=null)
            {
                ionS3.setText(pageCon2.getCon3());
            }
            if(pageCon2.getCon4()!=null)
            {
                ionS4.setText(pageCon2.getCon4());
            }
        }
    }

    private void setIonType() {
        conF1.setText(sample1.getIonType());
        conF2.setText(sample2.getIonType());
        conF3.setText(sample3.getIonType());
        conF4.setText(sample4.getIonType());

        conS1.setText(sample1.getIonType());
        conS2.setText(sample2.getIonType());
        conS3.setText(sample3.getIonType());
        conS4.setText(sample4.getIonType());

        nameF1.setText(sample1.getName());
        nameF2.setText(sample2.getName());
        nameF3.setText(sample3.getName());
        nameF4.setText(sample4.getName());

        nameS1.setText(sample1.getName());
        nameS2.setText(sample2.getName());
        nameS3.setText(sample3.getName());
        nameS4.setText(sample4.getName());
    }


    @Override
    public void onStart() {
        super.onStart();
        startF.setOnClickListener(new startMeasureF());
        stopF.setOnClickListener(new stopMeasure());
        startS.setOnClickListener(new startMeasureS());
        stopS.setOnClickListener(new stopMeasure());
        step01.setOnClickListener(new step01OnClick());
        warring.setOnClickListener(new waringOnClick());
        clearF.setOnClickListener(new clearData());
        clearS.setOnClickListener(new clearData());
        next.setOnClickListener(new nextStep());
    }


    private void findViewById() {
        conF1 = view.findViewById(R.id.conF1);
        conF2 = view.findViewById(R.id.conF2);
        conF3 = view.findViewById(R.id.conF3);
        conF4 = view.findViewById(R.id.conF4);
        ionF1 = view.findViewById(R.id.ionF1);
        ionF2 = view.findViewById(R.id.ionF2);
        ionF3 = view.findViewById(R.id.ionF3);
        ionF4 = view.findViewById(R.id.ionF4);
        startF = view.findViewById(R.id.startF);
        stopF = view.findViewById(R.id.stopF);

        conS1 = view.findViewById(R.id.conS1);
        conS2 = view.findViewById(R.id.conS2);
        conS3 = view.findViewById(R.id.conS3);
        conS4 = view.findViewById(R.id.conS4);
        ionS1 = view.findViewById(R.id.ionS1);
        ionS2 = view.findViewById(R.id.ionS2);
        ionS3 = view.findViewById(R.id.ionS3);
        ionS4 = view.findViewById(R.id.ionS4);
        startS = view.findViewById(R.id.startS);
        stopS = view.findViewById(R.id.stopS);

        nameF1 = view.findViewById(R.id.nameF1);
        nameF2 = view.findViewById(R.id.nameF2);
        nameF3 = view.findViewById(R.id.nameF3);
        nameF4 = view.findViewById(R.id.nameF4);


        nameS1 = view.findViewById(R.id.nameS1);
        nameS2 = view.findViewById(R.id.nameS2);
        nameS3 = view.findViewById(R.id.nameS3);
        nameS4 = view.findViewById(R.id.nameS4);

        step01 = view.findViewById(R.id.step01);
        next = view.findViewById(R.id.next);
        warring = view.findViewById(R.id.warning);

        clearF=view.findViewById(R.id.clearF);
        clearS=view.findViewById(R.id.clearS);
    }


    private Runnable measureThread = new Runnable() {
        @Override
        public void run() {
            tcpClient = new TCPClient("1", "1", IonChannelStep2Main.handlerMessage, IonChannelStep2Set.this);
            tcpClient.run();
        }
    };


    private class startMeasureF implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //防止重複量測
            if (startMeasure) {
                Common.showToast(activity, "Please wait util this measurement stop ");
                return;
            }

            if(IonChannelStep2Main.errorSample.size()>0)
            {
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("Data of ");
                for(String error:IonChannelStep2Main.errorSample)
                {
                    stringBuilder.append(error+" ");
                }
                if(IonChannelStep2Main.errorSample.size()>1)
                {
                    stringBuilder.append("are unusual");
                }else {
                    stringBuilder.append("is unusual");
                }
                stringBuilder.append(",Please check limit data or sample,and pressure \"clear\"");
                Common.showToast(activity,stringBuilder.toString());
                return;
            }

            String ionOne = ionF1.getText().toString();
            String ionTwo = ionF2.getText().toString();
            String ionThree = ionF3.getText().toString();
            String ionFour = ionF4.getText().toString();
            //one
            if (Common.checkViewIon(ionF1, ionOne)) {

                return;
            }
            ionOne = ionOne.trim();

            //two
            if (Common.checkViewIon(ionF2, ionTwo)) {
                return;
            }
            ionTwo = ionTwo.trim();

            //three
            if (Common.checkViewIon(ionF3, ionThree)) {
                return;
            }
            ionThree = ionThree.trim();

            //four
            if (Common.checkViewIon(ionF4, ionFour)) {
                return;
            }
            ionFour = ionFour.trim();

            //sample1
            if(sample1.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 1 not set lower voltage");
                return;
            }
            if(sample1.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 1 not set upper voltage");
                return;
            }

            //sample2
            if(sample2.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 2 not set lower voltage");
                return;
            }
            if(sample2.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 2 not set upper voltage");
                return;
            }

            //sample3
            if(sample3.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 3 not set lower voltage");
                return;
            }
            if(sample3.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 3 not set upper voltage");
                return;
            }

            //sample4
            if(sample4.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 4 not set lower voltage");
                return;
            }
            if(sample4.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 4 not set upper voltage");
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
            //connection
            solution1 = new Solution(ionOne, sample1.getID());
            solution2 = new Solution(ionTwo, sample2.getID());
            solution3 = new Solution(ionThree, sample3.getID());
            solution4 = new Solution(ionFour, sample4.getID());
            Common.showToast(activity, "Wifi Connecting");
            new Thread(measureThread).start();
            measureTimes = 1;
        }
    }

    private class stopMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            StopDialogFragment aa= new StopDialogFragment();
            aa.setObject(IonChannelStep2Set.this);
            aa.show(getFragmentManager(),"show");
        }
    }



    private class step01OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (startMeasure) {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            if (tcpClient != null) {
                tcpClient.cancelHomeTcpClient();
                tcpClient = null;
            }
            BatchStep1 batchStep1 = new BatchStep1();
            Common.switchFragment(batchStep1, getFragmentManager());
        }
    }


    private class startMeasureS implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //防止重複量測
            if (startMeasure) {
                Common.showToast(activity, "Please wait util this measurement stop ");
                return;
            }

            if(IonChannelStep2Main.errorSample.size()>0)
            {
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("Data of ");
                for(String error:IonChannelStep2Main.errorSample)
                {
                    stringBuilder.append(error+" ");
                }
                if(IonChannelStep2Main.errorSample.size()>1)
                {
                    stringBuilder.append("are unusual");
                }else {
                    stringBuilder.append("is unusual");
                }
                stringBuilder.append(",Please check limit data or sample,and pressure \"clear\"");
                Common.showToast(activity,stringBuilder.toString());
                return;
            }

            String ionOne = ionS1.getText().toString();
            String ionTwo = ionS2.getText().toString();
            String ionThree = ionS3.getText().toString();
            String ionFour = ionS4.getText().toString();
            //one
            if (Common.checkViewIon(ionS1, ionOne)) {

                return;
            }
            ionOne = ionOne.trim();

            //two
            if (Common.checkViewIon(ionS2, ionTwo)) {
                return;
            }
            ionTwo = ionTwo.trim();

            //three
            if (Common.checkViewIon(ionS3, ionThree)) {
                return;
            }
            ionThree = ionThree.trim();

            //four
            if (Common.checkViewIon(ionS4, ionFour)) {
                return;
            }
            ionFour = ionFour.trim();

            //sample1
            if(sample1.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 1 not set lower voltage");
                return;
            }
            if(sample1.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 1 not set upper voltage");
                return;
            }

            //sample2
            if(sample2.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 2 not set lower voltage");
                return;
            }
            if(sample2.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 2 not set upper voltage");
                return;
            }

            //sample3
            if(sample3.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 3 not set lower voltage");
                return;
            }
            if(sample3.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 3 not set upper voltage");
                return;
            }

            //sample4
            if(sample4.getLimitLowVoltage()==null)
            {
                Common.showToast(activity,"Sample 4 not set lower voltage");
                return;
            }
            if(sample4.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Sample 4 not set upper voltage");
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
            solution1 = new Solution(ionOne, sample1.getID());
            solution2 = new Solution(ionTwo, sample2.getID());
            solution3 = new Solution(ionThree, sample3.getID());
            solution4 = new Solution(ionFour, sample4.getID());
            Common.showToast(activity, "Wifi Connecting");
            new Thread(measureThread).start();
            measureTimes = 2;
        }
    }

    private class waringOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            pageCon1 = new PageCon();
            pageCon2 = new PageCon();
            String ionOne = ionF1.getText().toString();
            String ionTwo = ionF2.getText().toString();
            String ionThree = ionF3.getText().toString();
            String ionFour = ionF4.getText().toString();
            if (ionOne != null) {
                pageCon1.setCon1(ionOne);
            }
            if (ionTwo != null) {
                pageCon1.setCon2(ionTwo);
            }
            if (ionThree != null) {
                pageCon1.setCon3(ionThree);
            }
            if (ionFour != null) {
                pageCon1.setCon4(ionFour);
            }
            ionOne = ionS1.getText().toString();
            ionTwo = ionS2.getText().toString();
            ionThree = ionS3.getText().toString();
            ionFour = ionS4.getText().toString();
            if (ionOne != null) {
                pageCon2.setCon1(ionOne);
            }
            if (ionTwo != null) {
                pageCon2.setCon2(ionTwo);
            }
            if (ionThree != null) {
                pageCon2.setCon3(ionThree);
            }
            if (ionFour != null) {
                pageCon2.setCon4(ionFour);
            }
            switchFragment(new IonChannelStep2Limit(),getFragmentManager());
            oldFragment.add(IonChannel2Set);
        }
    }

    private class clearData implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            dataMap.put(sample1,new ArrayList<Solution>());
            dataMap.put(sample2,new ArrayList<Solution>());
            dataMap.put(sample3,new ArrayList<Solution>());
            dataMap.put(sample4,new ArrayList<Solution>());
            IonChannelStep2Data fragment= (IonChannelStep2Data) IonChannelStep2Main.adapter.getPage(1);
            if(fragment!=null)
            {
                fragment.setListView();
            }
            IonChannelStep2Main.errorSample=new ArrayList<>();
            Common.showToast(activity,"Data clear!");
        }
    }

    private class nextStep implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(sample1.getLimitHighVoltage()==null)
            {
                Common.showToast(activity,"Limit need to set!");
                return;
            }

            if(sample1.getSlope()==null)
            {
                Common.showToast(activity,"Calibrating  isn't complete");
                return;
            }

            SolutionDB solutionDB=new SolutionDB(new DataBase(activity));
            for(Sample sample:dataMap.keySet())
            {
                for(Solution solution:dataMap.get(sample))
                {
                    solutionDB.insert(solution);
                }
            }
            oldFragment.add(IonChannel2Set);
            switchFragment(new IonChannelStep3Main(),getFragmentManager());
        }
    }
}
