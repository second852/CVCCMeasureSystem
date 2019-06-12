package com.whc.cvccmeasuresystem.Client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Common.StopDialogFragment;
import com.whc.cvccmeasuresystem.Common.TimeRunnable;
import com.whc.cvccmeasuresystem.Control.Humidity.HumidityMain;
import com.whc.cvccmeasuresystem.Control.MainActivity;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.HumidityDB;
import com.whc.cvccmeasuresystem.DataBase.HumidityVoltageDB;
import com.whc.cvccmeasuresystem.Model.HumidityVO;
import com.whc.cvccmeasuresystem.Model.HumidityVoltage;
import com.whc.cvccmeasuresystem.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;




import static com.whc.cvccmeasuresystem.Common.Common.endMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;

/**
 * Created by Wang on 2018/12/22.
 */

public class JobHumidity extends android.app.job.JobService {

    private NotificationManager notificationManager;
    public static final String ServerIP = "192.168.4.1"; //your computer IP address
    public static final int ServerPort = 23;
    public String measureDuration;
    public String measureTime;
    public static Handler handlerMessage;
    public static boolean mRun;
    public static String measureType;
    public static String measureFragment;
    public static boolean onPause;

    public static Socket socket;
    public static boolean firstMeasure;
    public static int[] firstVoltage = new int[4];

    public int[] nowVoltage = new int[4];
    public int differV;
    public int overV;


    private InputStream in;
    private PrintWriter out;
    private SharedPreferences sharedPreferences;
    private long nowTime, differ;
    private boolean showError;
    public static boolean setTime;
    public HumidityDB humidityDB;
    public HumidityVoltageDB humidityVoltageDB;
    public List<HumidityVO> overHumidityVOs;
    public List<String> overName;
    public int measureCount;
    public static Thread oneWater, twoWater, threeWater, fourWater;
    public static TimeRunnable timeRunnable1, timeRunnable2, timeRunnable3, timeRunnable4;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        humidityDB = new HumidityDB(new DataBase(this));
        humidityVoltageDB=new HumidityVoltageDB(new DataBase(this));
        sharedPreferences = this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        firstMeasure = true;
        measureDuration = "1";
        measureTime = "10000";
        measureCount = 0;
        new Thread(measureRun).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mRun = false;
        sendEndMessage();
        return false;
    }


    public void sendStartMessage() {
        if (out != null && !out.checkError()) {
            out.print("D" + ',' + measureTime + ',' + measureDuration + ',');
            out.flush();
        }
    }

    public void sendEndMessage() {
        if (out != null && !out.checkError()) {
            out.print("S" + ',' + measureTime + ',' + measureDuration + ',');
            out.flush();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Runnable timeRun = new Runnable() {
        @Override
        public void run() {

            while (mRun) {

                nowTime = System.currentTimeMillis();

                differ = nowTime - HumidityMain.startTime;

                if (differ % 1000 == 0) {

                    HumidityMain.second = HumidityMain.second + 1;

                    if (HumidityMain.second >= 60) {
                        HumidityMain.second = 0;
                        HumidityMain.min = HumidityMain.min + 1;
                    }

                    if (HumidityMain.min >= 60) {
                        HumidityMain.min = 0;
                        HumidityMain.hour = HumidityMain.hour + 1;
                    }

                    handlerMessage.sendEmptyMessage(1);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };

    public Runnable measureRun = new Runnable() {
        @Override
        public void run() {
            try {
                //here you must put your computer's IP address.
                InetAddress serverAddress = InetAddress.getByName(ServerIP);

                Log.e("TCP Client", "C: Connecting...");

                //create a socket to make the connection with the server
                socket = new Socket(serverAddress, ServerPort);
                socket.setKeepAlive(true);
                try {

                    //send the message to the server
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    sendStartMessage();
                    Log.e("TCP Client", "C: Sent.");
                    Log.e("TCP Client", "C: Done.");

                    in = socket.getInputStream();
                    //in this while the client listens for the messages sent by the server
                    String str;
                    while (mRun) {
//
                        if (measureCount >= 60) {
                            socket.close();
                            socket = null;
                            Thread.sleep(1000);
                            socket = new Socket(serverAddress, ServerPort);
                            socket.setKeepAlive(true);
                            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                            sendStartMessage();
                            in = socket.getInputStream();
                            measureCount = 0;
                        }

                        long startTime = System.currentTimeMillis();
                        byte[] bytes = readStream(in);
                        str = new String(bytes, Charset.forName("UTF-8"));
                        Log.d("voltage_str nowVoltage", String.valueOf(str));
                        switch (str) {
                            case "$D,Start,#":

                                //----------------Time ---------------------------//
                                if (setTime) {
                                    HumidityMain.startTime = System.currentTimeMillis();
                                    setTime = false;
                                }
                                startMeasure = true;
                                new Thread(timeRun).start();
                                //-------------------------------------------------//
                                sharedPreferences.edit().putBoolean(endMeasure, false).apply();
                                handlerMessage.sendEmptyMessage(3);
                                HumidityMain.showAlert=true;

                                break;

                            default:
                                String[] voltages = str.split(",");
                                try {
                                    new Integer(voltages[2]);
                                } catch (Exception e) {
                                    continue;
                                }



                                for (int k = 1; k <= 4; k++) {
                                    nowVoltage[k - 1] = new Integer(voltages[k]);

                                }

                                int fileId=sharedPreferences.getInt(Common.fileId,0);
                                HumidityVoltage humidityVoltage=new HumidityVoltage();
                                humidityVoltage.setVoltage1(nowVoltage[0]);
                                humidityVoltage.setVoltage2(nowVoltage[1]);
                                humidityVoltage.setVoltage3(nowVoltage[2]);
                                humidityVoltage.setVoltage4(nowVoltage[3]);
                                humidityVoltage.setFileId(fileId);
                                humidityVoltageDB.insert(humidityVoltage);

                                if (firstMeasure) {
                                    for (int k = 1; k <= 4; k++) {

                                        firstVoltage[k - 1] = new Integer(voltages[k]);
                                        HumidityMain.humidityVOS[k - 1].setBaseVoltage(new Integer(voltages[k]));
                                        humidityDB.update(HumidityMain.humidityVOS[k - 1]);
                                    }
                                    firstMeasure = false;


                                }
                                overName=new ArrayList<>();
                                overHumidityVOs = new ArrayList<>();
                                showError = false;
                                for (int k = 0; k <= 3; k++) {


                                    overV = new Integer(sharedPreferences.getString(Common.settingsVoltage, "0"));
                                    differV = firstVoltage[k]-nowVoltage[k] ;
                                    if (differV > overV) {
                                        showError = true;

                                        HumidityMain.humidityVOS[k].setOverVoltage(new Integer(nowVoltage[k]));

                                        if(!HumidityMain.humidityVOS[k].isLight())
                                        {
                                            HumidityMain.humidityVOS[k].setLight(true);
                                            HumidityMain.humidityVOS[k].setBeginTime(new Timestamp(System.currentTimeMillis()));
                                        }

                                        humidityDB.update(HumidityMain.humidityVOS[k]);
                                        overHumidityVOs.add(HumidityMain.humidityVOS[k]);
                                        overName.add("Channel " +(k+1));
                                        showError = true;
                                    }
                                    Log.d("voltage", String.valueOf(k) + ". old : " + firstVoltage[k] + " now :" + nowVoltage[k] + " differ :" + differV);
                                }

                                if (!overHumidityVOs.isEmpty()) {
                                    Collections.sort(overHumidityVOs, new Comparator<HumidityVO>() {
                                        @Override
                                        public int compare(HumidityVO humidityVO, HumidityVO t1) {
                                            return humidityVO.getBeginTime().compareTo(t1.getBeginTime());
                                        }
                                    });

                                    int size = overHumidityVOs.size();
                                    if (size >= 1 && oneWater == null) {
                                        timeRunnable1 = new TimeRunnable(0, overHumidityVOs.get(0),sharedPreferences,Common.HumidityOneThread);
                                        oneWater = new Thread(timeRunnable1);
                                        oneWater.start();
                                    }
                                    if (size >= 2 && twoWater == null) {
                                        timeRunnable2 = new TimeRunnable(1, overHumidityVOs.get(1),sharedPreferences,Common.HumidityTwoThread);
                                        twoWater = new Thread(timeRunnable2);
                                        twoWater.start();
                                    }
                                    if (size >= 3 && threeWater == null) {
                                        timeRunnable3 = new TimeRunnable(2, overHumidityVOs.get(2),sharedPreferences,Common.HumidityThreeThread);
                                        threeWater = new Thread(timeRunnable3);
                                        threeWater.start();
                                    }
                                    if (size >= 4 && fourWater == null) {
                                        timeRunnable4 = new TimeRunnable(3, overHumidityVOs.get(3),sharedPreferences,Common.HumidityFourThread);
                                        fourWater = new Thread(timeRunnable4);
                                        fourWater.start();
                                    }


                                }

                                //show error
                                if (showError) {

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        showNotification1("Wet!");
                                    } else {
                                        showNotification2("Wet!");
                                    }
                                    Message message=new Message();
                                    message.what=2;
                                    message.obj=overName;
                                    HumidityMain.showLightHandler.sendMessage(message);
                                }

                        }

                        measureCount++;
                        Log.d("voltage_str", str + " Time :" + (System.currentTimeMillis() - startTime));
                    }
                    Log.e("RESPONSE FROM SERVER", " END");
                } catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                } finally {
                    //the socket must be closed. It is not possible to reconnect to this socket
                    // after it is closed, which means a new socket instance has to be created.
                    socket.close();
                }

            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
                Common.showToast(JobHumidity.this, "Connecting fail! \n Please Reboot WiFi and \n Pressure \"Start\" again!");
            }

        }
    };


    public byte[] readStream(InputStream inStream) throws Exception {
        int count = 0;
        while (count == 0) {
            if (socket.isClosed()) {
                break;
            }
            count = inStream.available();
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }

    //set Channel
    public final String PRIMARY_CHANNEL = "BCS";

    private void showNotification1(String message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    PRIMARY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(chan1);

            Intent activeI = new Intent(this, MainActivity.class);

            PendingIntent appIntent = PendingIntent.getActivity(this, 0, activeI, 0);
            Notification.Builder nb = new Notification.Builder(this.getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle("BCS")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.notify)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(appIntent);
            notificationManager.notify(0, nb.build());
        }
    }


    private void showNotification2(String message) {

        Intent activeI = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                activeI, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("BCS")
                .setContentText(message)
                .setSmallIcon(R.drawable.notify)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    public static void stopErrorTime() {
        TimeRunnable.timeRun = false;
        if (oneWater != null) {
            oneWater.interrupt();
            oneWater = null;
        }
        if (twoWater != null) {
            twoWater.interrupt();
            twoWater = null;
        }
        if (threeWater != null) {
            threeWater.interrupt();
            threeWater = null;
        }
        if (fourWater != null) {
            fourWater.interrupt();
            fourWater = null;
        }
    }

}
