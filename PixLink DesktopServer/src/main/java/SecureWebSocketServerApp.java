import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.cert.CertificateException;

public class SecureWebSocketServerApp extends WebSocketServer {

    public SecureWebSocketServerApp(int port) throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        super(new InetSocketAddress(port));

        // SSL SETTINGS//
        String keystorePath = "keystore.jks";
        String keystorePassword = System.getenv("KEYSTORE_PASSWORD");

        // KEYSTORAGE //
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        // SSL INIT //
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        // SSL FACTORY SETUP //
        this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Connection with -" + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Connection closed :( - " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("MSG - " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("ERR - " + e);
    }

    @Override
    public void onStart() {
        System.out.println("WSS DesktopServer Started!");
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        SecureWebSocketServerApp server = new SecureWebSocketServerApp(port);
        server.start();
        System.out.println("Server Port: " + port);
    }
}
