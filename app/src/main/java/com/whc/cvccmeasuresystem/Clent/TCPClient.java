package com.whc.cvccmeasuresystem.Clent;


import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import android.os.Handler;

import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Chart;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Main;
import com.whc.cvccmeasuresystem.Control.Batch.BatchStep2Set;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep1;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Chart;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Data;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Main;
import com.whc.cvccmeasuresystem.Control.Dift.DriftStep2Set;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Chart;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Data;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Main;
import com.whc.cvccmeasuresystem.Control.Hysteresis.HysteresisStep2Set;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2ConChart;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Main;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2Set;
import com.whc.cvccmeasuresystem.Control.Sensitivity.SensitivityStep2TimeChart;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Data;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Main;
import com.whc.cvccmeasuresystem.Control.ionChannel.IonChannelStep2Set;
import com.whc.cvccmeasuresystem.R;


import static com.whc.cvccmeasuresystem.Common.Common.*;


public class TCPClient {


    public static final String SERVERIP = "192.168.4.1"; //your computer IP address
    public static final int SERVERPORT = 23;
    private boolean mRun = false;

    private String measureDuration;
    private String measureTime;
    private Handler handlerMessage;
    private Object object;
    private Socket socket;

   private PrintWriter out;


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
        }
    }

    public void cancelTcpClient()
    {
        mRun=false;
        startMeasure=false;
        if(object instanceof BatchStep2Set)
        {
            BatchStop();
        }else if(object instanceof SensitivityStep2Set)
        {
            SensitivityEnd();
        }else if(object instanceof IonChannelStep2Set)
        {

        }else if(object instanceof DriftStep2Set)
        {
            DriftEnd();
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

                InputStream inTest = socket.getInputStream();
                //in this while the client listens for the messages sent by the server
                int i=0;
                int times=Integer.valueOf(measureTime)/Integer.valueOf(measureDuration);
                String str;
                Message message;
                while (mRun) {

                    if(!socket.isConnected())
                    {
                        return;
                    }

                    long startTime = System.currentTimeMillis();
                    byte[] bytes = readStream(inTest);
                    str = new String(bytes, Charset.forName("UTF-8"));
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
                            }else if(object instanceof HysteresisStep2Set)
                            {
                                HysteresisStart();
                            }else if(object instanceof DriftStep2Set)
                            {
                                DriftStart();
                            }else if(object instanceof IonChannelStep2Set)
                            {
                                IonChannelStep2Start();
                            }
                            break;
                        case "$D,End,#":

                            mRun=false;
                            startMeasure=false;
                            if(object instanceof BatchStep2Set)
                            {
                                BatchStop();
                            }else if(object instanceof SensitivityStep2Set)
                            {
                                SensitivityEnd();
                            }else if(object instanceof HysteresisStep2Set)
                            {
                                 HysteresisEnd();
                            }else if(object instanceof DriftStep2Set)
                            {
                                DriftEnd();
                            }else if(object instanceof IonChannelStep2Set)
                            {
                                IonChannelStep2Stop();
                            }
                            break;

                        default:
                            if(i>times)
                            {
                                mRun=false;
                                startMeasure=false;
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


    public void SensitivityStart()
    {
        Fragment fragment= SensitivityStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof  SensitivityStep2TimeChart)
        {
            SensitivityStep2TimeChart.message.setText(R.string.measure_start);
            SensitivityStep2TimeChart.message.setTextColor(Color.BLUE);
        }else if(fragment instanceof  SensitivityStep2ConChart){
            SensitivityStep2ConChart.message.setText(R.string.measure_start);
            SensitivityStep2ConChart.message.setTextColor(Color.BLUE);
        }
    }

    public void SensitivityEnd()
    {
        sendEndMessage();
        indicateColor++;
        mRun=false;
        startMeasure=false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Fragment fragment= SensitivityStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof  SensitivityStep2TimeChart)
        {
            SensitivityStep2TimeChart.message.setText(R.string.measure_stop);
            SensitivityStep2TimeChart.message.setTextColor(Color.RED);
        }else if(fragment instanceof  SensitivityStep2ConChart){
            SensitivityStep2ConChart.message.setText(R.string.measure_stop);
            SensitivityStep2ConChart.message.setTextColor(Color.RED);
        }
        handlerMessage.sendEmptyMessage(2);

    }

    public  void IonChannelStep2Start()
    {
        Fragment fragment= IonChannelStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof IonChannelStep2Data)
        {
            IonChannelStep2Data ionChannelStep2Data= (IonChannelStep2Data) fragment;
            ionChannelStep2Data.senMessage.setTextColor(Color.RED);
            ionChannelStep2Data.senMessage.setText(R.string.measure_stop);
        }
    }

    public void IonChannelStep2Stop()
    {
        sendEndMessage();
        mRun=false;
        startMeasure=false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Fragment fragment= IonChannelStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof IonChannelStep2Data)
        {
            IonChannelStep2Data ionChannelStep2Data= (IonChannelStep2Data) fragment;
            ionChannelStep2Data.senMessage.setTextColor(Color.RED);
            ionChannelStep2Data.senMessage.setText(R.string.measure_stop);
        }
        handlerMessage.sendEmptyMessage(2);
    }

    public  void BatchStart()
    {
        Fragment fragment= BatchStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof BatchStep2Chart)
        {
            BatchStep2Chart.message.setText(R.string.measure_start);
            BatchStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public void BatchStop()
    {
        sendEndMessage();
        indicateColor++;
        mRun=false;
        startMeasure=false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Fragment fragment= BatchStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof BatchStep2Chart)
        {
            BatchStep2Chart.message.setText(R.string.measure_stop);
            BatchStep2Chart.message.setTextColor(Color.RED);
        }
        handlerMessage.sendEmptyMessage(2);
    }


    public void HysteresisStart()
    {
        Fragment fragment= HysteresisStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof HysteresisStep2Chart){
            HysteresisStep2Chart.message.setText(R.string.measure_start);
            HysteresisStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public void HysteresisEnd()
    {
        sendEndMessage();
        indicateColor++;
        mRun=false;
        startMeasure=false;
        Fragment fragment= HysteresisStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof HysteresisStep2Chart){
            HysteresisStep2Chart.message.setText(R.string.measure_stop);
            HysteresisStep2Chart.message.setTextColor(Color.RED);
        }
    }


    public void DriftStart()
    {
        Fragment fragment= DriftStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof DriftStep2Chart)
        {
            DriftStep2Chart.message.setText(R.string.measure_start);
            DriftStep2Chart.message.setTextColor(Color.BLUE);
        }
    }

    public void DriftEnd()
    {

        sendEndMessage();
        indicateColor++;
        mRun=false;
        startMeasure=false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Fragment fragment= DriftStep2Main.adapter.getPage(currentPage);
        if(fragment instanceof DriftStep2Chart)
        {
            DriftStep2Chart.message.setText(R.string.measure_stop);
            DriftStep2Chart.message.setTextColor(Color.RED);
        }
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