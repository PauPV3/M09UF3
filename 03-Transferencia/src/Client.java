import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp";
    
    public void connectar() throws IOException {
        Socket socket = new Socket("localhost", 9999);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
        String nomFitxer = scanner.nextLine();
        
        oos.writeObject(nomFitxer);
        byte[] fileContent = (byte[]) ois.readObject();
        
        FileOutputStream fos = new FileOutputStream(DIR_ARRIBADA + "/" + new File(nomFitxer).getName());
        fos.write(fileContent);
        fos.close();
    }
}