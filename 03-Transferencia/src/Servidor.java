import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    
    public Socket connectar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        Socket socket = serverSocket.accept();
        return socket;
    }
    
    public void enviarFitxers(Socket socket) {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String nomFitxer = (String) ois.readObject();
            
            File file = new File(nomFitxer);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(fileContent);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}