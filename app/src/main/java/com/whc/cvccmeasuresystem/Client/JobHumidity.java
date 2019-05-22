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
import android.util.Log;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.MainActivity;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Solution;
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

import static com.whc.cvccmeasuresystem.Common.Common.arrayColor;
import static com.whc.cvccmeasuresystem.Common.Common.endMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.endModule;
import static com.whc.cvccmeasuresystem.Common.Common.finalFragment;
import static com.whc.cvccmeasuresystem.Common.Common.indicateColor;
import static com.whc.cvccmeasuresystem.Common.Common.measureTimes;
import static com.whc.cvccmeasuresystem.Common.Common.oneColor;
import static com.whc.cvccmeasuresystem.Common.Common.sample1;
import static com.whc.cvccmeasuresystem.Common.Common.sample2;
import static com.whc.cvccmeasuresystem.Common.Common.sample3;
import static com.whc.cvccmeasuresystem.Common.Common.sample4;
import static com.whc.cvccmeasuresystem.Common.Common.startMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.userShare;

/**
 * Created by Wang on 2018/12/22.
 */

public class JobHumidity extends android.app.job.JobService{

    private NotificationManager notificationManager;
    public static final String ServerIP = "192.168.4.1"; //your computer IP address
    public static final int ServerPort = 23;
    public static String measureDuration;
    public static String measureTime;
    public static Handler handlerMessage;
    public static boolean mRun;
    public static String measureType;
    public static String measureFragment;
    public static boolean onPause;

    public static Socket socket;
    public static boolean firstMeasure;
    public static int[] firstVoltage=new int[4];

    public  int[] nowVoltage=new int[4];
    public int differV,overCounts;

    private InputStream in;
    private PrintWriter out;
    private SharedPreferences sharedPreferences;
    public static int endMin,endHour,showHour,showMin,showSecond;
    public static long endTime,nowTime;





    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        sharedPreferences = this.getSharedPreferences(userShare, Context.MODE_PRIVATE);

        firstMeasure=true;
        new Thread(measureRun).start();
        new Thread(timeRun).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mRun=false;
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



    public Runnable timeRun =new Runnable() {
        @Override
        public void run() {

               while (mRun)
               {

                   nowTime=System.currentTimeMillis();
                   Log.d("voltage nowTime", String.valueOf(nowTime));
                   Log.d("voltage endTime", String.valueOf(endTime));
                   if(endTime<nowTime) {
                       mRun=false;
                       startMeasure=false;
                       sharedPreferences.edit().putBoolean(endMeasure,true).apply();
                       sendEndMessage();
                       handlerMessage.sendEmptyMessage(2);
                       if(onPause)
                       {
                           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                               showNotification1("Measurement has been completed!");
                           }else{
                               showNotification2("Measurement has been completed!");
                           }
                       }


                       break;
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
                       handlerMessage.sendEmptyMessage(3);

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                   }
               }

        }
    };

    public Runnable measureRun =new Runnable() {
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

                        if(in==null)
                        {
                            mRun=false;
                            startMeasure=false;
                            break;
                        }

                        long startTime = System.currentTimeMillis();
                        byte[] bytes = readStream(in);
                        str = new String(bytes, Charset.forName("UTF-8"));
                        switch (str)
                        {
                            case "$D,Start,#":
                                startMeasure=true;
                                sharedPreferences.edit().putBoolean(endMeasure,false).apply();
                                break;
                            default:

                                String[] voltages = str.split(",");
                                try {
                                    new Integer(voltages[2]);
                                } catch (Exception e) {
                                    continue;
                                }

                                for(int k=1;k<=4;k++)
                                {
                                    nowVoltage[k-1]=new Integer(voltages[k]);
                                }

                                if(firstMeasure)
                                {
                                    for(int k=1;k<=4;k++)
                                    {
                                        firstVoltage[k-1]=new Integer(voltages[k]);
                                    }
                                    firstMeasure=false;
                                }

                                overCounts=0;
                                for(int k=0;k<=3;k++)
                                {

                                    differV= nowVoltage[k]-firstVoltage[k];
                                    differV=Math.abs(differV);
                                    if(differV>0)
                                    {
                                        overCounts++;
                                    }
                                    Log.d("voltage",String.valueOf(k)+". old : "+firstVoltage[k]+" now :" +nowVoltage[k]+" differ :"+differV);
                                }

                                //show error
                                if(overCounts>=3)
                                {
                                    handlerMessage.sendEmptyMessage(1);
                                    if(onPause)
                                    {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            showNotification1("Very Wet!");
                                        }else{
                                            showNotification2("Very Wet!");
                                        }
                                    }

                                }
                        }

                        Log.d("voltage", str + " Time :" + (System.currentTimeMillis() - startTime));
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
            if(socket.isClosed())
            {
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
}
