package com.nashglover.myapplication.app;

/**
 * Created by nash on 6/12/14.
 */
public class LoggingThread extends Thread {

    LoggingThread()
    {

    }

    public void run()
    {
        System.out.printf("Hello from a thread!%n");

    }
}
