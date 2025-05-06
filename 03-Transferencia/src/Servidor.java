import java.io.*;
import java.net.*;
import java.nio.file.*;


//Esta fet que ho guardi a la carpeta /tmp perque jo tinc linux

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    public Socket connectar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            System.out.println("Tancant connexió amb el client: " + socket.getRemoteSocketAddress());
            socket.close();
        }
    }

    public void enviarFitxers(Socket socket) {
        try (
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) ois.readObject();
            
            if (nomFitxer == null || nomFitxer.isEmpty()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            System.out.println("Nomfitxer rebut: " + nomFitxer);
            File file = new File(nomFitxer);
            
            if (!file.exists()) {
                oos.writeObject(null); // Notifica error al client
                oos.flush();
                System.out.println("Error: El fitxer no existeix");
                return;
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());
            System.out.println("Contingut del fitxer a enviar: " + fileContent.length + " bytes");

            oos.writeObject(fileContent);
            oos.flush();
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        } catch (ClassNotFoundException e) {
            System.err.println("Error llegint el fitxer del client: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de comunicació: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = null;
        
        try {
            socket = servidor.connectar();
            servidor.enviarFitxers(socket);
        } catch (IOException e) {
            System.err.println("Error al servidor: " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    servidor.tancarConnexio(socket);
                }
            } catch (IOException e) {
                System.err.println("Error en tancar la connexió: " + e.getMessage());
            }
        }
    }
}