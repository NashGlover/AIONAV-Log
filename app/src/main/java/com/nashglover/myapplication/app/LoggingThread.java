package com.nashglover.myapplication.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nash on 6/12/14.
 */
public class LoggingThread extends Thread {

    Handler mainHandler;
    ServerSocket listener;
    Socket inSocket;

    Vector coordinateVector = new Vector<Coordinate>(300, 100);

    private DataInputStream in;
    private AtomicBoolean tracking;
    private AtomicBoolean logging;

    public LoggingThread(Socket _inSocket, Handler _handler)
    {
        System.out.println("Creating log thread");
        inSocket = _inSocket;
        mainHandler = _handler;
        logging = new AtomicBoolean(false);
        tracking = new AtomicBoolean(false);
    }

    public void startLogging() {
        logging.set(true);
        if (logging.get()) { System.out.println("Logging is enabled"); }
        Message msg = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("type", "Logging");
        msg.setData(bundle);
        mainHandler.sendMessage(msg);
    }

    public void stopLogging() {
        tracking.set(false);
        logging.set(false);
    }

    private void updateLog(String line) {
        Message msg = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("type", "New Coordinate");
        bundle.putString("coordinate", line);
        msg.setData(bundle);
        mainHandler.sendMessage(msg);
    }

    public void run()
    {
        System.out.printf("Hello from a logging thread!%n");
        byte[] messageByte = new byte[1000];
        int length;
        int packetType;
        long device1, device2;
        long timestamp = 0;
        double longitude, latitude, altitude;
        tracking.set(true);
        double x, y, z;
        try {
            in = new DataInputStream(inSocket.getInputStream());
        } catch (Exception e) {
            System.out.println("Starting data input stream: " + e.getMessage());
        }
        while (tracking.get()) {
            try {
                int bytesRead;
                inSocket.setSoTimeout(10000);
                System.out.println("READING...");
                bytesRead = in.read(messageByte);
                ByteBuffer buffer = ByteBuffer.wrap(messageByte);

                if (bytesRead == 32 || bytesRead == 56) {
                    length = buffer.getInt();
                    packetType = buffer.getInt(4);
                    device1 = buffer.getLong(8);
                    device2 = buffer.getLong(16);
                    if (bytesRead == 32) {
                        timestamp = buffer.getLong(24);
                        // String line = String.format("Length: %d, Packet Type: %d, Device ID: %d %d, Timestamp: %d%n", length, packetType, device1, device2, timestamp);
                        // addToLog(line);
                        System.out.println("Heartbeat!");
                    } else if (bytesRead == 56) {
                        System.out.println("Location updated!");
                        System.out.println("Packet type: " + packetType);
                        if (packetType == 1) {
                            if (logging.get()) {
                                System.out.println("Logging is enabled!");
                                timestamp = buffer.getLong(24);
                                x = buffer.getDouble(32);
                                y = buffer.getDouble(40);
                                z = buffer.getDouble(48);

                                x = Math.round(x * 1000.00) / 1000.00;
                                y = Math.round(y * 1000.00) / 1000.00;
                                z = Math.round(z * 1000.00) / 1000.00;

                                Coordinate currentCoordinate = new Coordinate(timestamp, x, y, z);
                                coordinateVector.add(currentCoordinate);
                                System.out.println("Coordinate vector size: " + coordinateVector.size());
                                System.out.println("Position Update");
                                String line = String.format("Timestamp: %d, x: %.3f, y: %.3f, z: %.3f%n", timestamp, x, y, z);
                                updateLog(line);
                            }
                        }
                    }
                }
            } catch (SocketTimeoutException timeoutException) {
                System.out.println("Socket timed out!");
            } catch (IOException e) {
                System.out.println("Logging: " + e.getMessage());
            }

        }

    }
}
