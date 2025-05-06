import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp";
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;

    public void connectar() throws IOException {
        System.out.println("Connectant a -> localhost:9999");
        socket = new Socket("localhost", 9999);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
    }

    public void rebreFitxers() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine();
            
            // Enviar el nom del fitxer al servidor
            outputStream.writeObject(nomFitxer);
            outputStream.flush();
            
            // Rebre la resposta del servidor
            byte[] fileContent = (byte[]) inputStream.readObject();
            
            System.out.print("Nom del fitxer a guardar: ");
            String desti = scanner.nextLine();
            
            // Guardar el fitxer rebut
            try (FileOutputStream fos = new FileOutputStream(DIR_ARRIBADA + "/" + desti)) {
                fos.write(fileContent);
            }
            System.out.println("Fitxer rebut i guardat com: " + DIR_ARRIBADA + "/" + desti);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    public void tancarConnexio() throws IOException {
        if (outputStream != null) outputStream.close();
        if (inputStream != null) inputStream.close();
        System.out.println("Connexio tancada..");
    }

    public static void main(String[] args) {
        Client client = new Client();
        
        try {
            client.connectar();
            client.rebreFitxers();
        } catch (IOException e) {
            System.err.println("Error al client: " + e.getMessage());
        } finally {
            try {
                client.tancarConnexio();
            } catch (IOException e) {
                System.err.println("Error en tancar la connexió: " + e.getMessage());
            }
        }
    }
}