package com.example.doorbellandroidapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {
    private final String HOST = "172.17.153.177";
    private final int PORT = 4444;

    Socket socket;
    PrintWriter printWriter;


    @Override
    protected Void doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];
        JSONObject object = new JSONObject();

        try {
            object.put("request","login");
            object.put("username",username);
            object.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            socket = new Socket(HOST, PORT);
            printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write(object.toString());
            printWriter.flush();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
