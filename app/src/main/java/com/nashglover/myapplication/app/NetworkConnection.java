package com.nashglover.myapplication.app;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.ServerSocket;

/**
 * Created by nash on 7/2/14.
 */
public class NetworkConnection {

    private ServerSocket listener;
    private Socket inSocket;
    private String address;
    private int port;

    NetworkConnection(String _address, int _port) {
        address = _address;
        port = _port;
    }

    public void connect() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    listener = new ServerSocket(2222);
                    inSocket = new Socket();
                    //anotherSocket.setSoTimeout(10000);
                    inSocket = listener.accept();
                } catch (IOException e) {
                    System.out.println("Couldn't connect: " + e.getMessage());
                }
            }
        };
    }

    public Boolean isConnected() {
        return inSocket.isConnected();
    }
}
