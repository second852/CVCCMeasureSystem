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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.whc.cvccmeasuresystem.Client.TCPClient;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.FinishDialogFragment;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Sample;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.Model.Solution;
import com.whc.cvccmeasuresystem.R;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.whc.cvccmeasuresystem.Common.Common.*;
import static com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main.initParameter;


public class IonChannelStep3Set extends Fragment {

    private View view;
    private Activity activity;
    private ImageView sample1I,sample2I,sample3I,sample4I;
//    private AwesomeTextView sample1N,sample2N,sample3N,sample4N;
    private BootstrapEditText measureTime;
    private BootstrapButton start,stop,finish,step02,step01,step04;
    private String mType;



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
        view = inflater.inflate(R.layout.ion_channel_step3_set, container, false);
        findViewById();
        return view;
    }






    @Override
    public void onStart() {
        super.onStart();
        setImage();
    }

    public void setImage()
    {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        List<Solution> sampleSolution=IonChannelStep3Main.dataMap.get(sample1);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample1.getLimitHighVoltage()))
            {
                sample1I.setImageResource(R.drawable.lighto);
                sample1I.startAnimation(animation);
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample1.getLimitLowVoltage()))
            {
                sample1I.setImageResource(R.drawable.lighto);
                sample1I.startAnimation(animation);
                solution.setNoNormalV(true);
            }

            if(solution.isNoNormalV())
            {
                IonChannelStep2Main.errorSample.add(sample1String);
            }
        }

        sampleSolution=IonChannelStep3Main.dataMap.get(sample2);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample2.getLimitHighVoltage()))
            {
                sample2I.setImageResource(R.drawable.lighto);
                sample2I.startAnimation(animation);
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample2.getLimitLowVoltage()))
            {
                sample2I.setImageResource(R.drawable.lighto);
                sample2I.startAnimation(animation);
                solution.setNoNormalV(true);
            }

            if(solution.isNoNormalV())
            {
                IonChannelStep2Main.errorSample.add(sample2String);
            }
        }

        sampleSolution=IonChannelStep3Main.dataMap.get(sample3);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample3.getLimitHighVoltage()))
            {
                sample3I.setImageResource(R.drawable.lighto);
                sample3I.startAnimation(animation);
//                sample3N.setText(sample3.getName());
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample3.getLimitLowVoltage()))
            {
                sample3I.setImageResource(R.drawable.lighto);
                sample3I.startAnimation(animation);
//                sample3N.setText(sample3.getName());
                solution.setNoNormalV(true);
            }

            if(solution.isNoNormalV())
            {
                IonChannelStep2Main.errorSample.add(sample3String);
            }

        }

        sampleSolution=IonChannelStep3Main.dataMap.get(sample4);
        for(Solution solution:sampleSolution)
        {
            if(solution.getVoltage()>Integer.valueOf(sample4.getLimitHighVoltage()))
            {
                sample4I.setImageResource(R.drawable.lighto);
                sample4I.startAnimation(animation);
//                sample4N.setText(sample4.getName());
                solution.setNoNormalV(true);
            }
            if(solution.getVoltage()<Integer.valueOf(sample4.getLimitLowVoltage()))
            {
                sample4I.setImageResource(R.drawable.lighto);
                sample4I.startAnimation(animation);
//                sample4N.setText(sample4.getName());
                solution.setNoNormalV(true);
            }
            if(solution.isNoNormalV())
            {
                IonChannelStep2Main.errorSample.add(sample4String);
            }
        }
    }



    private void findViewById() {
        sample1I=view.findViewById(R.id.sample1I);
//        sample1N=view.findViewById(R.id.sample1N);
//        sample1N.setText(sample1.getName());

        sample2I=view.findViewById(R.id.sample2I);
//        sample2N=view.findViewById(R.id.sample2N);
//        sample2N.setText(sample2.getName());

        sample3I=view.findViewById(R.id.sample3I);
//        sample3N=view.findViewById(R.id.sample3N);
//        sample3N.setText(sample3.getName());

        sample4I=view.findViewById(R.id.sample4I);
//        sample4N=view.findViewById(R.id.sample4N);
//        sample4N.setText(sample4.getName());

        measureTime=view.findViewById(R.id.measureTime);
        start=view.findViewById(R.id.start);
        start.setOnClickListener(new startMeasureData());
        stop=view.findViewById(R.id.stop);
        stop.setOnClickListener(new stopMeasure());
        finish=view.findViewById(R.id.finish);
        finish.setOnClickListener(new finishFragment());
        step02=view.findViewById(R.id.step02);
        step02.setOnClickListener(new step02OnClick());
        step01=view.findViewById(R.id.step01);
        step01.setOnClickListener(new step01OnClick());
        step04=view.findViewById(R.id.step04);
        step04.setOnClickListener(new finishFragment());
    }


    private Runnable measureThread = new Runnable() {
        @Override
        public void run() {
            tcpClient = new TCPClient("1", mType, IonChannelStep3Main.handlerMessage, IonChannelStep3Set.this);
            tcpClient.run();
        }
    };


    private class startMeasureData implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //防止重複量測
            if (startMeasure) {
                Common.showToast(activity, "Please wait util this measurement stop ");
                return;
            }

            mType=measureTime.getText().toString();
            mType=checkViewInteger(measureTime,mType);
            if(mType==null)
            {
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

            new Thread(measureThread).start();
        }
    }

    private class stopMeasure implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            StopDialogFragment aa= new StopDialogFragment();
            aa.setObject(IonChannelStep3Set.this);
            aa.show(getFragmentManager(),"show");
        }
    }

    private class finishFragment implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(startMeasure)
            {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            FinishDialogFragment aa= new FinishDialogFragment();
            aa.setObject(IonChannelStep3Set.this);
            aa.show(getFragmentManager(),"show");
        }
    }

    public void finishMeasure()
    {
        DataBase dataBase=new DataBase(activity);
        SaveFileDB saveFileDB=new SaveFileDB(dataBase);
        SaveFile saveFile=saveFileDB.findOldSaveFileById(sample1.getFileID());
        saveFile.setEndTime(new Timestamp(System.currentTimeMillis()));
        saveFileDB.update(saveFile);
        oldFragment=new ArrayList<>();
        oldFragment.add(Common.CFName);
        IonChannelStep2Set.pageCon1=null;
        IonChannelStep2Set.pageCon1=null;
        needSet=false;
        Common.switchFragment(new IonChannelStep1(),getFragmentManager());
        tcpClient=null;
        initParameter=true;
        IonChannelStep3Main.noInitParameter=false;
    }

    private class step02OnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (startMeasure) {
                Common.showToast(activity, measureStartNotExist);
                return;
            }
            switchFragment(new IonChannelStep2Main(),getFragmentManager());
            oldFragment.remove(oldFragment.size() - 1);
            IonChannelStep2Main.needOldData=true;
            IonChannelStep3Main.noInitParameter=true;
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
            IonChannelStep3Main.noInitParameter=true;
            needSet=true;
            IonChannelStep1 ionChannelStep1=new IonChannelStep1();
            oldFragment.remove(oldFragment.size() - 1);
            Common.switchFragment(ionChannelStep1, getFragmentManager());
        }
    }
}
