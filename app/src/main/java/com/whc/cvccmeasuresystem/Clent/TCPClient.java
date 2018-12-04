package com.whc.cvccmeasuresystem.Clent;
import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TCPClient {

    private String serverMessage;
    public static final String SERVERIP = "192.168.4.1"; //your computer IP address
    public static final int SERVERPORT = 23;
    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;



    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(char message){
        if (out != null && !out.checkError()) {
            out.print("D"+','+"6"+','+"1"+',');
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run(){

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
                sendMessage(',');

                Log.e("TCP Client", "C: Sent.");
                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                InputStream inTest=socket.getInputStream();
                //in this while the client listens for the messages sent by the server
                StringBuilder stringBuilder=new StringBuilder();
                while (mRun) {
                    long startTime=System.currentTimeMillis();
                   byte[] bytes=readStream(inTest);
                   String str = new String(bytes, Charset.forName("UTF-8"));
                   stringBuilder.append(str);
                   if(str.equals("$D,Start,#"))
                   {

                   }else{
                       sendMessage(',');
                   }
                   Log.d("XXXXXxx",str+" Time :"+(System.currentTimeMillis()-startTime));
                }
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

        }

    }


    public  byte[] readStream(InputStream inStream) throws Exception {
        int count = 0;
        while (count == 0) {
            count = inStream.available();
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }


}