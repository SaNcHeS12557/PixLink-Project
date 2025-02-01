package com.example.pixlinkmobileclient;

import android.content.Context;
import android.util.Log;

import javax.net.ssl.SSLContext;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class WebSocketClientHelper {
    private static final String TAG = "WebSocket";
    private WebSocket webSocket;
    private Context context;

    public WebSocketClientHelper(Context context) {
        this.context = context;
    }

    public void connectToServer() throws Exception {
        Log.d(TAG, "Connecting to server...");
        OkHttpClient client = getSecureClient();
        Request request = new Request.Builder()
                .url("wss://100.107.1.32:8080")
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
    private OkHttpClient getSecureClient() throws Exception {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.server_cert);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.d(TAG, "Loaded CA: " + ca.toString());
            } finally {
                caInput.close();
            }

            // KEYSTORE //
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("server", ca);

            // TRUSTMANAGER //
            String tmfAlgo = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgo);
            tmf.init(keyStore);

            // X509 //
            TrustManager[] trustManagers = tmf.getTrustManagers();
            if(trustManagers.length == 0 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Fail with default trust managers: " + trustManagers);
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // SSL + TRUSTMANAGER SETUP //
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            // OKHTTP CLIENT //

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .hostnameVerifier((hostname, session) -> {
                        // TESTING ONLY!!!!!!!!!!!!!!!
                        return "100.107.1.32".equals(hostname);
                    })
                        ///////////////////////////////////
                    .build();
        } catch (Exception e) {
            Log.e(TAG, "CA Failed: ", e);
        }
        return null;
    }
}
