package com.nashglover.myapplication.app;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

/**
 * Created by nash on 6/12/14.
 */
public class LoggingThread extends IntentService {

    MainActivity mainActivity;
    ServerSocket listener;

    Handler logHandler = new Handler() {
        @Override
        public void handleMessage(Message status)
        {

        }
    };

    @Override
    protected void onHandleIntent(Intent intent){
        connectToApp();
    }

    public void connectToApp(){
        /*try {
            //listener = new ServerSocket(2222);
            //listener.setSoTimeout(2000);
            //listener.accept();
        }
        /*catch (SocketTimeoutException timeout) {
            System.out.println("Hello");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
    }

    public LoggingThread(ServerSocket _listener) {
        super("LoggingThread");
        listener = _listener;
    }

    public void run()
    {
        System.out.printf("Hello from a thread!%n");

    }
}
