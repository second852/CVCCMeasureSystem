package com.whc.cvccmeasuresystem.Control.Humidity;


import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whc.cvccmeasuresystem.Client.JobHumidity;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.HumidityDB;
import com.whc.cvccmeasuresystem.DataBase.SaveFileDB;
import com.whc.cvccmeasuresystem.FloatWindow.FloatWindowManager;
import com.whc.cvccmeasuresystem.Model.HumidityVO;
import com.whc.cvccmeasuresystem.Model.PageCon;
import com.whc.cvccmeasuresystem.Model.SaveFile;
import com.whc.cvccmeasuresystem.R;




import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;


import static com.whc.cvccmeasuresystem.Client.JobHumidity.handlerMessage;
import static com.whc.cvccmeasuresystem.Client.JobHumidity.measureFragment;
import static com.whc.cvccmeasuresystem.Common.Common.*;


public class HumidityMain extends Fragment {


    public static Activity activity;

    private static ImageView[] oneWater;
    private static ImageView[] twoWater;
    private static ImageView[] threeWater;
    private static ImageView[] fourWater;

    private static SharedPreferences sharedPreferences;
    public static TextView showTime;
    public static long startTime;
    public static long hour,min,second;
    private static String setTime;
    public static HumidityVO[] humidityVOS;
    public static boolean pauseNow;
    public static ImageView pause, stop, start,measureStatue;

    public ImageView minimize;




    public static Handler showLightHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Common.clearImageAnimation(oneWater);
                    Common.clearImageAnimation(twoWater);
                    Common.clearImageAnimation(threeWater);
                    Common.clearImageAnimation(fourWater);
                    break;
                case 1:
                    startStatus();
                    setTime = String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", second);
                    showTime.setText(setTime);
                    break;
                case 2:
                    break;
                case 3:
                    Common.showToast(activity, activity.getString(R.string.measure_start));
                    break;
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
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(endModule, false).apply();
        sharedPreferences.edit().putString(finalFragment, HumidityMainString).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.humidity_main, container, false);
        humidityVOS=new HumidityVO[4];
        findViewById(view);
        start.setOnClickListener(new startAction());
        pause.setOnClickListener(new continueAction());
        stop.setOnClickListener(new stopAction());
        return view;
    }

    private void findViewById(View view) {

        oneWater = new ImageView[1];
        oneWater[0]=view.findViewById(R.id.one_water);

        twoWater=new ImageView[2];
        twoWater[0] = view.findViewById(R.id.two_water1);
        twoWater[1] = view.findViewById(R.id.two_water2);

        threeWater=new ImageView[3];
        threeWater[0] = view.findViewById(R.id.three_water1);
        threeWater[1] = view.findViewById(R.id.three_water2);
        threeWater[2] = view.findViewById(R.id.three_water3);

        fourWater=new ImageView[4];
        fourWater[0]=view.findViewById(R.id.four_water1);
        fourWater[1]=view.findViewById(R.id.four_water2);
        fourWater[2]=view.findViewById(R.id.four_water3);
        fourWater[3]=view.findViewById(R.id.four_water4);




        showTime = view.findViewById(R.id.showTime);
        start = view.findViewById(R.id.start);
        pause = view.findViewById(R.id.pause);
        stop = view.findViewById(R.id.stop);
        measureStatue=view.findViewById(R.id.measureStatue);

        minimize = view.findViewById(R.id.minimize);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatWindowManager.getInstance().applyOrShowFloatWindow(activity);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        JobHumidity.onPause=false;
        startMeasure=!(sharedPreferences.getBoolean(endMeasure,true));
        pauseNow=sharedPreferences.getBoolean("pauseNow",true);
        if(!startMeasure)
        {
            stopStatus();
        }else
        {
            if(pauseNow){
                pauseStatus();
            }else {
                startStatus();
            }
        }

        if(Common.pageCon!=null)
        {
            if(Common.pageCon.getCon1()==null||Common.pageCon.getCon1().trim().isEmpty())
            {
                showTime.setText("00:00:00");
            }else{
                showTime.setText(Common.pageCon.getCon1());
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        JobHumidity.onPause=true;
        PageCon pageCon=new PageCon();
        pageCon.setCon1(showTime.getText().toString());
        Common.savePageParameter(sharedPreferences,pageCon);
        sharedPreferences.edit().putBoolean("pauseNow",pauseNow).apply();
    }


    private class startAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (startMeasure||!pauseNow) {
                Common.showToast(activity, "Measuring Now! Please Press Stop");
                return;
            }

            //重置圖片
            showLightHandler.sendEmptyMessage(0);
            //重置時間
            hour=0;min=0;second=0;

            JobHumidity.setTime=true;


            startMeasure();


            DataBase dataBase=new DataBase(activity);
            SaveFileDB saveFileDB=new SaveFileDB(dataBase);
            int useId=sharedPreferences.getInt(Common.userId,0);
            SaveFile saveFile=new SaveFile();
            saveFile.setMeasureType("0");
            saveFile.setName(Common.timeToString.format(new Date(System.currentTimeMillis())));
            saveFile.setStatTime(new Timestamp(System.currentTimeMillis()));
            saveFile.setEndTime(new Timestamp(0));
            saveFile.setUserId(useId);
            saveFileDB.insert(saveFile);
            SaveFile nowFile=saveFileDB.findOldSaveFile(saveFile.getName());

            int i=0;
            HumidityDB humidityDB=new HumidityDB(dataBase);
            for (HumidityVO humidityVO:humidityVOS)
            {
                humidityVO=new HumidityVO();
                humidityVO.setFileID(nowFile.getID());
                humidityVO.setBeginTime(new Timestamp(0));
                humidityVO.setName("Sample"+i);
                humidityVO.setBaseVoltage(0);
                humidityVO.setOverVoltage(0);
                humidityVO.setLight(false);
                humidityVO.setId(humidityDB.insert(humidityVO));
                Log.d("XXXXXX Id"," sharedPreferences: "+ humidityVO.getId());
                sharedPreferences.edit().putLong("HumidityVO"+i,humidityVO.getId()).apply();
                i++;
            }
        }
    }

    private class continueAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (!startMeasure) {
                return;
            }

            if (!pauseNow) {
                pauseStatus();
                stopMeasure();
                JobHumidity.mRun = false;
                pauseNow=true;
            } else {
                showLightHandler.sendEmptyMessage(1);
                startMeasure();
                JobHumidity.mRun = true;
                pauseNow=false;
            }
        }
    }

    private class stopAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            StopDialogFragment aa = new StopDialogFragment();
            aa.setObject(HumidityMain.this);
            aa.show(getFragmentManager(), "show");
        }
    }

    public void stopMeasure() {



        JobHumidity.mRun = false;
        if (JobHumidity.socket != null) {
            try {
                JobHumidity.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(0);
        Common.showToast(activity, getString(R.string.measure_stop));
    }


    private void startMeasure() {
        //check Wifi
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssId = wifiInfo.getSSID();
        if (ssId == null || ssId.indexOf("BCS_Device") == -1) {
            Common.showToast(activity, "Please connect BCS_Device");
            return;
        }

        JobScheduler tm = (JobScheduler) activity.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobHumidity.handlerMessage = HumidityMain.this.showLightHandler;
        JobHumidity.mRun = true;
        JobHumidity.measureType = "5";
        measureFragment = Common.HumidityMainString;

        ComponentName mServiceComponent = new ComponentName(activity, JobHumidity.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        tm.cancelAll();

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(2);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());

        pauseNow=false;
    }

    public void setMeasureNowData(){
        HumidityDB humidityDB=new HumidityDB(new DataBase(activity));

        for(int i=0;i<4;i++)
        {
            long id=sharedPreferences.getLong("HumidityVO"+i,0);
             Log.d("XXXXXX"," sharedPreferences: "+ id);
             humidityVOS[i]=humidityDB.getById(id);
        }
    };


    public static void startStatus()
    {
        start.setImageResource(R.drawable.start_button_down);
        pause.setImageResource(R.drawable.break_button_up);
        stop.setImageResource(R.drawable.stop_button_up);
        measureStatue.setImageResource(R.drawable.start);
        showTime.setBackground(activity.getDrawable(R.drawable.corners_model_green));
        showTime.setTextColor(activity.getColor(R.color.bootstrap_brand_success));
    }

    public static void pauseStatus()
    {
        start.setImageResource(R.drawable.start_button_up);
        pause.setImageResource(R.drawable.break_button_down);
        stop.setImageResource(R.drawable.stop_button_up);
        measureStatue.setImageResource(R.drawable.pause);
        showTime.setBackground(activity.getDrawable(R.drawable.corners_model_yellow));
        showTime.setTextColor(activity.getColor(R.color.bootstrap_brand_warning));
    }

    public static void stopStatus()
    {
        sharedPreferences.edit().putBoolean(endMeasure, true).apply();
        HumidityMain.start.setImageResource(R.drawable.start_button_up);
        HumidityMain.pause.setImageResource(R.drawable.break_button_up);
        HumidityMain.stop.setImageResource(R.drawable.stop_button_down);
        HumidityMain.measureStatue.setImageResource(R.drawable.stop);
        HumidityMain.showTime.setBackground(HumidityMain.activity.getDrawable(R.drawable.corners_model_gray));
        HumidityMain.showTime.setTextColor(HumidityMain.activity.getColor(R.color.bootstrap_gray_light));
    }


}

