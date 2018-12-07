package com.whc.cvccmeasuresystem.Clent;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import android.os.Handler;

import com.whc.cvccmeasuresystem.Control.BatchStep2Chart;
import com.whc.cvccmeasuresystem.Control.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.BatchStep2Set;
import com.whc.cvccmeasuresystem.Control.SensitivityStep2ConChart;
import com.whc.cvccmeasuresystem.Control.SensitivityStep2Main;
import com.whc.cvccmeasuresystem.Control.SensitivityStep2Set;
import com.whc.cvccmeasuresystem.Control.SensitivityStep2TimeChart;
import com.whc.cvccmeasuresystem.R;


import static com.whc.cvccmeasuresystem.Common.Common.*;


public class TCPClient {

    private String serverMessage;
    public static final String SERVERIP = "192.168.4.1"; //your computer IP address
    public static final int SERVERPORT = 23;
    private boolean mRun = false;

    private String measureDuration;
    private String measureTime;
    private Handler handlerMessage;
    private Object object;

   private PrintWriter out;
   private BufferedReader in;


    public TCPClient(String measureDuration, String measureTime, Handler handlerMessage, Object object) {
        this.measureDuration = measureDuration;
        this.measureTime = measureTime;
        this.handlerMessage = handlerMessage;
        this.object = object;
    }

    public void sendMessage() {
        if (out != null && !out.checkError()) {
            out.print("D" + ',' + measureTime + ',' + measureDuration + ',');
            out.flush();
        }
    }

    public void cancelTcpClient()
    {
        mRun=false;
        BatchStop();
    }

    public void cancelHomeTcpClient()
    {
        startMeasure=false;
        mRun=false;
    }



    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
            socket.setKeepAlive(true);
            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                sendMessage();
                Log.e("TCP Client", "C: Sent.");
                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                InputStream inTest = socket.getInputStream();
                //in this while the client listens for the messages sent by the server
                int i=0;
                int times=Integer.valueOf(measureTime)/Integer.valueOf(measureDuration);
                Message message;
                while (mRun) {
                    long startTime = System.currentTimeMillis();
                    byte[] bytes = readStream(inTest);
                    String str = new String(bytes, Charset.forName("UTF-8"));
                    switch (str)
                    {
                        case "$D,Start,#":
                            finishToSave=false;
                            startMeasure=true;
                            handlerMessage.sendEmptyMessage(1);

                            if(object instanceof BatchStep2Set)
                            {
                                BatchStart();
                            }else if(object instanceof SensitivityStep2Set)
                            {
                                SensitivityStart();
                            }

                            break;
                        case "$D,End,#":


                            if(object instanceof BatchStep2Set)
                            {
                                BatchStop();
                            }else if(object instanceof SensitivityStep2Set)
                            {
                                SensitivityEnd();
                            }
                            break;

                        default:
                            if(i>times)
                            {
                                mRun=false;
                            }
                            message=new Message();
                            message.obj=str;
                            message.what=3;
                            handlerMessage.sendMessage(message);
                            i++;
                            break;
                    }
                    Log.d("XXXXXxx", str + " Time :" + (System.currentTimeMillis() - startTime));
                }
                socket.close();
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
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


    public void SensitivityStart()
    {
        if(currentPage==1)
        {
            SensitivityStep2TimeChart.message.setText(R.string.measure_start);
            SensitivityStep2TimeChart.message.setTextColor(Color.BLUE);
        }else if(currentPage==2){
            SensitivityStep2ConChart.message.setText(R.string.measure_start);
            SensitivityStep2ConChart.message.setTextColor(Color.BLUE);
        }
    }

    public void SensitivityEnd()
    {

        if(currentPage==1)
        {
            SensitivityStep2TimeChart.message.setText(R.string.measure_stop);
            SensitivityStep2TimeChart.message.setTextColor(Color.RED);
        }else if(currentPage==2){
            SensitivityStep2ConChart.message.setText(R.string.measure_stop);
            SensitivityStep2ConChart.message.setTextColor(Color.RED);
        }
        startMeasure=false;
        handlerMessage.sendEmptyMessage(2);
    }



    public  void BatchStart()
    {

        if(currentPage==1)
        {
            BatchStep2Chart.message.setText(R.string.measure_start);
            BatchStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public void BatchStop()
    {

        if(currentPage==1)
        {
            BatchStep2Chart.message.setText(R.string.measure_stop);
            BatchStep2Chart.message.setTextColor(Color.RED);
        }
        startMeasure=false;
        handlerMessage.sendEmptyMessage(2);
    }



    public byte[] readStream(InputStream inStream) throws Exception {
        int count = 0;
        while (count == 0) {
            count = inStream.available();
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }


}