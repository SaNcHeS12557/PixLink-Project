package com.example.pixlinkmobileclient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClientHelper {
    private static final String TAG = "WebSocket";
    private WebSocket webSocket;

    public void connectToServer() {
        Log.d(TAG, "Connecting to server...");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://100.107.1.32:8080")
                .build();
        webSocket = client.newWebSocket(request, new WebSocketListener(){

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, "Connected to PixLink Server");
                webSocket.send("Hi From PixLink Mobile Client!!!!!!");
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Received: " + text);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.d(TAG, "Fail: " + t.getMessage());
            }
        });

    }
}
