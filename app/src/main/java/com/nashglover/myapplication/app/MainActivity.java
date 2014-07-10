package com.nashglover.myapplication.app;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.os.Handler;
import android.os.Message;

import com.nashglover.myapplication.app.networking.BluetoothConnection;
import com.nashglover.myapplication.app.networking.BluetoothLogging;
import com.nashglover.myapplication.app.networking.NetworkConnection;

import java.io.File;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends ActionBarActivity {

    /* UI Elements */
    TextView logText;
    ScrollView logScroll;

    int flag = 1;

    Button endButton;
    Button saveButton;
    Button startLogButton;
    Button endLogButton;
    Button connectButton;

    AtomicBoolean tracking;
    Boolean connecting;
    ServerSocket listener;
    Socket anotherSocket;
    Boolean bluetooth;

    NetworkConnection network;
    BluetoothConnection bluetoothNetwork;

    LoggingThread logThread = null;
    BluetoothLogging bluetoothLogging = null;

    int count = 0;
    DataInputStream in;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("In handle message");
            Bundle bundle = msg.getData();
            String type = bundle.getString("type");
            if (type.equals("Connected")){
                String networkType = bundle.getString("connection");
                if (networkType.equals("Network")) {
                    logText.append(String.format("Connected%n"));
                    saveButton.setEnabled(true);
                    startLogButton.setEnabled(true);
                    logThread = new LoggingThread(network.getSocket(), handler);
                    (new Thread(logThread)).start();
                }
                else if (networkType.equals("Bluetooth")) {
                    System.out.println("In bluetooth connection");
                    logText.append(String.format("Connected%n"));
                    saveButton.setEnabled(true);
                    bluetoothLogging = new BluetoothLogging(bluetoothNetwork.getSocket(), handler);
                    System.out.println("Created new BluetoothLogging");
                    (new Thread(bluetoothLogging)).start();
                    startLogButton.setEnabled(true);
                }
            }
            else if (type.equals("Disconnected")) {
                addToLog(String.format("Disconnected%n"));
                startLogButton.setEnabled(false);
                endLogButton.setEnabled(false);
                connectButton.setEnabled(true);
                endButton.setEnabled(false);
                count++;
            }
            else if (type.equals("Logging")) {
                startLogButton.setEnabled(false);
                endLogButton.setEnabled(true);
            }
            else if (type.equals("New Coordinate")) {
                addToLog(bundle.getString("coordinate"));
            }
        }
    };

    /* Holds all the coordinates */
    Vector coordinateVector = new Vector(300, 100);

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
        saveButton.setEnabled(true);
        startLogButton.setEnabled(false);
        endLogButton.setEnabled(false);
        logText = (TextView) findViewById(R.id.log_message);
        logScroll = (ScrollView) findViewById(R.id.log_scroll);
        tracking = new AtomicBoolean();
        bluetooth = true;
        System.out.println("FLAG: " + flag);
    }

    @Override
    protected void onStop() {
        super.onStop();
        flag = 2;
        System.out.println("Activity has been stopped!");
    }

    @Override
    protected void onDestroy() {
      //  super.onDestroy();
        flag = 2;
        System.out.println("Activity has been destroyed!");
        super.onDestroy();
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

    public void onRadioButtonClicked (View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_bluetooth:
                if (checked) {
                    bluetooth = true;
                }
                    break;
            case R.id.radio_wifi:
                if (checked) {
                    System.out.println("Wifi chosen!");
                    bluetooth = false;
                }
                    break;
        }

    }

    public void disconnectClick(View view)
    {
        logThread.stopLogging();
        network.disconnect();
    }

    public void stopLogging(View view)
    {
        tracking.set(false);
        runOnUiThread(new Runnable() {
            public void run() {
                logText.append(String.format("Stopped Logging%n"));
                startLogButton.setEnabled(true);
                endLogButton.setEnabled(false);
            }
        });
    }

    public void connectClick(View view) throws InterruptedException
    {
        /* For initial connection to AIONAV application on Windows */
        logText.append(String.format("Connecting to device...%n"));
        System.out.println("About to connect");
        connecting = true;
        if (!bluetooth) {
            connectButton.setEnabled(false);
            network = new NetworkConnection(2222, handler);
            network.connect();
            System.out.println("Testing again!");
        }
        else if (bluetooth) {
            System.out.println("Bluetooth!");
            bluetoothNetwork = new BluetoothConnection(handler);
            bluetoothNetwork.connect();
        }
        (findViewById(R.id.start_button)).setEnabled(false);
        (findViewById(R.id.end_button)).setEnabled(true);
        System.out.println("After the button");
        (findViewById(R.id.radio_bluetooth)).setEnabled(false);
        (findViewById(R.id.radio_wifi)).setEnabled(false);


    }

    public void addToLog(String newMessage)
    {
        final String finalString = newMessage;
        logText.append(finalString);
        logScroll.fullScroll(View.FOCUS_DOWN);
    }


    public void startLogging(View view)
    {
        logThread.startLogging();
    }

}
