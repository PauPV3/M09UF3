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
        
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Rebut: " + inputLine);
            if (inputLine.equals("Adéu!") || inputLine.equals("Adeu!")) {
                break;
            }
        }
        
        in.close();
    }
    
    public void tanca() throws IOException {
        if (clientSocket != null) clientSocket.close();
        if (srvsocket != null) srvsocket.close();
        System.out.println("Servidor tancat.");
    }
    
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            servidor.connecta();
            servidor.repDades();
        } catch (IOException e) {
            System.err.println("Error al servidor: " + e.getMessage());
        } finally {
            try {
                servidor.tanca();
            } catch (IOException e) {
                System.err.println("Error en tancar connexions: " + e.getMessage());
            }
        }
    }
}