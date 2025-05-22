import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir;

    public ClientXat() {
        this.sortir = false;
    }

    //conexio amb el servidor
    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            System.out.println("Client connectat a " + socket.getInetAddress() + ":" + socket.getPort());
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.err.println("Error en connectar al servidor: " + e.getMessage());
            sortir = true;
        }
    }

    //eniav missatge al servidor
    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                System.out.println("Enviant missatge: " + missatge);
                oos.writeObject(missatge);
                oos.flush();
            } else {
                System.out.println("oos null. Sortint...");
                sortir = true;
            }
        } catch (IOException e) {
            System.err.println("Error enviant missatge: " + e.getMessage());
            sortir = true;
        }
    }

    //tanca el client
    public void tancarClient() {
        try {
            if (ois != null) {
                ois.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (oos != null) {
                oos.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Tancant client...");
            }
        } catch (IOException e) {
            System.err.println("Error en tancar el client: " + e.getMessage());
        }
    }

    //fil per rebre missatges del servidor
    public void executaRebudaMissatges() {
        new Thread(() -> {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                String missatgeCru;
                while (!sortir) {
                    missatgeCru = (String) ois.readObject();
                    String codi = Missatge.getCodiMissatge(missatgeCru);
                    String[] parts = Missatge.getPartsMissatge(missatgeCru);

                    if (codi == null || parts == null) {
                        System.err.println("Missatge rebut incorrecte.");
                        continue;
                    }

                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            System.out.println("Tots els clients s'estan tancant.");
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length > 2) {
                                String remitent = parts[1];
                                String missatge = parts[2];
                                System.out.println("Missatge de (" + remitent + "): " + missatge);
                            }
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length > 1) {
                                String missatge = parts[1];
                                System.out.println(missatge);
                            }
                            break;
                        default:
                            System.err.println("Codi de missatge rebut desconegut: " + codi);
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!sortir) {
                    System.err.println("Error rebent missatge. Sortint...");
                }
                sortir = true;
            } finally {
                tancarClient();
            }
        }).start();
    }

    //opcions
    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    //llegir entrada del usuari
    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linea = "";
        do {
            System.out.print(missatge);
            linea = scanner.nextLine().trim();
            if (obligatori && linea.isEmpty()) {
                System.out.println("Aquest camp és obligatori.");
            }
        } while (obligatori && linea.isEmpty());
        return linea;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);
        String opcio;

        client.connecta(); //iniciar la conexio
        if (client.sortir) {
            System.out.println("No es pot connectar al servidor. Surt del programa.");
            return;
        }

        client.executaRebudaMissatges();

        while (!client.sortir) {
            client.ajuda();
            opcio = client.getLinea(scanner, "", false);

            if (opcio.isEmpty() || opcio.equals("4")) {
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                client.sortir = true;
            } else {
                switch (opcio) {
                    case "1": //nom de conexio
                        String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                        if (nom != null && !nom.isEmpty()) {
                            client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                        }
                        break;
                    case "2": //missatge personal
                        String destinatari = client.getLinea(scanner, "Destinatari:: ", true);
                        String msgPersonal = client.getLinea(scanner, "Missatge a enviar: ", true);
                        if (destinatari != null && !destinatari.isEmpty() && msgPersonal != null && !msgPersonal.isEmpty()) {
                            client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, msgPersonal));
                        }
                        break;
                    case "3": //missatge de grup
                        String msgGrup = client.getLinea(scanner, "Missatge a enviar al grup: ", true);
                        if (msgGrup != null && !msgGrup.isEmpty()) {
                            client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                        }
                        break;
                    case "5": //tot fora
                        client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                        client.sortir = true;
                        break;
                    default:
                        System.out.println("Opció no vàlida.");
                        break;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Fil principal interromput.");
                client.sortir = true;
            }
        }

        scanner.close();
        client.tancarClient();
    }
}