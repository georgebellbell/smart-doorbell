package com.example.doorbellandroidapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Client extends Thread {
    // Connection details
    private static final String HOST = "192.168.0.35";
    private static final int PORT = 4444;

    private JSONObject request;

    public void setRequest(JSONObject request) {
        this.request = request;
    }

    public String getStringRequest() {
        return request.toString();
    }

    /**
     * Handles the server's response to request sent
     * @param response - Response received from server
     * @throws JSONException - JSON Exception
     */
    public abstract void handleResponse(JSONObject response) throws JSONException;

    public void run() {
        try {
            // Create connection
            Socket socket = new Socket(HOST, PORT);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Write request to server
            printWriter.println(getStringRequest());

            // Handle response
            String serverResponse;
            if ((serverResponse = bufferedReader.readLine()) != null) {
                JSONObject response = new JSONObject(serverResponse);
                handleResponse(response);
            }

            // Close connection
            printWriter.flush();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
