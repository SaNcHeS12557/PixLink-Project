import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketServerApp  extends WebSocketServer {

    public WebSocketServerApp(int port) {
        super(new InetSocketAddress(port));
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
        System.out.println("DesktopServer Started!");
    }

    public static void main(String[] args) {
        int port = 8080;
        WebSocketServerApp server = new WebSocketServerApp(port);
        server.start();
        System.out.println("Server Port: " + port);
    }
}
