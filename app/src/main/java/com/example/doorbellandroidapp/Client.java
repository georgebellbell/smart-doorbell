package com.example.doorbellandroidapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {
    private final String HOST = "192.168.56.1";
    private final int PORT = 4444;

    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;

    @Override
    protected Void doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];
        JSONObject object = new JSONObject();

        try {
            object.put("request","login");
            object.put("username", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            // Create connection
            socket = new Socket(HOST, PORT);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Write JSON object to server
            printWriter.println(object.toString());

            // Handle response
            String fromServer;
            if ((fromServer = bufferedReader.readLine()) != null) {
                JSONObject response = new JSONObject(fromServer);
                System.out.println("Server: " + fromServer);
                switch (response.getString("response")) {
                    case "success":
                        System.out.println("Login success");
                        break;
                    case "fail":
                        System.out.println(response.getString("message"));
                        break;
                }
            }

            printWriter.flush();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
