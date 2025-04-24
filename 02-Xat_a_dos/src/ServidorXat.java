import java.io.*;
import java.net.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat");
    }

    public static void main(String[] args) {
        try {
            ServidorXat servidor = new ServidorXat();
            servidor.iniciarServidor();
            
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat");
            
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            String missatge = (String) in.readObject();
            System.out.println("Rebut: " + missatge);
            
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}