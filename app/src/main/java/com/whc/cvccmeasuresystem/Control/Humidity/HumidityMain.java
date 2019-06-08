package com.whc.cvccmeasuresystem.Control.Humidity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whc.cvccmeasuresystem.Client.JobHumidity;
import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.CustomDialogFragment;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.Common.TimeRunnable;
import com.whc.cvccmeasuresystem.Control.MainActivity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.whc.cvccmeasuresystem.Client.JobHumidity.measureFragment;
import static com.whc.cvccmeasuresystem.Common.Common.*;


public class HumidityMain extends Fragment {


    public static Activity activity;

    private static ImageView[] oneWater;
    private static ImageView[] twoWater;
    private static ImageView[] threeWater;
    private static ImageView[] fourWater;

    private static SharedPreferences sharedPreferences;
    public static TextView showTime,overTime1,overTime2,overTime3,overTime4;
    public static long startTime;
    public static long hour,min,second;
    private static String setTime;
    public static HumidityVO[] humidityVOS;
    public static boolean pauseNow;
    public static ImageView pause, stop, start,measureStatue;

    public ImageView minimize,settings;
    public LinearLayout settingsL;
    public TextView overVoltage;
    public static boolean showAlert;
    public  static FragmentManager fragmentManager;
    public ImageView showImage;



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
                    setTime = String.format("%02d", hour) + ":" + String.format("%02d", min) ;
                    setTime=String.valueOf(setTime);
                    showTime.setText(setTime);
                    sharedPreferences.edit().putString(Common.HumidityMainTime,setTime).apply();
                    break;
                case 2:

                    if( JobHumidity.onPause)
                    {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                    }else{
                        if(showAlert)
                        {
                            new CustomDialogFragment(activity).Channel((List<String>) msg.obj).show();
                            showAlert=false;
                        }
                    }
                    break;
                case 3:
                    Common.clearImageAnimation(oneWater);
                    Common.clearImageAnimation(twoWater);
                    Common.clearImageAnimation(threeWater);
                    Common.clearImageAnimation(fourWater);

                    setTextViewBlack(overTime1);
                    setTextViewBlack(overTime2);
                    setTextViewBlack(overTime3);
                    setTextViewBlack(overTime4);

                    overTime1.setText("00:00");
                    overTime2.setText("00:00");
                    overTime3.setText("00:00");
                    overTime4.setText("00:00");
                    Common.showToast(activity, activity.getString(R.string.measure_start));
                    break;
            }
        }
    };



    public static Handler overHandler = new Handler(Looper.myLooper()) {
        String setTime;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setTime= (String) msg.obj;
            switch (msg.what)
            {
                case 0:
                    setTextViewBlue(overTime1);
                    overTime1.setText(setTime);
                    Common.setImageAnimation(oneWater);
                    break;
                case 1:
                    setTextViewBlue(overTime2);
                    overTime2.setText(setTime);
                    Common.setImageAnimation(twoWater);
                    break;
                case 2:
                    setTextViewBlue(overTime3);
                    overTime3.setText(setTime);
                    Common.setImageAnimation(threeWater);
                  break;
                case 3:
                    setTextViewBlue(overTime4);
                    overTime4.setText(setTime);
                    Common.setImageAnimation(fourWater);
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
        fragmentManager=getFragmentManager();
        //init
        activity.setTitle("Humidity Measure");
        sharedPreferences = activity.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(endModule, false).apply();
        sharedPreferences.edit().putString(finalFragment, HumidityMainString).apply();
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


        overTime1=view.findViewById(R.id.overTime1);
        overTime2=view.findViewById(R.id.overTime2);
        overTime3=view.findViewById(R.id.overTime3);
        overTime4=view.findViewById(R.id.overTime4);



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

        settingsL=view.findViewById(R.id.settingsL);
        settings=view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsL.setVisibility(View.VISIBLE);
            }
        });
        settingsL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsL.setVisibility(View.GONE);
                sharedPreferences.edit().putString(settingsVoltage,overVoltage.getText().toString()).apply();
            }
        });
        overVoltage=view.findViewById(R.id.overVoltage);

        showImage=view.findViewById(R.id.showData);
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new HumidityData();
                Common.switchFragment(fragment,getFragmentManager());
                Common.oldFragment.add(Common.HumidityMainString);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        JobHumidity.onPause=false;
        startMeasure=!(sharedPreferences.getBoolean(endMeasure,true));
        pauseNow=sharedPreferences.getBoolean(Common.pauseNow,true);
        if(!startMeasure)
        {
            pauseNow=true;
            stopStatus();
        }else
        {
            if(pauseNow){
                pauseStatus();
            }else {
                startStatus();
            }
            setMeasureNowData();
        }

        overVoltage.setText(sharedPreferences.getString(Common.settingsVoltage,"0"));
        showTime.setText(sharedPreferences.getString(Common.HumidityMainTime,"00:00"));

    }

    @Override
    public void onPause() {
        super.onPause();
        JobHumidity.onPause=true;
        sharedPreferences.edit().putBoolean(Common.pauseNow,pauseNow).apply();
    }


    private class startAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if(settingsL.getVisibility()==View.VISIBLE)
            {
                Common.showToast(activity, "Please Close View of Setting Voltage !");
                return;
            }

            if (startMeasure||!pauseNow) {
                Common.showToast(activity, "Measuring Now! Please Press Stop");
                return;
            }

            //重置圖片
            showLightHandler.sendEmptyMessage(0);
            //重置時間
            hour=0;min=0;second=0;

            JobHumidity.setTime=true;
            TimeRunnable.timeRun=true;

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
            sharedPreferences.edit().putInt(Common.fileId,nowFile.getID()).apply();


            HumidityDB humidityDB=new HumidityDB(dataBase);
            for (int j=0;j<humidityVOS.length;j++)
            {
                humidityVOS[j]=new HumidityVO();
                humidityVOS[j].setFileID(nowFile.getID());
                humidityVOS[j].setBeginTime(new Timestamp(0));
                humidityVOS[j].setName("HumidityName"+(j+1));
                humidityVOS[j].setBaseVoltage(0);
                humidityVOS[j].setOverVoltage(0);
                humidityVOS[j].setLight(false);
                humidityVOS[j].setId(humidityDB.insert(humidityVOS[j]));
                Log.d("XXXXXX Id"," sharedPreferences: "+ humidityVOS[j].getId());
                sharedPreferences.edit().putLong("HumidityVO"+j,humidityVOS[j].getId()).apply();
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
        int count=0;
        List<String> wet=new ArrayList<>();
        for(int i=0;i<4;i++)
        {
            long id=sharedPreferences.getLong("HumidityVO"+i,0);
            Log.d("XXXXXX"," sharedPreferences: "+ id);
            humidityVOS[i]=humidityDB.getById(id);
            if(humidityVOS[i].isLight())
            {
                count++;
                wet.add("Channel "+(i+1));
            }
        }

        if(showAlert&&(!wet.isEmpty()))
        {
            Log.d("count  wet","show "+ wet);
            new CustomDialogFragment(activity).Channel(wet).show();
            showAlert=false;
        }

        if(count>=1)
        {
            setTextViewBlue(overTime1);
            overTime1.setText(sharedPreferences.getString(Common.HumidityOneThread,"00:00"));

        }

        if(count>=2)
        {
            setTextViewBlue(overTime2);
            overTime2.setText(sharedPreferences.getString(Common.HumidityTwoThread,"00:00"));
        }

        if(count>=3)
        {
            setTextViewBlue(overTime3);
            overTime3.setText(sharedPreferences.getString(Common.HumidityThreeThread,"00:00"));
        }

        if(count>=4)
        {
            setTextViewBlue(overTime4);
            overTime4.setText(sharedPreferences.getString(Common.HumidityFourThread,"00:00"));
        }

        switch (count)
        {
            case 1:
                Common.setImageAnimation(oneWater);
                Common.clearImageAnimation(twoWater);
                Common.clearImageAnimation(threeWater);
                Common.clearImageAnimation(fourWater);
                break;
            case 2:
                Common.setImageAnimation(oneWater);
                Common.setImageAnimation(twoWater);
                Common.clearImageAnimation(threeWater);
                Common.clearImageAnimation(fourWater);
                break;
            case 3:
                Common.setImageAnimation(oneWater);
                Common.setImageAnimation(twoWater);
                Common.setImageAnimation(threeWater);
                Common.clearImageAnimation(fourWater);
                break;
            case 4:
                Common.setImageAnimation(oneWater);
                Common.setImageAnimation(twoWater);
                Common.setImageAnimation(threeWater);
                Common.setImageAnimation(fourWater);
                break;
            default:
                Common.clearImageAnimation(oneWater);
                Common.clearImageAnimation(twoWater);
                Common.clearImageAnimation(threeWater);
                Common.clearImageAnimation(fourWater);
                break;
        }
    }


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
        sharedPreferences.edit().putBoolean(Common.pauseNow,true).apply();
        HumidityMain.start.setImageResource(R.drawable.start_button_up);
        HumidityMain.pause.setImageResource(R.drawable.break_button_up);
        HumidityMain.stop.setImageResource(R.drawable.stop_button_down);
        HumidityMain.measureStatue.setImageResource(R.drawable.stop);

        setTextViewBlack( HumidityMain.showTime);

    }

    public static  void  setTextViewBlack(TextView textView)
    {
        textView.setBackground(HumidityMain.activity.getDrawable(R.drawable.corners_model_gray));
        textView.setTextColor(HumidityMain.activity.getColor(R.color.bootstrap_gray_light));
    }

    public static  void  setTextViewBlue(TextView textView)
    {
        textView.setBackground(HumidityMain.activity.getDrawable(R.drawable.corners_model_blue));
        textView.setTextColor(HumidityMain.activity.getColor(R.color.bootstrap_brand_info));
    }







}

