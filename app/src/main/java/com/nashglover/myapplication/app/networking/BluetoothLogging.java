package com.nashglover.myapplication.app.networking;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.nashglover.myapplication.app.Coordinate;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluetoothLogging implements Runnable {

    Vector coordinateVector = new Vector<Coordinate>(300, 100);
    BluetoothSocket serverSocket;
    Handler mainHandler;
    AtomicBoolean tracking;
    AtomicBoolean logging;

    DataInputStream in;

    public BluetoothLogging(BluetoothSocket _serverSocket, Handler _mainHandler) {
        serverSocket = _serverSocket;
        mainHandler = _mainHandler;
        tracking = new AtomicBoolean(false);
        logging = new AtomicBoolean(false);
    }

    public void updateLog(String line) {
        Message msg = mainHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("type", "New Coordinate");
        bundle.putString("coordinate", line);
        msg.setData(bundle);
        mainHandler.sendMessage(msg);
    }

    public void run() {
        System.out.printf("Hello from a logging thread!%n");
        byte[] messageByte = new byte[1000];
        int length;
        int packetType;
        long device1, device2;
        long timestamp = 0;
        double longitude, latitude, altitude;
        tracking.set(true);
        logging.set(true);
        double x, y, z;
        try {
            in = new DataInputStream(serverSocket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error starting data input stream: " + e.getMessage());
        }
        while (tracking.get()) {
            try {
                int bytesRead;
                System.out.println("Reading...");
                bytesRead = in.read(messageByte);
                System.out.println("Bytes read: " + bytesRead);
                ByteBuffer buffer = ByteBuffer.wrap(messageByte);
                length = buffer.getInt();
                System.out.println(length);
                if (length == 32 || length == 56) {
                    System.out.println(length);
                    packetType = buffer.getInt(4);
                    device1 = buffer.getLong(8);
                    device2 = buffer.getLong(16);
                    if (length == 32) {
                        timestamp = buffer.getLong(24);
                        // String line = String.format("Length: %d, Packet Type: %d, Device ID: %d %d, Timestamp: %d%n", length, packetType, device1, device2, timestamp);
                        // addToLog(line);
                        System.out.println("Heartbeat!");
                    } else if (length == 56) {
                        System.out.println("In movement");
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
