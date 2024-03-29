/*
 * @author Dominykas Makarovas and Jack Reed
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * This class manages the connection between the server and app
 */
public abstract class Client extends Thread {
    // Connection details
    private static final String HOST = "172.18.53.209"; /** <--- CHANGE THIS TO YOU LOCAL IP ADDRESS*/
    private static final int PORT = 4444;

    private Activity activity;
    private JSONObject request;

    public Client(Activity activity) {
        this.activity = activity;
    }

    /**
     * Sets the token of the app to the server with all messages
     * @param request JSONObject being sent to the server
     */
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

    /**
     * Contacts server with request
     */
    public void run() {
        try {
            // Create connection
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(HOST, PORT), 5025);
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
        } catch (SocketTimeoutException | UnknownHostException e) {
            // Unable to connect to socket
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Popups.showInformation(activity,"server");
                    Toast.makeText(activity, "Unknown host, unable to connect to server",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            // Error while communicating with socket
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Popups.showInformation(activity,"server");
                    Toast.makeText(activity, "Error while communicating with server",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
