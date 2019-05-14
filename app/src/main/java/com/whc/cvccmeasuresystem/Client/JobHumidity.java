package com.whc.cvccmeasuresystem.Client;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Solution;

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

    public static final String ServerIP = "192.168.4.1"; //your computer IP address
    public static final int ServerPort = 23;
    public static String measureDuration;
    public static String measureTime;
    public static Handler handlerMessage;
    public static boolean mRun;
    public static String measureType;
    public static String measureFragment;

    public static Socket socket;
    public static boolean firstMeasure;
    public static int[] firstVoltage=new int[4];

    public  int[] nowVoltage=new int[4];
    public int differV,overCounts;

    private InputStream in;
    private PrintWriter out;
    private SharedPreferences sharedPreferences;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        sharedPreferences = this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        firstMeasure=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
              JobHumidity.this.run();
            }
        }).start();
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
                int i=0;
                int times=Integer.valueOf(measureTime)/Integer.valueOf(measureDuration);
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
                            sharedPreferences.edit().putBoolean(endModule,false).apply();
                            sharedPreferences.edit().putString(finalFragment,measureFragment).apply();
                            sharedPreferences.edit().putBoolean(endMeasure,false).apply();
                            break;
                        case "$D,End,#":
                            sharedPreferences.edit().putBoolean(endModule,true).apply();
                            sharedPreferences.edit().putBoolean(endMeasure,true).apply();

                            break;

                        default:
                            if(i>times)
                            {
                                mRun=false;
                                startMeasure=false;

                            }




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
                                break;
                            }
                    }
                    Log.d("voltage", str + " Time :" + (System.currentTimeMillis() - startTime));
                }
                sendEndMessage();
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
            Common.showToast(this, "Connecting fail! \n Please Reboot WiFi and \n Pressure \"Start\" again!");
        }

    }

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

}
