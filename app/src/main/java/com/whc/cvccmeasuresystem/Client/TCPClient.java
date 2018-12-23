package com.whc.cvccmeasuresystem.Client;


import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import android.os.Handler;




import static com.whc.cvccmeasuresystem.Common.Common.*;


public class TCPClient {


    public static final String SERVERIP = "192.168.4.1"; //your computer IP address
    public static final int SERVERPORT = 23;
    public static  boolean mRun = false;

    private String measureDuration;
    private String measureTime;
    private Handler handlerMessage;
    private Object object;
    public Socket socket;
    private InputStream in;

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