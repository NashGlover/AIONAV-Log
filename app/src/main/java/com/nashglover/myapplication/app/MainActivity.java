package com.nashglover.myapplication.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {

    Boolean logging;
    Boolean connecting;
    Socket clientSocket = null;
    DataInputStream in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button endButton = (Button) findViewById(R.id.end_button);
        Button saveButton = (Button) findViewById(R.id.save_button);
        Button startLogButton = (Button) findViewById(R.id.start_log_button);
        Button endLogButton = (Button) findViewById(R.id.end_log_button);
        endButton.setEnabled(false);
        saveButton.setEnabled(false);
        startLogButton.setEnabled(false);
        endLogButton.setEnabled(false);
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

    public void connectClick(View view) throws InterruptedException
    {
        /* For initial connection to AIONAV application on Windows */
        final TextView logText = (TextView) findViewById(R.id.log_message);
        logText.append(String.format("Connecting to device...%n"));
        System.out.println("About to connect");
        connecting = true;
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("TESTING");

                /* While until finding connection */
                while (connecting) {
                    try {
                        int timeout = 3000;
                        int port = 2222;
                        String addr = "localhost";
                        System.out.println("Before the socket!");
                        InetSocketAddress inetAddr = new InetSocketAddress(addr, port);
                        clientSocket.connect(inetAddr, timeout);
                        connecting = false;
                    } catch (UnknownHostException e2) {
                        System.out.println("Unknown host");
                    } catch (Exception e) {
                        try {
                            Thread.sleep(1000);
                        }
                        catch (Exception threadException){

                        }
                        // logText.append(String.format("Still not connected...%n"));
                    }
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
        ((Button) findViewById(R.id.start_button)).setEnabled(false);
        ((Button) findViewById(R.id.end_button)).setEnabled(true);
        ((Button) findViewById(R.id.save_button)).setEnabled(true);
        ((Button) findViewById(R.id.start_log_button)).setEnabled(true);
       // ((Button) findViewById(R.id.end_log_button)).setEnabled(true);

    }
    public void startLogging(View view)
    {
        /*LoggingThread logThread = new LoggingThread();
        logThread.run();*/
        logging = true;

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
                        Thread.sleep(1000);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

}
