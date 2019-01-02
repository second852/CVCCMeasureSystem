package com.whc.cvccmeasuresystem.Client;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Set;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.SaveFile;
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

import static com.whc.cvccmeasuresystem.Common.Common.Drift2Set;
import static com.whc.cvccmeasuresystem.Common.Common.arrayColor;
import static com.whc.cvccmeasuresystem.Common.Common.endMeasure;
import static com.whc.cvccmeasuresystem.Common.Common.endModule;
import static com.whc.cvccmeasuresystem.Common.Common.finalFragment;
import static com.whc.cvccmeasuresystem.Common.Common.indicateColor;
import static com.whc.cvccmeasuresystem.Common.Common.measureTimes;
import static com.whc.cvccmeasuresystem.Common.Common.onPause;
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

public class JobService extends android.app.job.JobService{

    public static final String ServerIP = "192.168.4.1"; //your computer IP address
    public static final int ServerPort = 23;
    public static String measureDuration;
    public static String measureTime;
    public static Handler handlerMessage;
    public static boolean mRun;
    public static String measureType;
    public static String measureFragment;

    public static Socket socket;
    private InputStream in;
    private PrintWriter out;
    private SharedPreferences sharedPreferences;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        sharedPreferences = this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
              JobService.this.run();
            }
        }).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
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
            InetAddress serverAddr = InetAddress.getByName(ServerIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, ServerPort);
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
                            handlerMessage.sendEmptyMessage(1);
                            break;
                        case "$D,End,#":

                            handlerMessage.sendEmptyMessage(2);
                            sendEndMessage();
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


                            Solution solution1 = new Solution(Common.solution1.getConcentration(), sample1.getID());
                            Solution solution2 = new Solution(Common.solution2.getConcentration(), sample2.getID());
                            Solution solution3 = new Solution(Common.solution3.getConcentration(), sample3.getID());
                            Solution solution4 = new Solution(Common.solution4.getConcentration(), sample4.getID());


                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                            solution1.setVoltage(new Integer(voltages[1]));
                            solution2.setVoltage(new Integer(voltages[2]));
                            solution3.setVoltage(new Integer(voltages[3]));
                            solution4.setVoltage(new Integer(voltages[4]));


                            solution1.setTime(timestamp);
                            solution2.setTime(timestamp);
                            solution3.setTime(timestamp);
                            solution4.setTime(timestamp);


                            solution1.setMeasureType(measureType);
                            solution2.setMeasureType(measureType);
                            solution3.setMeasureType(measureType);
                            solution4.setMeasureType(measureType);

                            solution1.setNumber(String.valueOf(measureTimes));
                            solution2.setNumber(String.valueOf(measureTimes));
                            solution3.setNumber(String.valueOf(measureTimes));
                            solution4.setNumber(String.valueOf(measureTimes));
                            measureTimes++;

                            oneColor = Color.parseColor(arrayColor[indicateColor%arrayColor.length]);
                            solution1.setColor(oneColor);
                            solution2.setColor(oneColor);
                            solution3.setColor(oneColor);
                            solution4.setColor(oneColor);


                            SolutionDB solutionDB=new SolutionDB(new DataBase(this));
                            solutionDB.insert(solution1);
                            solutionDB.insert(solution2);
                            solutionDB.insert(solution3);
                            solutionDB.insert(solution4);

                            Common.solution1=solution1;
                            Common.solution2=solution1;
                            Common.solution3=solution1;
                            Common.solution4=solution1;


                            handlerMessage.sendEmptyMessage(3);
                            i++;
                            break;
                    }
                    Log.d("XXXXXxx", str + " Time :" + (System.currentTimeMillis() - startTime));
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
            handlerMessage.sendEmptyMessage(4);
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
