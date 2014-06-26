package com.nashglover.myapplication.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class MainActivity extends ActionBarActivity {

    /* UI Elements */
    TextView logText;

    Button endButton;
    Button saveButton;
    Button startLogButton;
    Button endLogButton;
    Button connectButton;

    Boolean logging;
    Boolean connecting;
    ServerSocket listener;
    Socket anotherSocket;

    int count = 0;
    DataInputStream in;

    Thread logThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = (Button) findViewById(R.id.start_button);
        endButton = (Button) findViewById(R.id.end_button);
        saveButton = (Button) findViewById(R.id.save_button);
        startLogButton = (Button) findViewById(R.id.start_log_button);
        endLogButton = (Button) findViewById(R.id.end_log_button);
        endButton.setEnabled(false);
        saveButton.setEnabled(false);
        startLogButton.setEnabled(false);
        endLogButton.setEnabled(false);
        logText = (TextView) findViewById(R.id.log_message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void disconnectClick(View view)
    {
        try {
            listener.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    logText.append(String.format("Disconnected%n"));
                    startLogButton.setEnabled(false);
                    endLogButton.setEnabled(false);
                    connectButton.setEnabled(true);
                    endButton.setEnabled(false);
                    count++;
                    logging = false;
                }
            });
        }
        catch (IOException e)
        {
            e.getMessage();
        }
    }

    public void connectClick(View view) throws InterruptedException
    {
        /* For initial connection to AIONAV application on Windows */
        logText.append(String.format("Connecting to device...%n"));
        System.out.println("About to connect");
        connecting = true;
        connectButton.setEnabled(false);
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("TESTING");

                /* While until finding connection */
                    try {
                        int timeout = 7000;
                        int port = 2222;
                        String addr = "localhost";
                        System.out.println("Before the socket!");
                      //  InetSocketAddress inetAddr = new InetSocketAddress(addr, port);
                        // listener.setSoTimeout(4000);
                        //listener = new ServerSocket(2222);
                        // Socket input = listener.accept();
                        //anotherSocket = new Socket("localhost", 2222);
                        if (count>0 && listener.isClosed())
                        {
                            System.out.println("It's closed!");
                        }
                        listener = new ServerSocket(2222);
                        anotherSocket = new Socket();
                        anotherSocket = listener.accept();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                logText.append(String.format("Connected%n"));
                                (findViewById(R.id.save_button)).setEnabled(true);
                                (findViewById(R.id.start_log_button)).setEnabled(true);
                            }
                        });
                        System.out.println("After the socket");
                        //logText.append(String.format("Connected!%n"));
                        connecting = false;
                    } catch (SocketTimeoutException timeoutException){
                        System.out.println("TIMEOUT!");
                    } catch (UnknownHostException e) {
                        System.out.println("Unknown host");
                        // logText.append(String.format("Still not connected...%n"));
                    } catch (IOException ioException) {
                        System.out.println(ioException.getMessage());
                    }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
        System.out.println("Testing again!");
        /*catch (SocketException e)
        {
            System.out.printf(e.toString() + "%n");
        }*/
        (findViewById(R.id.start_button)).setEnabled(false);
        (findViewById(R.id.end_button)).setEnabled(true);
        System.out.println("After the button");
       // ((Button) findViewById(R.id.end_log_button)).setEnabled(true);

    }

    public void  addToLog(String newMessage)
    {

    }

    public void startLogging(View view)
    {
        System.out.println("Pressed start logging");
        /*LoggingThread logThread = new LoggingThread();
        logThread.run();*/
       /* logging = true;

        Runnable runnable = new Runnable() {
            public void run() {
                while (logging)
                {
                    try {
                        System.out.printf("HERE'S A MESSAGE!%n");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                TextView logText = (TextView) findViewById(R.id.log_message);
                                logText.append(String.format("Stuff%n"));
                            }
                        });
                        Thread.sleep(300);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();*/
        //LoggingThread logThread = new LoggingThread(listener);
        /*Intent i = new Intent(this, LoggingThread.class);
        startService(i);*/

        Runnable runnable = new Runnable() {
            public void run() {
                byte[] messageByte = new byte[1000];
                int length;
                int packetType;
                long device1, device2;
                long timestamp = 0;
                double longitude, latitude, altitude;
                logging = true;
                while (logging) {
                    try {
                        in = new DataInputStream(anotherSocket.getInputStream());
                        int bytesRead;
                        bytesRead = in.read(messageByte);
                        System.out.println("Bytes read: %d\n");
                        ByteBuffer buffer = ByteBuffer.wrap(messageByte);

                        if (bytesRead == 32 || bytesRead == 56) {
                            if (bytesRead == 32) {
                                length = buffer.getInt();
                                packetType = buffer.getInt(4);
                                device1 = buffer.getLong(8);
                                device2 = buffer.getLong(16);
                                timestamp = buffer.getLong(24);

                                final String line = String.format("Length: %d, Packet Type: %d, Device ID: %d %d, Timestamp: %d%n", length, packetType, device1, device2, timestamp);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        logText.append(line);
                                    }
                                });
                                System.out.println("Heartbeat!");
                            }

                            if (bytesRead == 56) {
                                System.out.println("Position Update");


                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Logging: " + e.getMessage());
                    }
                }
            }
        };
        logThread = new Thread(runnable);
        logThread.start();
    }

}
