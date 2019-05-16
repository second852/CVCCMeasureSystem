package com.whc.cvccmeasuresystem.Control.Humidity;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.whc.cvccmeasuresystem.Client.JobHumidity;
import com.whc.cvccmeasuresystem.Client.JobService;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep3Main;
import com.whc.cvccmeasuresystem.R;


import java.util.Calendar;

import static com.whc.cvccmeasuresystem.Common.Common.*;



public class HumidityMain extends Fragment {


    private static Activity activity;
    private BootstrapButton timeEndN,start,hour,minute,second,con,stop,minimize;
    private long endTime,nowTime;
    private int endMin,endHour,showHour,showMin,showSecond;
    private boolean timeBreak;
    private String setTime;
    private static ImageView sensor;


    public static Handler showLightHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            switch (msg.what)
            {
                case 0:
                    sensor.setImageResource(R.drawable.lighte);
                    sensor.clearAnimation();
                    break;
                case 1:
                    sensor.setImageResource(R.drawable.lighto);
                    sensor.startAnimation(animation);
                   break;
            }
        }
    };

    private Handler showTimeHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==1)
            {
                showHour=0;
                showMin=0;
                showSecond=0;

            }

            second.setText(String.format("%02d", showSecond));
            minute.setText(String.format("%02d", showMin));
            hour.setText(String.format("%02d", showHour));
        }
    };

    private Runnable calculateTime=new Runnable() {
        @Override
        public void run() {

            while (!timeBreak)
            {
                nowTime=System.currentTimeMillis();

                if(endTime<nowTime) {
                    timeBreak=true;
                    showTimeHandler.sendEmptyMessage(1);
                }


                if(nowTime%1000==0) {
                    showSecond=showSecond-1;
                    if(showSecond<0)
                    {
                        showSecond=showSecond+60;
                        showMin=showMin-1;
                    }
                    if(showMin<0)
                    {
                        showHour=showHour-1;
                        showMin=showMin+60;
                    }
                    if(showHour<0)
                    {
                        showHour=0;
                        showMin=0;
                        showSecond=0;
                    }
                    showTimeHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }

        //init
        activity.setTitle("Humidity Measure");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.humidity_main, container, false);
        findViewById(view);
        timeEndN.setOnClickListener(new showTime());
        start.setOnClickListener(new startAction());
        con.setOnClickListener(new continueAction());
        stop.setOnClickListener(new stopAction());
        return view;
    }

    private void findViewById(View view) {
        timeEndN=view.findViewById(R.id.timeEndN);
        hour=view.findViewById(R.id.hour);
        minute=view.findViewById(R.id.minute);
        second=view.findViewById(R.id.second);
        start=view.findViewById(R.id.start);
        con=view.findViewById(R.id.con);
        stop=view.findViewById(R.id.stop);
        sensor=view.findViewById(R.id.sensor);
        minimize=view.findViewById(R.id.minimize);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    private class showTime implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    timeEndN.setError(null);
                    setTime=String.format("%02d", hourOfDay)+" : "+String.format("%02d", minute);
                    timeEndN.setText(setTime);

                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);

                    endTime=calendar.getTimeInMillis();
                    endHour=hourOfDay;
                    endMin=minute;
                }
            }, hour, minute, false).show();
        }
    }


    private class startAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            timeBreak=false;

            nowTime=System.currentTimeMillis();
            Calendar nowCalendar=Calendar.getInstance();
            int nowMin=nowCalendar.get(Calendar.MINUTE);
            int nowHour=nowCalendar.get(Calendar.HOUR_OF_DAY);
            showMin=endMin-nowMin;
            showHour=endHour-nowHour;
            showSecond=00;

            if ((endTime-nowTime)<0)
            {
                timeEndN.setError(" ");
                Common.showToast(activity,getString(R.string.error_little_time));
                return;
            }


            if(showMin<0)
            {
                showMin=showMin+60;
                showHour=showHour-1;
            }

            //重置圖片
            showLightHandler.sendEmptyMessage(0);




//            startMeasure();




            second.setText(String.format("%02d", showSecond));
            minute.setText(String.format("%02d", showMin));
            hour.setText(String.format("%02d", showHour));
            new Thread(calculateTime).start();
        }
    }

    private class continueAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if(timeBreak)
            {

                con.setText("continue");
                con.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                timeBreak=false;
                stopMeasure();

            }else{
                con.setText("break");
                con.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                timeBreak=true;
//                startMeasure();
                new Thread(calculateTime).start();
            }
        }
    }

    private class stopAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }

    private void stopMeasure()
    {
        JobHumidity.mRun=false;
        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }



    private void startMeasure()
    {
        //check Wifi
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssId = wifiInfo.getSSID();
        if (ssId == null || ssId.indexOf("BCS_Device") == -1) {
            Common.showToast(activity, "Please connect BCS_Device");
            return;
        }
        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobHumidity.handlerMessage= HumidityMain.this.showLightHandler;
        JobHumidity.measureDuration="1";
        JobHumidity.measureTime=String.valueOf(showHour*60+showMin);
        JobHumidity.mRun=true;
        JobHumidity.measureType="5";
        JobHumidity.measureFragment=Common.HumidityMain;


        ComponentName mServiceComponent = new ComponentName(activity, JobHumidity.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        tm.cancelAll();

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(2);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());

    }

}

