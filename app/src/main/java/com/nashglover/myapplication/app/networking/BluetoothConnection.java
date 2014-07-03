package com.nashglover.myapplication.app.networking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.nashglover.myapplication.app.networking.Connection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/*
    Class created to handle Bluetooth connections.
*/
public class BluetoothConnection implements Connection {

    ArrayList<BluetoothAdapter> adapterArray;
    private BluetoothAdapter adapter = null;
    private BluetoothDevice clientDevice = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothConnection() {
    }

    public void connect() {
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

                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        System.out.println(device.getName());
                        if (device.getName().equals("IMAGING078")) {
                            System.out.println("Setting the device up.");
                            clientDevice = device;
                            break;
                        }
                    }
                }
                startClient();
            }
        };
        Thread connectThread = new Thread(runnable);
        connectThread.start();
    }

    public void startClient() {
        try {
            btSocket = clientDevice.createRfcommSocketToServiceRecord(MY_UUID);
            System.out.println("Created the socket worked.");
        } catch (IOException e) {
            System.out.println("From starting client: " + e.getMessage());
        }

        try {
            btSocket.connect();
            System.out.println("Connection established and data link opened...");
        } catch (IOException e) {
            System.out.println("Creating socket: " + e.getMessage());
        }
        sendToServer();
    }

    public void sendToServer() {
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error with output stream: " + e.getMessage());
        }
    }


    public void disconnect() {

    }

    public Boolean isConnected(){

        return true;
    }

}
