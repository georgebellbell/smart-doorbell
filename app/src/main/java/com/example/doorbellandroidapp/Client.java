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
    private final String HOST = "172.17.153.177";
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
            socket = new Socket(HOST, PORT);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fromServer;
            //printWriter.write(object.toString());
            
            while ((fromServer = bufferedReader.readLine()) != null) {
                JSONObject response = new JSONObject(fromServer);
                System.out.println("Server: " + fromServer);
                if (response.getString("response").equals("connected")) {
                    printWriter.write(object.toString());
                    System.out.println("This ran like Usain Bolt");
                }
                else if (response.getString("response").equals("fail")) {
                    System.out.println("Password doesn't work");
                    break;
                } else if (response.getString("response").equals("success")){
                    System.out.println("Works!!!!!!!!!!!!!!!!!!!1");
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
