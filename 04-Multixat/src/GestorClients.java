import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nomClient;
    private boolean sortir;

    public GestorClients(Socket clientSocket, ServidorXat servidorXat) {
        this.clientSocket = clientSocket;
        this.servidorXat = servidorXat;
        this.sortir = false;
        try {
            //comunicacio amb el client
            this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
            this.ois = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error inicialitzant streams: " + e.getMessage());
            sortir = true;
        }
    }

    public String getNom() {
        return nomClient;
    }

    @Override
    public void run() {
        String missatgeRaw;
        try {
            //llegeix el missatge del cleient
            while (!sortir) {
                missatgeRaw = (String) ois.readObject();
                processaMissatge(missatgeRaw);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!sortir) {
                System.err.println("Error rebent missatge: " + e.getMessage());
            }
        } finally {
            tancarClient();
        }
    }

    //envia missatge al client
    public void enviarMissatge(String remitent, String missatge) throws IOException {
        if (oos != null) {
            String msgAEnviar;
            if (remitent.equals("Servidor")) {
                msgAEnviar = Missatge.getMissatgeGrup(missatge);
            } else {
                msgAEnviar = Missatge.CODI_MSG_PERSONAL + "#" + remitent + "#" + missatge;
            }
            oos.writeObject(msgAEnviar);
            oos.flush();
        } else {
            System.err.println("ObjectOutputStream és null per al client " + nomClient);
        }
    }

    //analiza el missatge
    private void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null || parts == null) {
            System.err.println("Missatge rebut incorrecte: " + missatgeRaw);
            return;
        }

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length > 1) {
                    nomClient = parts[1];
                    servidorXat.afegirClient(this);
                }
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                if (nomClient != null) {
                    servidorXat.eliminarClient(nomClient);
                }
                System.out.println("Client " + nomClient + " sol·licita sortir.");
                break;
            case Missatge.CODI_SORTIR_TOTS: 
                sortir = true;
                servidorXat.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL: 
                if (parts.length > 2) {
                    String destinatari = parts[1];
                    String missatge = parts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, nomClient, missatge);
                }
                break;
            case Missatge.CODI_MSG_GRUP:
                if (parts.length > 1) {
                    String missatge = parts[1];
                    servidorXat.enviarMissatgeGrup(nomClient + ": " + missatge);
                }
                break;
            default:
                System.err.println("Codi de missatge desconegut: " + codi);
                break;
        }
    }

    private void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Connexió amb " + nomClient + " tancada.");
            }
        } catch (IOException e) {
            System.err.println("Error tancant recursos: " + e.getMessage());
        }
    }
}