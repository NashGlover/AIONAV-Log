package com.nashglover.myapplication.app;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.ServerSocket;

import android.os.Handler;
import android.os.Bundle;
import android.os.Message;


/**
 * Created by nash on 7/2/14.
 */
public class NetworkConnection implements Connection {

    private ServerSocket listener;
    private Socket inSocket;
    private int port;
    private Handler mainHandler;

    NetworkConnection(int _port, Handler _handler) {
        port = _port;
        mainHandler = _handler;
    }

    public void connect() {
        System.out.println("In outside of connect");
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    System.out.println("In connect.");
                    listener = new ServerSocket(2222);
                    inSocket = new Socket();
                    //anotherSocket.setSoTimeout(10000);
                    System.out.println("Connecting...");
                    inSocket = listener.accept();
                    Message msg = mainHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "Connected");
                    msg.setData(bundle);
                    mainHandler.sendMessage(msg);

                } catch (IOException e) {
                    System.out.println("Couldn't connect: " + e.getMessage());
                }
            }
        };
        Thread connectThread = new Thread(runnable);
        connectThread.start();
    }

    public void disconnect() {
        System.out.println("Disconnecting...");
        try {
            listener.close();
            System.out.println("Listener closed.");
            inSocket.shutdownInput();
            inSocket.shutdownOutput();
            inSocket.close();
            Message msg = mainHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("type", "Disconnected");
            msg.setData(bundle);
            mainHandler.sendMessage(msg);

        } catch (IOException e) {
            System.out.println("Disconnection error: " + e.getMessage());
        }
    }
    public Socket getSocket() {
        return inSocket;
    }

    public Boolean isConnected() {
        return inSocket.isConnected();
    }
}
