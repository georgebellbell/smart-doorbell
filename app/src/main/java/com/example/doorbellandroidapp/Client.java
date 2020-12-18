package com.example.doorbellandroidapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {
    private final String HOST = "172.17.214.209";
    private final int PORT = 4444;

    Socket socket;
    PrintWriter printWriter;
    @Override
    protected Void doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];

        try {
            socket = new Socket(HOST, PORT);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write(username + "," + password);
            printWriter.flush();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
