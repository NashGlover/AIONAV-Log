package com.nashglover.myapplication.app.networking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/*
    Class created to handle Bluetooth connections.
*/
public class BluetoothConnection implements Connection {

    public BluetoothLogging logThread;
    private Thread loggingThread;
    ArrayList<BluetoothAdapter> adapterArray;
    private BluetoothAdapter adapter = null;
    private BluetoothDevice serverDevice = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    boolean flag = false;

    Handler mainHandler = null;

    private static final UUID MY_UUID = UUID.fromString("00000001-0000-1000-8000-00805F9B34FB");

    public BluetoothConnection(Handler _mainHandler) {
        mainHandler = _mainHandler;
    }

    /* Connect to Bluetooth Server */
    public void connect() {
        System.out.println("In bluetooth connect()");
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println("The Bluetooth Thread: " + Thread.currentThread().getName());
                System.out.println("Connecting...");
                adapter = BluetoothAdapter.getDefaultAdapter();

                if (adapter != null) {
                    System.out.println("Success!");
                } else {
                    System.out.println("Failure!");
                }
                if (adapter.isEnabled()) {
                    System.out.println("It's enabled!");
                }

                System.out.println("Device: " + serverDevice.getName());
                connectToServer();
            }
        };
        Thread connectThread = new Thread(runnable);
        connectThread.start();
    }

    public void connectToServer() {
        try {
            btSocket = serverDevice.createRfcommSocketToServiceRecord(MY_UUID);
            System.out.println("Created the socket worked.");
            System.out.println("Connecting...");
            btSocket.connect();
            System.out.println("About to send...");
            Message msg = mainHandler.obtainMessage();
            System.out.println("Getting message...");
            Bundle bundle = new Bundle();
            bundle.putString("type", "Connected");
            bundle.putString("connection", "Bluetooth");
            msg.setData(bundle);
            mainHandler.sendMessage(msg);
            System.out.println("Connection established and data link opened...");
            sendToServer();
        } catch (IOException e) {
            System.out.println("From starting client: " + e.getMessage());
            Message msg = mainHandler.obtainMessage();
            System.out.println("Getting message...");
            Bundle bundle = new Bundle();
            bundle.putString("type", "Connection Failed");
            bundle.putString("connection", "Bluetooth");
            msg.setData(bundle);
            mainHandler.sendMessage(msg);
        }
    }

    public void setDevice (BluetoothDevice _device) {
        serverDevice = _device;
        System.out.println("Device name: " + serverDevice.getName());
        System.out.println("Address: " + serverDevice.getAddress().toString());
    }

    public void sendToServer() {
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error with output stream: " + e.getMessage());
        }
    }

    public void startLogging() {
        logThread = new BluetoothLogging(this, this.btSocket, mainHandler);
        loggingThread = new Thread(logThread);
        loggingThread.start();
    }

    public void stopLogging() {
        if (logThread.isLogging()) {
            logThread.stopLogging();
        }
    }

    public void disconnect() {
        stopLogging();
        Runnable disconnect = new Runnable() {
            public void run() {
                try{
                    loggingThread.join();
                    btSocket.close();
               } catch (IOException e) {
                    System.out.println("Error closing Bluetooth socket: " + e.getMessage());
               } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
               }
            }
        };
        Thread disconnectThread = new Thread(disconnect);
    }

    public BluetoothSocket getSocket() {
        return btSocket;
    }
    public Boolean isConnected(){
        return true;
    }

}
