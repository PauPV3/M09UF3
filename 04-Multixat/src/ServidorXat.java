import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class ServidorXat {
    private final int PORT = 9999;
    private final String HOST = "localhost";
    private final String MSG_SORTIR = "sortir";

    //guardar clients actius
    private Hashtable<String, GestorClients> clientsConnectats;
    private ServerSocket serverSocket;
    private boolean sortir; //vucle prinicpal del server

    public ServidorXat() {
        clientsConnectats = new Hashtable<>();
        sortir = false;
    }

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            //bucle per aceptar noves conexions
            while (!sortir) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());

                GestorClients gestor = new GestorClients(clientSocket, this);
                gestor.start();
            }
        } catch (IOException e) {
            if (!sortir) {
                System.err.println("Error al servidor: " + e.getMessage());
            }
        } finally {
            pararServidor();
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat.");
            }
        } catch (IOException e) {
            System.err.println("Error en tancar el servidor: " + e.getMessage());
        }
    }

    //missatge de tots fora
    public void finalitzarXat() {
        System.out.println("Tancant tots els clients.");
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));

        clientsConnectats.clear();
        sortir = true;
        pararServidor();
        System.exit(0);
    }

    //notificacio a tots
    public synchronized void afegirClient(GestorClients client) {
        if (client.getNom() != null && !client.getNom().isEmpty()) {
            clientsConnectats.put(client.getNom(), client);
            System.out.println(client.getNom() + " connectat.");
            enviarMissatgeGrup("Entra: " + client.getNom());
        }
    }

    //elimina client
    public synchronized void eliminarClient(String nomClient) {
        if (clientsConnectats.containsKey(nomClient)) {
            clientsConnectats.remove(nomClient);
            System.out.println(nomClient + " desconnectat.");
            enviarMissatgeGrup("Surt: " + nomClient);
        }
    }

    //missatge dels clients conectats
    public synchronized void enviarMissatgeGrup(String missatge) {
        System.out.println("DEBUG: multicast " + missatge);
        for (Map.Entry<String, GestorClients> entry : clientsConnectats.entrySet()) {
            GestorClients gestor = entry.getValue();
            try {
                gestor.enviarMissatge("Servidor", missatge);
            } catch (IOException e) {
                System.err.println("Error enviant missatge a " + gestor.getNom() + ": " + e.getMessage());
                eliminarClient(gestor.getNom());
            }
        }
    }

    //missatge a client specific
    public synchronized void enviarMissatgePersonal(String nomDestinatari, String nomRemitent, String missatge) {
        System.out.println("Missatge personal per (" + nomDestinatari + ") de (" + nomRemitent + "): " + missatge);
        GestorClients gestorDestinatari = clientsConnectats.get(nomDestinatari);
        if (gestorDestinatari != null) {
            try {
                gestorDestinatari.enviarMissatge(nomRemitent, missatge);
            } catch (IOException e) {
                System.err.println("Error enviant missatge personal a " + nomDestinatari + ": " + e.getMessage());
                eliminarClient(nomDestinatari);
            }
        } else {
            System.out.println("El client " + nomDestinatari + " no està connectat.");
            GestorClients gestorRemitent = clientsConnectats.get(nomRemitent);
            if(gestorRemitent != null){
                try {
                    gestorRemitent.enviarMissatge("Servidor", "El client " + nomDestinatari + " no es troba connectat.");
                } catch (IOException ex) {
                    System.err.println("Error enviant notificació a remitent " + nomRemitent + ": " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
}