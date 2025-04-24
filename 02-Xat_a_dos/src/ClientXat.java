import java.io.*;
import java.net.*;

public class ClientXat {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9999);
            System.out.println("Client connectat");
            
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            
            out.writeObject("Hola servidor!");
            System.out.println("Missatge enviat");
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}