package com.nashglover.myapplication.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {

    TextView logText;
    Boolean logging;
    Boolean connecting;
    ServerSocket listener = null;

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
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("TESTING");

                /* While until finding connection */
                    try {
                        int timeout = 3000;
                        int port = 5553;
                        String addr = "localhost";
                        System.out.println("Before the socket!");
                        //InetSocketAddress inetAddr = new InetSocketAddress(addr, port);
                        listener = new ServerSocket(port);
                        listener.accept();
                        System.out.println("After the socket");
                        //logText.append(String.format("Connected!%n"));
                        connecting = false;
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
        (findViewById(R.id.save_button)).setEnabled(true);
        (findViewById(R.id.start_log_button)).setEnabled(true);
       // ((Button) findViewById(R.id.end_log_button)).setEnabled(true);

    }

    public void addToLog(String newMessage)
    {

    }

    public void startLogging(View view)
    {
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
        Intent i = new Intent(this, LoggingThread.class);
        startService(i);
    }

}
