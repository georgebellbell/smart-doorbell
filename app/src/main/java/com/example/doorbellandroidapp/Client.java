package com.example.doorbellandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Client extends Thread {
    // Connection details
    private static final String HOST = "172.17.179.97";
    private static final int PORT = 4444;

    private Activity activity;
    private JSONObject request;

    public Client(Activity activity) {
        this.activity = activity;
    }

    public void setRequest(JSONObject request) {
        this.request = request;
        try {
            this.request.put("token", FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    /**
     * Checks if session is valid from response and sends user back to login if it was invalid
     * @param response - Response received from server
     * @return if session was valid
     */
    private boolean checkSession(final JSONObject response) {
        boolean valid = true;
        try {
            if (response.getString("response").equals("invalid")) {
                valid = false;
                final String error = response.getString("message");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    Toast.makeText(activity, error,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    }
                });
            }
        } catch (JSONException ignored) {
        }
        return valid;
    }

    public void run() {
        try {
            // Create connection
            Socket socket = new Socket(HOST, PORT);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Write connection type to server
            printWriter.println("user");

            // Write request to server
            printWriter.println(getStringRequest());

            // Handle response
            String serverResponse;
            if ((serverResponse = bufferedReader.readLine()) != null) {
                final JSONObject response = new JSONObject(serverResponse);

                if (!checkSession(response)) {
                    return;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handleResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
