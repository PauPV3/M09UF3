import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 7777;
    
    private Socket socket;
    private PrintWriter out;
    
    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
    }
    
    public void envia(String missatge) {
        out.println(missatge);
        System.out.println("Enviat al servidor: " + missatge);
    }
    
    public void tanca() throws IOException {
        if (out != null) out.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat");
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        
        try {
            client.connecta();
            
            client.envia("Prova d'enviament 1");
            client.envia("Prova d'enviament 2");
            client.envia("Adéu!");
            
            System.out.println("Prem Enter per tancar el client...");
            scanner.nextLine();
            
        } catch (IOException e) {
            System.err.println("Error al client: " + e.getMessage());
        } finally {
            try {
                client.tanca();
            } catch (IOException e) {
                System.err.println("Error en tancar connexions: " + e.getMessage());
            }
            scanner.close();
        }
    }
}