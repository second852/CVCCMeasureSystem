package com.whc.cvccmeasuresystem.Client;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import android.os.Handler;


import com.whc.cvccmeasuresystem.Common.Common;
import com.whc.cvccmeasuresystem.DataBase.DataBase;
import com.whc.cvccmeasuresystem.DataBase.SolutionDB;
import com.whc.cvccmeasuresystem.Model.Solution;

import static com.whc.cvccmeasuresystem.Common.Common.*;


public class TCPClient extends JobService {


    public static final String SERVERIP = "192.168.4.1"; //your computer IP address
    public static final int SERVERPORT = 23;
    public boolean mRun = false;

    private String measureDuration;
    private String measureTime;
    private Handler handlerMessage;
    private Object object;
    public Socket socket;
    private InputStream in;

   private PrintWriter out;
   private SharedPreferences sharedPreferences;


    public TCPClient(String measureDuration, String measureTime, Handler handlerMessage, Object object) {
        this.measureDuration = measureDuration;
        this.measureTime = measureTime;
        this.handlerMessage = handlerMessage;
        this.object = object;
        mRun = true;
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



    public void cancelHomeTcpClient()
    {
        startMeasure=false;
        mRun=false;
    }



    public void run() {



        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, SERVERPORT);
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
                Message message;
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
                            finishToSave=false;
                            startMeasure=true;
                            handlerMessage.sendEmptyMessage(1);
                            break;
                        case "$D,End,#":

                            mRun=false;
                            startMeasure=false;
                            handlerMessage.sendEmptyMessage(2);
                            break;

                        default:

                            if(i>times)
                            {
                                mRun=false;
                                startMeasure=false;
                                break;
                            }

                            String[] voltages = str.split(",");
                            try {
                                new Integer(voltages[2]);
                            } catch (Exception e) {
                                break;
                            }


                            if(solution1==null)
                            {
                                sharedPreferences = this.getSharedPreferences(userShare, Context.MODE_PRIVATE);
                               
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


                            solution1.setMeasureType("3");
                            solution2.setMeasureType("3");
                            solution3.setMeasureType("3");
                            solution4.setMeasureType("3");

                            solution1.setNumber(String.valueOf(measureTimes));
                            solution2.setNumber(String.valueOf(measureTimes));
                            solution3.setNumber(String.valueOf(measureTimes));
                            solution4.setNumber(String.valueOf(measureTimes));
                            measureTimes++;

                            int color = Color.parseColor(arrayColor[indicateColor%arrayColor.length]);
                            solution1.setColor(color);
                            solution2.setColor(color);
                            solution3.setColor(color);
                            solution4.setColor(color);
                            choiceColor.add(color);

                            dataMap.get(sample1).add(solution1);
                            dataMap.get(sample2).add(solution2);
                            dataMap.get(sample3).add(solution3);
                            dataMap.get(sample4).add(solution4);


                            SolutionDB solutionDB=new SolutionDB(new DataBase(this));
                            solutionDB.insert(solution1);
                            solutionDB.insert(solution2);
                            solutionDB.insert(solution3);
                            solutionDB.insert(solution4);


                            message=new Message();
                            message.obj=str;
                            message.what=3;
                            handlerMessage.sendMessage(message);
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



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        run();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }
}