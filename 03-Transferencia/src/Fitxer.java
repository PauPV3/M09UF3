import java.io.*;
import java.nio.file.*;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() throws IOException {
        File file = new File(nom);
        if (!file.exists()) {
            throw new FileNotFoundException("El fitxer no existeix: " + nom);
        }
        contingut = Files.readAllBytes(file.toPath());
        return contingut;
    }
}