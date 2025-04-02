import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private ServerSocket srvsocket;
    private Socket clientSocket;
    
    public void connecta() throws IOException {
        srvsocket = new ServerSocket(PORT);
        System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
        System.out.println("Esperant connexions a " + HOST + ":" + PORT);
        
        clientSocket = srvsocket.accept();
        System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
    }
    
    public void repDades() throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        
        
        in.close();
    }
}