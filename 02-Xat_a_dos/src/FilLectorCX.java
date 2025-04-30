import java.io.*;

public class FilLectorCX implements Runnable {
    private ObjectInputStream in;
    
    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = (String) in.readObject()) != null) {
                System.out.println("Rebut: " + missatge);
                if (missatge.equals("sortir")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}